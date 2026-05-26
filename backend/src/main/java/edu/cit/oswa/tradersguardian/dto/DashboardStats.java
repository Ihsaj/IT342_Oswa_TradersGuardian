package edu.cit.oswa.tradersguardian.dto;

public class DashboardStats {
    private long totalTrades;
    private long approvedTrades;
    private long disapprovedTrades;
    private long winTrades;
    private long lossTrades;
    private double currentDailyLoss;   // percentage used today
    private double dailyLossLimit;     // percentage limit
    private double accountBalance;

    public DashboardStats(long totalTrades, long approvedTrades, long disapprovedTrades,
                          long winTrades, long lossTrades,
                          double currentDailyLoss, double dailyLossLimit, double accountBalance) {
        this.totalTrades = totalTrades;
        this.approvedTrades = approvedTrades;
        this.disapprovedTrades = disapprovedTrades;
        this.winTrades = winTrades;
        this.lossTrades = lossTrades;
        this.currentDailyLoss = currentDailyLoss;
        this.dailyLossLimit = dailyLossLimit;
        this.accountBalance = accountBalance;
    }

    public long getTotalTrades() { return totalTrades; }
    public void setTotalTrades(long totalTrades) { this.totalTrades = totalTrades; }

    public long getApprovedTrades() { return approvedTrades; }
    public void setApprovedTrades(long approvedTrades) { this.approvedTrades = approvedTrades; }

    public long getDisapprovedTrades() { return disapprovedTrades; }
    public void setDisapprovedTrades(long disapprovedTrades) { this.disapprovedTrades = disapprovedTrades; }

    public long getWinTrades() { return winTrades; }
    public void setWinTrades(long winTrades) { this.winTrades = winTrades; }

    public long getLossTrades() { return lossTrades; }
    public void setLossTrades(long lossTrades) { this.lossTrades = lossTrades; }

    public double getCurrentDailyLoss() { return currentDailyLoss; }
    public void setCurrentDailyLoss(double currentDailyLoss) { this.currentDailyLoss = currentDailyLoss; }

    public double getDailyLossLimit() { return dailyLossLimit; }
    public void setDailyLossLimit(double dailyLossLimit) { this.dailyLossLimit = dailyLossLimit; }

    public double getAccountBalance() { return accountBalance; }
    public void setAccountBalance(double accountBalance) { this.accountBalance = accountBalance; }
}
