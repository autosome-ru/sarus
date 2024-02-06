package ru.autosome;

public interface ResultFormatter {
    String format(double score, int pos_start, String strand);
    String formatOccupancy(double occupancy, int seqLength);

    String formatNoMatch();

    boolean shouldOutputNoMatch();

    int getFlankLength();
}
