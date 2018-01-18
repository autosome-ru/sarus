package ru.autosome.motifModel;

//matrix: first string - name; row - position, column - nucleotide
public interface PWM<T extends PWM> {
    T revcomp();
    int motif_length(); // length of matched sequence in nucleotides
}
