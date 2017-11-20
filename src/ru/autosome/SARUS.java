package ru.autosome;

import ru.autosome.motifModel.PWM;
import ru.autosome.motifModel.mono.MPWM;
import ru.autosome.motifModel.mono.SMPWM;
import ru.autosome.sequenceModel.Sequence;
import ru.autosome.sequenceModel.mono.MSequence;
import ru.autosome.sequenceModel.mono.SMSequence;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SARUS {

  //matrix: first string - name; row - position, column - nucleotide

  public static void main(String[] args) throws IOException {
    String helpString =
        "SPRY-SARUS command line: <sequences.multifasta> <weight.matrix> <threshold>|besthit [suppress] [direct] [revcomp] [skipn] [transpose]\n" +
            "Options:\n" +
            "  [--pvalues-file FILE] - specify PWM score <--> P-value conversion\n" +
            "  [--output-scoring-mode MODE] - output score/pvalue/logpvalue (default: score). logpvalue := -log10(P-value)\n" +
            "  [--threshold-mode MODE] - threshold specified for score/pvalue/logpvalue (default: score). logpvalue := -log10(P-value)\n" +
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

    PvalueBsearchList pvalueBsearchList = null; // scores <--> Pvalues mapping
    ScoreType outputScoringModel = ScoreType.SCORE; // output site strength as score, pvalue or logpvalue
    ScoreType inputScoringModel = ScoreType.SCORE; // site strength threshold as score, pvalue or logpvalue

    Integer precision = null; // round scores (or P-values) in output

    boolean outputAsBed = false;
    String motifName;

    if (argsList.contains("--output-scoring-mode")) {
      int arg_index = argsList.indexOf("--output-scoring-mode");
      outputScoringModel = ScoreType.valueOf( argsList.get(arg_index + 1).toUpperCase() );
    }

    if (argsList.contains("--threshold-mode")) {
      int arg_index = argsList.indexOf("--threshold-mode");
      inputScoringModel = ScoreType.valueOf( argsList.get(arg_index + 1).toUpperCase() );
    }


    if (argsList.contains("--pvalues-file")) {
      int arg_index = argsList.indexOf("--pvalues-file");
      String threshold_pvalues_filename = argsList.get(arg_index + 1);
      File threshold_pvalues_file = new File(threshold_pvalues_filename);
      pvalueBsearchList = PvalueBsearchList.load_from_file(threshold_pvalues_file);
    }

    if (pvalueBsearchList != null && outputScoringModel == ScoreType.SCORE && inputScoringModel == ScoreType.SCORE) {
      System.err.println("Warning! Score-pvalue conversion is specified but not used for neither output scores nor threshold score.");
    }
    if (pvalueBsearchList == null && !(outputScoringModel == ScoreType.SCORE && inputScoringModel == ScoreType.SCORE)) {
      System.err.println("Error! Score <--> P-value conversion not specified but P-value is used for output or threshold.\n" +
                         "Use `--pvalues-file FILE` option.");
      System.exit(1);
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

    ScoreFormatter scoreFormatter = new ScoreFormatter(precision, outputScoringModel, pvalueBsearchList);

    PWM pwm, revcomp_pwm;
    if (naive) {
      pwm = MPWM.readMPWM(pwm_filename, N_isPermitted, transpose);
    } else {
      pwm = SMPWM.readSMPWM(pwm_filename, N_isPermitted, transpose);
    }
    revcomp_pwm = pwm.revcomp();

    ResultFormatter formatter;
    if (!outputAsBed) {
      formatter = new SarusResultFormatter(scoreFormatter);
    } else {
      // it is fictious formatter which is never used (it's overwritten at each new sequence)
      formatter = new BedResultFormatter(scoreFormatter, "chr", 0, motifName, pwm.motif_length());
    }

    // replace with fake pwm if 1-strand search is used
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
        seq = MSequence.sequenceFromString(namedSequence.getSequence());
      } else {
        seq = SMSequence.sequenceFromString(namedSequence.getSequence());
      }

      if (args[2].matches("besthit")) {
        seq.bestHit(pwm, revcomp_pwm, formatter);
      } else {
        double threshold = inputScoringModel.valueInScoreUnits(Double.parseDouble(args[2]), pvalueBsearchList);
        seq.scan(pwm, revcomp_pwm, threshold, formatter);
      }
    }
  }
}
