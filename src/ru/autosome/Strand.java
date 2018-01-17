package ru.autosome;

public enum Strand {
    direct("+"),
    revcomp("-")
    ;
    private final String sign;
    Strand(String sign) {
        this.sign = sign;
    }
    public String shortSign() {
        return this.sign;
    }
}
