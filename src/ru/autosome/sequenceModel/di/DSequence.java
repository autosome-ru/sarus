package ru.autosome.sequenceModel.di;

import ru.autosome.Assistant;
import ru.autosome.sequenceModel.AbstractSequence;

public class DSequence implements AbstractSequence {
    public final byte[] sequence;
    DSequence(byte[] sequence) {
        this.sequence = sequence;
    }

    @Override
    public int length() {
        return this.sequence.length + 1;
    }
    public static DSequence sequenceFromString(String str) {
        int length = str.length();
        byte[] genome = new byte[length - 1];
        for (int j = 0; j < length - 1; j++) {  // A C G T N
            genome[j] = (byte) (5 * Assistant.charToByte(str.charAt(j)) + Assistant.charToByte(str.charAt(j + 1)));
        }
        return new DSequence(genome);
    }
}
