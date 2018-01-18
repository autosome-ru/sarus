package ru.autosome.sequenceModel.mono;

import ru.autosome.Assistant;
import ru.autosome.motifModel.PWM;
import ru.autosome.sequenceModel.Sequence;

public class SMSequence extends Sequence {

    SMSequence(byte[] sequence) {
        super(sequence);
    }

    public static SMSequence sequenceFromString(String str) {
        int length = str.length();
        byte[] genome = new byte[length + 1];
        genome[0] = (byte) (5 * 4 + Assistant.charToByte(str.charAt(0)));
        for (int j = 0; j < length - 1; j++) {
            genome[j + 1] = (byte) (5 * Assistant.charToByte(str.charAt(j)) + Assistant.charToByte(str.charAt(j + 1)));
        }
        genome[length] = (byte) (5 * Assistant.charToByte(str.charAt(length - 1)) + 4);
        return new SMSequence(genome);
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
