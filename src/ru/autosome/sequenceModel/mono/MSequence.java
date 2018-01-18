package ru.autosome.sequenceModel.mono;

import ru.autosome.Assistant;

public class MSequence {
    public final byte[] sequence;
    MSequence(byte[] sequence) {
        this.sequence = sequence;
    }

    public static MSequence sequenceFromString(String str) {
        int length = str.length();
        byte[] genome = new byte[length];
        for (int j = 0; j < length; j++) {  // A C G T N
            genome[j] = Assistant.charToByte(str.charAt(j));
        }
        return new MSequence(genome);
    }
}
