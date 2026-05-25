import { useState, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { useSettings } from "../context/SettingsContext";
import dashboardService from "../services/dashboardService";

export default function Dashboard() {
  const navigate = useNavigate();
  const { logout, isAuthenticated } = useAuth();
  const { settings } = useSettings();
  const [stats, setStats] = useState({
    totalTrades: 0, approvedTrades: 0, disapprovedTrades: 0,
    lossTrades: 0, currentDailyLoss: 0, dailyLossLimit: 0, accountBalance: 0
  });
  const [dataLoading, setDataLoading] = useState(true);

  const fetchStats = useCallback(async () => {
    try {
      setDataLoading(true);
      const res = await dashboardService.getStats();
      setStats(res.data.data);
    } catch (err) {
      console.error("Failed to fetch stats", err);
    } finally {
      setDataLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchStats();
  }, [fetchStats]);

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  if (!isAuthenticated) return null;

  const balance = settings?.accountBalance ?? 10000;
  const riskPct = settings?.riskPerTrade ?? 2;
  const dailyLimitPct = settings?.dailyLossLimit ?? 5;
  // currentDailyLoss comes from stats (updated live when losses recorded in History)
  const currentDailyLossPct = stats.currentDailyLoss ?? settings?.currentDailyLoss ?? 0;
  const dailyLossAmount = (balance * currentDailyLossPct) / 100;
  const dailyLossLimit = (balance * dailyLimitPct) / 100;
  const progressPct = dailyLossLimit > 0 ? Math.min((dailyLossAmount / dailyLossLimit) * 100, 100) : 0;

  return (
    <div style={styles.page}>
      {/* ── NAV ── */}
      <nav style={styles.nav}>
        <div style={styles.navInner}>
          {/* Logo + brand */}
          <div style={styles.navLeft}>
            <div style={styles.logoBox}>
              <svg width="20" height="20" fill="none" stroke="#0d0d0d" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round" viewBox="0 0 24 24">
                <polyline points="3 17 9 11 13 15 21 7" />
                <polyline points="14 7 21 7 21 14" />
              </svg>
            </div>
            <span style={styles.brand}>Trader's Guardian</span>
          </div>

          {/* Nav links */}
          <div style={styles.navLinks}>
            <button style={{ ...styles.navLink, ...styles.navLinkActive }} onClick={() => navigate("/dashboard")}>
              <svg width="14" height="14" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></svg>
              Dashboard
            </button>
            <button style={styles.navLink} onClick={() => navigate("/plan-trade")}>
              <svg width="14" height="14" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6"/></svg>
              Plan Trade
            </button>
            <button style={styles.navLink} onClick={() => navigate("/history")}>
              <svg width="14" height="14" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
              History
            </button>
            <button style={styles.navLink} onClick={() => navigate("/settings")}>
              <svg width="14" height="14" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24"><circle cx="12" cy="12" r="3"/><path strokeLinecap="round" strokeLinejoin="round" d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 4.68 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 2.83-2.83l.06.06A1.65 1.65 0 0 0 9 4.68a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z"/></svg>
              Settings
            </button>
          </div>

          {/* Logout */}
          <button style={styles.logoutBtn} onClick={handleLogout}>
            <svg width="14" height="14" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"/>
            </svg>
            Logout
          </button>
        </div>
      </nav>

      {/* ── MAIN ── */}
      <main style={styles.main}>
        {/* Header row */}
        <div style={styles.headerRow}>
          <div>
            <h1 style={styles.pageTitle}>Dashboard</h1>
            <p style={styles.pageSubtitle}>Monitor your trading account and performance</p>
          </div>
          <button style={styles.planBtn} onClick={() => navigate("/plan-trade")}>
            <svg width="14" height="14" fill="none" stroke="currentColor" strokeWidth="2.5" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6"/>
            </svg>
            Plan New Trade
          </button>
        </div>

        {/* Account Summary */}
        <div style={styles.card}>
          <div style={styles.cardHeader}>
            <svg width="16" height="16" fill="none" stroke="#00c8e0" strokeWidth="2" viewBox="0 0 24 24">
              <line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/>
            </svg>
            <span style={styles.cardTitle}>Account Summary</span>
          </div>

          <div style={styles.statsRow}>
            <div style={styles.statItem}>
              <span style={styles.statLabel}>Account Balance</span>
              <span style={styles.statValue}>${balance.toLocaleString()}</span>
            </div>
            <div style={styles.statItem}>
              <span style={styles.statLabel}>Risk Per Trade</span>
              <span style={{ ...styles.statValue, color: "#00c8e0" }}>{riskPct}%</span>
            </div>
            <div style={styles.statItem}>
              <span style={styles.statLabel}>Daily Loss Limit</span>
              <span style={{ ...styles.statValue, color: "#f59e0b" }}>{dailyLimitPct}%</span>
            </div>
            <div style={styles.statItem}>
              <span style={styles.statLabel}>Current Daily Loss</span>
              <span style={styles.statValue}>{currentDailyLossPct.toFixed(2)}%</span>
            </div>
          </div>

          {/* Progress bar */}
          <div style={styles.progressSection}>
            <div style={styles.progressLabel}>
              <span style={styles.statLabel}>Daily Loss Progress</span>
              <span style={styles.progressAmount}>${dailyLossAmount.toFixed(2)} / ${dailyLossLimit.toFixed(2)}</span>
            </div>
            <div style={styles.progressTrack}>
              <div style={{
                ...styles.progressFill,
                width: `${progressPct}%`,
                background: progressPct > 75 ? "#ef4444" : progressPct > 40 ? "#f59e0b" : "#00c8e0",
              }} />
            </div>
          </div>
        </div>

        {/* Quick Statistics */}
        <div style={styles.card}>
          <div style={styles.cardHeader}>
            <svg width="16" height="16" fill="none" stroke="#00c8e0" strokeWidth="2" viewBox="0 0 24 24">
              <line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/>
            </svg>
            <span style={styles.cardTitle}>Quick Statistics</span>
          </div>

          <div style={{ ...styles.statsGrid, gridTemplateColumns: "repeat(4, 1fr)" }}>
            {/* Total Trades */}
            <div style={styles.statCard}>
              <div style={styles.statCardTop}>
                <svg width="18" height="18" fill="none" stroke="#00c8e0" strokeWidth="2" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6"/>
                </svg>
                <span style={styles.statCardLabel}>Total Trades</span>
              </div>
              <span style={styles.statCardValue}>{dataLoading ? "—" : stats.totalTrades}</span>
            </div>

            {/* Approved Trades */}
            <div style={styles.statCard}>
              <div style={styles.statCardTop}>
                <svg width="18" height="18" fill="none" stroke="#22c55e" strokeWidth="2" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
                </svg>
                <span style={styles.statCardLabel}>Approved</span>
              </div>
              <span style={{ ...styles.statCardValue, color: "#22c55e" }}>{dataLoading ? "—" : stats.approvedTrades}</span>
            </div>

            {/* Disapproved Trades */}
            <div style={styles.statCard}>
              <div style={styles.statCardTop}>
                <svg width="18" height="18" fill="none" stroke="#6b7280" strokeWidth="2" viewBox="0 0 24 24">
                  <circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/>
                </svg>
                <span style={styles.statCardLabel}>Disapproved</span>
              </div>
              <span style={{ ...styles.statCardValue, color: "#6b7280" }}>{dataLoading ? "—" : stats.disapprovedTrades}</span>
            </div>

            {/* Losing Trades */}
            <div style={{ ...styles.statCard, border: stats.lossTrades > 0 ? "1px solid rgba(239,68,68,0.35)" : "1px solid #2a2d35" }}>
              <div style={styles.statCardTop}>
                <svg width="18" height="18" fill="none" stroke="#ef4444" strokeWidth="2" viewBox="0 0 24 24">
                  <polyline points="23 18 13.5 8.5 8.5 13.5 1 6"/><polyline points="17 18 23 18 23 12"/>
                </svg>
                <span style={styles.statCardLabel}>Losing Trades</span>
              </div>
              <span style={{ ...styles.statCardValue, color: "#ef4444" }}>{dataLoading ? "—" : stats.lossTrades}</span>
            </div>
          </div>
        </div>

        {/* Quick Actions */}
        <div style={styles.quickActionsGrid}>
          <div style={styles.actionCard} onClick={() => navigate("/plan-trade")} role="button">
            <svg width="22" height="22" fill="none" stroke="#00c8e0" strokeWidth="2" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6"/>
            </svg>
            <div style={styles.actionCardText}>
              <span style={styles.actionCardTitle}>Plan New Trade</span>
              <span style={styles.actionCardSub}>Validate your next trade before execution</span>
            </div>
          </div>

          <div style={styles.actionCard} onClick={() => navigate("/history")} role="button">
            <svg width="22" height="22" fill="none" stroke="#00c8e0" strokeWidth="2" viewBox="0 0 24 24">
              <line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/>
            </svg>
            <div style={styles.actionCardText}>
              <span style={styles.actionCardTitle}>View History</span>
              <span style={styles.actionCardSub}>Review your past trading decisions</span>
            </div>
          </div>

          <div style={styles.actionCard} onClick={() => navigate("/settings")} role="button">
            <svg width="22" height="22" fill="none" stroke="#00c8e0" strokeWidth="2" viewBox="0 0 24 24">
              <path d="M19 9l-7 7-7-7"/>
            </svg>
            <div style={styles.actionCardText}>
              <span style={styles.actionCardTitle}>Account Settings</span>
              <span style={styles.actionCardSub}>Configure your risk parameters</span>
            </div>
          </div>
        </div>
      </main>

      {/* Help button */}
      <button style={styles.helpBtn} title="Help">?</button>
    </div>
  );
}

const styles = {
  page: { minHeight: "100vh", background: "#111316", fontFamily: "'Inter', 'Segoe UI', sans-serif", color: "#e2e8f0" },
  loadingWrap: { minHeight: "100vh", background: "#111316", display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "center", gap: 12 },
  spinner: { width: 36, height: 36, border: "3px solid #2a2d35", borderTop: "3px solid #00c8e0", borderRadius: "50%", animation: "spin 0.8s linear infinite" },
  loadingText: { color: "#6b7280", fontSize: 14 },

  // Nav
  nav: { background: "#1a1d23", borderBottom: "1px solid #2a2d35", position: "sticky", top: 0, zIndex: 50 },
  navInner: { maxWidth: 1200, margin: "0 auto", padding: "0 24px", height: 52, display: "flex", alignItems: "center", gap: 32 },
  navLeft: { display: "flex", alignItems: "center", gap: 10, flexShrink: 0 },
  logoBox: { width: 30, height: 30, background: "#00c8e0", borderRadius: 6, display: "flex", alignItems: "center", justifyContent: "center" },
  brand: { fontSize: 15, fontWeight: 600, color: "#f0f0f0", whiteSpace: "nowrap" },
  navLinks: { display: "flex", alignItems: "center", gap: 4, flex: 1 },
  navLink: { display: "flex", alignItems: "center", gap: 6, padding: "6px 12px", borderRadius: 6, border: "none", background: "transparent", color: "#9ca3af", fontSize: 13, fontWeight: 500, cursor: "pointer", transition: "all 0.15s" },
  navLinkActive: { background: "rgba(0,200,224,0.12)", color: "#00c8e0" },
  logoutBtn: { display: "flex", alignItems: "center", gap: 6, padding: "6px 14px", borderRadius: 6, border: "1px solid #2a2d35", background: "transparent", color: "#9ca3af", fontSize: 13, fontWeight: 500, cursor: "pointer", marginLeft: "auto", transition: "all 0.15s", flexShrink: 0 },

  // Main
  main: { maxWidth: 1200, margin: "0 auto", padding: "28px 24px", display: "flex", flexDirection: "column", gap: 20 },
  headerRow: { display: "flex", alignItems: "flex-start", justifyContent: "space-between" },
  pageTitle: { fontSize: 26, fontWeight: 700, color: "#f0f0f0", margin: 0 },
  pageSubtitle: { fontSize: 13, color: "#6b7280", margin: "4px 0 0" },
  planBtn: { display: "flex", alignItems: "center", gap: 8, padding: "9px 18px", borderRadius: 8, border: "none", background: "#00c8e0", color: "#0d1117", fontSize: 13, fontWeight: 600, cursor: "pointer", flexShrink: 0 },

  // Cards
  card: { background: "#1a1d23", border: "1px solid #2a2d35", borderRadius: 12, padding: "20px 24px" },
  cardHeader: { display: "flex", alignItems: "center", gap: 8, marginBottom: 18 },
  cardTitle: { fontSize: 14, fontWeight: 600, color: "#e2e8f0" },

  // Account Summary
  statsRow: { display: "grid", gridTemplateColumns: "repeat(4, 1fr)", gap: 24, marginBottom: 18 },
  statItem: { display: "flex", flexDirection: "column", gap: 4 },
  statLabel: { fontSize: 11, color: "#6b7280", fontWeight: 500, textTransform: "uppercase", letterSpacing: "0.05em" },
  statValue: { fontSize: 22, fontWeight: 700, color: "#f0f0f0" },

  // Progress
  progressSection: { display: "flex", flexDirection: "column", gap: 6 },
  progressLabel: { display: "flex", justifyContent: "space-between", alignItems: "center" },
  progressAmount: { fontSize: 11, color: "#6b7280" },
  progressTrack: { height: 6, background: "#2a2d35", borderRadius: 99, overflow: "hidden" },
  progressFill: { height: "100%", borderRadius: 99, transition: "width 0.4s ease" },

  // Quick Stats
  statsGrid: { display: "grid", gridTemplateColumns: "repeat(3, 1fr)", gap: 16 },
  statCard: { background: "#212428", border: "1px solid #2a2d35", borderRadius: 10, padding: "16px 18px", display: "flex", flexDirection: "column", gap: 10 },
  statCardTop: { display: "flex", alignItems: "center", gap: 8 },
  statCardLabel: { fontSize: 12, color: "#9ca3af", fontWeight: 500 },
  statCardValue: { fontSize: 28, fontWeight: 700, color: "#f0f0f0" },

  // Quick Actions
  quickActionsGrid: { display: "grid", gridTemplateColumns: "repeat(3, 1fr)", gap: 16 },
  actionCard: { background: "#1a1d23", border: "1px solid #2a2d35", borderRadius: 12, padding: "20px", display: "flex", flexDirection: "column", gap: 12, cursor: "pointer", transition: "border-color 0.2s, background 0.2s" },
  actionCardText: { display: "flex", flexDirection: "column", gap: 4 },
  actionCardTitle: { fontSize: 14, fontWeight: 600, color: "#e2e8f0" },
  actionCardSub: { fontSize: 12, color: "#6b7280" },

  // Help
  helpBtn: { position: "fixed", bottom: 24, right: 24, width: 36, height: 36, borderRadius: "50%", border: "1px solid #2a2d35", background: "#1a1d23", color: "#9ca3af", fontSize: 14, fontWeight: 600, cursor: "pointer", display: "flex", alignItems: "center", justifyContent: "center" },
};