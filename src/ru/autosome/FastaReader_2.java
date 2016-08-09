package ru.autosome;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Iterator;

public class FastaReader_2 implements Iterable<NamedSequence> {
  final BufferedReader reader;

  FastaReader_2(BufferedReader reader) {
    this.reader = reader;
  }

  public static FastaReader_2 fromFile(File file) throws FileNotFoundException {
    return new FastaReader_2(new BufferedReader(new FileReader(file, Charset.forName("US-ASCII")), 500*1024*1024));
  }

  public static FastaReader_2 fromFile(String filename) throws FileNotFoundException {
    return fromFile(new File(filename));
  }

  public static FastaReader_2 fromString(String string) {
    return new FastaReader_2(new BufferedReader(new StringReader(string), 500*1024*1024));
  }

  @Override
  public Iterator<NamedSequence> iterator() {
    return new FastaScanner_2(reader);
  }
}
