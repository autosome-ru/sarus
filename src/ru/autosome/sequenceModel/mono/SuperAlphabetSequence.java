package ru.autosome.sequenceModel.mono;

import ru.autosome.Assistant;

public class SuperAlphabetSequence {
    public final byte[] sequence;
    SuperAlphabetSequence(byte[] sequence) {
        this.sequence = sequence;
    }

    public int length() {
        return sequence.length - 1;
    }

    public static SuperAlphabetSequence sequenceFromString(String str) {
        int length = str.length();
        byte[] genome = new byte[length + 1];
        genome[0] = (byte) (5 * 4 + Assistant.charToByte(str.charAt(0)));
        for (int j = 0; j < length - 1; j++) {
            genome[j + 1] = (byte) (5 * Assistant.charToByte(str.charAt(j)) + Assistant.charToByte(str.charAt(j + 1)));
        }
        genome[length] = (byte) (5 * Assistant.charToByte(str.charAt(length - 1)) + 4);
        return new SuperAlphabetSequence(genome);
    }
}
