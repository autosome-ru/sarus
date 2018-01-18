package ru.autosome;

public class NamedSequence {
    final String sequence;
    final String name;

    public NamedSequence(String sequence, String name) {
        this.sequence = sequence;
        this.name = name;
    }

    public String getSequence() {
        return sequence;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return ">" + name + "\n" + sequence;
    }
}
