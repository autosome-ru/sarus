package ru.autosome.di;

import ru.autosome.FastaReader;
import ru.autosome.NamedSequence;
import ru.autosome.PvalueBsearchList;
import ru.autosome.ResultFormatter;
import ru.autosome.motifModel.PWM;
import ru.autosome.motifModel.di.DPWM;
import ru.autosome.motifModel.di.SDPWM;
import ru.autosome.sequenceModel.Sequence;
import ru.autosome.sequenceModel.di.DSequence;
import ru.autosome.sequenceModel.di.SDSequence;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class SARUS {

  //matrix: first string - name; row - position, column - nucleotide

  public static void main(String[] args) throws IOException {
    String helpString =
            "di.SPRY-SARUS command line: <sequences.multifasta> <dinucleotide.matrix> <threshold>|besthit [suppress] [direct] [revcomp] [skipn] [transpose]\n" +
            "Options:\n" +
            "  [--pvalues-file FILE] - convert PWM scores into P-values using precalculated score <--> P-value mapping\n" +
            "  [--use-log-scale] - convert P-values in log10 scale\n" +
            "  [--precision N] - round result (either score or P-value) up to N digits after floating point\n";
    if (args.length < 3) {
      System.err.print(helpString);
      System.exit(1);
    }

    List<String> argsList = Arrays.asList(args);
    boolean N_isPermitted = !(argsList.contains("skipn") || argsList.contains("nskip"));
    boolean suppressNames = argsList.contains("suppress");
    boolean transpose = argsList.contains("transpose");
    boolean naive = argsList.contains("naive");
    boolean only_direct = (argsList.contains("direct") || argsList.contains("forward"));
    boolean only_revcomp = (argsList.contains("revcomp") || argsList.contains("reverse"));

    PvalueBsearchList pvalueBsearchList = null; // scores --> Pvalues
    boolean useLogScale = false; // output Pvalues in log10 scale
    Integer precision = null;

    if (argsList.contains("--pvalues-file")) {
      int arg_index = argsList.indexOf("--pvalues-file");
      String threshold_pvalues_filename = argsList.get(arg_index + 1);
      File threshold_pvalues_file = new File(threshold_pvalues_filename);
      pvalueBsearchList = PvalueBsearchList.load_from_file(threshold_pvalues_file);
    }
    if (argsList.contains("--use-log-scale")) {
      if (pvalueBsearchList == null) {
        System.err.println("Error! Log-scale can be used only when scores are converted to P-values");
        System.exit(1);
      }
      useLogScale = true;
    }
    if (argsList.contains("--precision")) {
      int arg_index = argsList.indexOf("--precision");
      precision = Integer.valueOf(argsList.get(arg_index + 1));
    }

    ResultFormatter formatter = new ResultFormatter(precision, pvalueBsearchList, useLogScale);
    String fasta_filename = args[0];
    String pwm_filename = args[1];

    PWM pwm, revcomp_pwm;
    if (naive) {
      pwm = DPWM.readDPWM(pwm_filename, N_isPermitted, transpose);
    } else {
      pwm = SDPWM.readSDPWM(pwm_filename, N_isPermitted, transpose);
    }
    revcomp_pwm = pwm.revcomp();

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
        seq = DSequence.sequenceFromString(namedSequence.getSequence());
      } else {
        seq = SDSequence.sequenceFromString(namedSequence.getSequence());
      }

      if (args[2].matches("besthit")) {
        seq.bestHit(pwm, revcomp_pwm, formatter);
      } else {
        double threshold = Double.parseDouble(args[2]);
        seq.scan(pwm, revcomp_pwm, threshold, formatter);
      }
    }
  }
}
