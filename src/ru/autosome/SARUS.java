package ru.autosome;

import ru.autosome.motifModel.PWM;
import ru.autosome.motifModel.mono.MPWM;
import ru.autosome.motifModel.mono.SMPWM;
import ru.autosome.sequenceModel.Sequence;
import ru.autosome.sequenceModel.mono.MSequence;
import ru.autosome.sequenceModel.mono.SMSequence;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SARUS {

  //matrix: first string - name; row - position, column - nucleotide

  public static void main(String[] args) throws IOException {
    if (args.length < 3) {
      System.err.println("SPRY-SARUS command line: <sequences.multifasta> <weight.matrix> <threshold>|besthit [suppress] [direct] [revcomp] [skipn] [transpose]");
      System.exit(1);
    }

    List<String> argsList = Arrays.asList(args);
    boolean N_isPermitted = !(argsList.contains("skipn") || argsList.contains("nskip"));
    boolean suppressNames = argsList.contains("suppress");
    boolean transpose = argsList.contains("transpose");
    boolean naive = argsList.contains("naive");
    boolean only_direct = (argsList.contains("direct") || argsList.contains("forward"));
    boolean only_revcomp = (argsList.contains("revcomp") || argsList.contains("reverse"));

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

    for (NamedSequence namedSequence: FastaReader.fromFile(fasta_filename)) {
      if (!suppressNames) {
        System.out.println(">" + namedSequence.getName());
      }

      Sequence seq;
      if (naive) {
        seq = MSequence.sequenceFromString(namedSequence.getSequence());
      } else {
        seq = SMSequence.sequenceFromString(namedSequence.getSequence());
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
