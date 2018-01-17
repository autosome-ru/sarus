package ru.autosome.sequenceModel;

// import java.text.DecimalFormat;

import ru.autosome.Occurence;
import ru.autosome.ResultFormatter;
import ru.autosome.Strand;
import ru.autosome.motifModel.PWM;

import java.util.function.Consumer;
import java.util.function.Function;

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
  void internalScanCallback(PWM pwm, PWM revComp_pwm, int startIndex, int endIndex, int shiftForScoreInRevCompPWM, Consumer<Occurence> consumer) {
    Occurence current = new Occurence(Double.NEGATIVE_INFINITY, 0, Strand.direct);
    for (int i = startIndex; i < endIndex; i++) {
      current.replace(pwm.score(this, i), i, Strand.direct);
      consumer.accept(current);
      current.replace(revComp_pwm.score(this, i + shiftForScoreInRevCompPWM), i, Strand.revcomp);
      consumer.accept(current);
    }
  }

  public void internalScan(PWM pwm, PWM revComp_pwm, double threshold, int startIndex, int endIndex, int shiftForScoreInRevCompPWM, int shiftForPrint, ResultFormatter formatter) {
    internalScanCallback(pwm, revComp_pwm, startIndex, endIndex, shiftForScoreInRevCompPWM, occurence -> {
      if (occurence.goodEnough(threshold)) {
        String occurence_info = formatter.format(occurence.score,
                occurence.pos + shiftForPrint, occurence.strand.shortSign());
        System.out.println(occurence_info);
      }
    });

  }

  public void internalBestHit(PWM pwm, PWM revComp_pwm, int startIndex, int endIndex, int shiftForScoreInRevCompPWM, int shiftForPrint, ResultFormatter formatter) {
    if (startIndex >= endIndex) { // sequence is shorter than motif
      if (formatter.shouldOutputNoMatch()) {
        System.out.println(formatter.formatNoMatch());
      }
      return;
    }

    Occurence bestOccurence = new Occurence(Double.NEGATIVE_INFINITY, 0, Strand.direct);
    internalScanCallback(pwm, revComp_pwm, startIndex, endIndex, shiftForScoreInRevCompPWM, occurence -> {
      bestOccurence.replaceIfBetter(occurence);
    });

    String occurence_info = formatter.format(bestOccurence.score, bestOccurence.pos + shiftForPrint, bestOccurence.strand.shortSign());
    System.out.println(occurence_info);
  }
}
