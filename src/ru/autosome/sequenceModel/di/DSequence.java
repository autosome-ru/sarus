package ru.autosome.sequenceModel.di;

import ru.autosome.Assistant;
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

    @Override public int scanningStartIndex() { return 0; }
    @Override public int scanningEndIndex(PWM pwm) { return this.sequence.length - pwm.matrix_length() + 1; }
    @Override public int shiftForRevcompScore(PWM pwm) { return 0; }
    @Override public int shiftForPrint() { return 0; }
}
