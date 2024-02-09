package ru.autosome.scanningModel.builder;

import ru.autosome.motifModel.di.SuperAlphabetDPWM;
import ru.autosome.scanningModel.SuperAlphabetDPWMScanner;
import ru.autosome.sequenceModel.di.SuperAlphabetDSequence;

public class SuperAlphabetDPWMSequenceScannerBuilder extends SequenceScannerBuilder<SuperAlphabetDPWM> {
    public SuperAlphabetDPWMSequenceScannerBuilder(SuperAlphabetDPWM motif, boolean scanDirect, boolean scanRevcomp) {
        super(motif, scanDirect, scanRevcomp);
    }

    public SuperAlphabetDPWMScanner scannerForSequence(String str) {
        return new SuperAlphabetDPWMScanner(motif, SuperAlphabetDSequence.sequenceFromString(str), scanDirect, scanRevcomp);
    }
}
