package ru.autosome.di;

import ru.autosome.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class SARUS {

  //matrix: first string - name; row - position, column - nucleotide

  public static void main(String[] args) throws IOException {

    boolean N_isPermitted = true;
    boolean suppressNames = false;
    boolean transpose = false;

    if (args.length >= 3) {

      if (Arrays.asList(args).contains("skipn")) N_isPermitted = false;
      if (Arrays.asList(args).contains("suppress")) suppressNames = true;
      if (Arrays.asList(args).contains("transpose")) transpose = true;

      ArrayList<String> namesOfSequences = new ArrayList<String>();
      ArrayList<String> sequences = Assistant.readFastaFile(args[0], namesOfSequences);

      if (!(Arrays.asList(args).contains("naive"))) {

        PWM sd_pwm = SDPWM.readSDPWM(args[1], N_isPermitted, transpose);
        PWM revcomp_sd_pwm = sd_pwm.revcomp();

        if (Arrays.asList(args).contains("direct") || Arrays.asList(args).contains("forward")) {
          revcomp_sd_pwm = PWM.makeDummy(revcomp_sd_pwm.length());
        } else {
          if (Arrays.asList(args).contains("revcomp") || Arrays.asList(args).contains("reverse")) {
            sd_pwm = PWM.makeDummy(sd_pwm.length());
          }
        }

        for (int s = 0; s < sequences.size(); s++) {

          if (!suppressNames) System.out.println(">" + namesOfSequences.get(s));

          SDSequence sd_seq = SDSequence.sequenceFromString(sequences.get(s));

          if (args[2].matches("besthit")) {
            sd_seq.bestHit(sd_pwm, revcomp_sd_pwm);
          } else {
            double threshold = Double.parseDouble(args[2]);
            sd_seq.scan(sd_pwm, revcomp_sd_pwm, threshold);
          }
        }

      } else {

        PWM d_pwm = DPWM.readDPWM(args[1], N_isPermitted, transpose);
        PWM revcomp_d_pwm = d_pwm.revcomp();

        if (Arrays.asList(args).contains("direct")) {
          revcomp_d_pwm = PWM.makeDummy(revcomp_d_pwm.length());
        } else {
          if (Arrays.asList(args).contains("revcomp")) {
            d_pwm = PWM.makeDummy(d_pwm.length());
          }
        }

        for (int s = 0; s < sequences.size(); s++) {

          if (!suppressNames) System.out.println(">" + namesOfSequences.get(s));

          DSequence seq = DSequence.sequenceFromString(sequences.get(s));

          if (args[2].matches("besthit")) {
            seq.bestHit(d_pwm, revcomp_d_pwm);
          } else {
            double threshold = Double.parseDouble(args[2]);
            seq.scan(d_pwm, revcomp_d_pwm, threshold);
          }

        }
      }

    } else {
      System.err.println("di.SPRY-SARUS command line: <sequences.multifasta> <dinucleotide.matrix> <threshold>|besthit [suppress] [direct] [revcomp] [skipn] [transpose]");

    }

  }
}
