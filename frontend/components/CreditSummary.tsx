"use client";

import { useEffect, useState } from "react";
import { currentCredits, type Credits } from "../lib/api";

export default function CreditSummary() {
  const [credits, setCredits] = useState<Credits | null>(null);
  const [error, setError] = useState(false);

  useEffect(() => {
    let active = true;
    currentCredits()
      .then((value) => { if (active) setCredits(value); })
      .catch(() => { if (active) setError(true); });
    return () => { active = false; };
  }, []);

  return (
    <div className="credit-summary" aria-live="polite">
      {!credits && !error && <span>Loading credits…</span>}
      {error && <span>Credits unavailable. Refresh to try again.</span>}
      {credits && <><strong>{credits.balance} credit{credits.balance === 1 ? "" : "s"}</strong><span>{credits.plan} plan</span></>}
    </div>
  );
}
