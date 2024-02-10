package ru.autosome.scanningModel;

import ru.autosome.Occurrence;
import ru.autosome.ResultFormatter;
import ru.autosome.Strand;
import ru.autosome.motifModel.Motif;
import ru.autosome.sequenceModel.AbstractSequence;

import java.util.function.Consumer;

public abstract class SequenceScanner<M extends Motif<M, S>, S extends AbstractSequence> {
    protected final M motif, revcomp_motif;
    protected final S sequence;
    private final boolean scanDirect, scanRevcomp;
    SequenceScanner(M motif, S sequence, boolean scanDirect, boolean scanRevcomp) {
        this.motif = motif;
        this.revcomp_motif = motif.revcomp();
        this.sequence = sequence;
        this.scanDirect = scanDirect;
        this.scanRevcomp = scanRevcomp;
    }

    abstract double direct_score(int position);
    abstract double revcomp_score(int position);

    abstract int scanningStartIndex();
    abstract int scanningEndIndex();
    abstract int shiftForRevcompScore();
    abstract int shiftForPrint();
    int sequenceLength() {
        return this.sequence.length();
    }

    private void internalScanCallback(Consumer<Occurrence> consumer) {
        Occurrence current = new Occurrence(Double.NEGATIVE_INFINITY, 0, Strand.direct);
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
        internalScanCallback(occurrence -> {
            if (occurrence.goodEnough(threshold)) {
                String occurrence_info = formatter.format(occurrence.score,
                        occurrence.pos + shiftForPrint(), occurrence.strand.shortSign());
                System.out.println(occurrence_info);
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

        Occurrence bestOccurrence = new Occurrence(Double.NEGATIVE_INFINITY, 0, Strand.direct);
        internalScanCallback(occurrence -> bestOccurrence.replaceIfBetter(occurrence));

        String occurrence_info = formatter.format(bestOccurrence.score, bestOccurrence.pos + shiftForPrint(), bestOccurrence.strand.shortSign());
        System.out.println(occurrence_info);
    }


}
