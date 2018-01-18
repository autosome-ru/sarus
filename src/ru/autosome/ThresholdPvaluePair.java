package ru.autosome;

import java.util.Comparator;

public class ThresholdPvaluePair {
    public final Double threshold;
    public final Double pvalue;

    public ThresholdPvaluePair(Double threshold, Double pvalue) {
        this.threshold = threshold;
        this.pvalue = pvalue;
    }

    static final Comparator<ThresholdPvaluePair> thresholdComparator =
            Comparator.comparing(o -> (o.threshold));

    // reversed comparison (thresholds are sorted ascending, so pvalues descending)
    static final Comparator<ThresholdPvaluePair> pvalueComparator =
            Comparator.comparing(o -> (-o.pvalue));

    @Override
    public boolean equals(Object other) {
        if (other instanceof ThresholdPvaluePair) {
            ThresholdPvaluePair otherConv = (ThresholdPvaluePair) other;
            return threshold.equals(otherConv.threshold) && pvalue.equals(otherConv.pvalue);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 17 + threshold.hashCode();
        hash = hash * 31 + pvalue.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        return threshold + "\t" + pvalue;
    }
}
