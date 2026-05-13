import { useState, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import dashboardService from "../services/dashboardService";

function SharedNav({ active, onLogout }) {
  const navigate = useNavigate();
  const links = [
    { key: "dashboard", label: "Dashboard", path: "/dashboard" },
    { key: "plan-trade", label: "Plan Trade", path: "/plan-trade" },
    { key: "history", label: "History", path: "/history" },
    { key: "settings", label: "Settings", path: "/settings" },
  ];
  return (
    <nav style={{ background: "#1a1d23", borderBottom: "1px solid #2a2d35", position: "sticky", top: 0, zIndex: 50 }}>
      <div style={{ maxWidth: 1200, margin: "0 auto", padding: "0 24px", height: 52, display: "flex", alignItems: "center", gap: 32 }}>
        <div style={{ display: "flex", alignItems: "center", gap: 10, flexShrink: 0 }}>
          <div style={{ width: 30, height: 30, background: "#00c8e0", borderRadius: 6, display: "flex", alignItems: "center", justifyContent: "center" }}>
            <svg width="20" height="20" fill="none" stroke="#0d0d0d" strokeWidth="2.5" viewBox="0 0 24 24">
              <polyline points="3 17 9 11 13 15 21 7"/><polyline points="14 7 21 7 21 14"/>
            </svg>
          </div>
          <span style={{ fontSize: 15, fontWeight: 600, color: "#f0f0f0" }}>Trader's Guardian</span>
        </div>
        <div style={{ display: "flex", alignItems: "center", gap: 4, flex: 1 }}>
          {links.map(l => (
            <button key={l.key}
              style={{ padding: "6px 12px", borderRadius: 6, border: "none", fontSize: 13, fontWeight: 500, cursor: "pointer",
                background: active === l.key ? "rgba(0,200,224,0.12)" : "transparent",
                color: active === l.key ? "#00c8e0" : "#9ca3af" }}
              onClick={() => navigate(l.path)}>{l.label}</button>
          ))}
        </div>
        <button onClick={onLogout} style={{ padding: "6px 14px", borderRadius: 6, border: "1px solid #2a2d35", background: "transparent", color: "#9ca3af", fontSize: 13, cursor: "pointer" }}>Logout</button>
      </div>
    </nav>
  );
}

const STATUS_COLORS = { APPROVED: "#22c55e", DISAPPROVED: "#ef4444", PENDING: "#f59e0b" };
const STATUS_BG = { APPROVED: "rgba(34,197,94,0.12)", DISAPPROVED: "rgba(239,68,68,0.12)", PENDING: "rgba(245,158,11,0.12)" };

export default function History() {
  const navigate = useNavigate();
  const { user, loading, logout } = useAuth();
  const [trades, setTrades] = useState([]);
  const [dataLoading, setDataLoading] = useState(true);
  const [filter, setFilter] = useState("ALL");
  const [actionLoading, setActionLoading] = useState(null);
  const [error, setError] = useState("");

  const fetchTrades = useCallback(async () => {
    try {
      setDataLoading(true);
      const res = await dashboardService.getTrades();
      setTrades(res.data.data || []);
    } catch { setError("Failed to load trade history."); }
    finally { setDataLoading(false); }
  }, []);

  useEffect(() => { if (user) fetchTrades(); }, [user, fetchTrades]);

  const handleApprove = async (id) => {
    setActionLoading(id);
    try { await dashboardService.approveTrade(id); await fetchTrades(); } catch {}
    finally { setActionLoading(null); }
  };

  const handleDisapprove = async (id) => {
    const reason = window.prompt("Reason for disapproval (optional):");
    setActionLoading(id);
    try { await dashboardService.disapproveTrade(id, reason || ""); await fetchTrades(); } catch {}
    finally { setActionLoading(null); }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Delete this trade plan?")) return;
    setActionLoading(id);
    try { await dashboardService.deleteTrade(id); await fetchTrades(); } catch {}
    finally { setActionLoading(null); }
  };

  if (loading || !user) return null;

  const filtered = filter === "ALL" ? trades : trades.filter(t => t.status === filter);

  return (
    <div style={{ minHeight: "100vh", background: "#111316", fontFamily: "'Inter','Segoe UI',sans-serif", color: "#e2e8f0" }}>
      <SharedNav active="history" onLogout={() => { logout(); navigate("/login"); }} />
      <main style={{ maxWidth: 1200, margin: "0 auto", padding: "28px 24px", display: "flex", flexDirection: "column", gap: 20 }}>
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start" }}>
          <div>
            <h1 style={{ fontSize: 26, fontWeight: 700, color: "#f0f0f0", margin: 0 }}>Trade History</h1>
            <p style={{ fontSize: 13, color: "#6b7280", margin: "4px 0 0" }}>Review and manage your past trading decisions</p>
          </div>
          <button onClick={() => navigate("/plan-trade")} style={{ padding: "9px 18px", borderRadius: 8, border: "none", background: "#00c8e0", color: "#0d1117", fontSize: 13, fontWeight: 600, cursor: "pointer" }}>+ Plan New Trade</button>
        </div>

        {error && <div style={{ padding: "12px 16px", borderRadius: 8, background: "rgba(239,68,68,0.12)", border: "1px solid rgba(239,68,68,0.3)", color: "#ef4444", fontSize: 13 }}>{error}</div>}

        {/* Filter tabs */}
        <div style={{ display: "flex", gap: 8 }}>
          {["ALL", "PENDING", "APPROVED", "DISAPPROVED"].map(f => (
            <button key={f} onClick={() => setFilter(f)}
              style={{ padding: "6px 14px", borderRadius: 6, border: "1px solid", fontSize: 12, fontWeight: 500, cursor: "pointer",
                borderColor: filter === f ? "#00c8e0" : "#2a2d35",
                background: filter === f ? "rgba(0,200,224,0.12)" : "transparent",
                color: filter === f ? "#00c8e0" : "#9ca3af" }}>
              {f}
            </button>
          ))}
        </div>

        {/* Table */}
        <div style={{ background: "#1a1d23", border: "1px solid #2a2d35", borderRadius: 12, overflow: "hidden" }}>
          {dataLoading ? (
            <div style={{ padding: 48, textAlign: "center", color: "#6b7280" }}>Loading...</div>
          ) : filtered.length === 0 ? (
            <div style={{ padding: 48, textAlign: "center" }}>
              <svg width="40" height="40" fill="none" stroke="#2a2d35" strokeWidth="2" viewBox="0 0 24 24" style={{ margin: "0 auto 12px", display: "block" }}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
              </svg>
              <p style={{ color: "#6b7280", fontSize: 14 }}>No trade plans found</p>
              <button onClick={() => navigate("/plan-trade")} style={{ marginTop: 12, padding: "8px 16px", borderRadius: 8, border: "none", background: "#00c8e0", color: "#0d1117", fontSize: 13, fontWeight: 600, cursor: "pointer" }}>Plan Your First Trade</button>
            </div>
          ) : (
            <table style={{ width: "100%", borderCollapse: "collapse" }}>
              <thead>
                <tr style={{ borderBottom: "1px solid #2a2d35" }}>
                  {["Symbol", "Type", "Entry", "Stop Loss", "Take Profit", "Position Size", "Risk", "Status", "Date", "Actions"].map(h => (
                    <th key={h} style={{ padding: "12px 16px", textAlign: "left", fontSize: 11, fontWeight: 600, color: "#6b7280", textTransform: "uppercase", letterSpacing: "0.05em", whiteSpace: "nowrap" }}>{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {filtered.map(t => (
                  <tr key={t.id} style={{ borderBottom: "1px solid #2a2d35" }}>
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
                    <td style={{ padding: "12px 16px", fontSize: 12, color: "#6b7280", whiteSpace: "nowrap" }}>
                      {new Date(t.createdAt).toLocaleDateString()}
                    </td>
                    <td style={{ padding: "12px 16px" }}>
                      <div style={{ display: "flex", gap: 6 }}>
                        {t.status === "PENDING" && (
                          <>
                            <button onClick={() => handleApprove(t.id)} disabled={actionLoading === t.id}
                              style={{ padding: "4px 10px", borderRadius: 6, border: "none", background: "rgba(34,197,94,0.15)", color: "#22c55e", fontSize: 11, fontWeight: 600, cursor: "pointer" }}>Approve</button>
                            <button onClick={() => handleDisapprove(t.id)} disabled={actionLoading === t.id}
                              style={{ padding: "4px 10px", borderRadius: 6, border: "none", background: "rgba(239,68,68,0.15)", color: "#ef4444", fontSize: 11, fontWeight: 600, cursor: "pointer" }}>Reject</button>
                          </>
                        )}
                        <button onClick={() => handleDelete(t.id)} disabled={actionLoading === t.id}
                          style={{ padding: "4px 10px", borderRadius: 6, border: "1px solid #2a2d35", background: "transparent", color: "#6b7280", fontSize: 11, cursor: "pointer" }}>Del</button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </main>
    </div>
  );
}
