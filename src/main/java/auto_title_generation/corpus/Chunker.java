package auto_title_generation.corpus;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Sequence;
import opennlp.tools.util.Span;

public class Chunker {

	private static POSTaggerME TAGGER = null;

	private static ChunkerME CHUNKER = null;

	public static void printArray(Object[] array) {
		System.out.println(Arrays.toString(array));
	}

	static {
		InputStream modelIn = null;

		try {
			modelIn = Chunker.class.getResourceAsStream("/model/en-pos-maxent.bin");
			POSModel model = new POSModel(modelIn);
			TAGGER = new POSTaggerME(model);
			modelIn = Chunker.class.getResourceAsStream("/model/en-chunker.bin");
			ChunkerModel cmodel = new ChunkerModel(modelIn);
			CHUNKER = new ChunkerME(cmodel);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static void testpos() {
		InputStream modelIn = null;

		try {
			modelIn = Chunker.class.getResourceAsStream("/en-pos-maxent.bin");
			POSModel model = new POSModel(modelIn);
			POSTaggerME tagger = new POSTaggerME(model);
			String sent[] = new String[] { "Most", "large", "cities", "in", "the", "US", "had", "morning", "and", "afternoon", "newspapers", "." };
			String tags[] = tagger.tag(sent);

		} catch (IOException e) {
			// Model loading failed, handle the error
			e.printStackTrace();
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
				}
			}
		}

	}

	public static void testchunk() {
		InputStream modelIn = null;
		ChunkerModel model = null;

		try {
			modelIn = Chunker.class.getResourceAsStream("/model/en-chunker.bin");
			model = new ChunkerModel(modelIn);
		} catch (IOException e) {
			// Model loading failed, handle the error
			e.printStackTrace();
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
				}
			}
		}
		ChunkerME chunker = new ChunkerME(model);

		String sent[] = new String[] { "Rockwell", "International", "Corp.", "'s", "Tulsa", "unit", "said", "it", "signed", "a", "tentative", "agreement", "extending", "its", "contract", "with", "Boeing", "Co.", "to", "provide", "structural", "parts", "for", "Boeing", "'s", "747", "jetliners", "." };

		String pos[] = new String[] { "NNP", "NNP", "NNP", "POS", "NNP", "NN", "VBD", "PRP", "VBD", "DT", "JJ", "NN", "VBG", "PRP$", "NN", "IN", "NNP", "NNP", "TO", "VB", "JJ", "NNS", "IN", "NNP", "POS", "CD", "NNS", "." };

		String tag[] = chunker.chunk(sent, pos);

		Span[] spans = chunker.chunkAsSpans(sent, pos);

		String[] phrases = Span.spansToStrings(spans, sent);

		for (String p : phrases) {
			System.out.println(p);
		}

		double probs[] = chunker.probs();

		Sequence topSequences[] = chunker.topKSequences(sent, pos);

		printArray(tag);
		System.out.println(Arrays.toString(probs));
		printArray(topSequences);
	}

	public static void testTokenizer() {
		InputStream modelIn = Chunker.class.getResourceAsStream("/model/en-token.bin");
		TokenizerModel model = null;
		try {
			model = new TokenizerModel(modelIn);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
				}
			}
		}

		Tokenizer tokenizer = new TokenizerME(model);
		String tokens[] = tokenizer.tokenize("An input sample sentence.");
		printArray(tokens);
	}

	public static String[] chunk(String sent) {
		String[] sents = MyTokenizer.tokenize(sent);
		return chunk(sents);
	}

	public static String[] chunk(String[] sent) {
		String[] pos = TAGGER.tag(sent);
		Span[] spans = CHUNKER.chunkAsSpans(sent, pos);
		String[] phrases = Span.spansToStrings(spans, sent);
		return phrases;
	}

	public static void main(String[] args) {
		// String sent[] = new String[] { "Rockwell", "International", "Corp.",
		// "'s", "Tulsa", "unit", "said", "it", "signed", "a", "tentative",
		// "agreement", "extending", "its", "contract", "with", "Boeing", "Co.",
		// "to", "provide", "structural", "parts", "for", "Boeing", "'s", "747",
		// "jetliners", "." };
		// String[] p = chunk(sent);
		// printArray(p);
		// testTokenizer();
		printArray(chunk("Chinese dissidents in the United States generally favor a partial withdrawal of Beijing 's privil    eged trading status targeting state - owned firms , not complete revocation , dissident leaders s    aid here Thursday ."));
	}
}
