package ru.autosome.scanningModel.builder;

import ru.autosome.motifModel.mono.SuperAlphabetPWM;
import ru.autosome.scanningModel.SuperAlphabetPWMScanner;
import ru.autosome.sequenceModel.mono.SuperAlphabetSequence;

public class SuperAlphabetPWMSequenceScannerBuilder extends SequenceScannerBuilder<SuperAlphabetPWM> {
    public SuperAlphabetPWMSequenceScannerBuilder(SuperAlphabetPWM motif, boolean scanDirect, boolean scanRevcomp) {
        super(motif, scanDirect, scanRevcomp);
    }

    public SuperAlphabetPWMScanner scannerForSequence(String str) {
        return new SuperAlphabetPWMScanner(motif, SuperAlphabetSequence.sequenceFromString(str), scanDirect, scanRevcomp);
    }
}
