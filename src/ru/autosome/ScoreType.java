package ru.autosome;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public enum ScoreType {
    SCORE,
    PVALUE,
    LOGPVALUE; // -log10(Pvalue)

    // Return score corresponding to a given value which is treated as either score, or P-value, or logPvalue
    public double valueInScoreUnits(double value, PvalueBsearchList pvalueBsearchList) {
        double score;
        if (this == ScoreType.SCORE) {
            score = value;
        } else {
            if (pvalueBsearchList == null) {
                throw new IllegalArgumentException("P-value based value given but score <--> pvalue conversion not specified.");
            }
            double pvalue;
            if (this == ScoreType.PVALUE) {
                pvalue = value;
            } else if (this == ScoreType.LOGPVALUE) {
                double logPvalue = value;
                pvalue = Math.pow(10.0, -logPvalue);
            } else {
                throw new NotImplementedException();
            }
            if (pvalue < 1) {
                score = pvalueBsearchList.threshold_by_pvalue(pvalue);
            } else {
                // Any score will be higher than -Inf
                // It isn't enough to take worst_score from threshold-pvalue list
                // because due to floating point errors, sometimes (e.g. in superalphabet)
                // score can be a bit less than the worst score.
                score = Double.NEGATIVE_INFINITY;
            }
        }
        return score;
    }

    // Take score and convert it to either score itself, or P-value, or logPvalue
    public double fromScoreUnits(double score, PvalueBsearchList pvalueBsearchList) {
        if (this == ScoreType.SCORE) {
            return score;
        } else {
            if (pvalueBsearchList == null) {
                throw new IllegalArgumentException("Scoring model should return P-value but score <--> pvalue mapping not specified.");
            }
            double pvalue = pvalueBsearchList.pvalue_by_threshold(score);
            if (this == ScoreType.PVALUE) {
                return pvalue;
            } else if (this == ScoreType.LOGPVALUE) {
                return -Math.log10(pvalue);
            } else {
                throw new NotImplementedException();
            }
        }
    }
}
