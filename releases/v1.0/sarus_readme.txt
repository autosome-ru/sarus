========= SPRY-SARUS readme =========

SPRy-SARUS stands for Straightforward yet Powerful Rapid 
SuperAlphabet Representation Utilized for motif Search. 

This is a simple tool which uses superalphabet approach 
presented by [Pizzi, Rastas & Ukkonen; 2007] to scan a given set 
of sequences for (di)PWM hits scoring no less than a given threshold. 

SPRY-SARUS uses fairly simple command line format, 
accepts weight matrices in plain text files (with log-odds 
or similarly transformed weights) and DNA sequences in multifasta. 

SPRY-SARUS is written in Java (and requires Java >=1.6).
The all-in-one jar files are available on the ChIPMunk web page. 
The zip-file with sample PWMs and sequences is also available for download.

The proper command line format is printed if SARUS is executed w/o arguments:
  java -cp sarus.jar ru.autosome.SARUS
or in a shorter form 
  java -jar sarus.jar

The output from this command is:
  SPRY-SARUS command line: <sequences.multifasta> <weight.matrix> 
                           <threshold>|besthit [naive] [suppress] [transpose] [direct] [revcomp] [skipn]

You may want to provide Java with more memory in case of large sequence sets, e.g.:
  java -Xmx1G -cp sarus.jar ru.autosome.SARUS <more parameters here>

[!!] Please note, that all the arguments (except for filenames) should be given in lowercase letters.

The arguments have self-speaking names. The weight matrix can
be given either with or without header line (starting from ">", see examples).

By default each line in the file corresponds to a single position of a motif, 
i.e. each line should contain 4 (or 16) elements for PWM (diPWM).

The nucleotide order for mono-PWMs is alphabetical A-C-G-T.
The dinucleotide order for di-PWMs is also alphabetical AA-AC-AG-..-TT. 

Please note, that SARUS can use raw ChIPMunk (but not ChIPHorde) output 
extracting the resulting motif right from the log-file 
(if the ChIPMunk output was redirected into a file as suggested in ChIPMunk guide).

Additional "modifiers":

transpose
  suggests SARUS to use the transposed file format
  (letters as rows, positions as columns).
 
suppress
  suppresses sequences names in output.

besthit
  can be used instead (!) of a threshold value to force SARUS 
  look for a single best hit in each sequence.

skipn
  forces SARUS to completely skip words with N letters in sequences
  (by default N receives zero weight).

direct
  and 
revcomp
  provide single-strand search mode.

naive
  switches from the superalphabet to a naive scanning mode (useful for debugging purposes only).


The output format is fairly simple showing the sequence header 
(via ">" as in the input multifasta file), 
the PWM score, the position and the strand orientation of the PWM hit (is passing the threshold).

An example of the command line (based on example data from the webpage):
  java -jar sarus.jar SP1_peaks.mfa SP1_example.pwm besthit
The messages are printed to $STDERR and the result is printed to $STDOUT
so it is possible to redirect the result into a file:
  java -jar sarus.jar SP1_peaks.mfa SP1_example.pwm besthit > result.log


--- Using dinucleotide version ---

The dinucleotide version can be used in a similar way (with the same command-line parameters):
  java -cp sarus.jar ru.autosome.di.SARUS
For example:
  java -cp sarus.jar ru.autosome.di.SARUS SP1_peaks.mfa SP1_example.dpwm besthit > result.log

[NOTE!!!] The dinucleotide version is located in 
  ru.autosome.di
package, and this is the only difference in command line format.
