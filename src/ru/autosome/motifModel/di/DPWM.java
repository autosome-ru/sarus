package ru.autosome.motifModel.di;

import ru.autosome.Assistant;
import ru.autosome.motifModel.Motif;
import ru.autosome.sequenceModel.di.DSequence;

import java.io.IOException;
import java.util.ArrayList;

import static ru.autosome.Assistant.listDoubleRowsToMatrix;

public class DPWM implements Motif<DPWM> {
    public final double[][] matrix;
    DPWM(double[][] matrix) {
        this.matrix = matrix;
    }

    public double score(DSequence seq, int position) {
        double score = 0.0;
        for (int k = 0; k < matrix.length; k++) {
            byte letter = seq.sequence[position + k];
            score += matrix[k][letter];
        }
        return score;
    }

    public static double[][] matrixAcceptingN(double[][] matrix, boolean N_isPermitted) {
        double[][] result = new double[matrix.length][25];
        for (int i = 0; i < matrix.length; i++) {
            for (int first_letter = 0; first_letter < 4; ++first_letter) {
                for (int second_letter = 0; second_letter < 4; ++second_letter) { // [ACGT][ACGT]
                    result[i][5 * first_letter + second_letter] = matrix[i][4 * first_letter + second_letter];
                }
            }
            for (int first_letter = 0; first_letter < 4; ++first_letter) { // [ACGT]N
                double sum = 0.0;
                for (int second_letter = 0; second_letter < 4; ++second_letter) {
                    sum += matrix[i][4 * first_letter + second_letter];
                }
                result[i][5 * first_letter + 4] = N_isPermitted ? (sum / 4) : Double.NEGATIVE_INFINITY;
            }
            for (int second_letter = 0; second_letter < 4; ++second_letter) { // N[ACGT]
                double sum = 0.0;
                for (int first_letter = 0; first_letter < 4; ++first_letter) {
                    sum += matrix[i][4 * first_letter + second_letter];
                }
                result[i][5 * 4 + second_letter] = N_isPermitted ? (sum / 4) : Double.NEGATIVE_INFINITY;
            }
            {
                double sum = 0;
                for (int di_letter = 0; di_letter < 16; ++di_letter) {
                    sum += matrix[i][di_letter];
                }
                result[i][5 * 4 + 4] = N_isPermitted ? (sum / 16) : Double.NEGATIVE_INFINITY;
            }
        }
        return result;
    }

    public static DPWM readDPWM(String path, boolean N_isPermitted, boolean transpose) throws IOException {
        ArrayList<String> strings = Assistant.load(path);
        ArrayList<Double[]> parsed = Assistant.parseDi(strings, transpose);
        return new DPWM(matrixAcceptingN(listDoubleRowsToMatrix(parsed), N_isPermitted));
    }

    @Override
    public DPWM revcomp() {
        double[][] new_matrix = new double[this.matrix.length][25];
        for (int k = 0; k < matrix.length; k++) {
            for (int j = 0; j < 25; j++) {
                new_matrix[matrix.length - 1 - k][j] = matrix[k][Assistant.dComplimentaryElements[j]];
            }
        }
        return new DPWM(new_matrix);
    }

    @Override
    public int motif_length() {
        return matrix.length + 1;
    }
}
