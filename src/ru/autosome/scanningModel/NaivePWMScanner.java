package ru.autosome.scanningModel;

import ru.autosome.motifModel.mono.MPWM;
import ru.autosome.sequenceModel.mono.MSequence;

public class NaivePWMScanner extends SequenceScanner {
    public static class Builder extends SequenceScanner.Builder<MPWM> {
        public Builder(MPWM motif, boolean scanDirect, boolean scanRevcomp) {
            super(motif, scanDirect, scanRevcomp);
        }
        public NaivePWMScanner scannerForSequence(String str) {
            return new NaivePWMScanner(motif, MSequence.sequenceFromString(str), scanDirect, scanRevcomp);
        }
    }

    private final MPWM motif, revcomp_motif;
    private final MSequence sequence;
    public NaivePWMScanner(MPWM motif, MSequence sequence, boolean scanDirect, boolean scanRevcomp) {
        super(scanDirect, scanRevcomp);
        this.motif = motif;
        this.revcomp_motif = motif.revcomp();
        this.sequence = sequence;
    }

    @Override double direct_score(int position) { return motif.score(sequence, position); }
    @Override double revcomp_score(int position) { return revcomp_motif.score(sequence, position); }

    @Override public int scanningStartIndex() { return 0; }
    @Override public int scanningEndIndex() { return sequence.sequence.length - motif.matrix.length + 1; }
    @Override public int shiftForRevcompScore() { return 0; }
    @Override public int shiftForPrint() { return 0; }
}
