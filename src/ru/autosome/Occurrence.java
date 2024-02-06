package ru.autosome;

public class Occurrence {
    public double score;
    public int pos;
    public Strand strand;

    public Occurrence(double score, int pos, Strand strand) {
        this.score = score;
        this.pos = pos;
        this.strand = strand;
    }

    public void replace(double newScore, int newPos, Strand newStrand) {
        score = newScore;
        pos = newPos;
        strand = newStrand;
    }

    public boolean replaceIfBetter(double newScore, int newPos, Strand newStrand) {
        if (newScore > score) {
            replace(newScore, newPos, newStrand);
            return true;
        }
        return false;
    }

    public boolean replaceIfBetter(Occurrence occurrence) {
        return replaceIfBetter(occurrence.score, occurrence.pos, occurrence.strand);
    }

    public boolean goodEnough(double threshold) {
        return score >= threshold;
    }
}
