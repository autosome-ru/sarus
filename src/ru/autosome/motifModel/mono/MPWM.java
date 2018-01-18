package ru.autosome.motifModel.mono;

import ru.autosome.Assistant;
import ru.autosome.motifModel.PWM;
import ru.autosome.sequenceModel.mono.MSequence;

import java.io.IOException;
import java.util.ArrayList;

public class MPWM implements PWM<MPWM> {
    public final double[][] matrix;
    MPWM(double[][] matrix) {
        this.matrix = matrix;
    }

    public double score(MSequence seq, int position) {
        double score = 0.0;
        for (int k = 0; k < matrix.length; k++) {
            byte letter = seq.sequence[position + k];
            score += matrix[k][letter];
        }
        return score;
    }

    public static MPWM readMPWM(String path, boolean N_isPermitted, boolean transpose) throws IOException {
        ArrayList<String> strings = Assistant.load(path);
        ArrayList<Double[]> parsed = Assistant.parseMono(strings, transpose);

        int len = parsed.size();
        double[][] resultPWM = new double[len][5];

        for (int i = 0; i < len; i++) {
            Double[] line = parsed.get(i);
            for (int j = 0; j < 4; j++) {
                resultPWM[i][j] = line[j];
            }

            if (N_isPermitted) {
                resultPWM[i][4] = (resultPWM[i][0] + resultPWM[i][1] + resultPWM[i][2] + resultPWM[i][3]) / 4;
            } else {
                resultPWM[i][4] = Double.NEGATIVE_INFINITY;
            }
        }
        return new MPWM(resultPWM);
    }

    @Override
    public MPWM revcomp() {
        double[][] new_matrix = new double[this.matrix.length][5];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < 4; j++) {
                new_matrix[matrix.length - 1 - i][j] = matrix[i][3 - j];
            }
            new_matrix[matrix.length - 1 - i][4] = matrix[i][4];
        }
        return new MPWM(new_matrix);
    }

    @Override
    public int motif_length() {
        return this.matrix.length;
    }

}
