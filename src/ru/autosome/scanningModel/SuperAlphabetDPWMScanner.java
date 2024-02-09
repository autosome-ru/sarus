package ru.autosome.scanningModel;

import ru.autosome.motifModel.di.SuperAlphabetDPWM;
import ru.autosome.sequenceModel.di.SuperAlphabetDSequence;

public class SuperAlphabetDPWMScanner extends SequenceScanner<SuperAlphabetDPWM, SuperAlphabetDSequence> {
    public SuperAlphabetDPWMScanner(SuperAlphabetDPWM motif, SuperAlphabetDSequence sequence, boolean scanDirect, boolean scanRevcomp) {
        super(motif, sequence, scanDirect, scanRevcomp);
    }

    @Override double direct_score(int position) { return motif.score(sequence, position); }
    @Override double revcomp_score(int position) { return revcomp_motif.score(sequence, position); }

    @Override public int scanningStartIndex() { return 1; }
    @Override public int scanningEndIndex() {
        if (motif.motif_length() % 2 == 0) {
            return sequence.sequence.length - 2 * motif.matrix.length + 2;
        } else {
            return sequence.sequence.length - 2 * motif.matrix.length + 1;
        }
    }
    @Override public int shiftForRevcompScore() {
        if (motif.motif_length() % 2 == 0) {
            return -1;
        } else {
            return 0;
        }
    }
    @Override public int shiftForPrint() { return -1; }
}
