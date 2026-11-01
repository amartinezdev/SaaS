"use client";

import { useEffect, useState, type FormEvent } from "react";
import {
  ApiError,
  createProject,
  deleteProject,
  generateListing,
  latestListing,
  listProjects,
  projectImageUrl,
  renameProject,
  uploadProjectImage,
  type Project,
  type Listing,
} from "../lib/api";

export default function ProjectDashboard({ csrfToken, onCreditsChanged }:
  { csrfToken: string; onCreditsChanged: () => void }) {
  const [projects, setProjects] = useState<Project[]>([]);
  const [selectedId, setSelectedId] = useState<string | null>(null);
  const [name, setName] = useState("");
  const [rename, setRename] = useState("");
  const [file, setFile] = useState<File | null>(null);
  const [loading, setLoading] = useState(true);
  const [busy, setBusy] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const [listing, setListing] = useState<Listing | null>(null);
  const [listingLoading, setListingLoading] = useState(false);
  const [generating, setGenerating] = useState(false);
  const [language, setLanguage] = useState<"en" | "es">("en");

  useEffect(() => {
    let active = true;
    listProjects()
      .then((items) => {
        if (!active) return;
        setProjects(items);
        if (items.length > 0) {
          setSelectedId(items[0].id);
          setRename(items[0].name);
        }
      })
      .catch(() => { if (active) setMessage("Unable to load projects. Refresh to try again."); })
      .finally(() => { if (active) setLoading(false); });
    return () => { active = false; };
  }, []);

  const selected = projects.find((project) => project.id === selectedId);

  useEffect(() => {
    if (!selectedId) { setListing(null); return; }
    let active = true;
    setListing(null);
    setListingLoading(true);
    latestListing(selectedId)
      .then((value) => { if (active) setListing(value); })
      .catch((error) => {
        if (active && !(error instanceof ApiError && error.status === 404)) {
          setMessage("Unable to load the listing. Refresh to try again.");
        }
      })
      .finally(() => { if (active) setListingLoading(false); });
    return () => { active = false; };
  }, [selectedId]);

  function selectProject(project: Project) {
    setSelectedId(project.id);
    setRename(project.name);
    setFile(null);
    setMessage(null);
  }

  function replaceProject(updated: Project) {
    setProjects((items) => items.map((item) => item.id === updated.id ? updated : item));
  }

  async function handleCreate(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (busy || !name.trim()) return;
    setBusy(true);
    setMessage(null);
    try {
      const project = await createProject(name.trim(), csrfToken);
      setProjects((items) => [project, ...items]);
      setSelectedId(project.id);
      setRename(project.name);
      setName("");
    } catch {
      setMessage("Unable to create the project. Please try again.");
    } finally {
      setBusy(false);
    }
  }

  async function handleRename(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!selected || busy || !rename.trim()) return;
    setBusy(true);
    setMessage(null);
    try {
      replaceProject(await renameProject(selected.id, rename.trim(), csrfToken));
    } catch {
      setMessage("Unable to rename the project. Please try again.");
    } finally {
      setBusy(false);
    }
  }

  async function handleUpload(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!selected || !file || busy) return;
    if (file.size === 0 || file.size > 10 * 1024 * 1024) {
      setMessage("Choose an image smaller than 10 MB.");
      return;
    }
    setBusy(true);
    setMessage(null);
    try {
      replaceProject(await uploadProjectImage(selected.id, file, csrfToken));
      setFile(null);
      setMessage("Image uploaded.");
    } catch {
      setMessage("Upload failed. Use a valid PNG, JPEG or WebP image under 10 MB.");
    } finally {
      setBusy(false);
    }
  }

  async function handleDelete() {
    if (!selected || busy || !window.confirm(`Delete "${selected.name}"?`)) return;
    setBusy(true);
    setMessage(null);
    try {
      await deleteProject(selected.id, csrfToken);
      const remaining = projects.filter((project) => project.id !== selected.id);
      setProjects(remaining);
      setSelectedId(remaining[0]?.id ?? null);
      setRename(remaining[0]?.name ?? "");
      setFile(null);
    } catch {
      setMessage("Unable to delete the project. Please try again.");
    } finally {
      setBusy(false);
    }
  }

  async function handleGenerateListing() {
    if (!selected?.hasImage || generating || busy) return;
    setGenerating(true);
    setMessage(null);
    try {
      const result = await generateListing(selected.id, language, csrfToken);
      setListing(result);
      onCreditsChanged();
      setMessage("Listing generated. Review it before publishing.");
    } catch (error) {
      if (error instanceof ApiError && error.status === 409) {
        setMessage("No credits remain, or this request is already in progress.");
      } else if (error instanceof ApiError && error.status === 429) {
        setMessage("Today's listing limit has been reached. Try again tomorrow.");
      } else if (error instanceof ApiError && error.status === 503) {
        setMessage("Listing generation needs a Gemini API key on the server.");
      } else {
        setMessage("Generation failed. Your credit was returned if it was reserved.");
      }
    } finally {
      setGenerating(false);
    }
  }

  return (
    <section className="dashboard" aria-label="Projects">
      <div className="dashboard-heading">
        <h2>Your projects</h2>
        <p>Start with a render or photo of your 3D model.</p>
      </div>

      <form className="inline-form" onSubmit={(event) => void handleCreate(event)}>
        <label htmlFor="project-name">New project</label>
        <div className="form-row">
          <input id="project-name" maxLength={160} onChange={(event) => setName(event.target.value)}
            placeholder="Model name" required value={name} />
          <button className="button" disabled={busy || !name.trim()} type="submit">Create</button>
        </div>
      </form>

      {message && <p role="status" className="message">{message}</p>}
      {loading && <p role="status">Loading projects…</p>}
      {!loading && projects.length === 0 && <p>No projects yet. Create one above.</p>}

      {projects.length > 0 && (
        <div className="project-grid">
          <nav className="project-list" aria-label="Project list">
            {projects.map((project) => (
              <button key={project.id} type="button"
                className={project.id === selectedId ? "project-item selected" : "project-item"}
                onClick={() => selectProject(project)}>
                <strong>{project.name}</strong>
                <span>{project.hasImage ? "Image ready" : "Needs image"}</span>
              </button>
            ))}
          </nav>

          {selected && (
            <div className="project-detail">
              <h3>{selected.name}</h3>
              {selected.hasImage ? (
                <img className="project-image" src={projectImageUrl(selected)} alt={`Source image for ${selected.name}`} />
              ) : <div className="image-placeholder">No image uploaded</div>}

              <form onSubmit={(event) => void handleUpload(event)}>
                <label htmlFor="project-image">Model image (PNG, JPEG or WebP, up to 10 MB)</label>
                <div className="form-row">
                  <input id="project-image" type="file" accept="image/png,image/jpeg,image/webp"
                    onChange={(event) => setFile(event.target.files?.[0] ?? null)} />
                  <button className="button" disabled={busy || !file} type="submit">
                    {busy ? "Working…" : "Upload image"}
                  </button>
                </div>
              </form>

              <form onSubmit={(event) => void handleRename(event)}>
                <label htmlFor="rename-project">Project name</label>
                <div className="form-row">
                  <input id="rename-project" maxLength={160} required value={rename}
                    onChange={(event) => setRename(event.target.value)} />
                  <button className="button button-secondary" disabled={busy || !rename.trim()} type="submit">Rename</button>
                </div>
              </form>

              <div className="listing-panel">
                <h4>AI listing</h4>
                <p>Generate a draft from the model image, then check every claim before publishing.</p>
                <label htmlFor="listing-language">Language</label>
                <select id="listing-language" value={language}
                  onChange={(event) => setLanguage(event.target.value as "en" | "es")}>
                  <option value="en">English</option>
                  <option value="es">Español</option>
                </select>
                <button className="button" type="button" disabled={!selected.hasImage || busy || generating}
                  onClick={() => void handleGenerateListing()}>
                  {generating ? "Generating…" : "Generate listing (1 credit)"}
                </button>
                {!selected.hasImage && <p>Upload an image first.</p>}
                {listingLoading && <p role="status">Loading listing…</p>}
                {listing && <div className="listing-result">
                  <h5>{listing.title}</h5>
                  <p>{listing.description}</p>
                  <p><strong>Category:</strong> {listing.category}</p>
                  <p><strong>Tags:</strong> {listing.tags.join(", ")}</p>
                  <p><strong>Keywords:</strong> {listing.keywords.join(", ")}</p>
                </div>}
              </div>

              <button className="text-button" disabled={busy} onClick={() => void handleDelete()} type="button">
                Delete project
              </button>
            </div>
          )}
        </div>
      )}
    </section>
  );
}
