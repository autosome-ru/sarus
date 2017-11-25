package ru.autosome;

public class SarusResultFormatter implements ResultFormatter {
  ScoreFormatter scoreFormatter;
  boolean outputNoMatch;
  public SarusResultFormatter(ScoreFormatter scoreFormatter, boolean outputNoMatch) {
    this.scoreFormatter = scoreFormatter;
    this.outputNoMatch = outputNoMatch;
  }

  @Override
  public boolean shouldOutputNoMatch() {
    return outputNoMatch;
  }

  @Override
  public String format(double score, int pos_start, String strand) {
    return scoreFormatter.formatScore(score) + "\t" + pos_start + "\t" + strand;

  }

  @Override
  public String formatNoMatch() {
    return scoreFormatter.formatScore(Double.NEGATIVE_INFINITY) + "\t" + (-1) + "\t" + "+";
  }
}
