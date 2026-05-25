import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { useSettings } from "../context/SettingsContext";

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

export default function Settings() {
  const navigate = useNavigate();
  const { logout, isAuthenticated } = useAuth();
  const { settings, settingsLoading, saveSettings, fetchSettings } = useSettings();

  const [form, setForm] = useState({ accountBalance: "", riskPerTrade: "", dailyLossLimit: "" });
  const [saving, setSaving] = useState(false);
  const [success, setSuccess] = useState("");
  const [error, setError] = useState("");

  // Populate form when shared settings load
  useEffect(() => {
    if (settings) {
      setForm({
        accountBalance: settings.accountBalance ?? 10000,
        riskPerTrade: settings.riskPerTrade ?? 2,
        dailyLossLimit: settings.dailyLossLimit ?? 5,
      });
    }
  }, [settings]);

  // Refetch on mount to ensure fresh data
  useEffect(() => {
    fetchSettings();
  }, [fetchSettings]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm(f => ({ ...f, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true); setError(""); setSuccess("");

    const balance = parseFloat(form.accountBalance);
    const rpt = parseFloat(form.riskPerTrade);
    const dll = parseFloat(form.dailyLossLimit);

    if (balance <= 0) { setError("Account balance must be positive."); setSaving(false); return; }
    if (rpt <= 0 || rpt > 100) { setError("Risk per trade must be between 0 and 100%."); setSaving(false); return; }
    if (dll <= 0 || dll > 100) { setError("Daily loss limit must be between 0 and 100%."); setSaving(false); return; }

    try {
      // saveSettings updates shared context + saves to backend in one call
      await saveSettings({ accountBalance: balance, riskPerTrade: rpt, dailyLossLimit: dll });
      setSuccess("Settings saved successfully! Changes are reflected on Dashboard.");
      setTimeout(() => setSuccess(""), 3000);
    } catch (err) {
      setError(err.response?.data?.message || "Failed to save settings.");
    } finally {
      setSaving(false);
    }
  };

  const handleReset = () => {
    if (settings) {
      setForm({
        accountBalance: settings.accountBalance,
        riskPerTrade: settings.riskPerTrade,
        dailyLossLimit: settings.dailyLossLimit,
      });
    }
  };

  if (!isAuthenticated) return null;

  // Derived preview values
  const balance = parseFloat(form.accountBalance) || 0;
  const rpt = parseFloat(form.riskPerTrade) || 0;
  const dll = parseFloat(form.dailyLossLimit) || 0;
  const riskAmount = (balance * rpt) / 100;
  const dailyLossAmount = (balance * dll) / 100;

  const inputStyle = {
    background: "#212428", border: "1px solid #2a2d35", borderRadius: 8,
    padding: "10px 14px", color: "#e2e8f0", fontSize: 14, outline: "none",
    width: "100%", boxSizing: "border-box", transition: "border-color 0.2s",
  };
  const labelStyle = { fontSize: 12, color: "#9ca3af", fontWeight: 500, marginBottom: 6, display: "block" };
  const cardStyle = { background: "#1a1d23", border: "1px solid #2a2d35", borderRadius: 12, padding: "24px" };

  return (
    <div style={{ minHeight: "100vh", background: "#111316", fontFamily: "'Inter','Segoe UI',sans-serif", color: "#e2e8f0" }}>
      <SharedNav active="settings" onLogout={() => { logout(); navigate("/login"); }} />

      <main style={{ maxWidth: 900, margin: "0 auto", padding: "28px 24px", display: "flex", flexDirection: "column", gap: 20 }}>
        {/* Header */}
        <div>
          <h1 style={{ fontSize: 26, fontWeight: 700, color: "#f0f0f0", margin: 0 }}>Account Settings</h1>
          <p style={{ fontSize: 13, color: "#6b7280", margin: "4px 0 0" }}>Configure your risk parameters and account details</p>
        </div>

        {success && (
          <div style={{ padding: "12px 16px", borderRadius: 8, background: "rgba(34,197,94,0.12)", border: "1px solid rgba(34,197,94,0.3)", color: "#22c55e", fontSize: 13 }}>
            ✓ {success}
          </div>
        )}
        {error && (
          <div style={{ padding: "12px 16px", borderRadius: 8, background: "rgba(239,68,68,0.12)", border: "1px solid rgba(239,68,68,0.3)", color: "#ef4444", fontSize: 13 }}>
            {error}
          </div>
        )}

        <div style={{ display: "grid", gridTemplateColumns: "1fr 320px", gap: 20 }}>
          {/* Settings Form */}
          <form onSubmit={handleSubmit} style={cardStyle}>
            <h2 style={{ fontSize: 14, fontWeight: 600, color: "#e2e8f0", margin: "0 0 20px" }}>Risk Parameters</h2>

            {settingsLoading ? (
              <p style={{ color: "#6b7280", fontSize: 13 }}>Loading settings...</p>
            ) : (
              <div style={{ display: "flex", flexDirection: "column", gap: 18 }}>
                {/* Account Balance */}
                <div>
                  <label style={labelStyle}>Account Balance ($)</label>
                  <input name="accountBalance" type="number" step="0.01" min="1" value={form.accountBalance}
                    onChange={handleChange} style={inputStyle} required />
                  <p style={{ fontSize: 11, color: "#6b7280", margin: "6px 0 0" }}>
                    Your total trading account balance used for risk calculations.
                  </p>
                </div>

                {/* Risk Per Trade */}
                <div>
                  <label style={labelStyle}>Risk Per Trade (%)</label>
                  <input name="riskPerTrade" type="number" step="0.1" min="0.1" max="100" value={form.riskPerTrade}
                    onChange={handleChange} style={inputStyle} required />
                  <p style={{ fontSize: 11, color: "#6b7280", margin: "6px 0 0" }}>
                    Maximum percentage of account to risk on a single trade. Recommended: 1–2%.
                  </p>
                </div>

                {/* Daily Loss Limit */}
                <div>
                  <label style={labelStyle}>Daily Loss Limit (%)</label>
                  <input name="dailyLossLimit" type="number" step="0.1" min="0.1" max="100" value={form.dailyLossLimit}
                    onChange={handleChange} style={inputStyle} required />
                  <p style={{ fontSize: 11, color: "#6b7280", margin: "6px 0 0" }}>
                    Stop trading for the day when this loss threshold is reached. Recommended: 3–6%.
                  </p>
                </div>

                <div style={{ display: "flex", gap: 10, marginTop: 4 }}>
                  <button type="button" onClick={handleReset}
                    style={{ flex: 1, padding: "10px", borderRadius: 8, border: "1px solid #2a2d35", background: "transparent", color: "#9ca3af", fontSize: 13, fontWeight: 500, cursor: "pointer" }}>
                    Reset Changes
                  </button>
                  <button type="submit" disabled={saving}
                    style={{ flex: 2, padding: "10px", borderRadius: 8, border: "none", background: "#00c8e0", color: "#0d1117", fontSize: 13, fontWeight: 600, cursor: saving ? "not-allowed" : "pointer", opacity: saving ? 0.7 : 1 }}>
                    {saving ? "Saving..." : "Save Settings"}
                  </button>
                </div>
              </div>
            )}
          </form>

          {/* Live Preview */}
          <div style={{ display: "flex", flexDirection: "column", gap: 16 }}>
            <div style={cardStyle}>
              <h2 style={{ fontSize: 14, fontWeight: 600, color: "#e2e8f0", margin: "0 0 16px" }}>Live Preview</h2>
              <div style={{ display: "flex", flexDirection: "column", gap: 12 }}>
                {[
                  { label: "Account Balance", value: `$${balance.toLocaleString(undefined, { minimumFractionDigits: 2 })}`, color: "#f0f0f0" },
                  { label: "Risk Per Trade", value: `${rpt}%`, color: "#00c8e0" },
                  { label: "Max Risk Amount", value: `$${riskAmount.toFixed(2)}`, color: "#ef4444" },
                  { label: "Daily Loss Limit", value: `${dll}%`, color: "#f59e0b" },
                  { label: "Max Daily Loss", value: `$${dailyLossAmount.toFixed(2)}`, color: "#f59e0b" },
                ].map(({ label, value, color }) => (
                  <div key={label} style={{ display: "flex", justifyContent: "space-between", alignItems: "center", padding: "10px 0", borderBottom: "1px solid #2a2d35" }}>
                    <span style={{ fontSize: 12, color: "#9ca3af" }}>{label}</span>
                    <span style={{ fontSize: 14, fontWeight: 600, color }}>{value}</span>
                  </div>
                ))}
              </div>
            </div>

            {/* Risk guide */}
            <div style={{ ...cardStyle, border: "1px solid rgba(0,200,224,0.2)" }}>
              <h2 style={{ fontSize: 13, fontWeight: 600, color: "#00c8e0", margin: "0 0 12px" }}>💡 Risk Guidelines</h2>
              <ul style={{ margin: 0, padding: "0 0 0 16px", display: "flex", flexDirection: "column", gap: 8 }}>
                {[
                  "Risk 1–2% per trade (professional standard)",
                  "Never risk more than 5% in one day",
                  "A 2% risk with 1:2 R:R = 4% potential reward",
                  "Consistent small risks protect your capital",
                ].map(tip => (
                  <li key={tip} style={{ fontSize: 12, color: "#9ca3af", lineHeight: 1.5 }}>{tip}</li>
                ))}
              </ul>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}
