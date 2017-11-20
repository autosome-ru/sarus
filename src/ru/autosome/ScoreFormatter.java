package ru.autosome;

import java.text.DecimalFormat;

public class ScoreFormatter {
  public final DecimalFormat decimalFormat;
  public final PvalueBsearchList pvalueBsearchList; // if specified, converts scores into P-values
  public final boolean use_log_scale; // -log10(Pvalue); only if score-->pvalue conversion specified

  public ScoreFormatter(DecimalFormat decimalFormat, PvalueBsearchList pvalueBsearchList, boolean use_log_scale) {
    this.pvalueBsearchList = pvalueBsearchList;
    this.use_log_scale = use_log_scale;
    this.decimalFormat = decimalFormat;
  }

  public ScoreFormatter(Integer precision, PvalueBsearchList pvalueBsearchList, boolean use_log_scale) {
    this.pvalueBsearchList = pvalueBsearchList;
    this.use_log_scale = use_log_scale;
    this.decimalFormat = new DecimalFormat();
    if (precision == null) {
      decimalFormat.setMaximumFractionDigits(340);
    } else {
      decimalFormat.setMaximumFractionDigits(precision);
    }
  }

  public double scaleScore(double score) {
    if (pvalueBsearchList == null) {
      return score;
    }
    double pvalue = pvalueBsearchList.pvalue_by_threshold(score);
    return use_log_scale ? -Math.log10(pvalue) : pvalue;
  }

  public String formatScore(double score) {
    return decimalFormat.format(scaleScore(score));
  }
}
