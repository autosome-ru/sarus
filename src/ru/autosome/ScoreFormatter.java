package ru.autosome;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.text.DecimalFormat;

public class ScoreFormatter {
  public final DecimalFormat decimalFormat;
  public final PvalueBsearchList pvalueBsearchList; // if specified, converts scores into P-values
  public final ScoreType outputScoringModel; // output score, pvalue or logpvalue

  public ScoreFormatter(DecimalFormat decimalFormat, ScoreType outputScoringModel, PvalueBsearchList pvalueBsearchList) {
    this.outputScoringModel = outputScoringModel;
    this.pvalueBsearchList = pvalueBsearchList;
    this.decimalFormat = decimalFormat;
    if (pvalueBsearchList == null && (outputScoringModel == ScoreType.PVALUE || outputScoringModel == ScoreType.LOGPVALUE)) {
      throw new IllegalArgumentException("Scoring model needs P-values but score --> pvalue mapping not specified.");
    }
  }

  public ScoreFormatter(Integer precision, ScoreType outputScoringModel, PvalueBsearchList pvalueBsearchList) {
    this.outputScoringModel = outputScoringModel;
    this.pvalueBsearchList = pvalueBsearchList;
    this.decimalFormat = new DecimalFormat();
    if (precision == null) {
      decimalFormat.setMaximumFractionDigits(340);
    } else {
      decimalFormat.setMaximumFractionDigits(precision);
    }
    if (pvalueBsearchList == null && (outputScoringModel == ScoreType.PVALUE || outputScoringModel == ScoreType.LOGPVALUE)) {
      throw new IllegalArgumentException("Scoring model needs P-values but score --> pvalue mapping not specified.");
    }
  }

  public double scaleScore(double score) {
    return outputScoringModel.fromScoreUnits(score, pvalueBsearchList);
  }

  public String formatScore(double score) {
    return decimalFormat.format(scaleScore(score));
  }
}
