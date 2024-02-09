package ru.autosome.scanningModel.builder;

import ru.autosome.motifModel.mono.PWM;
import ru.autosome.scanningModel.PWMScanner;
import ru.autosome.sequenceModel.mono.Sequence;

public class PWMSequenceScannerBuilder extends SequenceScannerBuilder<PWM> {
    public PWMSequenceScannerBuilder(PWM motif, boolean scanDirect, boolean scanRevcomp) {
        super(motif, scanDirect, scanRevcomp);
    }

    public PWMScanner scannerForSequence(String str) {
        return new PWMScanner(motif, Sequence.sequenceFromString(str), scanDirect, scanRevcomp);
    }
}
