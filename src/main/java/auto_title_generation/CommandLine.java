package auto_title_generation;

public class CommandLine {

	private static final String MODELDIR = "modelDir";
	private static final String CORPUSDIR = "corpusDir";
	private static final String MODE = "mode";
	private static final String OUTPUTDIR = "outputDir";
	private static final String WORKDIR = "workDir";

	public static void parseCommandOption(String[] args) {
		int i = 0;
		while (i < args.length) {
			if (args[i].toLowerCase().indexOf("mode") != -1) {
				i++;
				System.setProperty(MODE, args[i]);
			} else if (args[i].toLowerCase().indexOf("workdir") != -1) {
				i++;
				System.setProperty(WORKDIR, args[i]);
			}
			i++;
		}
	}

	public static String getModelDir() {
		return System.getProperty(WORKDIR) + "/model/";
	}

	public static String getMode() {
		return System.getProperty(MODE);
	}

	public static String getCorpusDir() {
		return System.getProperty(WORKDIR) + "/corpus/";
	}

	public static String getOutputDir() {
		return System.getProperty(WORKDIR) + "/output/";
	}
}
