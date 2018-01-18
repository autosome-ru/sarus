package ru.autosome.sequenceModel;

import ru.autosome.Occurence;
import ru.autosome.ResultFormatter;
import ru.autosome.Strand;
import ru.autosome.motifModel.PWM;

import java.util.function.Consumer;

public abstract class Sequence {

    public final byte[] sequence;

    public Sequence(byte[] sequence) {
        this.sequence = sequence;
    }

    public abstract int scanningStartIndex();
    public abstract int scanningEndIndex(PWM pwm);
    public abstract int shiftForRevcompScore(PWM pwm);
    public abstract int shiftForPrint();

    void internalScanCallback(PWM pwm, PWM revComp_pwm, Consumer<Occurence> consumer) {
        Occurence current = new Occurence(Double.NEGATIVE_INFINITY, 0, Strand.direct);
        for (int i = scanningStartIndex(); i < scanningEndIndex(pwm); i++) {
            current.replace(pwm.score(this, i), i, Strand.direct);
            consumer.accept(current);
            current.replace(revComp_pwm.score(this, i + shiftForRevcompScore(pwm)), i, Strand.revcomp);
            consumer.accept(current);
        }
    }

    public void scan(PWM pwm, PWM revComp_pwm, double threshold, ResultFormatter formatter) {
        internalScanCallback(pwm, revComp_pwm, occurence -> {
            if (occurence.goodEnough(threshold)) {
                String occurence_info = formatter.format(occurence.score,
                        occurence.pos + shiftForPrint(), occurence.strand.shortSign());
                System.out.println(occurence_info);
            }
        });

    }

    public void bestHit(PWM pwm, PWM revComp_pwm, ResultFormatter formatter) {
        if (scanningStartIndex() >= scanningEndIndex(pwm)) { // sequence is shorter than motif
            if (formatter.shouldOutputNoMatch()) {
                System.out.println(formatter.formatNoMatch());
            }
            return;
        }

        Occurence bestOccurence = new Occurence(Double.NEGATIVE_INFINITY, 0, Strand.direct);
        internalScanCallback(pwm, revComp_pwm, occurence -> {
            bestOccurence.replaceIfBetter(occurence);
        });

        String occurence_info = formatter.format(bestOccurence.score, bestOccurence.pos + shiftForPrint(), bestOccurence.strand.shortSign());
        System.out.println(occurence_info);
    }
}
