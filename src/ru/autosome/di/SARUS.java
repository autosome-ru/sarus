package ru.autosome.di;

import ru.autosome.motifModel.di.DPWM;
import ru.autosome.motifModel.di.SuperAlphabetDPWM;
import ru.autosome.scanningModel.builder.DPWMSequenceScannerBuilder;
import ru.autosome.scanningModel.builder.SequenceScannerBuilder;
import ru.autosome.scanningModel.DPWMScanner;
import ru.autosome.scanningModel.builder.SuperAlphabetDPWMSequenceScannerBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class SARUS extends ru.autosome.cli.SARUS {
    @Override
    public String classNameHelp() {
        return "ru.autosome.di.SARUS";
    }

    @Override
    public String rowContentHelp() {
        return "16 dinucleotides";
    }

    @Override
    public String precalculateThresholdsPkgHelp() {
        return "ru.autosome.ape.di.PrecalculateThresholds";
    }

    SARUS() {
    }

    @Override
    public SequenceScannerBuilder makeScannerBuilder() throws IOException {
        DPWM motif = DPWM.readDPWM(pwm_filename, N_isPermitted, transpose);
        if (naive) {
            return new DPWMSequenceScannerBuilder(motif, scanDirect, scanRevcomp);
        } else {
            return new SuperAlphabetDPWMSequenceScannerBuilder(SuperAlphabetDPWM.fromNaive(motif), scanDirect, scanRevcomp);
        }
    }

    public static void main(String[] args) throws IOException {
        ru.autosome.di.SARUS cli = new ru.autosome.di.SARUS();
        cli.setupFromArglist(new ArrayList<>(Arrays.asList(args)));
        cli.run();
    }
}
