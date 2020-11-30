package com.example.stockapp;

public class StockItem {

    private String ticker;
    private String trending;
    private String companyName;
    private String change;
    private String numShares;

    public StockItem(String ticker, String trending, String companyName, String change, String numShares) {
        this.ticker = ticker;
        this.trending = trending;
        this.companyName = companyName;
        this.change = change;
        this.numShares = numShares;
    }

    public String getTicker() {
        return ticker;
    }

    public String getTrending() {
        return trending;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getChange() {
        return change;
    }

    public String getNumShares() {
        return numShares;
    }
}
