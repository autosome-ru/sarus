package ru.autosome.sequenceModel.di;

import ru.autosome.Assistant;
import ru.autosome.ResultFormatter;
import ru.autosome.motifModel.PWM;
import ru.autosome.motifModel.di.DPWM;
import ru.autosome.sequenceModel.Sequence;

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
    public void scan(PWM pwm, PWM revComp_pwm, double threshold, ResultFormatter formatter) {
        //if (pwm.getClass() != SDPWM.class || revComp_pwm.getClass() != SDPWM.class)
        //  throw new RuntimeException();

        if (DPWM.lengthOfDPWMIsEven) {
            internalScan(pwm, revComp_pwm, threshold, 1, this.sequence.length - 2 * pwm.matrix_length() + 1, 0, -1, formatter);
        } else {
            internalScan(pwm, revComp_pwm, threshold, 1, this.sequence.length - 2 * pwm.matrix_length() + 2, -1, -1, formatter);
        }
    }

    @Override
    public void bestHit(PWM pwm, PWM revComp_pwm, ResultFormatter formatter) {
        //if (pwm.getClass() != SDPWM.class || revComp_pwm.getClass() != SDPWM.class)
        //  throw new RuntimeException();

        if (DPWM.lengthOfDPWMIsEven) {
            internalBestHit(pwm, revComp_pwm, 1, this.sequence.length - 2 * pwm.matrix_length() + 1, 0, -1, formatter);
        } else {
            internalBestHit(pwm, revComp_pwm, 1, this.sequence.length - 2 * pwm.matrix_length() + 2, -1, -1, formatter);
        }
    }

}
