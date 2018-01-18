package ru.autosome.sequenceModel.di;

import ru.autosome.Assistant;
import ru.autosome.motifModel.PWM;
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

    @Override public int scanningStartIndex() { return 1; }
    @Override public int scanningEndIndex(PWM pwm) {
        if (pwm.lengthOfNaiveMotifIsEven()) {
            return this.sequence.length - 2 * pwm.matrix_length() + 1;
        } else {
            return this.sequence.length - 2 * pwm.matrix_length() + 2;
        }
    }
    @Override public int shiftForRevcompScore(PWM pwm) {
        if (pwm.lengthOfNaiveMotifIsEven()) {
            return 0;
        } else {
            return -1;
        }
    }
    @Override public int shiftForPrint() { return -1; }
}
