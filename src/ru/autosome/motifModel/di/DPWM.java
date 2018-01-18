package ru.autosome.motifModel.di;

import ru.autosome.Assistant;
import ru.autosome.motifModel.PWM;
import ru.autosome.sequenceModel.Sequence;
import ru.autosome.sequenceModel.di.DSequence;

import java.io.IOException;
import java.util.ArrayList;

public class DPWM extends PWM {

    DPWM(double[][] matrix, boolean lengthOfNaiveMotifIsEven) {
        super(matrix, lengthOfNaiveMotifIsEven);
    }

    @Override
    public double score(Sequence seq, int position) {

        if (seq.getClass() != DSequence.class)
            throw new RuntimeException();

        double score = 0.0;

        for (int k = 0; k < this.matrix.length; k++) {

            byte letter = seq.sequence[position + k];

            score += this.matrix[k][letter];
        }
        return score;
    }

    public static DPWM readDPWM(String path, boolean N_isPermitted, boolean transpose) throws IOException {

        ArrayList<String> strings = Assistant.load(path);
        ArrayList<Double[]> parsed = Assistant.parseDi(strings, transpose);

        int len = parsed.size();

        double[][] resultPWM = new double[len][25];

        for (int i = 0; i < len; i++) {
            // String[] line = ((strings.get(i)).split(" |\t"));
            Double[] line = parsed.get(i);

            int j;
            int a = 0;
            for (j = 0; j < 20; j++) {
                if (((j + 1) % 5) != 0) { // [ACGT][ACGT]
                    resultPWM[i][j] = line[a];// Double.parseDouble(line[a]);
                    a += 1;
                } else { // [ACGT]N
                    if (N_isPermitted) {
                        resultPWM[i][j] = (resultPWM[i][j - 4] + resultPWM[i][j - 3] + resultPWM[i][j - 2] + resultPWM[i][j - 1]) / 4;
                    } else {
                        resultPWM[i][j] = Double.NEGATIVE_INFINITY;
                    }
                }

            }
            for (j = 20; j < 25; j++) { // N[ACGT] and NN
                if (N_isPermitted) {
                    resultPWM[i][j] = (resultPWM[i][j % 5] + resultPWM[i][5 + j % 5] + resultPWM[i][10 + j % 5] + resultPWM[i][15 + j % 5]) / 4;
                } else {
                    resultPWM[i][j] = Double.NEGATIVE_INFINITY;
                }
            }
        }
        return new DPWM(resultPWM, len % 2 == 0);
    }

    @Override
    public DPWM revcomp() {
        if (this.getClass() != DPWM.class)
            throw new RuntimeException();

        double[][] new_matrix = new double[this.matrix.length][25];

        for (int k = 0; k < matrix.length; k++) {
            for (int j = 0; j < 25; j++) {
                new_matrix[matrix.length - 1 - k][j] = matrix[k][Assistant.dComplimentaryElements[j]];
            }
        }

        return new DPWM(new_matrix, lengthOfNaiveMotifIsEven());
    }

    @Override
    public int motif_length() {
        return this.matrix.length + 1;
    }
}
