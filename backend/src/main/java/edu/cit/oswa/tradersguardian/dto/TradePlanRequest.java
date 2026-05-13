package edu.cit.oswa.tradersguardian.dto;

public class TradePlanRequest {
    private String symbol;
    private String tradeType;
    private double entryPrice;
    private double stopLoss;
    private double takeProfit;
    private double positionSize;
    private double riskAmount;
    private double riskPercent;
    private String notes;

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getTradeType() { return tradeType; }
    public void setTradeType(String tradeType) { this.tradeType = tradeType; }

    public double getEntryPrice() { return entryPrice; }
    public void setEntryPrice(double entryPrice) { this.entryPrice = entryPrice; }

    public double getStopLoss() { return stopLoss; }
    public void setStopLoss(double stopLoss) { this.stopLoss = stopLoss; }

    public double getTakeProfit() { return takeProfit; }
    public void setTakeProfit(double takeProfit) { this.takeProfit = takeProfit; }

    public double getPositionSize() { return positionSize; }
    public void setPositionSize(double positionSize) { this.positionSize = positionSize; }

    public double getRiskAmount() { return riskAmount; }
    public void setRiskAmount(double riskAmount) { this.riskAmount = riskAmount; }

    public double getRiskPercent() { return riskPercent; }
    public void setRiskPercent(double riskPercent) { this.riskPercent = riskPercent; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
