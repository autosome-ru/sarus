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
        double[][] sup_matrix;
        double[][] matrix = originalDPWM.matrix;

        if (matrix.length % 2 == 0) {
            sup_matrix = new double[matrix.length / 2][125];
            makeSDMatrixFromDMatrix(matrix, sup_matrix);
        } else {
            sup_matrix = new double[matrix.length / 2 + 1][125];
            makeSDMatrixFromDMatrix(with_zero_row(matrix), sup_matrix);
        }

        return new SuperAlphabetDPWM(sup_matrix, originalDPWM.motif_length());
    }

    static void makeSDMatrixFromDMatrix(double[][] matrix, double[][] sup_matrix) {
        for (int l = 0; l < matrix.length / 2; l++) {
            int k = 0, j = 0;
            for (int n = 0; n < 125; n++) {
                if (j > 0 && j % 5 == 0) {
                    k += 1;
                }
                if (j % 25 == 0) {
                    j = 0;
                }
                sup_matrix[l][n] = matrix[2 * l][k] + matrix[2 * l + 1][j];
                j += 1;
            }
        }
    }

    @Override
    public SuperAlphabetDPWM revcomp() {
        double[][] matrix = this.matrix;
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
