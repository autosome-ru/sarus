package ru.autosome.scanningModel.builder;

import ru.autosome.motifModel.Motif;
import ru.autosome.scanningModel.SequenceScanner;

public abstract class SequenceScannerBuilder<T extends Motif<T, ?>> {
    protected final boolean scanDirect, scanRevcomp;
    protected final T motif;

    SequenceScannerBuilder(T motif, boolean scanDirect, boolean scanRevcomp) {
        this.motif = motif;
        this.scanDirect = scanDirect;
        this.scanRevcomp = scanRevcomp;
    }

    public T getMotif() {
        return motif;
    }

    public abstract SequenceScanner scannerForSequence(String str);
}
