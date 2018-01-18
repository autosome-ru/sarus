package ru.autosome;

import java.io.*;
import java.util.Iterator;

public class FastaReader implements Iterable<NamedSequence> {
    final BufferedReader reader;
    boolean alreadyGotIterator = false;

    FastaReader(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public Iterator<NamedSequence> iterator() {
        if (!alreadyGotIterator) {
            alreadyGotIterator = true;
            return new FastaScanner(reader);
        } else {
            return null;
        }
    }

    public static FastaReader fromFile(File file) throws FileNotFoundException {
        return new FastaReader(new BufferedReader(new FileReader(file), 10 * 1024 * 1024));
    }

    public static FastaReader fromStdin() throws FileNotFoundException {
        return new FastaReader(new BufferedReader(new InputStreamReader(System.in), 10 * 1024 * 1024));
    }

    public static FastaReader fromFile(String filename) throws FileNotFoundException {
        if (filename.equals("-")) {
            return fromStdin();
        } else {
            return fromFile(new File(filename));
        }
    }

    public static FastaReader fromString(String string) {
        return new FastaReader(new BufferedReader(new StringReader(string)));
    }
}
