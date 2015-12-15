package auto_title_generation.corpus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;

import auto_title_generation.CommandLine;

public class Dictionary implements Serializable {

	private static final long serialVersionUID = 1L;

	private HashMap<String, double[]> dict = new HashMap<String, double[]>();

	private static final String dictName = "engVect.dict";

	private static Dictionary dictionary = null;

	public static Dictionary getDictionary() {
		if (dictionary == null) {
			dictionary = load();
		}
		return dictionary;
	}

	public void put(String w, double[] v) {
		this.dict.put(w, v);
	}

	public double[] getVect(String word) {
		return this.dict.get(word);
	}

	public void clear() {
		this.dict.clear();
	}

	public static Dictionary load(String dictName, int wordVectorNum) {

		Dictionary dict = new Dictionary();

		if (!dictName.startsWith("/")) {
			dictName = "/" + dictName;
		}
		String corpusPath = CommandLine.getCorpusDir();
		File f = new File(corpusPath + "/" + dictName);
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			try {
				String line;
				int count = 0;
				int all = 0;
				while ((line = br.readLine()) != null) {
					String[] l = line.split("\\s");
					if (count == 0) {
						all = Integer.parseInt(l[0]);
						if (wordVectorNum != Integer.parseInt(l[1])) {
							throw new RuntimeException("单词维度不一致！");
						}
						System.out.println("单词维度 ：" + wordVectorNum + " 字典包含" + all + "个单词");
					} else {
						String word = l[0];
						double[] wordVect = new double[wordVectorNum];
						for (int i = 0; i < wordVectorNum; i++) {
							wordVect[i] = Double.parseDouble(l[1 + i]);
						}
						dict.put(word, wordVect);
					}
					count++;
					if (count % 100000 == 0) {
						System.out.println("载入完成 " + (double) count / all * 100 + "%");
					}
				}

				System.out.println("实际载入单词数目 ：" + count);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				br.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dict;
	}

	public Dictionary load(int vectNum) {
		return load("EngVectors100.dat", vectNum);
	}

	public void saveDictionary(String fileName) {
		try {
			if (!fileName.startsWith("/")) {
				fileName = "/" + fileName;
			}
			String relativelyPath = CommandLine.getModelDir();
			fileName = relativelyPath + fileName;
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
			oos.writeObject(this);
			oos.flush();
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Dictionary loadDictionary(String fileName) {
		try {
			if (!fileName.startsWith("/")) {
				fileName = "/" + fileName;
			}

			String relativelyPath = CommandLine.getModelDir();
			fileName = relativelyPath + fileName;
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(fileName)));
			Dictionary dict = (Dictionary) in.readObject();
			in.close();
			return dict;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 默认还原的字典类,还原Dictionary对象
	 * 
	 * @return
	 */
	public static Dictionary load() {
		Dictionary di = Dictionary.loadDictionary("engVect.dict");
		return di;
	}

	public static void trainDict() {
		Dictionary dict = Dictionary.load("EngVectors100.dat", 100);
		dict.saveDictionary("engVect.dict");
		System.out.println("Done!");
	}

	public static void main(String[] args) {
		CommandLine.parseCommandOption(args);
		Dictionary dict = Dictionary.load("EngVectors(100).dat", 100);
		// Dictionary dict = new Dictionary();
		dict.saveDictionary(dictName);
		System.out.println(Arrays.toString(dict.getVect("the")));
		Dictionary di = Dictionary.loadDictionary(dictName);
		System.out.println(Arrays.toString(di.getVect("the")));
	}
}
