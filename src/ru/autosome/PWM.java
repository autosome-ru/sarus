package ru.autosome;

public abstract class PWM {

  double[][] matrix;

  public PWM(double[][] matrix) {
    this.matrix = matrix;
  }

  public abstract double score(Sequence seq, int position);

  public abstract PWM revcomp();

  public int length() {
    return matrix.length;
  }

  public static PWM makeDummy(int length) {
    return new Dummy(length);
  }

  private static class Dummy extends PWM {

    public Dummy(int length) {
      super(new double[length][]);
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
