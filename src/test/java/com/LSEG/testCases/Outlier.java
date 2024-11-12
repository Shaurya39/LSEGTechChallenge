package com.LSEG.testCases;

public class Outlier {
        String stockID;
        String timestamp;
        double actualPrice;
        double mean;
        double deviation;
        double percentDeviation;

       public Outlier(String stockID, String timestamp, double actualPrice, double mean, double deviation, double percentDeviation) {
            this.stockID = stockID;
            this.timestamp = timestamp;
            this.actualPrice = actualPrice;
            this.mean = mean;
            this.deviation = deviation;
            this.percentDeviation = percentDeviation;
        }
    }

