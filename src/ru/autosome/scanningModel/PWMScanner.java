package ru.autosome.scanningModel;

import ru.autosome.motifModel.mono.PWM;
import ru.autosome.sequenceModel.mono.Sequence;

public class PWMScanner extends SequenceScanner {
    public static class Builder extends SequenceScanner.Builder<PWM> {
        public Builder(PWM motif, boolean scanDirect, boolean scanRevcomp) {
            super(motif, scanDirect, scanRevcomp);
        }
        public PWMScanner scannerForSequence(String str) {
            return new PWMScanner(motif, Sequence.sequenceFromString(str), scanDirect, scanRevcomp);
        }
    }

    private final PWM motif, revcomp_motif;
    private final Sequence sequence;
    public PWMScanner(PWM motif, Sequence sequence, boolean scanDirect, boolean scanRevcomp) {
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
