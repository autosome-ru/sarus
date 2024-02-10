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

    @Override
    public double score(Sequence seq, int position) {
        double score = 1.0;
        for (int k = 0; k < matrix.length; k++) {
            byte letter = seq.sequence[position + k];
            score *= matrix[k][letter];
        }
        return score;
    }

    public static double[][] withPseudocount(double[][] matrix, double pseudocount) {
        double[][] result = new double[matrix.length][5];
        for (int i = 0; i < matrix.length; i++) {
            double sum = 0.0;
            for (int letter = 0; letter < 4; ++letter) {
                result[i][letter] = (matrix[i][letter] + pseudocount) / (1 + 4 * pseudocount);
            }
            result[i][4] = matrix[i][4];
        }
        return result;
    }

    public static double[][] matrixAcceptingN(double[][] matrix, boolean N_isPermitted) {
        double[][] result = new double[matrix.length][5];
        for (int i = 0; i < matrix.length; i++) {
            double sum = 0.0;
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
        return new PFM(matrixAcceptingN(withPseudocount(matrix, pseudocount), N_isPermitted));
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
