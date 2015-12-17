package auto_title_generation;

import auto_title_generation.cnn.core.RunCNN;
import auto_title_generation.corpus.Dictionary;
import auto_title_generation.corpus.PassageHandler;
import auto_title_generation.corpus.PhraseHandler;

public class Main {

	public static void main(String[] args) {
		CommandLine.parseCommandOption(args);
		String mode = CommandLine.getMode();
		System.out.println(mode);

		if (mode.toLowerCase().indexOf("passage") != -1) {
			PassageHandler.run();
		} else if (mode.toLowerCase().indexOf("traindict") != -1) {
			Dictionary.trainDict();
		} else if (mode.toLowerCase().indexOf("phrase") != -1) {
			PhraseHandler.run();
		} else if (mode.toLowerCase().indexOf("train") != -1) {
			RunCNN.runCnn();
		}

		// Title.test();
		// GigawordDataset.loadGiga(100, 1000, 50);
	}

}
