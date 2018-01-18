package ru.autosome;

public class SarusResultFormatter implements ResultFormatter {
    ScoreFormatter scoreFormatter;
    boolean outputNoMatch;
    int flankLength;

    public SarusResultFormatter(ScoreFormatter scoreFormatter, boolean outputNoMatch, int flankLength) {
        this.scoreFormatter = scoreFormatter;
        this.outputNoMatch = outputNoMatch;
        this.flankLength = flankLength;
    }

    @Override
    public boolean shouldOutputNoMatch() {
        return outputNoMatch;
    }

    @Override
    public String format(double score, int pos_start, String strand) {
        return scoreFormatter.formatScore(score) + "\t" + (pos_start - getFlankLength()) + "\t" + strand;
    }

    @Override
    public String formatNoMatch() {
        if (!shouldOutputNoMatch()) {
            throw new UnsupportedOperationException("Can't output non-matches when it's not declared explicitly in a formatter.");
        }
        return scoreFormatter.formatScore(Double.NEGATIVE_INFINITY) + "\t" + (-1) + "\t" + "+";
    }

    @Override
    public int getFlankLength() {
        return flankLength;
    }
}
