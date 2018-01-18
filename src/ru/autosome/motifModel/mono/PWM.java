package ru.autosome.motifModel.mono;

import ru.autosome.Assistant;
import ru.autosome.motifModel.Motif;
import ru.autosome.sequenceModel.mono.Sequence;

import java.io.IOException;
import java.util.ArrayList;

import static ru.autosome.Assistant.listDoubleRowsToMatrix;

public class PWM implements Motif<PWM> {
    public final double[][] matrix;
    PWM(double[][] matrix) {
        this.matrix = matrix;
    }

    public double score(Sequence seq, int position) {
        double score = 0.0;
        for (int k = 0; k < matrix.length; k++) {
            byte letter = seq.sequence[position + k];
            score += matrix[k][letter];
        }
        return score;
    }

    public static double[][] matrixAcceptingN(double[][] matrix, boolean N_isPermitted) {
        double[][] result = new double[matrix.length][5];
        for (int i = 0; i < matrix.length; i++) {
            double sum = 0.0;
            for (int letter = 0; letter < 4; ++letter) {
                result[i][letter] = matrix[i][letter];
                sum += matrix[i][letter];
            }
            result[i][4] = N_isPermitted ? (sum / 4) : Double.NEGATIVE_INFINITY;
        }
        return result;
    }

    public static PWM readMPWM(String path, boolean N_isPermitted, boolean transpose) throws IOException {
        ArrayList<String> strings = Assistant.load(path);
        ArrayList<Double[]> parsed = Assistant.parseMono(strings, transpose);
        return new PWM(matrixAcceptingN(listDoubleRowsToMatrix(parsed), N_isPermitted));
    }

    @Override
    public PWM revcomp() {
        double[][] new_matrix = new double[this.matrix.length][5];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < 4; j++) {
                new_matrix[matrix.length - 1 - i][j] = matrix[i][3 - j];
            }
            new_matrix[matrix.length - 1 - i][4] = matrix[i][4];
        }
        return new PWM(new_matrix);
    }

    @Override
    public int motif_length() {
        return this.matrix.length;
    }

}
