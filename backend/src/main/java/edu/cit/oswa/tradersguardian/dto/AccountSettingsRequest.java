package edu.cit.oswa.tradersguardian.dto;

public class AccountSettingsRequest {
    private double accountBalance;
    private double riskPerTrade;
    private double dailyLossLimit;

    public double getAccountBalance() { return accountBalance; }
    public void setAccountBalance(double accountBalance) { this.accountBalance = accountBalance; }

    public double getRiskPerTrade() { return riskPerTrade; }
    public void setRiskPerTrade(double riskPerTrade) { this.riskPerTrade = riskPerTrade; }

    public double getDailyLossLimit() { return dailyLossLimit; }
    public void setDailyLossLimit(double dailyLossLimit) { this.dailyLossLimit = dailyLossLimit; }
}
