package ru.autosome.sequenceModel.mono;

import ru.autosome.Assistant;

public class Sequence implements ru.autosome.sequenceModel.AbstractSequence {
    public final byte[] sequence;
    Sequence(byte[] sequence) {
        this.sequence = sequence;
    }
    @Override
    public int length() {
        return sequence.length;
    }

    public static Sequence sequenceFromString(String str) {
        int length = str.length();
        byte[] genome = new byte[length];
        for (int j = 0; j < length; j++) {  // A C G T N
            genome[j] = Assistant.charToByte(str.charAt(j));
        }
        return new Sequence(genome);
    }
}
