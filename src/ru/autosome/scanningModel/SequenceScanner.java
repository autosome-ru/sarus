package ru.autosome.scanningModel;

import ru.autosome.Occurence;
import ru.autosome.ResultFormatter;
import ru.autosome.Strand;
import ru.autosome.motifModel.PWM;

import java.util.function.Consumer;

public abstract class SequenceScanner {
    public abstract static class Builder<T extends PWM> {
        protected final boolean scanDirect, scanRevcomp;
        protected final T motif;
        Builder(T motif, boolean scanDirect, boolean scanRevcomp) {
            this.motif = motif;
            this.scanDirect = scanDirect;
            this.scanRevcomp = scanRevcomp;
        }
        public T getMotif() { return motif; }
        public abstract SequenceScanner scannerForSequence(String str);
    }

    private final boolean scanDirect, scanRevcomp;
    SequenceScanner(boolean scanDirect, boolean scanRevcomp) {
        this.scanDirect = scanDirect;
        this.scanRevcomp = scanRevcomp;
    }

    abstract double direct_score(int position);
    abstract double revcomp_score(int position);

    abstract int scanningStartIndex();
    abstract int scanningEndIndex();
    abstract int shiftForRevcompScore();
    abstract int shiftForPrint();

    private void internalScanCallback(Consumer<Occurence> consumer) {
        Occurence current = new Occurence(Double.NEGATIVE_INFINITY, 0, Strand.direct);
        if (scanDirect) {
            for (int i = scanningStartIndex(); i < scanningEndIndex(); i++) {
                current.replace(direct_score(i), i, Strand.direct);
                consumer.accept(current);
            }
        }
        if (scanRevcomp) {
            for (int i = scanningStartIndex(); i < scanningEndIndex(); i++) {
                current.replace(revcomp_score(i + shiftForRevcompScore()), i, Strand.revcomp);
                consumer.accept(current);
            }
        }
    }

    public void scan(double threshold, ResultFormatter formatter) {
        internalScanCallback(occurence -> {
            if (occurence.goodEnough(threshold)) {
                String occurence_info = formatter.format(occurence.score,
                        occurence.pos + shiftForPrint(), occurence.strand.shortSign());
                System.out.println(occurence_info);
            }
        });

    }

    public void bestHit(ResultFormatter formatter) {
        if (scanningStartIndex() >= scanningEndIndex()) { // sequence is shorter than motif
            if (formatter.shouldOutputNoMatch()) {
                System.out.println(formatter.formatNoMatch());
            }
            return;
        }

        Occurence bestOccurence = new Occurence(Double.NEGATIVE_INFINITY, 0, Strand.direct);
        internalScanCallback(occurence -> bestOccurence.replaceIfBetter(occurence));

        String occurence_info = formatter.format(bestOccurence.score, bestOccurence.pos + shiftForPrint(), bestOccurence.strand.shortSign());
        System.out.println(occurence_info);
    }
}
