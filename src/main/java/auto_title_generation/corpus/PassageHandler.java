package auto_title_generation.corpus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import auto_title_generation.CommandLine;

public class PassageHandler {

	private static final String passageSpliter = "------------";

	private static final int CHUNK_MIN_FREQ = 2;

	private static final int CHUNK_MAX_WORD = 5;

	private static final int CHUNK_MIN_WORD = 2;

	private static final String PHRASE_NAME = "counted_phrase";

	private static boolean isLegalPhrase(String chunk) {
		int wordNum = chunk.split("\\s").length;
		if (wordNum < CHUNK_MIN_WORD || wordNum > CHUNK_MAX_WORD) {
			return false;
		} else if (chunk.startsWith("'s")) {
			return false;
		}
		return true;
	}

	public static List<double[]> passage2vec(String passage) {
		List<double[]> ans = new ArrayList<double[]>();
		Dictionary dict = Dictionary.getDictionary();
		String[] words = MyTokenizer.tokenize(passage);
		for (String word : words) {
			double[] wordvec = dict.getVect(word);
			if (wordvec == null) {
				continue;
			}
			ans.add(wordvec);
		}

		return ans;
	}

	private static void test(String testFile) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(PassageHandler.class.getResourceAsStream("/" + testFile)));
			try {
				String title = null;
				title = br.readLine();
				String line = null;
				StringBuffer sb = new StringBuffer();
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				String content = sb.toString();

				String[] titleChunk = Chunker.chunk(title);
				String[] passageChunk = Chunker.chunk(content);

				passage2vec(content);

				Chunker.printArray(titleChunk);
				Chunker.printArray(passageChunk);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				br.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void countChunk(String chunk, HashMap<String, Integer> map, HashSet<String> resultChunks) {
		// if (!isLegalPhrase(chunk) || resultChunks.contains(chunk)) {
		// return;
		// }

		// if (CHUNK_MIN_FREQ <= 1) {
		// resultChunks.add(chunk);
		// return;
		// }

		if (map.containsKey(chunk)) {
			int num = map.get(chunk);
			map.put(chunk, num + 1);
		} else {
			map.put(chunk, 1);
		}
	}

	private static HashSet<String> filterChunk(HashMap<String, Integer> map) {
		Set<String> keys = map.keySet();
		HashSet<String> ans = new HashSet<String>();
		for (String chunk : keys) {
			if (map.get(chunk) < CHUNK_MIN_FREQ) {
				continue;
			}
			ans.add(chunk);
		}
		return ans;
	}

	public static void run() {
		HashSet<String> allChunks = new HashSet<String>();
		HashMap<String, Integer> chunkCountMap = new HashMap<String, Integer>(14000000);
		HashSet<String> titles = new HashSet<String>(3000000);

		String passagePath = CommandLine.getCorpusDir() + "newsourcetext/";
		System.out.println(passagePath);
		File file = new File(passagePath);
		File[] tempList = file.listFiles();
		String ouputDir = CommandLine.getOutputDir();
		try {
			// FileWriter fw = new FileWriter(ouputDir + "/passages");

			System.out.println("该目录下对象个数：" + tempList.length);
			for (int i = 0; i < tempList.length; i++) {
				if (tempList[i].isFile() && tempList[i].getName().startsWith("AFP")) {
					// 读取某个文件夹下的所有文件
					System.out.println("(" + i + "/" + tempList.length + ")文件：" + tempList[i]);
					try {
						BufferedReader br = new BufferedReader(new FileReader(tempList[i]));
						try {
							String title = null;
							title = br.readLine();
							String line = null;
							StringBuffer sb = new StringBuffer();
							while ((line = br.readLine()) != null) {
								sb.append(line);
							}
							String content = sb.toString();

							String[] titleChunk = Chunker.chunk(title);
							String[] passageChunk = Chunker.chunk(content);

							for (String chunk : titleChunk) {
								titles.add(chunk);
								countChunk(chunk, chunkCountMap, allChunks);
							}

							for (String chunk : passageChunk) {
								countChunk(chunk, chunkCountMap, allChunks);
							}
							// allChunks.addAll(filterChunk(chunkCountMap));
							// List<double[]> vecs = passage2vec(content);

							// fw.write("fileName: " + tempList[i].getName() +
							// "\n");
							// fw.write("title : " + title + "\n");
							// for (double[] vec : vecs) {
							// fw.write(Arrays.toString(vec) + "\n");
							// }
							// fw.write(passageSpliter);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							br.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			FileWriter phrasefw = new FileWriter(ouputDir + "/" + PHRASE_NAME);
			for (String chunk : chunkCountMap.keySet()) {
				if(titles.contains(chunk)){
					phrasefw.write("t");
				}
				phrasefw.write(String.format("%-4d %s\n", chunkCountMap.get(chunk), chunk));
			}
			// fw.close();
			phrasefw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Done!");
	}

	public static void main(String[] args) {
		CommandLine.parseCommandOption(args);
		run();
		// test("testpassage");

		// System.out.println(isLegalPhrase("'s a"));
	}
}
