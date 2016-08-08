package ru.autosome;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: nastia
 * Date: 27.02.14
 * Time: 16:57
 * To change this template use File | Settings | File Templates.
 */
public class MPWM extends PWM {

  static boolean lengthOfMPWMIsEven;

  MPWM(double[][] matrix) {
    super(matrix);
  }

  @Override
  public double score(Sequence seq, int position) {

    if (seq.getClass() != MSequence.class)
      throw new RuntimeException();

    double score = 0.0;

    for (int k = 0; k < this.matrix.length; k++) {

      byte letter = seq.sequence[position + k];

      score += this.matrix[k][letter];
    }
    return score;
  }

  static MPWM readMPWM(String path, boolean N_isPermitted, boolean transpose) throws IOException {

    ArrayList<String> strings = Assistant.load(path);
    ArrayList<Double[]> parsed = Assistant.parseMono(strings, transpose);

    int len = parsed.size();
    lengthOfMPWMIsEven = (len % 2 == 0);

    double[][] resultPWM = new double[len][5];

    for (int i = 0; i < len; i++) {
//      String[] line = ((strings.get(i)).split(" |\t"));
      Double[] line = parsed.get(i);

      for (int j = 0; j < 4; j++) {

        resultPWM[i][j] = line[j]; //Double.parseDouble(line[j]);
      }

      if (N_isPermitted) {
        resultPWM[i][4] = 0.0;
      } else {
        resultPWM[i][4] = Double.NEGATIVE_INFINITY;
      }

    }
    return new MPWM(resultPWM);
  }

  @Override
  public MPWM revcomp() {
    if (this.getClass() != MPWM.class)
      throw new RuntimeException();

    double[][] new_matrix = new double[this.matrix.length][5];

    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < 4; j++) {
        new_matrix[matrix.length - 1 - i][j] = matrix[i][3 - j];
      }
      new_matrix[matrix.length - 1 - i][4] = matrix[i][4];
    }

    return new MPWM(new_matrix);

  }

}
