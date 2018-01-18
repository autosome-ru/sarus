package ru.autosome.scanningModel;

import ru.autosome.motifModel.mono.SuperAlphabetPWM;
import ru.autosome.sequenceModel.mono.SuperAlphabetSequence;

public class SuperAlphabetPWMScanner extends SequenceScanner {
    public static class Builder extends SequenceScanner.Builder<SuperAlphabetPWM> {
        public Builder(SuperAlphabetPWM motif, boolean scanDirect, boolean scanRevcomp) {
            super(motif, scanDirect, scanRevcomp);
        }
        public SuperAlphabetPWMScanner scannerForSequence(String str) {
            return new SuperAlphabetPWMScanner(motif, SuperAlphabetSequence.sequenceFromString(str), scanDirect, scanRevcomp);
        }
    }

    private final SuperAlphabetPWM motif, revcomp_motif;
    private final SuperAlphabetSequence sequence;
    public SuperAlphabetPWMScanner(SuperAlphabetPWM motif, SuperAlphabetSequence sequence, boolean scanDirect, boolean scanRevcomp) {
        super(scanDirect, scanRevcomp);
        this.motif = motif;
        this.revcomp_motif = motif.revcomp();
        this.sequence = sequence;
    }

    @Override double direct_score(int position) { return motif.score(sequence, position); }
    @Override double revcomp_score(int position) { return revcomp_motif.score(sequence, position); }

    @Override public int scanningStartIndex() { return 1; }
    @Override public int scanningEndIndex() {
        if (motif.motif_length() % 2 == 0) {
            return sequence.sequence.length - 2 * motif.matrix.length + 1;
        } else {
            return sequence.sequence.length - 2 * motif.matrix.length + 2;
        }
    }
    @Override public int shiftForRevcompScore() {
        if (motif.motif_length() % 2 == 0) {
            return 0;
        } else {
            return -1;
        }
    }
    @Override public int shiftForPrint() { return -1; }
}
