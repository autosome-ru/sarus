package ru.autosome.sequenceModel.di;

import ru.autosome.Assistant;

public class SDSequence {
    public final byte[] sequence;
    SDSequence(byte[] sequence) {
        this.sequence = sequence;
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
}
