package ru.autosome;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

// List of pvalue-threshold pairs sorted by threshold ascending
public class PvalueBsearchList {
  private final List<ThresholdPvaluePair> list;

  public PvalueBsearchList(List<ThresholdPvaluePair> infos) {
    this.list = infos.stream()
                    .filter((ThresholdPvaluePair info) -> info.pvalue != 0)
                    .filter((ThresholdPvaluePair info) -> {
                      Double score = info.threshold;
                      return !score.isNaN() && !score.isInfinite();
                    })
                    .distinct()
                    .sorted(ThresholdPvaluePair.thresholdComparator)
                    .collect(Collectors.toList());
  }

  public double combine_pvalues(double pvalue_1, double pvalue_2) {
    return Math.sqrt(pvalue_1 * pvalue_2);
  }

  public double combine_thresholds(double threshold_1, double threshold_2) {
    return (threshold_1 + threshold_2) / 2;
  }

  public double pvalue_by_threshold(double threshold) {
    int index = Collections.binarySearch(list, new ThresholdPvaluePair(threshold, null), ThresholdPvaluePair.thresholdComparator);
    if (index >= 0) {
      return list.get(index).pvalue;
    }

    int insertion_point = -index - 1;
    if (insertion_point > 0 && insertion_point < list.size()) {
      return combine_pvalues(
          list.get(insertion_point).pvalue,
          list.get(insertion_point - 1).pvalue
      );
    } else if (insertion_point == 0) {
      return list.get(0).pvalue;
    } else {
      return list.get(list.size() - 1).pvalue;
    }
  }

  public double threshold_by_pvalue(double pvalue) {
    int index = Collections.binarySearch(list, new ThresholdPvaluePair(null, pvalue), ThresholdPvaluePair.pvalueComparator);
    if (index >= 0) {
      return list.get(index).threshold;
    }
    int insertion_point = -index - 1;
    if (insertion_point > 0 && insertion_point < list.size()) {
      return combine_thresholds(
          list.get(insertion_point).threshold,
          list.get(insertion_point - 1).threshold
      );
    } else if (insertion_point == 0) {
      return list.get(0).threshold;
    } else {
      return list.get(list.size() - 1).threshold;
    }
  }

  private static List<ThresholdPvaluePair> load_thresholds_list(BufferedReader reader) {
    return reader.lines()
               .map(line -> line.replaceAll("\\s+", "\t").split("\t"))
               .filter(tokens -> tokens.length >= 2)
               .map(tokens -> {
                 double threshold = Double.valueOf(tokens[0]);
                 double pvalue = Double.valueOf(tokens[1]);
                 return new ThresholdPvaluePair(threshold, pvalue);
               }).collect(Collectors.toList());
  }

  public static List<ThresholdPvaluePair> load_thresholds_list(File file) throws FileNotFoundException {
    return load_thresholds_list(new BufferedReader(new FileReader(file)));
  }

  public static PvalueBsearchList load_from_file(File file) throws FileNotFoundException {
    return new PvalueBsearchList(load_thresholds_list(file));
  }
}
