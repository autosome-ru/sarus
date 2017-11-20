package ru.autosome;

public class SarusResultFormatter implements ResultFormatter {
  ScoreFormatter scoreFormatter;
  public SarusResultFormatter(ScoreFormatter scoreFormatter) {
    this.scoreFormatter = scoreFormatter;
  }

  @Override
  public String format(double score, int pos_start, String strand) {
    return scoreFormatter.formatScore(score) + "\t" + pos_start + "\t" + strand;
  }
}
