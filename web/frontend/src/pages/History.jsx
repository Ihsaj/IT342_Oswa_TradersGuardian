import React, { useState, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { useSettings } from "../context/SettingsContext";
import dashboardService from "../services/dashboardService";

/* ─────────────────────────────────────────────────────────────
   Shared Nav — matches Dashboard style exactly (icons + logout)
───────────────────────────────────────────────────────────── */
function Nav({ onLogout }) {
  const navigate = useNavigate();
  const path = window.location.pathname;

  const links = [
    {
      to: "/dashboard", label: "Dashboard",
      icon: <><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></>,
    },
    {
      to: "/plan-trade", label: "Plan Trade",
      icon: <path strokeLinecap="round" strokeLinejoin="round" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6"/>,
    },
    {
      to: "/history", label: "History",
      icon: <><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></>,
    },
    {
      to: "/settings", label: "Settings",
      icon: <><circle cx="12" cy="12" r="3"/><path strokeLinecap="round" strokeLinejoin="round" d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 4.68 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 2.83-2.83l.06.06A1.65 1.65 0 0 0 9 4.68a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z"/></>,
    },
  ];

  return (
    <nav style={{ background: "#1a1d23", borderBottom: "1px solid #2a2d35", position: "sticky", top: 0, zIndex: 50 }}>
      <div style={{ maxWidth: 1200, margin: "0 auto", padding: "0 24px", height: 52, display: "flex", alignItems: "center", gap: 32 }}>
        {/* Logo */}
        <div style={{ display: "flex", alignItems: "center", gap: 10, flexShrink: 0 }}>
          <div style={{ width: 30, height: 30, background: "#00c8e0", borderRadius: 6, display: "flex", alignItems: "center", justifyContent: "center" }}>
            <svg width="20" height="20" fill="none" stroke="#0d0d0d" strokeWidth="2.5" viewBox="0 0 24 24">
              <polyline points="3 17 9 11 13 15 21 7"/><polyline points="14 7 21 7 21 14"/>
            </svg>
          </div>
          <span style={{ fontSize: 15, fontWeight: 600, color: "#f0f0f0" }}>Trader's Guardian</span>
        </div>

        {/* Nav links */}
        <div style={{ display: "flex", alignItems: "center", gap: 4, flex: 1 }}>
          {links.map(l => {
            const active = path === l.to;
            return (
              <button key={l.to}
                onClick={() => navigate(l.to)}
                style={{ display: "flex", alignItems: "center", gap: 6, padding: "6px 12px", borderRadius: 6, border: "none", fontSize: 13, fontWeight: 500, cursor: "pointer", transition: "all 0.15s",
                  background: active ? "rgba(0,200,224,0.12)" : "transparent",
                  color: active ? "#00c8e0" : "#9ca3af" }}>
                <svg width="14" height="14" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">{l.icon}</svg>
                {l.label}
              </button>
            );
          })}
        </div>

        {/* Logout */}
        <button onClick={onLogout}
          style={{ display: "flex", alignItems: "center", gap: 6, padding: "6px 14px", borderRadius: 6, border: "1px solid #2a2d35", background: "transparent", color: "#9ca3af", fontSize: 13, fontWeight: 500, cursor: "pointer", transition: "all 0.15s", flexShrink: 0 }}>
          <svg width="14" height="14" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"/>
          </svg>
          Logout
        </button>
      </div>
    </nav>
  );
}

/* ─────────────────────────────────────────────────────────────
   Status helpers
───────────────────────────────────────────────────────────── */
const STATUS_COLORS = { APPROVED: "#22c55e", DISAPPROVED: "#ef4444", PENDING: "#f59e0b" };
const STATUS_BG    = { APPROVED: "rgba(34,197,94,0.12)", DISAPPROVED: "rgba(239,68,68,0.12)", PENDING: "rgba(245,158,11,0.12)" };

/* ─────────────────────────────────────────────────────────────
   LossRow — shown ONLY under approved trades in APPROVED tab
───────────────────────────────────────────────────────────── */
function LossRow({ trade, onRecord, loading }) {
  const isLoss = trade.outcome === "LOSS";
  const [lossAmount, setLossAmount] = useState("");
  const [submitting, setSubmitting] = useState(false);

  // Sync when trade data refreshes
  useEffect(() => {
    if (trade.outcome === "LOSS" && trade.profitLossAmount != null) {
      setLossAmount(String(Math.abs(trade.profitLossAmount)));
    } else {
      setLossAmount("");
    }
  }, [trade.outcome, trade.profitLossAmount]);

  const handleMarkLoss = async () => {
    setSubmitting(true);
    try {
      const amount = parseFloat(lossAmount);
      await onRecord(trade.id, "LOSS", lossAmount ? -Math.abs(amount) : null);
    } finally {
      setSubmitting(false);
    }
  };

  const handleUnmark = async () => {
    setSubmitting(true);
    try {
      await onRecord(trade.id, "WIN", null);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <tr style={{ background: isLoss ? "rgba(239,68,68,0.04)" : "rgba(0,0,0,0.08)", borderBottom: "1px solid #2a2d35" }}>
      <td colSpan={11} style={{ padding: "8px 16px 10px 32px" }}>
        <div style={{ display: "flex", alignItems: "center", gap: 12, flexWrap: "wrap" }}>
          <svg width="13" height="13" fill="none" stroke={isLoss ? "#ef4444" : "#6b7280"} strokeWidth="2" viewBox="0 0 24 24">
            <circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/>
          </svg>
          <span style={{ fontSize: 12, color: "#9ca3af", fontWeight: 500 }}>
            {isLoss ? "Marked as loss — affects Daily Loss tracker:" : "Was this a loss?"}
          </span>

          {!isLoss && (
            <>
              <div style={{ display: "flex", alignItems: "center", gap: 6 }}>
                <span style={{ fontSize: 12, color: "#6b7280" }}>Loss amount $</span>
                <input
                  type="number" step="0.01" min="0" placeholder="optional"
                  value={lossAmount} onChange={e => setLossAmount(e.target.value)}
                  style={{ background: "#212428", border: "1px solid #2a2d35", borderRadius: 6, padding: "4px 10px", color: "#e2e8f0", fontSize: 12, width: 120, outline: "none" }}
                />
              </div>
              <button disabled={submitting || loading} onClick={handleMarkLoss}
                style={{ padding: "5px 16px", borderRadius: 6, border: "none", cursor: submitting ? "not-allowed" : "pointer",
                  background: "rgba(239,68,68,0.85)", color: "#fff", fontSize: 12, fontWeight: 700, opacity: submitting ? 0.6 : 1 }}>
                {submitting ? "Saving..." : "Mark as Loss"}
              </button>
            </>
          )}

          {isLoss && (
            <>
              <span style={{ padding: "3px 12px", borderRadius: 4, fontSize: 12, fontWeight: 700,
                background: "rgba(239,68,68,0.2)", color: "#ef4444", border: "1px solid rgba(239,68,68,0.4)" }}>
                LOSS {trade.profitLossAmount != null ? `— $${Math.abs(trade.profitLossAmount).toFixed(2)}` : ""}
              </span>
              <button disabled={submitting || loading} onClick={handleUnmark}
                style={{ padding: "4px 10px", borderRadius: 6, border: "1px solid #2a2d35", background: "transparent", color: "#6b7280", fontSize: 11, cursor: "pointer" }}>
                Unmark
              </button>
            </>
          )}
        </div>
      </td>
    </tr>
  );
}

/* ─────────────────────────────────────────────────────────────
   Trade row
───────────────────────────────────────────────────────────── */
function TradeRow({ t, showLossRow, actionLoading, onApprove, onDisapprove, onDelete, onOutcome }) {
  return (
    <React.Fragment key={t.id}>
      <tr style={{ borderBottom: showLossRow ? "none" : "1px solid #2a2d35" }}>
        <td style={{ padding: "12px 16px", fontSize: 13, fontWeight: 600, color: "#f0f0f0" }}>{t.symbol}</td>
        <td style={{ padding: "12px 16px" }}>
          <span style={{ padding: "2px 8px", borderRadius: 4, fontSize: 11, fontWeight: 600,
            background: t.tradeType === "BUY" ? "rgba(34,197,94,0.12)" : "rgba(239,68,68,0.12)",
            color: t.tradeType === "BUY" ? "#22c55e" : "#ef4444" }}>{t.tradeType}</span>
        </td>
        <td style={{ padding: "12px 16px", fontSize: 13, color: "#e2e8f0" }}>{t.entryPrice}</td>
        <td style={{ padding: "12px 16px", fontSize: 13, color: "#ef4444" }}>{t.stopLoss}</td>
        <td style={{ padding: "12px 16px", fontSize: 13, color: "#22c55e" }}>{t.takeProfit}</td>
        <td style={{ padding: "12px 16px", fontSize: 13, color: "#e2e8f0" }}>{t.positionSize?.toFixed(4)}</td>
        <td style={{ padding: "12px 16px", fontSize: 13, color: "#e2e8f0" }}>{t.riskPercent?.toFixed(1)}% / ${t.riskAmount?.toFixed(2)}</td>
        <td style={{ padding: "12px 16px" }}>
          <span style={{ padding: "2px 8px", borderRadius: 4, fontSize: 11, fontWeight: 600,
            background: STATUS_BG[t.status], color: STATUS_COLORS[t.status] }}>{t.status}</span>
        </td>
        {/* Loss badge */}
        <td style={{ padding: "12px 16px" }}>
          {t.outcome === "LOSS" ? (
            <span style={{ padding: "2px 10px", borderRadius: 4, fontSize: 11, fontWeight: 700,
              background: "rgba(239,68,68,0.18)", color: "#ef4444", border: "1px solid rgba(239,68,68,0.35)" }}>
              LOSS{t.profitLossAmount != null ? ` -$${Math.abs(t.profitLossAmount).toFixed(2)}` : ""}
            </span>
          ) : (
            <span style={{ color: "#4b5563", fontSize: 11 }}>—</span>
          )}
        </td>
        <td style={{ padding: "12px 16px", fontSize: 12, color: "#6b7280", whiteSpace: "nowrap" }}>
          {new Date(t.createdAt).toLocaleDateString()}
        </td>
        <td style={{ padding: "12px 16px" }}>
          <div style={{ display: "flex", gap: 6 }}>
            {t.status === "PENDING" && (
              <>
                <button onClick={() => onApprove(t.id)} disabled={actionLoading === t.id}
                  style={{ padding: "4px 10px", borderRadius: 6, border: "none", background: "rgba(34,197,94,0.15)", color: "#22c55e", fontSize: 11, fontWeight: 600, cursor: "pointer" }}>
                  Approve
                </button>
                <button onClick={() => onDisapprove(t.id)} disabled={actionLoading === t.id}
                  style={{ padding: "4px 10px", borderRadius: 6, border: "none", background: "rgba(239,68,68,0.15)", color: "#ef4444", fontSize: 11, fontWeight: 600, cursor: "pointer" }}>
                  Reject
                </button>
              </>
            )}
            <button onClick={() => onDelete(t.id)} disabled={actionLoading === t.id}
              style={{ padding: "4px 10px", borderRadius: 6, border: "1px solid #2a2d35", background: "transparent", color: "#6b7280", fontSize: 11, cursor: "pointer" }}>
              Delete
            </button>
          </div>
        </td>
      </tr>
      {/* Loss row: ONLY in APPROVED tab */}
      {showLossRow && (
        <LossRow trade={t} onRecord={onOutcome} loading={actionLoading === t.id} />
      )}
    </React.Fragment>
  );
}

/* ─────────────────────────────────────────────────────────────
   Main page
───────────────────────────────────────────────────────────── */
// Filter definitions
// ALL = approved + disapproved (no pending)
// PENDING = pending only
// APPROVED = approved (+ loss rows)
// DISAPPROVED = disapproved
const TABS = [
  { key: "ALL",          label: "All"         },
  { key: "PENDING",      label: "Pending"     },
  { key: "APPROVED",     label: "Approved"    },
  { key: "DISAPPROVED",  label: "Rejected"    },
];

export default function History() {
  const navigate = useNavigate();
  const { logout, isAuthenticated } = useAuth();
  const { fetchSettings } = useSettings();
  const [trades, setTrades] = useState([]);
  const [dataLoading, setDataLoading] = useState(true);
  const [activeTab, setActiveTab] = useState("ALL");
  const [actionLoading, setActionLoading] = useState(null);
  const [error, setError] = useState("");

  const fetchTrades = useCallback(async () => {
    try {
      setDataLoading(true);
      const res = await dashboardService.getTrades();
      setTrades(res.data.data || []);
      setError("");
    } catch (_e) {
      setError("Failed to load trade history.");
    } finally {
      setDataLoading(false);
    }
  }, []);

  useEffect(() => { fetchTrades(); }, [fetchTrades]);

  const handleApprove = async (id) => {
    setActionLoading(id);
    try { await dashboardService.approveTrade(id); await fetchTrades(); }
    catch (_e) { setError("Failed to approve trade."); }
    finally { setActionLoading(null); }
  };

  const handleDisapprove = async (id) => {
    const reason = window.prompt("Reason for disapproval (optional):");
    if (reason === null) return; // user cancelled
    setActionLoading(id);
    try { await dashboardService.disapproveTrade(id, reason || ""); await fetchTrades(); }
    catch (_e) { setError("Failed to disapprove trade."); }
    finally { setActionLoading(null); }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Delete this trade plan?")) return;
    setActionLoading(id);
    try { await dashboardService.deleteTrade(id); await fetchTrades(); }
    catch (_e) { setError("Failed to delete trade."); }
    finally { setActionLoading(null); }
  };

  const handleOutcome = async (id, outcome, profitLossAmount) => {
    setActionLoading(id);
    try {
      await dashboardService.recordOutcome(id, outcome, profitLossAmount);
      await Promise.all([fetchTrades(), fetchSettings()]);
    } catch (_e) {
      setError("Failed to record outcome.");
    } finally {
      setActionLoading(null);
    }
  };

  if (!isAuthenticated) return null;

  // Filter logic per tab
  const filtered = (() => {
    switch (activeTab) {
      case "ALL":         return trades.filter(t => t.status === "APPROVED" || t.status === "DISAPPROVED");
      case "PENDING":     return trades.filter(t => t.status === "PENDING");
      case "APPROVED":    return trades.filter(t => t.status === "APPROVED");
      case "DISAPPROVED": return trades.filter(t => t.status === "DISAPPROVED");
      default:            return trades;
    }
  })();

  // Loss rows only show in APPROVED tab
  const showLossRows = activeTab === "APPROVED";

  // Summary
  const totalLosses = trades.filter(t => t.outcome === "LOSS").length;
  const totalLossAmount = trades
    .filter(t => t.outcome === "LOSS" && t.profitLossAmount != null)
    .reduce((sum, t) => sum + Math.abs(t.profitLossAmount), 0);

  // Badge counts for tabs
  const counts = {
    ALL:         trades.filter(t => t.status === "APPROVED" || t.status === "DISAPPROVED").length,
    PENDING:     trades.filter(t => t.status === "PENDING").length,
    APPROVED:    trades.filter(t => t.status === "APPROVED").length,
    DISAPPROVED: trades.filter(t => t.status === "DISAPPROVED").length,
  };

  return (
    <div style={{ minHeight: "100vh", background: "#111316", fontFamily: "'Inter','Segoe UI',sans-serif", color: "#e2e8f0" }}>
      <Nav onLogout={() => { logout(); navigate("/login"); }} />

      <main style={{ maxWidth: 1200, margin: "0 auto", padding: "28px 24px", display: "flex", flexDirection: "column", gap: 20 }}>

        {/* Header */}
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start" }}>
          <div>
            <h1 style={{ fontSize: 26, fontWeight: 700, color: "#f0f0f0", margin: 0 }}>Trade History</h1>
            <p style={{ fontSize: 13, color: "#6b7280", margin: "4px 0 0" }}>Review trades and record losses to track your daily limit</p>
          </div>
          <button onClick={() => navigate("/plan-trade")}
            style={{ display: "flex", alignItems: "center", gap: 8, padding: "9px 18px", borderRadius: 8, border: "none", background: "#00c8e0", color: "#0d1117", fontSize: 13, fontWeight: 600, cursor: "pointer" }}>
            <svg width="14" height="14" fill="none" stroke="currentColor" strokeWidth="2.5" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6"/>
            </svg>
            Plan New Trade
          </button>
        </div>

        {/* Error banner */}
        {error && (
          <div style={{ padding: "12px 16px", borderRadius: 8, background: "rgba(239,68,68,0.12)", border: "1px solid rgba(239,68,68,0.3)", color: "#ef4444", fontSize: 13, display: "flex", justifyContent: "space-between", alignItems: "center" }}>
            <span>{error}</span>
            <button onClick={() => setError("")} style={{ background: "none", border: "none", color: "#ef4444", cursor: "pointer", fontSize: 18, lineHeight: 1, padding: "0 4px" }}>×</button>
          </div>
        )}

        {/* Loss summary — always visible if there are losses */}
        {totalLosses > 0 && (
          <div style={{ display: "grid", gridTemplateColumns: totalLossAmount > 0 ? "1fr 1fr" : "1fr", gap: 12 }}>
            <div style={{ background: "rgba(239,68,68,0.08)", border: "1px solid rgba(239,68,68,0.25)", borderRadius: 10, padding: "14px 18px" }}>
              <span style={{ fontSize: 11, color: "#6b7280", fontWeight: 500, textTransform: "uppercase", letterSpacing: "0.05em", display: "block", marginBottom: 4 }}>Losing Trades</span>
              <span style={{ fontSize: 26, fontWeight: 700, color: "#ef4444" }}>{totalLosses}</span>
            </div>
            {totalLossAmount > 0 && (
              <div style={{ background: "rgba(239,68,68,0.08)", border: "1px solid rgba(239,68,68,0.25)", borderRadius: 10, padding: "14px 18px" }}>
                <span style={{ fontSize: 11, color: "#6b7280", fontWeight: 500, textTransform: "uppercase", letterSpacing: "0.05em", display: "block", marginBottom: 4 }}>Total Loss Amount</span>
                <span style={{ fontSize: 26, fontWeight: 700, color: "#ef4444" }}>-${totalLossAmount.toFixed(2)}</span>
              </div>
            )}
          </div>
        )}

        {/* Filter tabs */}
        <div style={{ display: "flex", gap: 8 }}>
          {TABS.map(tab => (
            <button key={tab.key} onClick={() => setActiveTab(tab.key)}
              style={{ display: "flex", alignItems: "center", gap: 6, padding: "6px 14px", borderRadius: 6, border: "1px solid", fontSize: 12, fontWeight: 500, cursor: "pointer", transition: "all 0.15s",
                borderColor: activeTab === tab.key ? "#00c8e0" : "#2a2d35",
                background: activeTab === tab.key ? "rgba(0,200,224,0.12)" : "transparent",
                color: activeTab === tab.key ? "#00c8e0" : "#9ca3af" }}>
              {tab.label}
              {counts[tab.key] > 0 && (
                <span style={{ padding: "1px 6px", borderRadius: 10, fontSize: 10, fontWeight: 700,
                  background: activeTab === tab.key ? "rgba(0,200,224,0.25)" : "#2a2d35",
                  color: activeTab === tab.key ? "#00c8e0" : "#6b7280" }}>
                  {counts[tab.key]}
                </span>
              )}
            </button>
          ))}
        </div>

        {/* Table */}
        <div style={{ background: "#1a1d23", border: "1px solid #2a2d35", borderRadius: 12, overflow: "hidden" }}>
          {dataLoading ? (
            <div style={{ padding: 48, textAlign: "center", color: "#6b7280" }}>
              <div style={{ width: 32, height: 32, border: "3px solid #2a2d35", borderTop: "3px solid #00c8e0", borderRadius: "50%", animation: "spin 0.8s linear infinite", margin: "0 auto 12px" }}/>
              Loading...
            </div>
          ) : filtered.length === 0 ? (
            <div style={{ padding: 48, textAlign: "center" }}>
              <svg width="40" height="40" fill="none" stroke="#2a2d35" strokeWidth="1.5" viewBox="0 0 24 24" style={{ margin: "0 auto 12px", display: "block" }}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
              </svg>
              <p style={{ color: "#6b7280", fontSize: 14, margin: 0 }}>
                {activeTab === "PENDING" ? "No pending trades" : "No trades here"}
              </p>
              {activeTab === "PENDING" && (
                <button onClick={() => navigate("/plan-trade")} style={{ marginTop: 12, padding: "8px 16px", borderRadius: 8, border: "none", background: "#00c8e0", color: "#0d1117", fontSize: 13, fontWeight: 600, cursor: "pointer" }}>
                  Plan Your First Trade
                </button>
              )}
            </div>
          ) : (
            <div style={{ overflowX: "auto" }}>
              <table style={{ width: "100%", borderCollapse: "collapse", minWidth: 900 }}>
                <thead>
                  <tr style={{ borderBottom: "1px solid #2a2d35" }}>
                    {["Symbol", "Type", "Entry", "Stop Loss", "Take Profit", "Pos. Size", "Risk", "Status", "Loss", "Date", "Actions"].map(h => (
                      <th key={h} style={{ padding: "12px 16px", textAlign: "left", fontSize: 11, fontWeight: 600, color: "#6b7280", textTransform: "uppercase", letterSpacing: "0.05em", whiteSpace: "nowrap" }}>{h}</th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {filtered.map(t => (
                    <TradeRow
                      key={t.id}
                      t={t}
                      showLossRow={showLossRows && t.status === "APPROVED"}
                      actionLoading={actionLoading}
                      onApprove={handleApprove}
                      onDisapprove={handleDisapprove}
                      onDelete={handleDelete}
                      onOutcome={handleOutcome}
                    />
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </main>

      <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>
    </div>
  );
}
