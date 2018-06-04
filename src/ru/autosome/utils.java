package ru.autosome;

import java.io.File;

public class utils {
    public static String fileBasename(String filename) { // basename without extension
        return fileBasename(new File(filename));
    }

    static String fileBasename(File file) { // basename without extension
        String name = file.getName();
        int dotIdx = name.lastIndexOf(".");
        if (dotIdx != -1) {
            return name.substring(0, dotIdx);
        } else {
            return name;
        }
    }

    public static String polyN_flank(int flankLength) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < flankLength; ++i) {
            builder.append("N");
        }
        return builder.toString();
    }

    public static class IntervalStartCoordinate {
        public String chromosome;
        public int startPos;

        public IntervalStartCoordinate(String chromosome, int startPos) {
            this.chromosome = chromosome;
            this.startPos = startPos;
        }

        // TERT_GBM::chr5:1295104-1295362 --> chr5:1295104-1295362
        // chr16:85715965..85891755+ --> chr16:85715965..85891755+
        public static String dropIntervalName(String annotation) {
            int colonIdx = annotation.indexOf("::");
            if (colonIdx != -1) {
                return annotation.substring(colonIdx+2);
            }
            return annotation;
        }

        // `chr1:234` or `chr1:234-567` or `chr1:234..567` or `chr1:234-567,+` or `TERT_GBM::chr5:1295104-1295362`
        // only first coordinate matter, second coordinate and strand are ignored
        public static IntervalStartCoordinate fromIntervalNotation(String annotation) {
            String chrName;
            int startPos;
            annotation = dropIntervalName(annotation);
            int colonIdx = annotation.indexOf(':');
            if (colonIdx != -1) {
                chrName = annotation.substring(0, colonIdx);
                String rest_interval_annotation = annotation.substring(colonIdx + 1);
                int separatorIdx1 = rest_interval_annotation.indexOf('-');
                int separatorIdx2 = rest_interval_annotation.indexOf("..");
                int separatorIdx;
                if (separatorIdx1 == -1 && separatorIdx2 == -1) {
                    separatorIdx = -1;
                } else if (separatorIdx1 == -1) {
                    separatorIdx = separatorIdx2;
                } else if (separatorIdx2 == -1) {
                    separatorIdx = separatorIdx1;
                } else {
                    separatorIdx = separatorIdx1 < separatorIdx2 ? separatorIdx1 : separatorIdx2;
                }

                if (separatorIdx == -1) {
                    startPos = Integer.valueOf(rest_interval_annotation);
                } else {
                    startPos = Integer.valueOf(rest_interval_annotation.substring(0, separatorIdx));
                }
            } else {
                chrName = annotation;
                startPos = 0;
            }
            return new IntervalStartCoordinate(chrName, startPos);
        }
    }

    public static double[][] with_zero_row(double[][] matrix) {
        if (matrix.length == 0) {
            throw new IllegalArgumentException("Can't add zero row to an empty matrix");
        }
        int row_length = matrix[0].length;
        double[][] result = new double[matrix.length + 1][row_length];
        System.arraycopy(matrix, 0, result, 0, matrix.length);
        for (int i = 0; i < row_length; i++) {
            result[matrix.length][i] = 0;
        }
        return result;
    }
}
