# SPRY-SARUS
SPRy-SARUS stands for Straightforward yet Powerful Rapid 
SuperAlphabet Representation Utilized for motif Search. 

This is a simple tool which uses superalphabet approach 
presented by [Pizzi, Rastas & Ukkonen; 2007] to scan a given set 
of sequences for (di)PWM hits scoring no less than a given threshold. 

### Key features: 
* provides bed + plain text output
* supports both classic mono- and dinucleotide position weight matrices
* can output raw scores or scores converted to P-values


## Usage
SPRY-SARUS has fairly simple command line format, 
accepts weight matrices in plain text files (with log-odds 
or similarly transformed additive weights) and DNA sequences in multifasta. 

SPRY-SARUS is written in Java (and requires Java 1.8 or more recent). You can get it as the [jar file](https://raw.githubusercontent.com/VorontsovIE/sarus/master/releases/sarus-2.0.0.jar).
You can experiment with sample PWMs and sequences in [examples](https://github.com/VorontsovIE/sarus/tree/master/examples) folder.

The proper command line format is printed if SARUS is executed w/o arguments:

```java -cp sarus.jar ru.autosome.SARUS```

or in a shorter form 

```java -jar sarus.jar```

You may want to provide Java with more memory in case of large sequence sets, e.g. 2 gigabytes:

```java -Xmx2G -cp sarus.jar ru.autosome.SARUS <more parameters here>```

### Using dinucleotide version

The dinucleotide version can be used in a similar way (with the same command-line parameters):

```java -cp sarus.jar ru.autosome.di.SARUS```

For example:

```java -cp sarus.jar ru.autosome.di.SARUS SP1_peaks.mfa SP1_example.dpwm besthit > result.log```

[NOTE!] The dinucleotide version is located in `ru.autosome.di` package, 
and this is the only difference in command line format.

## Input data

SPRY-SARUS scans given sequences for occurrences of the given motif.
For each sequence it acts independently (no internal parallelization is implemented).

SPRY-SARUS operates in two different modes.

In the first mode it looks for the only best occurrence (besthit)
of the motif in each sequence:

```java -cp sarus.jar ru.autosome.SARUS <sequences.multifasta> <weight.matrix> besthit```

Another option is to return all motif occurrences in a sequence for which 
PWM score exceeds the specified threshold:

```java -cp sarus.jar ru.autosome.SARUS <sequences.multifasta> <weight.matrix> <threshold>```

The arguments have self-speaking names.
The multi-FASTA with sequences and the weight matrix file are specified by the corresponding filenames.

Sequences can be also be passed via the standard input (stdin) by specifying minus (`-`)
instead of the corresponding filename.


### Weight matrix format
The weight matrix can be given either with or without the header line 
(starting from `>`).

By default each line in the matrix file corresponds to a single position of a motif, 
i.e. each line should contain 4 (or 16) elements for PWM (diPWM).

The nucleotide order for mono-PWMs is alphabetical `A-C-G-T`.
The dinucleotide order for di-PWMs is also alphabetical `AA-AC-AG-..-TT`.

Please note, that SARUS can use raw ChIPMunk (but not ChIPHorde) output 
extracting the resulting motif right from the log-file 
(if the ChIPMunk output was redirected into a file as suggested in ChIPMunk guide, see the ChIPMunk [website](http://autosome.ru/ChIPMunk/)).

## Options
The first three arguments are mandatory. The next arguments are options that modify how the input
is treated and what should be modified in the output:

* `--transpose`
    suggests SARUS to use the transposed file format for the matrices (nucleotides or dinucleotides as rows, motif positions as columns).
* `--direct` or `--revcomp`
    forces single strand scanning mode to
    scan only the direct strand or only reverse-complementary strand
*  `--skipn`
    can be used to skip words with an unknown (N)-nucleotides.
*  `--precision N`
    should be used to round the resulting values (either raw PWM score or P-values) up to N digits after the floating point. Doesn't affect the internal precision of calculations.
  
Sequence motifs with long flanking sequences can hang over extremely short
sequences. By default SARUS ignores motif occurrences which are not located totally inside of a particular 
sequence. There are two options to override this behavior:
*  `--show-non-matching`
    In the `besthit` mode a user normally expects exactly one result for each
    input sequence. But for sequences shorter than the motif SARUS can't find 
    the best occurrence and outputs nothing.
    With this option SARUS outputs a fictional result instead (which can be useful 
    to simplify parsing of the resulting file). The fictional "occurrence" has 
    a score of `-∞` and is located at a virtual position `-1` with zero occurrence length.
*  `--add-flanks`
    Instead it is possible to extend each sequence by poly-N flanking sequences long enough
    to embrace putative sites.
    Given the flanking sequences are added, each sequence will have the "besthit" occurrence.
    Note, that the occurrence coordinates can point to the outside of sequence! Occurrence start
    can be negative, and occurrence endpoint can be higher than sequence length.

**Please note, that all the options (except for the filenames) should be given in lowercase letters.**

## Scores and P-values

By default SPRY-SARUS reports PWM scores of motif occurrences. Often it's
more convenient to use P-values corresponding to the scores.
To convert scores to P-values, SARUS relies on a precalculated mapping between score
thresholds and P-values. Such mapping can 
be obtained by [MACRO-PERFECTOS-APE](http://opera.autosome.ru/perfectosape/description) using 
`ru.autosome.ape.PrecalculateThresholds` with a uniform grid over 
logarithmic P-values with any given level of precision.

The P-values scanning requires the file with 
the mapping using `--pvalues-file FILE` option.

The output occurrence scores can be provided as P-values or 
negative logarithm of the P-values (`logpvalue := -log10(P-value)`) with the following option: `--output-scoring-mode MODE`

where `MODE` can be one of: `score`/`pvalue`/`logpvalue` (default: `score`).

For example, to find the best occurrences and report their logarithmic P-values the following command-line can be used:

```java -cp sarus.jar ru.autosome.SARUS sequences.mfa motif.pwm besthit --pvalues-file threshold_pvalue_mapping.txt --output-scoring-mode logpvalue```

Also, you can tell SPRY-SARUS to treat occurrence threshold as a P-value or a logarithmic P-value: `--threshold-mode MODE`.
For example, to find sites with P-value not greater than 0.0001:

```java -cp sarus.jar ru.autosome.SARUS sequences.mfa motif.pwm 0.0001 --pvalues-file threshold_pvalue_mapping.txt --threshold-mode pvalue```

## Output in `bed`-format
With the option `--output-bed` it is possible to output motif occurrences 
in `bed`-format. BED-6 format includes:
*chromosome name*,
*start* and *end* positions in [closed; open) 0-based indexing,
*interval name*,
*score* (or P-value/logarithmic P-value),
*strand*.

Chromosome name and sequence position is inferred from the multi-FASTA header lines.
To generate correct genome-wide coordinates each sequence should be named as ```>chrName:posStart-posEnd```

Note, that

`bedtools getfasta` generates headers in matching format.

The `posEnd` value can be omitted. SPRY-SARUS ignores header content after `posStart`.
If the `posStart` is missing, it's set to 0 by default.
Chromosome name and position shouldn't contain spaces and other whitespace characters.

Interval name in the 4-th column of `bed`-file is combined based on the motif name
and the sequence name. By default the motif name is inferred from the PWM filename but
can be redefined using the `--motif-name NAME` option.

In `bed` format it's natural not to output sequence names that belong to 
different sequences of the multi-FASTA. In default output format (plain text)
it's natural to output the names - this is the default behaviour. You can force
SPRY-SARUS both to suppress sequence names output in default mode using `--suppress` and
not to suppress them in `BED`-format using `--no-suppress`.

**Please note, that output is not sorted (because of matches on different strands). Consider using *`bedtools sort`* before supplying the output to other bed-based tools.**

## Output format
The default output format is fairly simple showing the sequence header 
(via `>` as in the input multifasta file), 
the PWM score, the position and the strand orientation of the motif occurence.

An example of the command line (based on example data from the webpage):

```java -jar sarus.jar SP1_peaks.mfa SP1_example.pwm besthit```

The messages are printed to STDERR and the result is printed to STDOUT
so it is possible to redirect the result into a file:

```java -jar sarus.jar SP1_peaks.mfa SP1_example.pwm besthit > result.log```

When `--output-bed` option is specified, the resulting output follows [BED-6 format specification](http://genome.ucsc.edu/FAQ/FAQformat#format1). The interval name is composed of the motif name and the sequence name separated by a semicolon, e.g.: `GATA1;chr1:100234-100567`.
