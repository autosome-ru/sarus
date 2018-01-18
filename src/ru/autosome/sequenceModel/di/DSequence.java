package ru.autosome.sequenceModel.di;

import ru.autosome.Assistant;
import ru.autosome.ResultFormatter;
import ru.autosome.motifModel.PWM;
import ru.autosome.sequenceModel.Sequence;

public class DSequence extends Sequence {

    DSequence(byte[] sequence) {
        super(sequence);
    }

    public static DSequence sequenceFromString(String str) {
        int length = str.length();

        byte[] genome = new byte[length - 1];

        for (int j = 0; j < length - 1; j++) {
            // A C G T N
            genome[j] = (byte) (5 * Assistant.charToByte(str.charAt(j)) + Assistant.charToByte(str.charAt(j + 1)));
        }
        return new DSequence(genome);
    }

    @Override
    public void scan(PWM pwm, PWM revComp_pwm, double threshold, ResultFormatter formatter) {
        //if (pwm.getClass() != DPWM.class || revComp_pwm.getClass() != DPWM.class)
        //  throw new RuntimeException();

        internalScan(pwm, revComp_pwm, threshold, 0, this.sequence.length - pwm.matrix_length() + 1, 0, 0, formatter);
    }

    @Override
    public void bestHit(PWM pwm, PWM revComp_pwm, ResultFormatter formatter) {
        //if (pwm.getClass() != DPWM.class || revComp_pwm.getClass() != DPWM.class)
        //  throw new RuntimeException();

        internalBestHit(pwm, revComp_pwm, 0, this.sequence.length - pwm.matrix_length() + 1, 0, 0, formatter);
    }
}
