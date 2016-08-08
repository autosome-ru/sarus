package ru.autosome;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: nastia
 * Date: 16.04.14
 * Time: 12:27
 * To change this template use File | Settings | File Templates.
 */
public class Test {

  public static void main(String[] args) throws IOException {

    boolean N_isPermitted = true;
    boolean transpose = false;

    if (args.length >= 3) {

      double threshold = Double.parseDouble(args[2]);

      if (Arrays.asList(args).contains("false")) N_isPermitted = false;
      if (Arrays.asList(args).contains("transpose")) transpose = true;

      ArrayList<String> namesOfSequences = new ArrayList<String>();
      ArrayList<String> sequences = Assistant.readFastaFile(args[0], namesOfSequences);


      if (Arrays.asList(args).contains("super")) {

        SDPWM sd_pwm = SDPWM.readSDPWM(args[1], N_isPermitted, transpose);
        SDPWM revcomp_sd_pwm = sd_pwm.revcomp();


        for (int s = 0; s < sequences.size(); s++) {

          System.err.println(namesOfSequences.get(s));

          SDSequence sd_seq = SDSequence.sequenceFromString(sequences.get(s));

          StopWatch watch = new StopWatch();
          sd_seq.scan(sd_pwm, revcomp_sd_pwm, threshold);
          double time = watch.elapsedTime();
          System.out.println(time);

        }

      } else {

        DPWM d_pwm = DPWM.readDPWM(args[1], N_isPermitted, transpose);
        DPWM revcomp_d_pwm = d_pwm.revcomp();

        for (int s = 0; s < sequences.size(); s++) {

          System.err.println(namesOfSequences.get(s));

          DSequence seq = DSequence.sequenceFromString(sequences.get(s));

          StopWatch watch = new StopWatch();
          seq.scan(d_pwm, revcomp_d_pwm, threshold);
          double time = watch.elapsedTime();
          System.out.println(time);

        }
      }

    } else {
      System.err.println("usage:<sequence><matrix><threshold><file_output><options> ");
    }

  }

}
