package ru.autosome.sequenceModel.mono;

// import java.text.DecimalFormat;

import ru.autosome.Assistant;
import ru.autosome.ResultFormatter;
import ru.autosome.motifModel.PWM;
import ru.autosome.sequenceModel.Sequence;

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
    public void scan(PWM pwm, PWM revComp_pwm, double threshold, ResultFormatter formatter) {

        //if (pwm.getClass() != MPWM.class || revComp_pwm.getClass() != MPWM.class)
        //  throw new RuntimeException();

        internalScan(pwm, revComp_pwm, threshold, 0, this.sequence.length - pwm.matrix_length() + 1, 0, 0, formatter);
    }

    @Override
    public void bestHit(PWM pwm, PWM revComp_pwm, ResultFormatter formatter) {
        //if (pwm.getClass() != MPWM.class || revComp_pwm.getClass() != MPWM.class)
        //throw new RuntimeException();

        internalBestHit(pwm, revComp_pwm, 0, this.sequence.length - pwm.matrix_length() + 1, 0, 0, formatter);
    }

}
