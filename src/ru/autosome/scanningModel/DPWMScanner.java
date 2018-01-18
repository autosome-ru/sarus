package ru.autosome.scanningModel;

import ru.autosome.motifModel.di.DPWM;
import ru.autosome.sequenceModel.di.DSequence;

public class DPWMScanner extends SequenceScanner {
    public static class Builder extends SequenceScanner.Builder<DPWM> {
        public Builder(DPWM motif, boolean scanDirect, boolean scanRevcomp) {
            super(motif, scanDirect, scanRevcomp);
        }
        public DPWMScanner scannerForSequence(String str) {
            return new DPWMScanner(motif, DSequence.sequenceFromString(str), scanDirect, scanRevcomp);
        }
    }

    private final DPWM motif, revcomp_motif;
    private final DSequence sequence;
    public DPWMScanner(DPWM motif, DSequence sequence, boolean scanDirect, boolean scanRevcomp) {
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
