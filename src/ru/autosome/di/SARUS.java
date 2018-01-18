package ru.autosome.di;

import ru.autosome.NamedSequence;
import ru.autosome.motifModel.PWM;
import ru.autosome.motifModel.di.DPWM;
import ru.autosome.motifModel.di.SDPWM;
import ru.autosome.sequenceModel.Sequence;
import ru.autosome.sequenceModel.di.DSequence;
import ru.autosome.sequenceModel.di.SDSequence;

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
  public String precalculateThresholdsPkgHelp(){
    return "ru.autosome.ape.di.PrecalculateThresholds";
  }

  @Override
  public PWM loadPWM() throws IOException {
    DPWM dpwm = DPWM.readDPWM(pwm_filename, N_isPermitted, transpose);
    if (naive) {
      return dpwm;
    } else {
      return SDPWM.fromDPWM(dpwm);
    }
  }

  @Override
  public Sequence convertSequence(String sequence) {
    if (naive) {
      return DSequence.sequenceFromString(sequence);
    } else {
      return SDSequence.sequenceFromString(sequence);
    }
  }

  SARUS() { }

  public static void main(String[] args) throws IOException {
    ru.autosome.di.SARUS cli = new ru.autosome.di.SARUS();
    cli.setupFromArglist(new ArrayList<>(Arrays.asList(args)));
    cli.run();
  }
}
