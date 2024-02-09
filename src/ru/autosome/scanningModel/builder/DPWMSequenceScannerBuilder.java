package ru.autosome.scanningModel.builder;

import ru.autosome.motifModel.di.DPWM;
import ru.autosome.scanningModel.DPWMScanner;
import ru.autosome.sequenceModel.di.DSequence;

public class DPWMSequenceScannerBuilder extends SequenceScannerBuilder<DPWM> {
    public DPWMSequenceScannerBuilder(DPWM motif, boolean scanDirect, boolean scanRevcomp) {
        super(motif, scanDirect, scanRevcomp);
    }

    public DPWMScanner scannerForSequence(String str) {
        return new DPWMScanner(motif, DSequence.sequenceFromString(str), scanDirect, scanRevcomp);
    }
}
