package ru.autosome;

import ru.autosome.motifModel.mono.PWM;
import ru.autosome.motifModel.mono.SuperAlphabetPWM;
import ru.autosome.scanningModel.PWMScanner;
import ru.autosome.scanningModel.SequenceScanner;
import ru.autosome.scanningModel.SuperAlphabetPWMScanner;

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
    public SequenceScanner.Builder makeScannerBuilder() throws IOException {
        PWM motif = PWM.readMPWM(pwm_filename, N_isPermitted, transpose);
        if (naive) {
            return new PWMScanner.Builder(motif, scanDirect, scanRevcomp);
        } else {
            return new SuperAlphabetPWMScanner.Builder(SuperAlphabetPWM.fromNaive(motif), scanDirect, scanRevcomp);
        }
    }

    public static void main(String[] args) throws IOException {
        ru.autosome.SARUS cli = new ru.autosome.SARUS();
        cli.setupFromArglist(new ArrayList<>(Arrays.asList(args)));
        cli.run();
    }
}
