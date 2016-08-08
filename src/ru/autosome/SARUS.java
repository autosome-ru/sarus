package ru.autosome;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class SARUS {

  //matrix: first string - name; row - position, column - nucleotide

  public static void main(String[] args) throws IOException {
    if (args.length < 3) {
      System.err.println("SPRY-SARUS command line: <sequences.multifasta> <weight.matrix> <threshold>|besthit [suppress] [direct] [revcomp] [skipn] [transpose]");
      System.exit(1);
    }

    boolean N_isPermitted = !(Arrays.asList(args).contains("skipn") || Arrays.asList(args).contains("nskip"));
    boolean suppressNames = Arrays.asList(args).contains("suppress");
    boolean transpose = Arrays.asList(args).contains("transpose");
    boolean naive = Arrays.asList(args).contains("naive");
    boolean only_direct = (Arrays.asList(args).contains("direct") || Arrays.asList(args).contains("forward"));
    boolean only_revcomp = (Arrays.asList(args).contains("revcomp") || Arrays.asList(args).contains("reverse"));

    String fasta_filename = args[0];
    String pwm_filename = args[1];

    PWM pwm, revcomp_pwm;
    if (naive) {
      pwm = MPWM.readMPWM(pwm_filename, N_isPermitted, transpose);
    } else {
      pwm = SMPWM.readSMPWM(pwm_filename, N_isPermitted, transpose);
    }
    revcomp_pwm = pwm.revcomp();

    // replace with fake pwm if 1-strand search is used
    if (only_direct && only_revcomp) {
      throw new IllegalArgumentException("Only-direct and only-revcomp modes are specified simultaneously");
    } else if (only_direct) {
      revcomp_pwm = PWM.makeDummy(revcomp_pwm.length());
    } else if (only_revcomp) {
      pwm = PWM.makeDummy(pwm.length());
    }

    ArrayList<String> namesOfSequences = new ArrayList<String>();
    ArrayList<String> sequences = Assistant.readFastaFile(fasta_filename, namesOfSequences);

    for (int s = 0; s < sequences.size(); s++) {
      if (!suppressNames) {
        System.out.println(">" + namesOfSequences.get(s));
      }

      Sequence seq;
      if (naive) {
        seq = MSequence.sequenceFromString(sequences.get(s));
      } else {
        seq = SMSequence.sequenceFromString(sequences.get(s));
      }

      if (args[2].matches("besthit")) {
        seq.bestHit(pwm, revcomp_pwm);
      } else {
        double threshold = Double.parseDouble(args[2]);
        seq.scan(pwm, revcomp_pwm, threshold);
      }
    }
  }
}
