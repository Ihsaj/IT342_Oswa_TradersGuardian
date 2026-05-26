import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { useSettings } from "../context/SettingsContext";
import dashboardService from "../services/dashboardService";

function SharedNav({ active, onLogout }) {
  const navigate = useNavigate();
  const links = [
    { key: "dashboard",  label: "Dashboard",  path: "/dashboard",  icon: <><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></> },
    { key: "plan-trade", label: "Plan Trade", path: "/plan-trade", icon: <path strokeLinecap="round" strokeLinejoin="round" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6"/> },
    { key: "history",    label: "History",    path: "/history",    icon: <><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></> },
    { key: "settings",   label: "Settings",   path: "/settings",   icon: <><circle cx="12" cy="12" r="3"/><path strokeLinecap="round" strokeLinejoin="round" d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 4.68 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 2.83-2.83l.06.06A1.65 1.65 0 0 0 9 4.68a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z"/></> },
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
              style={{ display: "flex", alignItems: "center", gap: 6, padding: "6px 12px", borderRadius: 6, border: "none", fontSize: 13, fontWeight: 500, cursor: "pointer", transition: "all 0.15s",
                background: active === l.key ? "rgba(0,200,224,0.12)" : "transparent",
                color: active === l.key ? "#00c8e0" : "#9ca3af" }}
              onClick={() => navigate(l.path)}>
              <svg width="14" height="14" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">{l.icon}</svg>
              {l.label}
            </button>
          ))}
        </div>
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

export default function PlanTrade() {
  const navigate = useNavigate();
  const { logout, isAuthenticated } = useAuth();
  const { settings } = useSettings();
  const [form, setForm] = useState({ symbol: "", tradeType: "BUY", entryPrice: "", stopLoss: "", takeProfit: "", notes: "" });
  const [calculated, setCalculated] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [success, setSuccess] = useState("");
  const [error, setError] = useState("");

  const handleChange = (e) => { const { name, value } = e.target; setForm(f => ({ ...f, [name]: value })); };

  const calculate = () => {
    const entry = parseFloat(form.entryPrice), sl = parseFloat(form.stopLoss), tp = parseFloat(form.takeProfit);
    if (!entry || !sl || !tp) { setError("Please fill in Entry Price, Stop Loss, and Take Profit."); return; }
    setError("");
    const balance = settings?.accountBalance ?? 10000;
    const riskPct = settings?.riskPerTrade ?? 2;
    const riskAmount = (balance * riskPct) / 100;
    const priceDiff = Math.abs(entry - sl);
    const positionSize = priceDiff > 0 ? riskAmount / priceDiff : 0;
    const rewardAmount = Math.abs(tp - entry) * positionSize;
    const rr = priceDiff > 0 ? Math.abs(tp - entry) / priceDiff : 0;
    setCalculated({ riskAmount, positionSize, riskPercent: riskPct, rewardAmount, rr });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!calculated) { setError("Please calculate risk first."); return; }
    setSubmitting(true); setError("");
    try {
      await dashboardService.createTrade({
        symbol: form.symbol, tradeType: form.tradeType,
        entryPrice: parseFloat(form.entryPrice), stopLoss: parseFloat(form.stopLoss), takeProfit: parseFloat(form.takeProfit),
        positionSize: calculated.positionSize, riskAmount: calculated.riskAmount,
        riskPercent: calculated.riskPercent, notes: form.notes,
      });
      setSuccess("Trade plan created!"); setForm({ symbol: "", tradeType: "BUY", entryPrice: "", stopLoss: "", takeProfit: "", notes: "" }); setCalculated(null);
      setTimeout(() => setSuccess(""), 3000);
    } catch (err) { setError(err.response?.data?.message || "Failed to create trade plan."); }
    finally { setSubmitting(false); }
  };

  if (!isAuthenticated) return null;
  const balance = settings?.accountBalance ?? 10000;
  const riskPct = settings?.riskPerTrade ?? 2;

  return (
    <div style={{ minHeight: "100vh", background: "#111316", fontFamily: "'Inter','Segoe UI',sans-serif", color: "#e2e8f0" }}>
      <SharedNav active="plan-trade" onLogout={() => { logout(); navigate("/login"); }} />
      <main style={{ maxWidth: 1200, margin: "0 auto", padding: "28px 24px", display: "flex", flexDirection: "column", gap: 20 }}>
        <div><h1 style={{ fontSize: 26, fontWeight: 700, color: "#f0f0f0", margin: 0 }}>Plan New Trade</h1>
          <p style={{ fontSize: 13, color: "#6b7280", margin: "4px 0 0" }}>Validate your trade setup and calculate risk before entering the market</p></div>

        {success && <div style={{ padding: "12px 16px", borderRadius: 8, background: "rgba(34,197,94,0.12)", border: "1px solid rgba(34,197,94,0.3)", color: "#22c55e", fontSize: 13 }}>{success}</div>}
        {error && <div style={{ padding: "12px 16px", borderRadius: 8, background: "rgba(239,68,68,0.12)", border: "1px solid rgba(239,68,68,0.3)", color: "#ef4444", fontSize: 13 }}>{error}</div>}

        <div style={{ display: "grid", gridTemplateColumns: "1fr 340px", gap: 20 }}>
          <form onSubmit={handleSubmit} style={{ background: "#1a1d23", border: "1px solid #2a2d35", borderRadius: 12, padding: "20px 24px" }}>
            <h2 style={{ fontSize: 14, fontWeight: 600, color: "#e2e8f0", margin: "0 0 16px" }}>Trade Setup</h2>
            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 14 }}>
              {[
                { label: "Symbol *", name: "symbol", type: "text", placeholder: "e.g. EUR/USD", span: false },
                { label: "Direction", name: "tradeType", type: "select", span: false },
                { label: "Entry Price *", name: "entryPrice", type: "number", placeholder: "0.00", span: false },
                { label: "Stop Loss *", name: "stopLoss", type: "number", placeholder: "0.00", span: false },
                { label: "Take Profit *", name: "takeProfit", type: "number", placeholder: "0.00", span: true },
              ].map(f => (
                <div key={f.name} style={{ display: "flex", flexDirection: "column", gap: 6, ...(f.span ? { gridColumn: "span 2" } : {}) }}>
                  <label style={{ fontSize: 12, color: "#9ca3af", fontWeight: 500 }}>{f.label}</label>
                  {f.type === "select"
                    ? <select name={f.name} value={form[f.name]} onChange={handleChange} style={{ background: "#212428", border: "1px solid #2a2d35", borderRadius: 8, padding: "9px 12px", color: "#e2e8f0", fontSize: 13, outline: "none" }}>
                        <option value="BUY">BUY (Long)</option><option value="SELL">SELL (Short)</option>
                      </select>
                    : <input name={f.name} type={f.type} step="any" value={form[f.name]} onChange={handleChange} placeholder={f.placeholder} required={f.label.includes("*")}
                        style={{ background: "#212428", border: "1px solid #2a2d35", borderRadius: 8, padding: "9px 12px", color: "#e2e8f0", fontSize: 13, outline: "none" }} />}
                </div>
              ))}
              <div style={{ display: "flex", flexDirection: "column", gap: 6, gridColumn: "span 2" }}>
                <label style={{ fontSize: 12, color: "#9ca3af", fontWeight: 500 }}>Notes</label>
                <textarea name="notes" value={form.notes} onChange={handleChange} placeholder="Trade rationale, confluences..."
                  style={{ background: "#212428", border: "1px solid #2a2d35", borderRadius: 8, padding: "9px 12px", color: "#e2e8f0", fontSize: 13, outline: "none", minHeight: 80, resize: "vertical" }} />
              </div>
            </div>
            <div style={{ display: "flex", gap: 10, marginTop: 16 }}>
              <button type="button" onClick={calculate} style={{ flex: 1, padding: "9px", borderRadius: 8, border: "1px solid #00c8e0", background: "transparent", color: "#00c8e0", fontSize: 13, fontWeight: 600, cursor: "pointer" }}>Calculate Risk</button>
              <button type="submit" disabled={submitting || !calculated} style={{ flex: 1, padding: "9px", borderRadius: 8, border: "none", background: "#00c8e0", color: "#0d1117", fontSize: 13, fontWeight: 600, cursor: "pointer", opacity: !calculated ? 0.5 : 1 }}>
                {submitting ? "Saving..." : "Save Trade Plan"}
              </button>
            </div>
          </form>

          <div style={{ display: "flex", flexDirection: "column", gap: 16 }}>
            <div style={{ background: "#1a1d23", border: "1px solid #2a2d35", borderRadius: 12, padding: "20px 24px" }}>
              <h2 style={{ fontSize: 14, fontWeight: 600, color: "#e2e8f0", margin: "0 0 16px" }}>Account Parameters</h2>
              {[["Account Balance", `$${balance.toLocaleString()}`, "#f0f0f0"], ["Risk Per Trade", `${riskPct}%`, "#00c8e0"], ["Daily Loss Limit", `${settings?.dailyLossLimit ?? 5}%`, "#f59e0b"]].map(([l, v, c]) => (
                <div key={l} style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 10 }}>
                  <span style={{ fontSize: 12, color: "#9ca3af" }}>{l}</span>
                  <span style={{ fontSize: 14, fontWeight: 600, color: c }}>{v}</span>
                </div>
              ))}
            </div>

            {calculated ? (
              <div style={{ background: "#1a1d23", border: "1px solid rgba(0,200,224,0.3)", borderRadius: 12, padding: "20px 24px" }}>
                <h2 style={{ fontSize: 14, fontWeight: 600, color: "#e2e8f0", margin: "0 0 16px" }}>Risk Analysis</h2>
                {[
                  ["Risk Amount", `$${calculated.riskAmount.toFixed(2)}`, "#ef4444"],
                  ["Position Size", calculated.positionSize.toFixed(4), "#f0f0f0"],
                  ["Potential Reward", `$${calculated.rewardAmount.toFixed(2)}`, "#22c55e"],
                  ["Risk/Reward", `1:${calculated.rr.toFixed(2)}`, calculated.rr >= 2 ? "#22c55e" : calculated.rr >= 1 ? "#f59e0b" : "#ef4444"],
                ].map(([l, v, c]) => (
                  <div key={l} style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 10 }}>
                    <span style={{ fontSize: 12, color: "#9ca3af" }}>{l}</span>
                    <span style={{ fontSize: 14, fontWeight: 600, color: c }}>{v}</span>
                  </div>
                ))}
                <div style={{ marginTop: 10, padding: "10px 14px", borderRadius: 8, background: calculated.rr >= 2 ? "rgba(34,197,94,0.1)" : "rgba(245,158,11,0.1)", border: `1px solid ${calculated.rr >= 2 ? "rgba(34,197,94,0.3)" : "rgba(245,158,11,0.3)"}` }}>
                  <span style={{ fontSize: 12, color: calculated.rr >= 2 ? "#22c55e" : "#f59e0b" }}>
                    {calculated.rr >= 2 ? "✓ Good risk/reward ratio (≥ 1:2)" : "⚠ Consider improving your R:R ratio"}
                  </span>
                </div>
              </div>
            ) : (
              <div style={{ background: "#1a1d23", border: "1px solid #2a2d35", borderRadius: 12, padding: "20px 24px", display: "flex", alignItems: "center", justifyContent: "center", minHeight: 160 }}>
                <p style={{ color: "#6b7280", fontSize: 13, textAlign: "center" }}>Fill in trade details then click<br/><strong style={{ color: "#00c8e0" }}>Calculate Risk</strong></p>
              </div>
            )}
          </div>
        </div>
      </main>
    </div>
  );
}
