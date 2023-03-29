package org.example.java2test;

public class DataEntry {
    private final String date;
    private final double currency1Value;
    private final double currency2Value;

    public DataEntry(String date, double currency1Value, double currency2Value) {
        this.date = date;
        this.currency1Value = currency1Value;
        this.currency2Value = currency2Value;
    }

    public String getDate() {
        return date;
    }

    public double getCurrency1Value() {
        return currency1Value;
    }

    public double getCurrency2Value() {
        return currency2Value;
    }
}
