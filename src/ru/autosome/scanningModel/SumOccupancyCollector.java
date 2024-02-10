package ru.autosome.scanningModel;

import ru.autosome.Occurrence;

public class SumOccupancyCollector implements java.util.function.Consumer<ru.autosome.Occurrence> {
    private double sum = 0.0;

    @Override
    public void accept(Occurrence occurrence) {
        sum += occurrence.score;
    }

    public double getSum() {
        return this.sum;
    }
}
