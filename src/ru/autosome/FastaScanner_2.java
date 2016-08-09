package ru.autosome;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;

public class FastaScanner_2 implements Iterator<NamedSequence> {
  BufferedReader reader;
  private String nextLine;
//  private StringBuilder sequenceBuilder;
  public FastaScanner_2(BufferedReader reader) {
    this.reader = reader;
    this.nextLine = null;
//    this.sequenceBuilder = new StringBuilder(100*1024*1024);
  }

  private String peekLine() {
    try {
      if (nextLine == null) {
        nextLine = reader.readLine();
      }
      return nextLine;
    } catch (IOException e) {
      return null;
    }
  }

  private String readLine() {
    try {
      if (nextLine == null) {
        return reader.readLine();
      } else {
        String result = nextLine;
        nextLine = null;
        return result;
      }
    } catch (IOException e) {
      return null;
    }
  }

  @Override
  public boolean hasNext() {
    return (peekLine() != null);
  }

  @Override
  public NamedSequence next() {
//    String name;
//    if (peekLine().startsWith(">")) {
//      name = readLine().substring(1);
//    } else {
//      throw new IllegalArgumentException("Wrong format, FASTA header should start with `>`");
//    }
//
////    while (peekLine() != null && !peekLine().startsWith(">")) {
////      sequenceBuilder.append(readLine());
////    }
//    String sequence = sequenceBuilder.toString();
//    sequenceBuilder.setLength(0);
    String name = readLine().substring(1);
    String sequence = readLine();
    System.err.println("Name: " + name);
    System.err.println("Sequence: " + sequence.substring(0,5));

    return new NamedSequence(sequence, name);
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }
}
