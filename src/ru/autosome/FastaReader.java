package ru.autosome;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Scanner;

public class FastaReader implements Iterable<NamedSequence> {
  final Scanner scanner;
  FastaReader(Scanner scanner) {
    this.scanner = scanner;
  }

  public static FastaReader fromFile(File file) throws FileNotFoundException {
    return new FastaReader(new Scanner(file));
  }

  public static FastaReader fromFile(String filename) throws FileNotFoundException {
    return fromFile(new File(filename));
  }

  public static FastaReader fromString(String string) {
    return new FastaReader(new Scanner(string));
  }

  @Override
  public Iterator<NamedSequence> iterator() {
    return new FastaScanner(scanner);
  }
}
