"use client";

import { useEffect, useState } from "react";
import ProjectDashboard from "../components/ProjectDashboard";
import CreditSummary from "../components/CreditSummary";
import {
  ApiError,
  apiUrl,
  bootstrapCsrf,
  currentUser,
  logout,
  type CurrentUser,
} from "../lib/api";

type SessionState =
  | { status: "loading" }
  | { status: "signed-out" }
  | { status: "signed-in"; user: CurrentUser; csrfToken: string }
  | { status: "error"; message: string };

export default function HomePage() {
  const [session, setSession] = useState<SessionState>({ status: "loading" });
  const [loggingOut, setLoggingOut] = useState(false);
  const [loginFailed, setLoginFailed] = useState(false);
  const [creditRefresh, setCreditRefresh] = useState(0);

  useEffect(() => {
    let active = true;
    setLoginFailed(new URLSearchParams(window.location.search).has("login_error"));

    async function loadSession() {
      try {
        const csrfToken = await bootstrapCsrf();
        const user = await currentUser();
        if (active) setSession({ status: "signed-in", user, csrfToken });
      } catch (error) {
        if (!active) return;
        if (error instanceof ApiError && error.status === 401) {
          setSession({ status: "signed-out" });
        } else {
          setSession({ status: "error", message: "Unable to load your account. Please try again." });
        }
      }
    }

    void loadSession();
    return () => { active = false; };
  }, []);

  async function handleLogout(csrfToken: string) {
    if (loggingOut) return;
    setLoggingOut(true);
    try {
      await logout(csrfToken);
      setSession({ status: "signed-out" });
    } catch {
      setSession({ status: "error", message: "Unable to sign out. Please try again." });
    } finally {
      setLoggingOut(false);
    }
  }

  return (
    <main className="shell">
      <section className={session.status === "signed-in" ? "card card-wide" : "card"}>
        <span className="eyebrow">3D Listing AI</span>
        <h1>Publish your model with less busywork.</h1>
        <p>Prepare your listing and promotional images in one place.</p>

        {session.status === "loading" && <p role="status">Checking your session…</p>}

        {session.status === "signed-out" && (
          <div>
            {loginFailed && <p role="alert">Google sign-in failed. Please try again.</p>}
            <a className="button" href={apiUrl("/oauth2/authorization/google")}>
              Sign in with Google
            </a>
          </div>
        )}

        {session.status === "signed-in" && (
          <div className="account">
            <p>Signed in as <strong>{session.user.name ?? session.user.email ?? "Creator"}</strong></p>
            {session.user.email && <p className="muted">{session.user.email}</p>}
            <CreditSummary key={creditRefresh} />
            <button
              className="button button-secondary"
              disabled={loggingOut}
              onClick={() => void handleLogout(session.csrfToken)}
              type="button"
            >
              {loggingOut ? "Signing out…" : "Sign out"}
            </button>
            <ProjectDashboard csrfToken={session.csrfToken}
              onCreditsChanged={() => setCreditRefresh((value) => value + 1)} />
          </div>
        )}

        {session.status === "error" && (
          <div role="alert">
            <p>{session.message}</p>
            <button className="button button-secondary" onClick={() => window.location.reload()} type="button">
              Retry
            </button>
          </div>
        )}
      </section>
    </main>
  );
}
