package ru.autosome.sequenceModel.di;

import ru.autosome.Assistant;
import ru.autosome.motifModel.di.DPWM;
import ru.autosome.motifModel.PWM;
import ru.autosome.sequenceModel.Sequence;

/**
 * Created with IntelliJ IDEA.
 * User: nastia
 * Date: 27.02.14
 * Time: 15:31
 * To change this template use File | Settings | File Templates.
 */
public class SDSequence extends Sequence {

  SDSequence(byte[] sequence) {
    super(sequence);
  }

  public static SDSequence sequenceFromString(String str) {
    int length = str.length();
    byte[] genome = new byte[length];

    genome[0] = (byte) (25 * 4 +
        5 * Assistant.charToByte(str.charAt(0)) +
        Assistant.charToByte(str.charAt(1)));

    for (int j = 0; j < length - 2; j++) {

      genome[j + 1] = (byte) (25 * Assistant.charToByte(str.charAt(j)) +
          5 * Assistant.charToByte(str.charAt(j + 1)) +
          Assistant.charToByte(str.charAt(j + 2)));
    }

    genome[length - 1] = (byte) (25 * Assistant.charToByte(str.charAt(length - 2)) +
        5 * Assistant.charToByte(str.charAt(length - 1)) + 4);

    return new SDSequence(genome);

  }

  @Override
  public void scan(PWM pwm, PWM revComp_pwm, double threshold) {
    //if (pwm.getClass() != SDPWM.class || revComp_pwm.getClass() != SDPWM.class)
    //  throw new RuntimeException();

    if (DPWM.lengthOfDPWMIsEven) {
      internalScan(pwm, revComp_pwm, threshold, 1, this.sequence.length - 2 * pwm.length() + 1, 0, 0, -1);
    } else {
      internalScan(pwm, revComp_pwm, threshold, 1, this.sequence.length - 2 * pwm.length() + 2, 0, -1, -1);

    }
  }

  @Override
  public void bestHit(PWM pwm, PWM revComp_pwm) {
    //if (pwm.getClass() != SDPWM.class || revComp_pwm.getClass() != SDPWM.class)
    //  throw new RuntimeException();

    if (DPWM.lengthOfDPWMIsEven) {
      internalBestHit(pwm, revComp_pwm, 1, this.sequence.length - 2 * pwm.length() + 1, 0, 0, -1);
    } else {
      internalBestHit(pwm, revComp_pwm, 1, this.sequence.length - 2 * pwm.length() + 2, 0, -1, -1);

    }
  }

}
