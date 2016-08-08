package ru.autosome;

import java.util.Iterator;
import java.util.Scanner;

public class FastaScanner implements Iterator<NamedSequence> {
  final Scanner scanner;
  private boolean closed;

  public FastaScanner(Scanner scanner) {
    this.scanner = scanner;
    scanner.useDelimiter("\\n");
    this.closed = false;
  }

  @Override
  public boolean hasNext() {
    if (! scanner.hasNext()) {
      scanner.close();
      closed = true;
      return false;
    }
    return true;
  }

  @Override
  public NamedSequence next() {
    if (scanner.hasNext(">.*")) {
      scanner.skip(">");
    } else {
      throw new RuntimeException("Wrong formatting of FASTA file");
    }
    String name = scanner.nextLine().trim();
    StringBuilder sequence = new StringBuilder();

    while (scanner.hasNextLine() && !scanner.hasNext(">.*")) {
      sequence.append(scanner.nextLine().trim());
    }

    if (! scanner.hasNext()) {
      closed = true;
    }

    return new NamedSequence(sequence.toString(), name);
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }
}
