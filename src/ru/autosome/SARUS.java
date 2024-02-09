package ru.autosome;

import ru.autosome.motifModel.mono.PWM;
import ru.autosome.motifModel.mono.SuperAlphabetPWM;
import ru.autosome.scanningModel.builder.PWMSequenceScannerBuilder;
import ru.autosome.scanningModel.builder.SequenceScannerBuilder;
import ru.autosome.scanningModel.builder.SuperAlphabetPWMSequenceScannerBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class SARUS extends ru.autosome.cli.SARUS {

    @Override
    public String classNameHelp() {
        return "ru.autosome.SARUS";
    }

    @Override
    public String rowContentHelp() {
        return "4 nucleotides";
    }

    @Override
    public String precalculateThresholdsPkgHelp() {
        return "ru.autosome.ape.PrecalculateThresholds";
    }

    SARUS() {
    }

    @Override
    public SequenceScannerBuilder makeScannerBuilder() throws IOException {
        PWM motif = PWM.readMPWM(pwm_filename, N_isPermitted, transpose);
        if (naive) {
            return new PWMSequenceScannerBuilder(motif, scanDirect, scanRevcomp);
        } else {
            return new SuperAlphabetPWMSequenceScannerBuilder(SuperAlphabetPWM.fromNaive(motif), scanDirect, scanRevcomp);
        }
    }

    public static void main(String[] args) throws IOException {
        ru.autosome.SARUS cli = new ru.autosome.SARUS();
        cli.setupFromArglist(new ArrayList<>(Arrays.asList(args)));
        cli.run();
    }
}
