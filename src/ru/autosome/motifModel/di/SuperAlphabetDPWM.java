package ru.autosome.motifModel.di;

import ru.autosome.Assistant;
import ru.autosome.motifModel.Motif;
import ru.autosome.sequenceModel.di.SuperAlphabetDSequence;

import static ru.autosome.utils.with_zero_row;

public class SuperAlphabetDPWM implements Motif<SuperAlphabetDPWM> {
    public final double[][] matrix;
    private final int motifLength;
    SuperAlphabetDPWM(double[][] matrix, int motifLength) {
        this.matrix = matrix;
        this.motifLength = motifLength;
    }

    public double score(SuperAlphabetDSequence seq, int position) {
        double score = 0.0;
        for (int k = 0; k < this.matrix.length; k++) {
            byte letter = seq.sequence[position + 2 * k];
            score += this.matrix[k][letter];
        }
        return score;
    }

    public static SuperAlphabetDPWM fromNaive(DPWM originalDPWM) {
        return new SuperAlphabetDPWM(makeSDMatrixFromDMatrix(originalDPWM.matrix), originalDPWM.motif_length());
    }

    private static double[][] makeSDMatrixFromDMatrix(double[][] matrix) {
        if (matrix.length % 2 != 0) { // make matrix length even
            matrix = with_zero_row(matrix);
        }
        double[][] result = new double[matrix.length / 2][125];
        for (int diPos = 0; diPos < matrix.length / 2; diPos++) {
            for (int firstLetter = 0; firstLetter < 5; ++firstLetter){
                for (int secondLetter = 0; secondLetter < 5; ++secondLetter) {
                    for (int thirdLetter = 0; thirdLetter < 5; ++thirdLetter) {
                        result[diPos][25 * firstLetter + 5 * secondLetter + thirdLetter] =
                                matrix[2 * diPos][5 * firstLetter + secondLetter] +
                                matrix[2 * diPos + 1][5 * secondLetter + thirdLetter];
                    }
                }
            }
        }
        return result;
    }

    @Override
    public SuperAlphabetDPWM revcomp() {
        double[][] new_matrix = new double[matrix.length][125];
        for (int k = 0; k < matrix.length; k++) {
            for (int j = 0; j < 125; j++) {
                new_matrix[matrix.length - 1 - k][j] = matrix[k][Assistant.sdComplimentaryElements[j]];
            }
        }
        return new SuperAlphabetDPWM(new_matrix, motifLength);
    }

    @Override
    public int motif_length() {
        return motifLength;
    }
}
