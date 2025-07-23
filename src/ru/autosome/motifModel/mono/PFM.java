package ru.autosome.motifModel.mono;

import ru.autosome.Assistant;
import ru.autosome.motifModel.Motif;
import ru.autosome.sequenceModel.mono.Sequence;

import java.io.IOException;
import java.util.ArrayList;

import static ru.autosome.Assistant.listDoubleRowsToMatrix;

public class PFM implements Motif<PFM, Sequence> {

    public final double[][] matrix;
    PFM(double[][] matrix) {
        this.matrix = matrix;
    }

    // works with N × 4 matrices, not with N × 5
    @Override
    public double score(Sequence seq, int position) {
        double score = 1.0;
        for (int k = 0; k < matrix.length; k++) {
            byte letter = seq.sequence[position + k];
            score *= matrix[k][letter];
        }
        return score;
    }

    public static double[][] normalizeMatrix(double[][] matrix, boolean warn_about_normalization) {
        boolean should_warn = false;
        double[][] result = new double[matrix.length][4];
        for (int i = 0; i < matrix.length; i++) {
            double sum = 0.0;
            for (int letter = 0; letter < 4; ++letter) {
                sum += matrix[i][letter];
            }
            if (Math.abs(sum - 1.0) > 0.01) {
                should_warn = true;
            }
            for (int letter = 0; letter < 4; ++letter) {
                result[i][letter] = matrix[i][letter] / sum;
            }
        }
        if (warn_about_normalization && should_warn) {
            System.err.println("Warning: PCM was transformed into PFM.");
        }
        return result;
    }

    // works with N × 4 matrices, not with N × 5
    public static double[][] withPseudocount(double[][] matrix, double pseudocount) {
        double[][] result = new double[matrix.length][4];
        for (int i = 0; i < matrix.length; i++) {
            double sum = 0.0;
            for (int letter = 0; letter < 4; ++letter) {
                result[i][letter] = (matrix[i][letter] + pseudocount) / (1 + 4 * pseudocount);
            }
        }
        return result;
    }

    public static double[][] matrixAcceptingN(double[][] matrix, boolean N_isPermitted) {
        double[][] result = new double[matrix.length][5];
        for (int i = 0; i < matrix.length; i++) {
            for (int letter = 0; letter < 4; ++letter) {
                result[i][letter] = matrix[i][letter];
            }
            result[i][4] = N_isPermitted ? 0.25 : 0.0;
        }
        return result;
    }

    public static PFM readMPFM(String path, boolean N_isPermitted, double pseudocount, boolean transpose) throws IOException {
        ArrayList<String> strings = Assistant.load(path);
        ArrayList<Double[]> parsed = Assistant.parseMono(strings, transpose);
        double[][] matrix = listDoubleRowsToMatrix(parsed);
        // `withPseudocount` internally renormalizes matrix, so no need to normalize it once again
        return new PFM(matrixAcceptingN(withPseudocount(normalizeMatrix(matrix, true), pseudocount), N_isPermitted));
    }

    @Override
    public PFM revcomp() {
        double[][] new_matrix = new double[this.matrix.length][5];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < 4; j++) {
                new_matrix[matrix.length - 1 - i][j] = matrix[i][3 - j];
            }
            new_matrix[matrix.length - 1 - i][4] = matrix[i][4];
        }
        return new PFM(new_matrix);
    }

    @Override
    public int length() {
        return this.matrix.length;
    }

}
