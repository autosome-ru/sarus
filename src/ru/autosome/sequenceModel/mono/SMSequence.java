package ru.autosome.sequenceModel.mono;

import ru.autosome.Assistant;
import ru.autosome.ResultFormatter;
import ru.autosome.motifModel.mono.MPWM;
import ru.autosome.motifModel.PWM;
import ru.autosome.motifModel.mono.SMPWM;
import ru.autosome.sequenceModel.Sequence;

/**
 * Created with IntelliJ IDEA.
 * User: nastia
 * Date: 20.07.14
 * Time: 19:37
 * To change this template use File | Settings | File Templates.
 */
public class SMSequence extends Sequence {

    SMSequence(byte[] sequence){
        super(sequence);
    }

    public static SMSequence sequenceFromString(String str){

        int length = str.length();
        byte[] genome = new byte[length+1];


        genome[0] = (byte) (5 * 4 +
                Assistant.charToByte(str.charAt(0)));

        for (int j = 0; j < length - 1; j++) {

            genome[j + 1] = (byte) (5 * Assistant.charToByte(str.charAt(j)) +
                    Assistant.charToByte(str.charAt(j + 1)));
        }

        genome[length] = (byte) (5 * Assistant.charToByte(str.charAt(length - 1)) + 4);

        return new SMSequence(genome);


    }

    @Override
    public void scan(PWM pwm, PWM revComp_pwm, double threshold, ResultFormatter formatter){
        // if (pwm.getClass() != SMPWM.class || revComp_pwm.getClass() != SMPWM.class)
        //    throw new RuntimeException();

        if(SMPWM.lengthOfMPWMIsEven){
            internalScan(pwm, revComp_pwm, threshold, 1, this.sequence.length - 2 * pwm.matrix_length() + 1, 0, -1, formatter);
        }  else {
            internalScan(pwm, revComp_pwm, threshold, 1, this.sequence.length - 2 * pwm.matrix_length() + 2, -1, -1, formatter);
        }
    }

    @Override
    public void bestHit(PWM pwm, PWM revComp_pwm, ResultFormatter formatter) {
        //if (pwm.getClass() != SMPWM.class || revComp_pwm.getClass() != SMPWM.class)
        //    throw new RuntimeException();

        if (MPWM.lengthOfMPWMIsEven) {
            internalBestHit(pwm, revComp_pwm, 1, this.sequence.length - 2 * pwm.matrix_length() + 1, 0, -1, formatter);
        } else {
            internalBestHit(pwm, revComp_pwm, 1, this.sequence.length - 2 * pwm.matrix_length() + 2, -1, -1, formatter);
        }
    }
}
