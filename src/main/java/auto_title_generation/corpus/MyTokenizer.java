package auto_title_generation.corpus;

import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class MyTokenizer {
	private static TokenizerME TOKENIZER = null;
	static {
		InputStream modelIn = null;
		try {
			modelIn = Chunker.class.getResourceAsStream("/model/en-token.bin");
			TokenizerModel tmodel = new TokenizerModel(modelIn);
			TOKENIZER = new TokenizerME(tmodel);
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

	public static String[] tokenize(String p) {
		return TOKENIZER.tokenize(p);
	}
}
