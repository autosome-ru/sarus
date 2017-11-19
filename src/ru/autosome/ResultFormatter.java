package ru.autosome;

public class ResultFormatter {
  public ResultFormatter() { }

  public String format(double score, int pos_start, String strand) {
    return String.valueOf(score) + "\t" + pos_start + "\t" + strand;
  }
}
