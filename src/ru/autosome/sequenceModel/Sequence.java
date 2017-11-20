package ru.autosome.sequenceModel;

// import java.text.DecimalFormat;

import ru.autosome.ResultFormatter;
import ru.autosome.motifModel.PWM;

/**
 * Created with IntelliJ IDEA.
 * User: nastia
 * Date: 27.02.14
 * Time: 14:00
 * To change this template use File | Settings | File Templates.
 */
public abstract class Sequence {

  public final byte[] sequence;

  public Sequence(byte[] sequence) {
    this.sequence = sequence;
  }

  public abstract void scan(PWM pwm, PWM revComp_pwm, double threshold, ResultFormatter formatter);

  public abstract void bestHit(PWM pwm, PWM revComp_pwm, ResultFormatter formatter);

  public void internalScan(PWM pwm, PWM revComp_pwm, double threshold, int startIndex, int endIndex, int shiftForScoreInRevCompPWM, int shiftForPrint, ResultFormatter formatter) {
    double score_direct, score_revcomp;
    for (int i = startIndex; i < endIndex; i++) {
      score_direct = pwm.score(this, i);
      if (score_direct >= threshold) {
        String occurence_info = formatter.format(score_direct, i + shiftForPrint, "+");
        System.out.println(occurence_info);
      }

      score_revcomp = revComp_pwm.score(this, i + shiftForScoreInRevCompPWM);
      if (score_revcomp >= threshold) {
        String occurence_info = formatter.format(score_revcomp, i + shiftForPrint, "-");
        System.out.println(occurence_info);
      }
    }
  }

  public void internalBestHit(PWM pwm, PWM revComp_pwm, int startIndex, int endIndex, int shiftForScoreInRevCompPWM, int shiftForPrint, ResultFormatter formatter) {
    double best_score = Double.NEGATIVE_INFINITY;
    String DNAseq = "+";
    int index = 0;

    for (int i = startIndex; i < endIndex; i++) {
      double score_direct = pwm.score(this, i);
      if (score_direct >= best_score) {
        best_score = score_direct;
        DNAseq = "+";
        index = i;
      }

      double score_revcomp = revComp_pwm.score(this, i + shiftForScoreInRevCompPWM);
      if (score_revcomp >= best_score) {
        best_score = score_revcomp;
        DNAseq = "-";
        index = i;
      }
    }

    String occurence_info = formatter.format(best_score, index + shiftForPrint, DNAseq);
    System.out.println(occurence_info);
  }
}
