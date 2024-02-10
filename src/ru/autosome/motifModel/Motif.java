package ru.autosome.motifModel;

import ru.autosome.sequenceModel.AbstractSequence;

//matrix: first string - name; row - position, column - nucleotide
public interface Motif<T extends Motif<T, S>, S extends AbstractSequence> {
    T revcomp();
    int length(); // length of matched sequence in nucleotides
    double score(S seq, int position);
}
