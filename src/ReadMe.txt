Sam Hage
Philip Chang
Homework 5

Files: TextGenerator.java, kimTwitter.txt, kantyeTwitter.txt, ronTwitter.txt (we didn't use the WordKGram class)
Output files: out-char1.txt, out-char3.txt, out-char5.txt, out-char8.txt, out-word1.txt, out-word2.txt, out-word3.txt, dummy.txt

Our output is stored in the text files in strings of approximate tweet length. In each case, there are ten for Kim, ten for Kantye, then ten for Ron Paul.

Sometimes we would get a null pointer exception during our many phases of testing. We suspect it's because the last k-gram of the text doesn't map to anything, so we tried wrapping the last gram around to the beginning. This may have solved it, but the bug was infrequent since the probability is low that any given tweet with try to use that gram, so it could persist.

Twitter users:
Kim Kardashian (@KimKardashian): personal twitter of a woman famous for being famous.
Kantye (@KantyeWest): a hybrid of Kanye West and Immanuel Kant. Combines Kanye's lyrics with Kantian sayings.
Ron Paul (@RonPaul): prominent libertarian politician and thinker.

Our results varied between users only in the style of the tweets, which we found to be fairly distinguishable. Variance in k created much more variance in output. 1 character generates more or less nonsense; 3 character, 5 character, 1 word, and 2 word give fairly convincing but not terribly comprehensible output; and 8 character and 3 word can basically output an entire tweet from the training text.

Our "human" improvements were all grammatical/syntactic. We made sure, for instance, that all tweets begin with a capitalized word and end in a period, unless there was already appropriate punctuation. This greatly increases how realistic the tweets look.