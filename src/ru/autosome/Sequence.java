package ru.autosome;

// import java.text.DecimalFormat;

/**
 * Created with IntelliJ IDEA.
 * User: nastia
 * Date: 27.02.14
 * Time: 14:00
 * To change this template use File | Settings | File Templates.
 */
public abstract class Sequence {

  byte[] sequence;

  Sequence(byte[] sequence) {
    this.sequence = sequence;
  }

  abstract void scan(PWM pwm, PWM revComp_pwm, double threshold);

  abstract void bestHit(PWM pwm, PWM revComp_pwm);

  void internalScan(PWM pwm, PWM revComp_pwm, double threshold, int startIndex, int endIndex, int shiftForScoreInPWM, int shiftForScoreInRevCompPWM, int shiftForPrint) {

    double score1, score2;

    StringBuilder b = new StringBuilder();
    // DecimalFormat df = new DecimalFormat("#.##");
    for (int i = startIndex; i < endIndex; i++) {

      score1 = pwm.score(this, i + shiftForScoreInPWM);
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

  void internalBestHit(PWM pwm, PWM revComp_pwm, int startIndex, int endIndex, int shiftForScoreInPWM, int shiftForScoreInRevCompPWM, int shiftForPrint) {

    double best_score = Double.NEGATIVE_INFINITY;

    String DNAseq = "+";
    int index = 0;

    for (int i = startIndex; i < endIndex; i++) {

      double score1 = pwm.score(this, i + shiftForScoreInPWM);
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


    StringBuilder b = new StringBuilder();
    // DecimalFormat df = new DecimalFormat("#.##");

    b.append(best_score).append("\t").append(index + shiftForPrint).append("\t").append(DNAseq);
    System.out.println(b.toString());

  }
}
