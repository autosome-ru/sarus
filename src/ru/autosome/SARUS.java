package ru.autosome;

import ru.autosome.motifModel.mono.PFM;
import ru.autosome.motifModel.mono.PWM;
import ru.autosome.motifModel.mono.SuperAlphabetPWM;
import ru.autosome.scanningModel.PFMOccupancyScanner;
import ru.autosome.scanningModel.PWMScanner;
import ru.autosome.scanningModel.SequenceScanner;
import ru.autosome.scanningModel.SuperAlphabetPWMScanner;
import ru.autosome.sequenceModel.mono.Sequence;
import ru.autosome.sequenceModel.mono.SuperAlphabetSequence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class SARUS extends ru.autosome.cli.SARUS {
    protected PWM motif;
    protected PFM pfm_motif;
    protected SuperAlphabetPWM motifSA;

    @Override
    protected void loadMotif() throws IOException {
        this.motif = PWM.readMPWM(motif_filename, N_isPermitted, transpose);
        this.motifSA = SuperAlphabetPWM.fromNaive(this.motif);
    }

    @Override public int motif_length() {
        if (lookForSumOccupancy) {
            return this.pfm_motif.length();
        } else {
            return this.motif.length();
        }
    }

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

    @Override public SequenceScanner<?, ?> makeScanner(String str) {
        if (naive) {
            Sequence sequence = Sequence.sequenceFromString(str);
            return new PWMScanner(this.motif, sequence, scanDirect, scanRevcomp);
        } else {
            SuperAlphabetSequence sequenceSA = SuperAlphabetSequence.sequenceFromString(str);
            return new SuperAlphabetPWMScanner(this.motifSA, sequenceSA, scanDirect, scanRevcomp);
        }
    }

    @Override
    protected void loadPFMMotif() throws IOException {
        if (N_isPermitted) {
            System.err.println("Warning: [--skipn] option is implied in pfm-sum-occupancy mode");
            this.N_isPermitted = false;
        }
        if (!naive) {
            System.err.println("Warning: only naive algorithm is implemented for PFM sum occupancy");
            this.naive = true;
        }
        PFM motif = PFM.readMPFM(motif_filename, N_isPermitted, pfmPseudocount, transpose);
        this.pfm_motif = motif;
    }

    @Override
    protected SequenceScanner<?, ?> makePFMScanner(Sequence sequence) {
        return new PFMOccupancyScanner(pfm_motif, sequence, scanDirect, scanRevcomp);
    }

    public static void main(String[] args) throws IOException {
        ru.autosome.SARUS cli = new ru.autosome.SARUS();
        cli.setupFromArglist(new ArrayList<>(Arrays.asList(args)));
        cli.run();
    }
}
