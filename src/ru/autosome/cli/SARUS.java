package ru.autosome.cli;

import ru.autosome.*;
import ru.autosome.scanningModel.SequenceScanner;

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
    protected boolean addFlanks;

    protected String fasta_filename;
    protected String pwm_filename;
    protected String thresholdOrBesthit;
    protected boolean lookForBestHit;
    protected Double threshold;

    protected boolean scanDirect, scanRevcomp;
    protected ScoreFormatter scoreFormatter;

    public abstract String classNameHelp();

    public abstract String rowContentHelp();

    public abstract String precalculateThresholdsPkgHelp();
    protected abstract void loadMotif() throws IOException;
    public abstract int motif_length();
    public abstract SequenceScanner<?, ?> makeScanner(String str);

    public String helpString() {
        return "Usage:\n" +
                "java -cp " + classNameHelp() + " <sequences.multifasta> <weight.matrix> <threshold> [options]\n" +
                "  or\n" +
                "java -cp " + classNameHelp() + " <sequences.multifasta> <weight.matrix> besthit [options]\n" +
                "Options:\n" +
                "  [--transpose] - use transposed PWM (rows correspond to " + rowContentHelp() + ", columns are positions)\n" +
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
                "  [--motif-name NAME] - motif name is included into interval name (the 4-th column in BED-6 format).\n" +
                "                        By default is inferred from PWM filename but can be redefined with this option.\n" +
                "  [--skipn] - Skip words with N-nucleotides.\n" +
                "  [--naive] - Don't use superalphabet-based scoring algorithm\n" +
                "  [--[no-]suppress] - Don't print sequence names (by default suppressed when output in BED-format)\n" +
                "  [--add-flanks] - Add polyN-flanks to sequences so that long motif could match short sequence.\n" +
                "                   In this mode every sequence will have besthit.\n" +
                "                   Note that in this mode site can be outside of sequence (coordinates can be even negative).\n" +
                "  [--show-non-matching] - Output fictive result for besthit when motif is wider than sequence\n";
    }

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
            this.outputScoringModel = ScoreType.valueOf(argsList.remove(arg_index + 1).toUpperCase());
            argsList.remove(arg_index);
        }

        if (argsList.contains("--threshold-mode")) {
            int arg_index = argsList.indexOf("--threshold-mode");
            this.inputScoringModel = ScoreType.valueOf(argsList.remove(arg_index + 1).toUpperCase());
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
        this.addFlanks = argsList.remove("--add-flanks");

        if (argsList.size() != 3) {
            System.err.print(
                    "Error: some arguments or parameters not recognized.\n" +
                            "Please check the following arguments (there must be 3 required arguments and some non-recognized options:\n");
            List<String> requiredArgs = argsList.subList(0, Math.min(argsList.size(), 3));
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
            System.err.println("Warning! `--show-non-matching` does nothing when polyN-flanks are added with `--add-flanks` options.");
        }

        this.fasta_filename = argsList.get(0);
        this.pwm_filename = argsList.get(1);
        this.thresholdOrBesthit = argsList.get(2);
        if (motifName == null) {
            this.motifName = utils.fileBasename(pwm_filename);
        }

        this.scoreFormatter = new ScoreFormatter(precision, outputScoringModel, pvalueBsearchList);

        scanDirect = true;
        scanRevcomp = true;
        if (only_direct && only_revcomp) {
            throw new IllegalArgumentException("Only-direct and only-revcomp modes are specified simultaneously");
        } else if (only_direct) {
            scanRevcomp = false;
        } else if (only_revcomp) {
            scanDirect = false;
        }

        if (thresholdOrBesthit.matches("besthit")) {
            this.lookForBestHit = true;
        } else {
            this.lookForBestHit = false;
            this.threshold = inputScoringModel.valueInScoreUnits(Double.parseDouble(thresholdOrBesthit), pvalueBsearchList);
        }

        loadMotif();
    }

    public void run() throws IOException {
        int motifLength = motif_length();
        int flankLength = addFlanks ? motifLength : 0;
        ResultFormatter sarusFormatter = new SarusResultFormatter(scoreFormatter, show_non_matching, flankLength);
        String flank = utils.polyN_flank(flankLength);

        for (NamedSequence namedSequence : FastaReader.fromFile(fasta_filename)) {
            ResultFormatter formatter;
            if (outputAsBed) {
                utils.IntervalStartCoordinate intervalCoordinate = utils.IntervalStartCoordinate.fromIntervalNotation(namedSequence.getName());
                String intervalName = motifName + ";" + namedSequence.getName().split("\\t")[0];
                formatter = new BedResultFormatter(scoreFormatter, intervalCoordinate.chromosome, intervalCoordinate.startPos, intervalName, motifLength, show_non_matching, flankLength);
            } else {
                formatter = sarusFormatter;
            }

            if (!suppressNames) {
                System.out.println(">" + namedSequence.getName());
            }

            SequenceScanner<?, ?> scanner = makeScanner(flank + namedSequence.getSequence() + flank);

            if (lookForBestHit) {
                scanner.bestHit(formatter);
            } else {
                scanner.scan(threshold, formatter);
            }
        }
    }

}
