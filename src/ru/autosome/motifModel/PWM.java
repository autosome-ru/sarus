package ru.autosome.motifModel;

import ru.autosome.sequenceModel.Sequence;

//matrix: first string - name; row - position, column - nucleotide
public abstract class PWM {

    public final double[][] matrix;

    public PWM(double[][] matrix) {
        this.matrix = matrix;
    }

    public abstract double score(Sequence seq, int position);

    public abstract PWM revcomp();

    public int matrix_length() {
        return matrix.length;
    }

    abstract public int motif_length(); // length of matched sequence in nucleotides

    public static PWM makeDummy(int length) {
        return new Dummy(length);
    }

    private static class Dummy extends PWM {

        public Dummy(int length) {
            super(new double[length][]);
        }

        @Override
        public int motif_length() {
            throw new RuntimeException("cannot return motif length of the Dummy PWM");
        }

        @Override
        public double score(Sequence seq, int position) {
            return Double.NEGATIVE_INFINITY;
        }

        @Override
        public PWM revcomp() {
            throw new RuntimeException("cannot do a reverse complementary transformation of the Dummy PWM");
        }
    }

}
