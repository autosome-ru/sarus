package ru.autosome.motifModel.mono;

import ru.autosome.Assistant;
import ru.autosome.motifModel.Motif;
import ru.autosome.sequenceModel.mono.SuperAlphabetSequence;

import static ru.autosome.utils.with_zero_row;

public class SuperAlphabetPWM implements Motif<SuperAlphabetPWM> {
    public final double[][] matrix;
    private final int motifLength;
    SuperAlphabetPWM(double[][] matrix, int motifLength) {
        this.matrix = matrix;
        this.motifLength = motifLength;
    }

    public double score(SuperAlphabetSequence seq, int position) {
        double score = 0.0;
        for (int k = 0; k < this.matrix.length; k++) {
            byte letter = seq.sequence[position + 2 * k];
            score += this.matrix[k][letter];
        }
        return score;
    }

    public static SuperAlphabetPWM fromNaive(PWM originalPWM) {
        return new SuperAlphabetPWM(makeSMMatrixFromMMatrix(originalPWM.matrix), originalPWM.motif_length());
    }

    private static double[][] makeSMMatrixFromMMatrix(double[][] matrix) {
        if (matrix.length % 2 != 0) { // make matrix length even
            matrix = with_zero_row(matrix);
        }
        double[][] result = new double[matrix.length / 2][25];
        for (int diPos = 0; diPos < matrix.length / 2; ++diPos) {
            for (int firstLetter = 0; firstLetter < 5; ++firstLetter) {
                for (int secondLetter = 0; secondLetter < 5; ++secondLetter) {
                    result[diPos][5 * firstLetter + secondLetter] =
                            matrix[2 * diPos][firstLetter] + matrix[2 * diPos + 1][secondLetter];
                }
            }
        }
        return result;
    }

    @Override
    public SuperAlphabetPWM revcomp() {
        double[][] new_matrix = new double[matrix.length][25];
        for (int k = 0; k < matrix.length; k++) {
            for (int j = 0; j < 25; j++) {
                new_matrix[matrix.length - 1 - k][j] = matrix[k][Assistant.dComplimentaryElements[j]];
            }
        }
        return new SuperAlphabetPWM(new_matrix, motifLength);
    }

    @Override
    public int motif_length() {
        return motifLength;
    }
}
