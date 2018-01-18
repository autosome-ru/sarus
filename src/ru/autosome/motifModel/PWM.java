package ru.autosome.motifModel;

import ru.autosome.sequenceModel.Sequence;

//matrix: first string - name; row - position, column - nucleotide
public abstract class PWM {

    public final double[][] matrix;
    private final boolean _lengthOfNaiveMotifIsEven;

    public PWM(double[][] matrix, boolean lengthOfNaiveMotifIsEven) {
        this.matrix = matrix;
        this._lengthOfNaiveMotifIsEven = lengthOfNaiveMotifIsEven;
    }

    public boolean lengthOfNaiveMotifIsEven() {
        return _lengthOfNaiveMotifIsEven;
    }
    public abstract double score(Sequence seq, int position);

    public abstract PWM revcomp();

    public int matrix_length() {
        return matrix.length;
    }

    abstract public int motif_length(); // length of matched sequence in nucleotides

    public static PWM makeDummy(int length, boolean lengthOfNaiveMotifIsEven) {
        return new Dummy(length, lengthOfNaiveMotifIsEven);
    }

    private static class Dummy extends PWM {

        public Dummy(int length, boolean lengthOfNaiveMotifIsEven) {
            super(new double[length][], lengthOfNaiveMotifIsEven);
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
