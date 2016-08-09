package ru.autosome.sequenceModel.mono;

// import java.text.DecimalFormat;

import ru.autosome.Assistant;
import ru.autosome.motifModel.PWM;
import ru.autosome.sequenceModel.Sequence;

/**
 * Created with IntelliJ IDEA.
 * User: nastia
 * Date: 27.02.14
 * Time: 15:28
 * To change this template use File | Settings | File Templates.
 */
public class MSequence extends Sequence {

  MSequence(byte[] sequence) {
    super(sequence);
  }

  public static MSequence sequenceFromString(String str) {

    int length = str.length();

    byte[] genome = new byte[length];

    for (int j = 0; j < length; j++) {
      // A C G T N
      genome[j] = Assistant.charToByte(str.charAt(j));
    }

    return new MSequence(genome);
  }

  @Override
  public void scan(PWM pwm, PWM revComp_pwm, double threshold) {

    //if (pwm.getClass() != MPWM.class || revComp_pwm.getClass() != MPWM.class)
    //  throw new RuntimeException();

    internalScan(pwm, revComp_pwm,threshold, 0, this.sequence.length-pwm.length() + 1, 0, 0);

  }

  @Override
  public void bestHit(PWM pwm, PWM revComp_pwm) {
    //if (pwm.getClass() != MPWM.class || revComp_pwm.getClass() != MPWM.class)
    //throw new RuntimeException();

    internalBestHit(pwm, revComp_pwm, 0, this.sequence.length - pwm.length() + 1, 0, 0);

  }

}
