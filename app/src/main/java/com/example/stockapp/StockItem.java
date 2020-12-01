package com.example.stockapp;

public class StockItem {

    private String ticker;
    private String lastPrice;
    private String companyName;
    private Double change;
    private String numShares;
    private String netWorth;

    public StockItem(String ticker, String lastPrice, String companyName, Double change, String numShares, String netWorth) {
        this.ticker = ticker;
        this.lastPrice = lastPrice;
        this.companyName = companyName;
        this.change = change;
        this.numShares = numShares;
        this.netWorth = netWorth;
    }

    public String getTicker() {
        return ticker;
    }

    public String getLastPrice() {return lastPrice;}

    public String getCompanyName() {
        return companyName;
    }

    public Double getChange() {
        return change;
    }

    public String getNumShares() {
        return numShares;
    }

    public String getNetWorth() {return netWorth;}
}
