package ru.autosome.di;

import ru.autosome.*;
import ru.autosome.motifModel.PWM;
import ru.autosome.motifModel.di.DPWM;
import ru.autosome.motifModel.di.SDPWM;
import ru.autosome.sequenceModel.Sequence;
import ru.autosome.sequenceModel.di.DSequence;
import ru.autosome.sequenceModel.di.SDSequence;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SARUS {

  //matrix: first string - name; row - position, column - nucleotide

  public static void main(String[] args) throws IOException {
    String helpString =
            "di.SPRY-SARUS command line: <sequences.multifasta> <dinucleotide.matrix> <threshold>|besthit [suppress] [direct] [revcomp] [skipn] [transpose]\n" +
            "Options:\n" +
            "  [--pvalues-file FILE] - specify PWM score <--> P-value conversion\n" +
            "  [--output-pvalues] - output P-values instead of scores\n" +
            "  [--pvalue-as-threshold] - threshold specified for P-value, not for score\n" +
            "  [--use-log-scale] - convert P-values into -log10 scale (both for input and output)\n" +
            "  [--precision N] - round result (either score or P-value) up to N digits after floating point\n" +
            "  [--output-bed] - format output results in BED-6. FASTA headers should be like chr1:23 (or chr1:23-45 or chr1:23..45,+)\n" +
            "  [--motif-name] - motif name goes in 4-th column in BED-6 format. By default is inferred from PWM filename\n";

    List<String> argsList = Arrays.asList(args);
    if (argsList.contains("-h") || argsList.contains("--help")) {
      System.err.print(helpString);
      System.exit(0);
    }

    if (args.length < 3) {
      System.err.print(helpString);
      System.exit(1);
    }

    boolean N_isPermitted = !(argsList.contains("skipn") || argsList.contains("nskip"));
    boolean suppressNames = argsList.contains("suppress");
    boolean transpose = argsList.contains("transpose");
    boolean naive = argsList.contains("naive");
    boolean only_direct = (argsList.contains("direct") || argsList.contains("forward"));
    boolean only_revcomp = (argsList.contains("revcomp") || argsList.contains("reverse"));

    PvalueBsearchList pvalueBsearchList = null; // scores --> Pvalues
    boolean outputPvalues = false; // output scores as Pvalues (or logPvalues)
    boolean pvalueAsThreshold = false; // measure specified threshold in Pvalue (or logPvalue)
    boolean useLogScale = false; // output Pvalues into -log10 scale
    Integer precision = null; // round scores (or P-values) in output

    boolean outputAsBed = false;
    String motifName;

    if (argsList.contains("--pvalues-file")) {
      int arg_index = argsList.indexOf("--pvalues-file");
      String threshold_pvalues_filename = argsList.get(arg_index + 1);
      File threshold_pvalues_file = new File(threshold_pvalues_filename);
      pvalueBsearchList = PvalueBsearchList.load_from_file(threshold_pvalues_file);

      if (argsList.contains("--output-pvalues")) {
        outputPvalues = true;
      }

      if (argsList.contains("--pvalue-as-threshold")) {
        pvalueAsThreshold = true;
      }

      if (argsList.contains("--use-log-scale")) {
        useLogScale = true;
      }

      if (!outputPvalues && !pvalueAsThreshold) {
        System.err.println("Error! Score-pvalue conversion is specified but not used for neither output (`--output-pvalues`) nor input (`--pvalue-as-threshold`).");
        System.exit(1);
      }
    } else { // score <--> pvalue conversion not specified
      if (argsList.contains("--output-pvalues") || argsList.contains("--pvalue-as-threshold") || argsList.contains("--use-log-scale")) {
        System.err.println("Error! Score <--> P-value conversion not specified but options using P-value are used.\n" +
                           "If you want to use any one of `--output-pvalues` or `--pvalue-as-threshold` or `--use-log-scale`,\n" +
                           "use also `--pvalues-file` option.");
        System.exit(1);
      }
    }

    if (argsList.contains("--precision")) {
      int arg_index = argsList.indexOf("--precision");
      precision = Integer.valueOf(argsList.get(arg_index + 1));
    }

    String fasta_filename = args[0];
    String pwm_filename = args[1];

    motifName = utils.fileBasename(pwm_filename);

    if (argsList.contains("--output-bed")) {
      outputAsBed = true;
      suppressNames = true;
      if (argsList.contains("--motif-name")) {
        int arg_index = argsList.indexOf("--motif-name");
        motifName = argsList.get(arg_index + 1);
      }
    }

    ScoreFormatter scoreFormatter;
    if (outputPvalues) {
      scoreFormatter = new ScoreFormatter(precision, pvalueBsearchList, useLogScale);
    } else {
      scoreFormatter = new ScoreFormatter(precision, null, false);
    }


    PWM pwm, revcomp_pwm;
    if (naive) {
      pwm = DPWM.readDPWM(pwm_filename, N_isPermitted, transpose);
    } else {
      pwm = SDPWM.readSDPWM(pwm_filename, N_isPermitted, transpose);
    }
    revcomp_pwm = pwm.revcomp();

    ResultFormatter formatter;
    if (!outputAsBed) {
      formatter = new SarusResultFormatter(scoreFormatter);
    } else {
      formatter = new BedResultFormatter(scoreFormatter, "chr", 0, motifName, pwm.motif_length());
    }

    if (only_direct && only_revcomp) {
      throw new IllegalArgumentException("Only-direct and only-revcomp modes are specified simultaneously");
    } else if (only_direct) {
      revcomp_pwm = PWM.makeDummy(revcomp_pwm.matrix_length());
    } else if (only_revcomp) {
      pwm = PWM.makeDummy(pwm.matrix_length());
    }

    for (NamedSequence namedSequence: FastaReader.fromFile(fasta_filename)) {
      if (outputAsBed) {
        utils.IntervalStartCoordinate intervalCoordinate = utils.IntervalStartCoordinate.fromIntervalNotation(namedSequence.getName());
        formatter = new BedResultFormatter(scoreFormatter, intervalCoordinate.chromosome, intervalCoordinate.startPos, motifName, pwm.motif_length());
      }

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
        double threshold;
        if (pvalueAsThreshold) { // threshold for P-value not for score
          double pvalue;
          if (useLogScale) {
            double logPvalue = Double.parseDouble(args[2]);
            pvalue = Math.pow(10.0, -logPvalue);
          } else {
            pvalue = Double.parseDouble(args[2]);
          }
          threshold = pvalueBsearchList.threshold_by_pvalue(pvalue);
        } else {
          threshold = Double.parseDouble(args[2]);
        }
        seq.scan(pwm, revcomp_pwm, threshold, formatter);
      }
    }
  }
}
