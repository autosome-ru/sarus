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
    if (naive) {
      return DPWM.readDPWM(pwm_filename, N_isPermitted, transpose);
    } else {
      return SDPWM.readSDPWM(pwm_filename, N_isPermitted, transpose);
    }
  }

  @Override
  public Sequence convertSequence(NamedSequence namedSequence) {
    if (naive) {
      return DSequence.sequenceFromString(namedSequence.getSequence());
    } else {
      return SDSequence.sequenceFromString(namedSequence.getSequence());
    }
  }

  SARUS() { }

  public static void main(String[] args) throws IOException {
    ru.autosome.di.SARUS cli = new ru.autosome.di.SARUS();
    cli.setupFromArglist(new ArrayList<>(Arrays.asList(args)));
    cli.run();
  }
}
