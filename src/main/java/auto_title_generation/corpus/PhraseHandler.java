package auto_title_generation.corpus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;

import auto_title_generation.CommandLine;

public class PhraseHandler {

	private static final String out_fileName = "filtered_phrase";
	private static final int MAX_WORD_NUM = 5;
	private static final int MIN_WORD_NUM = 3;

	private static final int MIN_WORD_FREQ = 5;

	private static boolean isLegal(String chunk) {
		if (chunk == null || chunk.equals("")) {
			return false;
		}
		String[] splitedChunk = chunk.split("\\s+");
		int wordNum = splitedChunk.length - 1;

		if (chunk.startsWith("t")) {
			if (wordNum > 1) {
				return true;
			} else {
				return false;
			}
		}

		if (wordNum <= 0 || wordNum < MIN_WORD_NUM || wordNum > MAX_WORD_NUM) {
			return false;
		}
		int wordFreq = Integer.parseInt(splitedChunk[0]);
		if (wordFreq < MIN_WORD_FREQ) {
			return false;
		} else if (splitedChunk[1].startsWith("'s")) {
			return false;
		}
		return true;
	}

	private static String extractChunk(String chunkWithFreq) {
		String[] tmp = chunkWithFreq.split("\\s+");
		StringBuffer sb = new StringBuffer();
		for (int i = 1; i < tmp.length; i++) {
			sb.append(tmp[i]);
			if (i != tmp.length - 1) {
				sb.append(" ");
			}
		}
		return sb.toString();
	}

	public static void run() {

		String fileName = CommandLine.getCorpusDir() + "/counted_phrase";
		String outFileName = CommandLine.getOutputDir() + "/filtered_phrase";
		File file = new File(fileName);
		File out = new File(outFileName);
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			FileWriter fw = new FileWriter(out);
			String line;
			int count = 0;
			int chunkNum = 0;
			HashSet<String> chunks = new HashSet<String>();
			while ((line = br.readLine()) != null) {
				count++;
				if (isLegal(line)) {
					chunkNum++;
					chunks.add(extractChunk(line));
				}
				if (count % 10000 == 0) {
					System.out.println("handled: " + count);
				}
			}
			fw.write(chunkNum + "\n");
			for (String chunk : chunks) {
				fw.write(chunk + "\n");
			}

			System.out.println("phraseHandle Done!");
			fw.close();
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
