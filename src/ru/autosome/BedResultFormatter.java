package ru.autosome;

public class BedResultFormatter implements ResultFormatter {
  ScoreFormatter scoreFormatter;
  String intervalChromosome;
  int intervalStartPos;
  String motifName;
  int motifLength;
  boolean outputNoMatch;
  public BedResultFormatter(ScoreFormatter scoreFormatter, String intervalChromosome, int intervalStartPos, String motifName, int motifLength, boolean outputNoMatch) {
    this.scoreFormatter = scoreFormatter;
    this.intervalChromosome = intervalChromosome;
    this.intervalStartPos = intervalStartPos;
    this.motifName = motifName;
    this.motifLength = motifLength;
    this.outputNoMatch = outputNoMatch;
  }

  @Override
  public String format(double score, int pos_start, String strand) {
    int from = intervalStartPos + pos_start;
    int to = intervalStartPos + pos_start + motifLength;
    return intervalChromosome + "\t" + from + "\t" + to + "\t" + motifName + "\t" + scoreFormatter.formatScore(score) + "\t" + strand;
  }

  @Override
  public String formatNoMatch() {
    // it's not the same as format(Double.NEGATIVE_INFINITY, -1, "+") because of different interval boundaries
    // -1 not shifted to intervalStartPos; interval length is zero, not motif length
    return intervalChromosome + "\t" + (-1) + "\t" + (-1) + "\t" + motifName + "\t" + scoreFormatter.formatScore(Double.NEGATIVE_INFINITY) + "\t" + "+";
  }

  @Override
  public boolean shouldOutputNoMatch() {
    return outputNoMatch;
  }
}
