package ru.autosome.sequenceModel.mono;

import ru.autosome.Assistant;
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

    @Override public int scanningStartIndex() { return 0; }
    @Override public int scanningEndIndex(PWM pwm) { return this.sequence.length - pwm.matrix_length() + 1; }
    @Override public int shiftForRevcompScore(PWM pwm) { return 0; }
    @Override public int shiftForPrint() { return 0; }
}
