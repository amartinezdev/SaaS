// Production uses the same origin as Spring Boot. Set the variable only when
// running the frontend development server on a separate port.
const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL ?? "";

export class ApiError extends Error {
  constructor(public readonly status: number) {
    super(`API request failed: ${status}`);
  }
}

export type CurrentUser = {
  id: string;
  email: string | null;
  name: string | null;
};

export type Project = {
  id: string;
  name: string;
  hasImage: boolean;
  createdAt: string;
  updatedAt: string;
};

export type Credits = {
  plan: "FREE" | "CREATOR" | "PRO";
  balance: number;
};

export type Listing = {
  id: string;
  title: string;
  description: string;
  tags: string[];
  keywords: string[];
  category: string;
  language: "en" | "es";
  createdAt: string;
};

export function apiUrl(path: string): string {
  return `${API_BASE_URL}${path}`;
}

export async function apiFetch<T>(
  path: string,
  init: RequestInit = {}
): Promise<T> {
  const response = await fetch(apiUrl(path), {
    ...init,
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
      ...(init.headers ?? {}),
    },
  });

  if (!response.ok) {
    throw new ApiError(response.status);
  }

  return response.json() as Promise<T>;
}

export async function bootstrapCsrf(): Promise<string> {
  const response = await fetch(apiUrl("/api/auth/csrf"), {
    credentials: "include",
  });

  if (!response.ok) {
    throw new Error("Unable to initialize CSRF protection");
  }

  const data = (await response.json()) as { token: string };
  return data.token;
}

export function currentUser(): Promise<CurrentUser> {
  return apiFetch<CurrentUser>("/api/auth/me");
}

export function currentCredits(): Promise<Credits> {
  return apiFetch<Credits>("/api/me/credits");
}

export async function logout(csrfToken: string): Promise<void> {
  const response = await fetch(apiUrl("/logout"), {
    method: "POST",
    credentials: "include",
    headers: { "X-XSRF-TOKEN": csrfToken },
  });

  if (!response.ok) {
    throw new ApiError(response.status);
  }
}

export function listProjects(): Promise<Project[]> {
  return apiFetch<Project[]>("/api/projects");
}

export function createProject(name: string, csrfToken: string): Promise<Project> {
  return apiFetch<Project>("/api/projects", {
    method: "POST",
    headers: { "X-XSRF-TOKEN": csrfToken },
    body: JSON.stringify({ name }),
  });
}

export function renameProject(id: string, name: string, csrfToken: string): Promise<Project> {
  return apiFetch<Project>(`/api/projects/${id}`, {
    method: "PUT",
    headers: { "X-XSRF-TOKEN": csrfToken },
    body: JSON.stringify({ name }),
  });
}

export async function deleteProject(id: string, csrfToken: string): Promise<void> {
  const response = await fetch(apiUrl(`/api/projects/${id}`), {
    method: "DELETE",
    credentials: "include",
    headers: { "X-XSRF-TOKEN": csrfToken },
  });
  if (!response.ok) throw new ApiError(response.status);
}

export async function uploadProjectImage(
  id: string,
  file: File,
  csrfToken: string
): Promise<Project> {
  const body = new FormData();
  body.append("file", file);
  const response = await fetch(apiUrl(`/api/projects/${id}/image`), {
    method: "POST",
    credentials: "include",
    headers: { "X-XSRF-TOKEN": csrfToken },
    body,
  });
  if (!response.ok) throw new ApiError(response.status);
  return response.json() as Promise<Project>;
}

export function projectImageUrl(project: Project): string {
  return apiUrl(`/api/projects/${project.id}/image?v=${encodeURIComponent(project.updatedAt)}`);
}

export function latestListing(projectId: string): Promise<Listing> {
  return apiFetch<Listing>(`/api/projects/${projectId}/listing`);
}

export function generateListing(projectId: string, language: "en" | "es", csrfToken: string): Promise<Listing> {
  return apiFetch<Listing>(`/api/projects/${projectId}/listing/generate`, {
    method: "POST",
    headers: { "X-XSRF-TOKEN": csrfToken, "Idempotency-Key": crypto.randomUUID() },
    body: JSON.stringify({ language }),
  });
}
