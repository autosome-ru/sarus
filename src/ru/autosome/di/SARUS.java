package ru.autosome.di;

import ru.autosome.motifModel.di.DPWM;
import ru.autosome.motifModel.di.SuperAlphabetDPWM;
import ru.autosome.scanningModel.DPWMScanner;
import ru.autosome.scanningModel.SequenceScanner;
import ru.autosome.scanningModel.SuperAlphabetDPWMScanner;
import ru.autosome.sequenceModel.di.DSequence;
import ru.autosome.sequenceModel.di.SuperAlphabetDSequence;
import ru.autosome.sequenceModel.mono.Sequence;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class SARUS extends ru.autosome.cli.SARUS {
    protected DPWM motif;
    protected SuperAlphabetDPWM motifSA;

    @Override
    protected void loadMotif() throws IOException {
        this.motif = DPWM.readDPWM(motif_filename, N_isPermitted, transpose);
        this.motifSA = SuperAlphabetDPWM.fromNaive(this.motif);
    }

    @Override public int motif_length() { return this.motif.length(); }

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

    @Override public SequenceScanner<?, ?> makeScanner(String str) {
        if (naive) {
            DSequence sequence = DSequence.sequenceFromString(str);
            return new DPWMScanner(this.motif, sequence, scanDirect, scanRevcomp);
        } else {
            SuperAlphabetDSequence sequenceSA = SuperAlphabetDSequence.sequenceFromString(str);
            return new SuperAlphabetDPWMScanner(this.motifSA, sequenceSA, scanDirect, scanRevcomp);
        }
    }

    @Override
    protected void loadPFMMotif() throws IOException {
        // PFM sum occupancy not yet implemented for dinucleotide motifs
        throw new NotImplementedException();
    }

    @Override
    protected SequenceScanner<?, ?> makePFMScanner(Sequence sequence) {
        // PFM sum occupancy not yet implemented for dinucleotide motifs
        throw new NotImplementedException();
    }

    public static void main(String[] args) throws IOException {
        ru.autosome.di.SARUS cli = new ru.autosome.di.SARUS();
        cli.setupFromArglist(new ArrayList<>(Arrays.asList(args)));
        cli.run();
    }
}
