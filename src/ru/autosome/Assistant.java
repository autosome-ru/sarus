package ru.autosome;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: nastia
 * Date: 27.02.14
 * Time: 14:00
 * To change this template use File | Settings | File Templates.
 */


public class Assistant {


  public static ArrayList<String> readFastaFile(String path, ArrayList<String> names) throws IOException {
    ArrayList<String> setOfSequences = new ArrayList<String>();
    for (NamedSequence namedSequence: FastaReader.fromFile(path)) {
      setOfSequences.add(namedSequence.getSequence());
      names.add(namedSequence.getName());
    }
    return setOfSequences;
  }

  static byte charToByte(char ch) {
    switch (Character.toLowerCase(ch)) {
      case 'a':
        return 0;
      case 'c':
        return 1;
      case 'g':
        return 2;
      case 't':
        return 3;
      case 'n':
        return 4;
      default:
        throw new IllegalStateException("Unknown nucleotide: '" + ch + "'");
    }
  }

  static ArrayList<String> load(String path) throws IOException {

    ArrayList<String> strings = new ArrayList<String>();
    String tempLine;

    BufferedReader br = new BufferedReader(new FileReader(path));

    while ((tempLine = br.readLine()) != null) {
      if (!tempLine.isEmpty()) {
        // if (i != 0) {
          //because first string is a Name of pwm
        strings.add(tempLine);
        // }
      }
    }
    br.close();

    return strings;
  }

  static int[] dComplimentaryElements;

  static {
    int ind = 0;
    String[] letters = {"a", "c", "g", "t", "n"};
    List<String> rev_letters = Arrays.asList("t", "g", "c", "a", "n");
    dComplimentaryElements = new int[25];
    for (int i = 0; i < letters.length; i++) {
      for (int j = 0; j < letters.length; j++) {
        int val = 5 * rev_letters.indexOf(letters[j]) + rev_letters.indexOf(letters[i]);
        dComplimentaryElements[ind] = val;
        ind += 1;
      }
    }
  }


  static int[] sdComplimentaryElements;

  static {
    int ind = 0;
    String[] letters = {"a", "c", "g", "t", "n"};
    List<String> rev_letters = Arrays.asList("t", "g", "c", "a", "n");
    sdComplimentaryElements = new int[125];
    for (int i = 0; i < letters.length; i++) {
      for (int j = 0; j < letters.length; j++) {
        for (int k = 0; k < letters.length; k++) {
          int val = 25 * rev_letters.indexOf(letters[k]) + 5 * rev_letters.indexOf(letters[j]) + rev_letters.indexOf(letters[i]);
          sdComplimentaryElements[ind] = val;
          ind += 1;
        }
      }
    }
  }

  public static ArrayList<Double[]> parseDi(ArrayList<String> strings, boolean transpose) {
    ArrayList<Double[]> result = new ArrayList<Double[]>();
    if (strings.get(0).length() >= 28 && strings.get(0).subSequence(0,28).equals("PROG|ru.autosome.di.ChIPMunk")) {
      // load ChIPMunk output

      int start = strings.lastIndexOf("OUTC|ru.autosome.di.ChIPMunk");
      for (int i = start; i < strings.size(); i++) {

        if (strings.get(i).subSequence(0,4).equals("PWAA")) {

          int len = strings.get(i).split(" |\t").length;
          for (int t = 0; t < len; t++) { result.add(new Double[16]); }

          for (int letter = 0; letter < 16; letter ++) {
            String[] weights = strings.get(letter + i).split("\\|")[1].split(" |\t");
            for (int position = 0; position < len; position++) {
              result.get(position)[letter] = Double.parseDouble(weights[position]);
            }
          }
          break;
        }


      }
      if (result.size() == 0) throw new RuntimeException("Corrupted ChIPMunk output detected.");
      return result;

    } else {
      // load basic matrix
      String header = strings.get(0);
      if (header.charAt(0) == '>' || header.split(" |\t").length == 1 || header.matches("A(.*)|a(.*)") ) {
        // skip 1-line header; either 1 id or anything starting with ">" or anything including "A" as a potential "AA AC AG AT.." or similar
        strings.remove(0);
      }
      if (transpose) {
        if (strings.size() != 16) throw new RuntimeException("Incorrect number of weight lines in the transposed matrix input file.");

        int len = strings.get(0).split(" |\t").length;
        for (int i = 0; i < len; i++) { result.add(new Double[16]); }
        for (int letter = 0; letter < 16; letter ++) {
          String[] weights = strings.get(letter).split(" |\t");
          for (int position = 0; position < len; position++) {
            result.get(position)[letter] = Double.parseDouble(weights[position]);
          }
        }

      } else {
        for (String s: strings) {
          String[] weights = s.split(" |\t");
          if (weights.length != 16) throw new RuntimeException("Incorrect number of weights per line in the matrix input file.");
          result.add(new Double[16]);
          for (int i = 0; i < 16; i++) {
            result.get(result.size()-1)[i] = Double.parseDouble(weights[i]);
          }
        }
      }

    }
    return result;
  }

  public static ArrayList<Double[]> parseMono(ArrayList<String> strings, boolean transpose ){
      ArrayList<Double[]> result = new ArrayList<Double[]>();
      if (strings.get(0).length() >= 25 && strings.get(0).subSequence(0,25).equals("PROG|ru.autosome.ChIPMunk")) {
          // load ChIPMunk output

          int start = strings.lastIndexOf("OUTC|ru.autosome.ChIPMunk");
          for (int i = start; i < strings.size(); i++) {

              if (strings.get(i).subSequence(0,4).equals("PWMA")) {

                  int len = strings.get(i).split(" |\t").length;
                  for (int t = 0; t < len; t++) { result.add(new Double[4]); }

                  for (int letter = 0; letter < 4; letter ++) {
                      String[] weights = strings.get(letter + i).split("\\|")[1].split(" |\t");
                      for (int position = 0; position < len; position++) {
                          result.get(position)[letter] = Double.parseDouble(weights[position]);
                      }
                  }
                  break;
              }


          }
          if (result.size() == 0) throw new RuntimeException("Corrupted ChIPMunk output detected.");
          return result;

      } else {
          // load basic matrix
          String header = strings.get(0);
          if (header.charAt(0) == '>' || header.split(" |\t").length == 1 || header.matches("A(.*)|a(.*)") ) {
              // skip 1-line header; either 1 id or anything starting with ">" or anything including "A" as a potential "AA AC AG AT.." or similar
              strings.remove(0);
          }
        if (transpose) {
              if (strings.size() != 4) throw new RuntimeException("Incorrect number of weight lines in the transposed matrix input file.");

              int len = strings.get(0).split(" |\t").length;
              for (int i = 0; i < len; i++) { result.add(new Double[4]); }
              for (int letter = 0; letter < 4; letter ++) {
                  String[] weights = strings.get(letter).split(" |\t");
                  for (int position = 0; position < len; position++) {
                      result.get(position)[letter] = Double.parseDouble(weights[position]);
                  }
              }

          } else {
              for (String s: strings) {
                  String[] weights = s.split(" |\t");
                  if (weights.length != 4) throw new RuntimeException("Incorrect number of weights per line in the matrix input file.");
                  result.add(new Double[4]);
                  for (int i = 0; i < 4; i++) {
                      result.get(result.size()-1)[i] = Double.parseDouble(weights[i]);
                  }
              }
          }

      }
      return result;
  }
}

