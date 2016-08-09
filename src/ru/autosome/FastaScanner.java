package ru.autosome;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;

public class FastaScanner implements Iterator<NamedSequence> {
  private String nextLine;
  BufferedReader reader;
  FastaScanner(BufferedReader reader) {
    this.reader = reader;
    this.nextLine = null;
  }

  private String readLine() {
    if (nextLine == null) {
      try {
        return reader.readLine();
      } catch (IOException e) {
        return null;
      }
    } else {
      String result = nextLine;
      nextLine = null;
      return result;
    }
  }

  private String peekLine() {
    if (nextLine == null) {
      try {
        nextLine = reader.readLine();
      } catch (IOException e) {
        return null;
      }
    }
    return nextLine;
  }

  @Override
  public boolean hasNext() {
    return (peekLine() != null);
  }

  @Override
  public NamedSequence next() {
    if (!peekLine().startsWith(">")) {
      throw new IllegalStateException("FASTA header should start with `>` mark");
    }
    String name = readLine().substring(1);
    StringBuilder sequenceBuilder = new StringBuilder();
    while (peekLine() != null && ! peekLine().startsWith(">")) {
      sequenceBuilder.append(readLine());
    }
    return new NamedSequence(sequenceBuilder.toString(), name);
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }
}
