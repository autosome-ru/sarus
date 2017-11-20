package ru.autosome;

public class BedResultFormatter implements ResultFormatter {
  ScoreFormatter scoreFormatter;
  String intervalChromosome;
  int intervalStartPos;
  String motifName;
  int motifLength;
  public BedResultFormatter(ScoreFormatter scoreFormatter, String intervalChromosome, int intervalStartPos, String motifName, int motifLength) {
    this.scoreFormatter = scoreFormatter;
    this.intervalChromosome = intervalChromosome;
    this.intervalStartPos = intervalStartPos;
    this.motifName = motifName;
    this.motifLength = motifLength;
  }

  @Override
  public String format(double score, int pos_start, String strand) {
    int from = intervalStartPos + pos_start;
    int to = intervalStartPos + pos_start + motifLength;
    return intervalChromosome + "\t" + from + "\t" + to + "\t" + motifName + "\t" + scoreFormatter.formatScore(score) + "\t" + strand;
  }
}
