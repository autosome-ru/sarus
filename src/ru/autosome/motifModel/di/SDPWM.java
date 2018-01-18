package ru.autosome.motifModel.di;

import ru.autosome.Assistant;
import ru.autosome.motifModel.PWM;
import ru.autosome.sequenceModel.Sequence;
import ru.autosome.sequenceModel.di.SDSequence;

public class SDPWM extends PWM {

    private final int motifLength;

    SDPWM(double[][] matrix, int motifLength, boolean lengthOfNaiveMotifIsEven) {
        super(matrix, lengthOfNaiveMotifIsEven);
        this.motifLength = motifLength;
    }

    @Override
    public double score(Sequence seq, int position) {

        if (seq.getClass() != SDSequence.class)
            throw new RuntimeException();

        double score = 0.0;

        for (int k = 0; k < this.matrix.length; k++) {

            byte letter = seq.sequence[position + 2 * k];

            score += this.matrix[k][letter];
        }
        return score;
    }

    public static SDPWM fromDPWM(DPWM original_dpwm) {
        double[][] sup_matrix;
        double[][] matrix = original_dpwm.matrix;

        if (original_dpwm.lengthOfNaiveMotifIsEven()) {
            sup_matrix = new double[(matrix.length) / 2][125];
            makeSDMatrixFromDMatrix(matrix, sup_matrix);
        } else {
            double[][] temp_matrix = new double[matrix.length + 1][25];
            System.arraycopy(matrix, 0, temp_matrix, 0, matrix.length);
            for (int i = 0; i < 25; i++) {
                temp_matrix[temp_matrix.length - 1][i] = 0;
            }

            sup_matrix = new double[(matrix.length) / 2 + 1][125];
            makeSDMatrixFromDMatrix(temp_matrix, sup_matrix);
        }

        return new SDPWM(sup_matrix, original_dpwm.motif_length(), original_dpwm.lengthOfNaiveMotifIsEven());
    }

    static void makeSDMatrixFromDMatrix(double[][] matrix, double[][] sup_matrix) {

        for (int l = 0; l < (matrix.length / 2); l++) {

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
    public SDPWM revcomp() {
        double[][] matrix = this.matrix;
        double[][] new_matrix = new double[matrix.length][125];

        for (int k = 0; k < matrix.length; k++) {
            for (int j = 0; j < 125; j++) {
                new_matrix[matrix.length - 1 - k][j] = matrix[k][Assistant.sdComplimentaryElements[j]];
            }
        }

        return new SDPWM(new_matrix, motifLength, lengthOfNaiveMotifIsEven());
    }

    @Override
    public int motif_length() {
        return motifLength;
    }
}
