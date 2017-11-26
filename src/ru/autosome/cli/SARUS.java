package ru.autosome.cli;

import ru.autosome.*;
import ru.autosome.motifModel.PWM;
import ru.autosome.sequenceModel.Sequence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class SARUS {
  protected boolean N_isPermitted;
  protected boolean suppressNames;
  protected boolean transpose;
  protected boolean naive;
  protected boolean only_direct, only_revcomp;

  protected PvalueBsearchList pvalueBsearchList; // scores <--> Pvalues mapping
  protected ScoreType outputScoringModel; // output site strength as score, pvalue or logpvalue
  protected ScoreType inputScoringModel; // site strength threshold as score, pvalue or logpvalue

  protected Integer precision; // round scores (or P-values) in output
  protected boolean outputAsBed;
  protected String motifName;
  protected boolean show_non_matching;
  protected int flankLength;

  protected String fasta_filename;
  protected String pwm_filename;
  protected String thresholdOrBesthit;

  protected PWM pwm, revcomp_pwm;
  protected ScoreFormatter scoreFormatter;

  public abstract String classNameHelp();
  public abstract String rowContentHelp();
  public abstract String precalculateThresholdsPkgHelp();
  public String helpString() {
    return "Usage:\n" +
        "java -cp " + classNameHelp() + " <sequences.multifasta> <weight matrix> <threshold> [options]\n" +
        "  or\n" +
        "java -cp " + classNameHelp() + " <sequences.multifasta> <weight matrix> besthit [options]\n" +
        "Options:\n" +
        "  [--transpose] - use transposed PWM (rows correspond to " + rowContentHelp() +", columns are positions)\n" +
        "  [--pvalues-file FILE] - specify PWM score <--> P-value conversion.\n" +
        "                          Use " + precalculateThresholdsPkgHelp() + " from `ape.jar` to calculate this file\n" +
        "  [--output-scoring-mode MODE] - output score/pvalue/logpvalue (default: score).\n" +
        "                                 logpvalue := -log10(P-value)\n" +
        "  [--threshold-mode MODE] - threshold specified for score/pvalue/logpvalue (default: score).\n" +
        "  [--output-bed] - format output results in BED-6.\n" +
        "                   FASTA headers should be like chr1:23 (or chr1:23-45 or chr1:23..45,+)\n" +
        "                   `bedtools getfasta` generates headers in matching format\n" +
        "  [--precision N] - round result (either score or P-value) up to N digits after floating point\n" +
        "  [--direct|--revcomp] - scan only direct/revcomp strand of DNA\n" +
        "  [--motif-name] - motif name goes in 4-th column in BED-6 format. By default is inferred from PWM filename\n" +
        "  [--skipn] - Skip words with N-nucleotides.\n" +
        "  [--naive] - Don't use superalphabet-based scoring algorithm\n" +
        "  [--[no-]suppress] - Don't print sequence names (by default suppressed when output in BED-format)\n" +
        "  [--dont-add-flanks] - By default polyN-flanks are added to a sequence so that long motif could match short sequence.\n" +
        "                        This option disables sequence expansion. Thus, there can be too short sequences with no besthit.\n" +
        "  [--show-non-matching] - Output fictive result for besthit when motif is wider than sequence\n";
  }

  abstract public PWM loadPWM() throws IOException;
  abstract public Sequence convertSequence(String sequence);

  public void setupFromArglist(ArrayList<String> argsList) throws IOException {
    if (argsList.contains("-h") || argsList.contains("--help")) {
      System.err.print(helpString());
      System.exit(0);
    }

    this.N_isPermitted = !(argsList.remove("--skipn") || argsList.remove("--nskip") || argsList.remove("skipn") || argsList.remove("nskip"));
    this.suppressNames = false;
    this.transpose = argsList.remove("--transpose") || argsList.remove("transpose");
    this.naive = argsList.remove("--naive") || argsList.remove("naive");
    this.only_direct = (argsList.remove("--direct") || argsList.remove("--forward") || argsList.remove("direct") || argsList.remove("forward"));
    this.only_revcomp = (argsList.remove("--revcomp") || argsList.remove("--reverse") || argsList.remove("revcomp") || argsList.remove("reverse"));

    this.pvalueBsearchList = null; // scores <--> Pvalues mapping
    this.outputScoringModel = ScoreType.SCORE; // output site strength as score, pvalue or logpvalue
    this.inputScoringModel = ScoreType.SCORE; // site strength threshold as score, pvalue or logpvalue

    this.precision = null; // round scores (or P-values) in output
    this.outputAsBed = false;
    this.motifName = null;
    this.show_non_matching = false;

    if (argsList.contains("--output-scoring-mode")) {
      int arg_index = argsList.indexOf("--output-scoring-mode");
      this.outputScoringModel = ScoreType.valueOf( argsList.remove(arg_index + 1).toUpperCase() );
      argsList.remove(arg_index);
    }

    if (argsList.contains("--threshold-mode")) {
      int arg_index = argsList.indexOf("--threshold-mode");
      this.inputScoringModel = ScoreType.valueOf( argsList.remove(arg_index + 1).toUpperCase() );
      argsList.remove(arg_index);
    }

    if (argsList.contains("--pvalues-file")) {
      int arg_index = argsList.indexOf("--pvalues-file");
      String threshold_pvalues_filename = argsList.remove(arg_index + 1);
      argsList.remove(arg_index);
      File threshold_pvalues_file = new File(threshold_pvalues_filename);
      this.pvalueBsearchList = PvalueBsearchList.load_from_file(threshold_pvalues_file);
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
      this.precision = Integer.valueOf(argsList.remove(arg_index + 1));
      argsList.remove(arg_index);
    }

    if (argsList.remove("--suppress")) {
      this.suppressNames = true;
    }
    if (argsList.remove("--show-non-matching")) {
      this.show_non_matching = true;
    }

    if (argsList.remove("--output-bed")) {
      this.outputAsBed = true;
      this.suppressNames = !argsList.remove("--no-suppress");
      if (argsList.contains("--motif-name")) {
        int arg_index = argsList.indexOf("--motif-name");
        this.motifName = argsList.remove(arg_index + 1);
        argsList.remove(arg_index);
      }
    }

    boolean addFlanks = true;
    if (argsList.remove("--dont-add-flanks")) {
      addFlanks = false;
    }

    if (argsList.size() != 3) {
      System.err.print(
          "Error: some arguments or parameters not recognized.\n" +
          "Please check the following arguments (there must be 3 required arguments and some non-recognized options:\n");
      List<String> requiredArgs = argsList.subList(0, (argsList.size() < 3) ? argsList.size() : 3);
      System.err.println("  Required (" + requiredArgs.size() + " of 3):");
      System.err.println(String.join("\n", requiredArgs.stream().map(arg -> "*\t" + arg).collect(Collectors.toList())));
      if (argsList.size() > 3) {
        List<String> optionalArgs = argsList.subList(3, argsList.size());
        System.err.println("  Optional:");
        System.err.println(String.join("\n", optionalArgs.stream().map(arg -> "*\t" + arg).collect(Collectors.toList())));
      }
      System.err.println();
      System.err.print(helpString());
      System.exit(1);
    }

    if (addFlanks && show_non_matching) {
      System.err.println("Warning! `--show-non-matching` does nothing when polyN-flanks are added. Take a look at `--dont-add-flanks` options.");
    }

    this.fasta_filename = argsList.get(0);
    this.pwm_filename = argsList.get(1);
    this.thresholdOrBesthit = argsList.get(2);
    if (motifName == null) {
      this.motifName = utils.fileBasename(pwm_filename);
    }

    this.scoreFormatter = new ScoreFormatter(precision, outputScoringModel, pvalueBsearchList);

    this.pwm = loadPWM();
    this.revcomp_pwm = pwm.revcomp();

    if (addFlanks) {
      this.flankLength = pwm.motif_length();
    } else {
      this.flankLength = 0;
    }

    // replace with fake pwm if 1-strand search is used
    if (only_direct && only_revcomp) {
      throw new IllegalArgumentException("Only-direct and only-revcomp modes are specified simultaneously");
    } else if (only_direct) {
      this.revcomp_pwm = PWM.makeDummy(revcomp_pwm.matrix_length());
    } else if (only_revcomp) {
      this.pwm = PWM.makeDummy(pwm.matrix_length());
    }
  }

  public void run() throws IOException {
    ResultFormatter sarusFormatter = new SarusResultFormatter(scoreFormatter, show_non_matching, flankLength);
    String flank = utils.polyN_flank(flankLength);

    for (NamedSequence namedSequence: FastaReader.fromFile(fasta_filename)) {
      ResultFormatter formatter;
      if (outputAsBed) {
        utils.IntervalStartCoordinate intervalCoordinate = utils.IntervalStartCoordinate.fromIntervalNotation(namedSequence.getName());
        String intervalName = motifName + ";" + namedSequence.getName().split("\\t")[0];
        formatter = new BedResultFormatter(scoreFormatter, intervalCoordinate.chromosome, intervalCoordinate.startPos, intervalName, pwm.motif_length(), show_non_matching, flankLength);
      } else {
        formatter = sarusFormatter;
      }

      if (!suppressNames) {
        System.out.println(">" + namedSequence.getName());
      }

      Sequence seq = convertSequence(flank + namedSequence.getSequence() + flank);

      if (thresholdOrBesthit.matches("besthit")) {
        seq.bestHit(pwm, revcomp_pwm, formatter);
      } else {
        double threshold = inputScoringModel.valueInScoreUnits(Double.parseDouble(thresholdOrBesthit), pvalueBsearchList);
        seq.scan(pwm, revcomp_pwm, threshold, formatter);
      }
    }
  }

}
