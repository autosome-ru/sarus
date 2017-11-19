package ru.autosome;

import java.text.DecimalFormat;

public class ResultFormatter {
  public final DecimalFormat decimalFormat;
  public final PvalueBsearchList pvalueBsearchList; // if specified, converts scores into P-values
  public final boolean use_log_scale; // -log10(Pvalue); only if score-->pvalue conversion specified

  public ResultFormatter(DecimalFormat decimalFormat, PvalueBsearchList pvalueBsearchList, boolean use_log_scale) {
    this.pvalueBsearchList = pvalueBsearchList;
    this.use_log_scale = use_log_scale;
    this.decimalFormat = decimalFormat;
  }

  public ResultFormatter(Integer precision, PvalueBsearchList pvalueBsearchList, boolean use_log_scale) {
    this.pvalueBsearchList = pvalueBsearchList;
    this.use_log_scale = use_log_scale;
    this.decimalFormat = new DecimalFormat();
    if (precision == null) {
      decimalFormat.setMaximumFractionDigits(340);
    } else {
      decimalFormat.setMaximumFractionDigits(precision);
    }
  }

  public String format(double score, int pos_start, String strand) {

    if (pvalueBsearchList != null) {
      score = pvalueBsearchList.pvalue_by_threshold(score);
      if (use_log_scale) {
        score = -Math.log10(score);
      }
    }
    return decimalFormat.format(score) + "\t" + pos_start + "\t" + strand;
  }
}
