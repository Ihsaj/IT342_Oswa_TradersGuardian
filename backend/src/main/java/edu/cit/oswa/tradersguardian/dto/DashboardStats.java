package edu.cit.oswa.tradersguardian.dto;

public class DashboardStats {
    private long totalTrades;
    private long approvedTrades;
    private long disapprovedTrades;

    public DashboardStats(long totalTrades, long approvedTrades, long disapprovedTrades) {
        this.totalTrades = totalTrades;
        this.approvedTrades = approvedTrades;
        this.disapprovedTrades = disapprovedTrades;
    }

    public long getTotalTrades() { return totalTrades; }
    public void setTotalTrades(long totalTrades) { this.totalTrades = totalTrades; }

    public long getApprovedTrades() { return approvedTrades; }
    public void setApprovedTrades(long approvedTrades) { this.approvedTrades = approvedTrades; }

    public long getDisapprovedTrades() { return disapprovedTrades; }
    public void setDisapprovedTrades(long disapprovedTrades) { this.disapprovedTrades = disapprovedTrades; }
}
