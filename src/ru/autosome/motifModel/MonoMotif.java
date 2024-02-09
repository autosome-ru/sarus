package ru.autosome.motifModel;

import ru.autosome.sequenceModel.AbstractSequence;

public interface MonoMotif<T extends Motif<T, S>, S extends AbstractSequence> extends Motif<T, S> {
}
