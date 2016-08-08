package ru.autosome;

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

      if (Arrays.asList(args).contains("skipn") || Arrays.asList(args).contains("nskip")) N_isPermitted = false;
      if (Arrays.asList(args).contains("suppress")) suppressNames = true;
      if (Arrays.asList(args).contains("transpose")) transpose = true;

      ArrayList<String> namesOfSequences = new ArrayList<String>();
      ArrayList<String> sequences = Assistant.readFastaFile(args[0], namesOfSequences);

      if (!(Arrays.asList(args).contains("naive"))) {

        PWM sm_pwm = SMPWM.readSMPWM(args[1], N_isPermitted, transpose);
        PWM revcomp_sm_pwm = sm_pwm.revcomp();

        // replace with fake pwm if 1-strand search is used
        if (Arrays.asList(args).contains("direct") || Arrays.asList(args).contains("forward")) {
          revcomp_sm_pwm = PWM.makeDummy(revcomp_sm_pwm.length());
        } else {
          if (Arrays.asList(args).contains("revcomp") || Arrays.asList(args).contains("reverse")) {
            sm_pwm = PWM.makeDummy(sm_pwm.length());
          }
        }

        for (int s = 0; s < sequences.size(); s++) {

          if (!suppressNames) System.out.println(">" + namesOfSequences.get(s));

          SMSequence sm_seq = SMSequence.sequenceFromString(sequences.get(s));

          if (args[2].matches("besthit")) {
            sm_seq.bestHit(sm_pwm, revcomp_sm_pwm);
          } else {
            double threshold = Double.parseDouble(args[2]);
            sm_seq.scan(sm_pwm, revcomp_sm_pwm, threshold);
          }

        }
      } else {

        PWM mpwm = MPWM.readMPWM(args[1], N_isPermitted, transpose);
        PWM revcomp_mpwm = mpwm.revcomp();

        // replace with fake pwm if 1-strand search is used
        if (Arrays.asList(args).contains("direct")) {
          revcomp_mpwm = PWM.makeDummy(revcomp_mpwm.length());
        } else {
          if (Arrays.asList(args).contains("revcomp")) {
            mpwm = PWM.makeDummy(mpwm.length());
          }
        }

        for (int s = 0; s < sequences.size(); s++) {

          if (!suppressNames) System.out.println(">" + namesOfSequences.get(s));

          MSequence mseq = MSequence.sequenceFromString(sequences.get(s));

          if (args[2].matches("besthit")) {
            mseq.bestHit(mpwm, revcomp_mpwm);
          } else {
            double threshold = Double.parseDouble(args[2]);
            mseq.scan(mpwm, revcomp_mpwm, threshold);
          }

        }
      }
    } else {
      System.err.println("SPRY-SARUS command line: <sequences.multifasta> <weight.matrix> <threshold>|besthit [suppress] [direct] [revcomp] [skipn] [transpose]");

    }

  }
}
