package ru.autosome.sequenceModel;

// import java.text.DecimalFormat;

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

  public abstract void scan(PWM pwm, PWM revComp_pwm, double threshold);

  public abstract void bestHit(PWM pwm, PWM revComp_pwm);

  public void internalScan(PWM pwm, PWM revComp_pwm, double threshold, int startIndex, int endIndex, int shiftForScoreInRevCompPWM, int shiftForPrint) {

    double score1, score2;

    StringBuilder b = new StringBuilder();
    // DecimalFormat df = new DecimalFormat("#.##");
    for (int i = startIndex; i < endIndex; i++) {

      score1 = pwm.score(this, i);
      if (score1 >= threshold) {

        b.append(score1).append("\t").append(i + shiftForPrint).append("\t").append("+");
        System.out.println(b.toString());
        b.setLength(0);

      }

      score2 = revComp_pwm.score(this, i + shiftForScoreInRevCompPWM);
      if (score2 >= threshold) {

        b.append(score2).append("\t").append(i + shiftForPrint).append("\t").append("-");
        System.out.println(b.toString());
        b.setLength(0);

      }

    }

  }

  public void internalBestHit(PWM pwm, PWM revComp_pwm, int startIndex, int endIndex, int shiftForScoreInRevCompPWM, int shiftForPrint) {

    double best_score = Double.NEGATIVE_INFINITY;

    String DNAseq = "+";
    int index = 0;

    for (int i = startIndex; i < endIndex; i++) {

      double score1 = pwm.score(this, i);
      if (score1 >= best_score) {
        best_score = score1;
        DNAseq = "+";
        index = i;
      }

      score1 = revComp_pwm.score(this, i + shiftForScoreInRevCompPWM);
      if (score1 >= best_score) {
        best_score = score1;
        DNAseq = "-";
        index = i;
      }

    }


    // DecimalFormat df = new DecimalFormat("#.##");

    System.out.println(String.valueOf(best_score) + "\t" + (index + shiftForPrint) + "\t" + DNAseq);

  }
}
