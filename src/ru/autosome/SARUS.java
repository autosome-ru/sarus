package ru.autosome;

import ru.autosome.motifModel.PWM;
import ru.autosome.motifModel.mono.MPWM;
import ru.autosome.motifModel.mono.SMPWM;
import ru.autosome.sequenceModel.Sequence;
import ru.autosome.sequenceModel.mono.MSequence;
import ru.autosome.sequenceModel.mono.SMSequence;

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
  public String precalculateThresholdsPkgHelp(){
    return "ru.autosome.ape.PrecalculateThresholds";
  }

  @Override
  public PWM loadPWM() throws IOException {
    if (naive) {
      return MPWM.readMPWM(pwm_filename, N_isPermitted, transpose);
    } else {
      return SMPWM.readSMPWM(pwm_filename, N_isPermitted, transpose);
    }
  }

  @Override
  public Sequence convertSequence(NamedSequence namedSequence) {
    if (naive) {
      return MSequence.sequenceFromString(namedSequence.getSequence());
    } else {
      return SMSequence.sequenceFromString(namedSequence.getSequence());
    }
  }

  SARUS() { }
  public static void main(String[] args) throws IOException {
    ru.autosome.SARUS cli = new ru.autosome.SARUS();
    cli.setupFromArglist(new ArrayList<>(Arrays.asList(args)));
    cli.run();
  }
}
