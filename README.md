# SPRY-SARUS
SPRy-SARUS stands for Straightforward yet Powerful Rapid 
SuperAlphabet Representation Utilized for motif Search. 

This is a simple tool which uses superalphabet approach 
presented by [Pizzi, Rastas & Ukkonen; 2007] to scan a given set 
of sequences for (di)PWM hits scoring no less than a given threshold. 

## Usage
SPRY-SARUS uses fairly simple command line format, 
accepts weight matrices in plain text files (with log-odds 
or similarly transformed weights) and DNA sequences in multifasta. 

SPRY-SARUS is written in Java (and requires Java >=1.8).
The all-in-one jar files are available on the [ChIPMunk](http://autosome.ru/ChIPMunk/) web page.
The [zip-file](http://autosome.ru/ChIPMunk/sarus_example.zip) with sample PWMs and sequences is also available for download.

The proper command line format is printed if SARUS is executed w/o arguments:

```java -cp sarus.jar ru.autosome.SARUS```

or in a shorter form 

```java -jar sarus.jar```

You may want to provide Java with more memory in case of large sequence sets, e.g.:

```java -Xmx1G -cp sarus.jar ru.autosome.SARUS <more parameters here>```

### Using dinucleotide version

The dinucleotide version can be used in a similar way (with the same command-line parameters):

```java -cp sarus.jar ru.autosome.di.SARUS```

For example:

```java -cp sarus.jar ru.autosome.di.SARUS SP1_peaks.mfa SP1_example.dpwm besthit > result.log```

[NOTE!!!] The dinucleotide version is located in `ru.autosome.di` package, 
and this is the only difference in command line format.

## Input data

SPRY-SARUS scans each of given sequences to find occurences of the given motif.
For each sequence it acts independently.

SPRY-SARUS operates in two different modes.

In the first mode it looks for the only best occurence (besthit)
of the motif in a sequence:

```java -cp ru.autosome.SARUS <sequences.multifasta> <weight.matrix> besthit```

Another option is to return all motif occurences in a sequence for which 
PWM score exceeds the specified threshold:

```java -cp ru.autosome.SARUS <sequences.multifasta> <weight.matrix> <threshold>```

The arguments have self-speaking names.
Sequences multi-FASTA and weight matrix are specified by corresponding filenames.

Sequences can be also be passed to standard input (stdin) by specifying `-`
instead of corresponding filename.


### Weight matrix format
The weight matrix can be given either with or without header line 
(starting from `>`, see examples).

By default each line in the file corresponds to a single position of a motif, 
i.e. each line should contain 4 (or 16) elements for PWM (diPWM).

The nucleotide order for mono-PWMs is alphabetical `A-C-G-T`.
The dinucleotide order for di-PWMs is also alphabetical `AA-AC-AG-..-TT`.

Please note, that SARUS can use raw ChIPMunk (but not ChIPHorde) output 
extracting the resulting motif right from the log-file 
(if the ChIPMunk output was redirected into a file as suggested in ChIPMunk guide).

## Options
After three mandatory arguments one can specify options that modify how an input
is treated and what is output as a result:

* `--transpose`
    suggests SARUS to use the transposed file format
    (letters as rows, positions as columns).
* `--direct` or `--revcomp`
    force single strand scanning mode
    scan only direct strand or only reverse-complemented strand of DNA
*  `--skipn`
    Skip words with N-nucleotides.
*  `--naive`
    Don't use superalphabet-based scoring algorithm
*  `--precision N`
    round result (either score or P-value) up to N digits after floating point. Doesn't affect the internal precision of calculations.
  
TF binding motifs with long flanking sequences can hang over given short
sequences. By default SARUS ignores sites which aren't totally inside of the 
sequence. There're two options overriding this behavior:
*  `--show-non-matching`
    In `besthit` mode user normally expects exactly one result for each
    input sequence. But for sequences shorter than motif SARUS can't find 
    the best occurence so outputs nothing.
    With this option SARUS outputs fictive result instead. This "occurence"
    has score `-∞` is located at position `-1` and has zero length.
    Position is `-1` both in genomic coordinates and relative to sequence start.
*  `--add-flanks`
    One can instead add to each sequence poly-N flanking sequences long enough
    to embrace possible site just overlapping given sequence.
    It is reasonable default choice unless you are sure that the site is deep
    inside of the sequence.    
    If one adds flanks, each sequence will have besthit even if original
    sequence is shorter than motif.
    Note that occurence coordinates can be outside of sequence! Occurence start
    can be even negative, occurence end can be higher than sequence length.

## Scores and P-values

By default SPRY-SARUS reports for each occurence their PWM scores. Often it's
more convenient to use P-value corresponding to this score instead.
To convert scores to P-values SARUS relies on precalculated mapping between
thresholds and P-values taken at several points. Such mapping can 
be obtained by [MACRO-PERFECTOS-APE](http://opera.autosome.ru/perfectosape/description) tool using 
`ru.autosome.ape.PrecalculateThresholds` package which uses uniform grid over 
logarithmic P-values with any level of precision.

If you want to work in P-values scale you should specify file with 
this mapping using `--pvalues-file FILE` option.

After that you'd be able to output occurence scores as P-values or 
logarithmic P-values (`logpvalue := -log10(P-value)`) with the following option: `--output-scoring-mode MODE`.

`MODE` can be one of: `score`/`pvalue`/`logpvalue` (default: `score`).

For example to find best occurences and report their logarithmic P-values one can run:

```java -cp sarus.jar ru.autosome.SARUS sequences.mfa motif.pwm besthit --pvalues-file threshold_pvalue_mapping.txt --output-scoring-mode logpvalue```

Also you can tell SPRY-SARUS to treat occurence threshold not as a score but 
as a P-value or logarithmic P-value: `--threshold-mode MODE`.
For example to find sites with P-value not greater than 0.0001 one can run:

```java -cp sarus.jar ru.autosome.SARUS sequences.mfa motif.pwm 0.0001 --pvalues-file threshold_pvalue_mapping.txt --threshold-mode pvalue```

## Output in `bed`-format
With option `--output-bed` it's also possible to output occurences 
in `bed`-format. BED-6 format includes:
*chromosome name*,
*start* and *end* positions in [closed; open) 0-based indexing,
*interval name*,
*score* (or P-value/logarithmic P-value),
*strand*.

Chromosome name and sequence position is infered from multi-FASTA header lines.
To generate correct genome-wide coordinates each sequence should be named:
```>chrName:posStart-posEnd```

`bedtools getfasta` generates headers in matching format.

`posEnd` can be omitted. SPRY-SARUS ignores header content after `posStart`.
If `posStart` is missing, it's set to 0 by default.
Chromosome name and position shouldn't contain spaces.

Interval name in the 4-th column of `bed`-file is combined from motif name
and sequence name. By default motif name is inferred from PWM filename but
it can be redefined using `--motif-name NAME` option

In `bed` format it's natural not to output sequence names separating motif
occurences in different sequences of multi-FASTA. In default output format
it's natural to output them - and these are defaults. But you can force
SPRY-SARUS both to suppress sequence names output in default mode and
not to suppress them in `BED`-format.

**Please note, that all the options (except for filenames) should be given in lowercase letters.**

## Output format
The default output format is fairly simple showing the sequence header 
(via `>` as in the input multifasta file), 
the PWM score, the position and the strand orientation of the motif occurence.

An example of the command line (based on example data from the webpage):

```java -jar sarus.jar SP1_peaks.mfa SP1_example.pwm besthit```

The messages are printed to STDERR and the result is printed to STDOUT
so it is possible to redirect the result into a file:

```java -jar sarus.jar SP1_peaks.mfa SP1_example.pwm besthit > result.log```

When `--output-bed` option os specified, output follows [BED-6 format specification](http://genome.ucsc.edu/FAQ/FAQformat#format1)
Interval name is composed of motif name and sequence name separated by a semicolon.
E.g.: `GATA1;chr1:100234-100567`.
