package auto_title_generation.corpus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import auto_title_generation.CommandLine;

public class Title {

	private static HashMap<String, Integer> w2i = null;
	private static HashMap<Integer, String> i2w = null;
	private static int size = 0;

	static {
		load();
	}

	public static void load() {
		String corpusDir = CommandLine.getCorpusDir();
		String fileName = corpusDir + "/filtered_phrase";
		try {
			File f = new File(fileName);
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line;
			int count = 0;
			while ((line = br.readLine()) != null) {
				if (w2i == null) {
					size = Integer.parseInt(line);
					w2i = new HashMap<String, Integer>(size);
					i2w = new HashMap<Integer, String>(size);
				} else {
					// System.out.print(line);
					w2i.put(line, count);
					i2w.put(count, line);
					count++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int getLableSize() {
		return size;
	}

	public static int[] getTitleVec(String title) {
		String[] titleChunk = Chunker.chunk(title);
		int[] ans = new int[size];
		for (String chunk : titleChunk) {
			if (w2i.containsKey(chunk)) {
				int k = w2i.get(chunk);
				ans[k] = 1;
			}
		}
		return ans;
	}

	public static List<String> vec2title(int[] titleVec) {
		List<String> ans = new ArrayList<String>();
		for (int i = 0; i < titleVec.length; i++) {
			if (titleVec[i] == 1) {
				ans.add(i2w.get(i));
			}
		}
		return ans;
	}

	public static void test() {
		String title = "URGENT Asylum seekers end 16-day hunger strike in Australia";
		load();
		System.out.println(Arrays.toString(getTitleVec(title)));
		System.out.println();
	}
}
