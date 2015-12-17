package auto_title_generation.cnn.dataset;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import auto_title_generation.CommandLine;
import auto_title_generation.corpus.Dictionary;
import auto_title_generation.corpus.PassageHandler;
import auto_title_generation.corpus.Title;

public class GigawordDataset {

	private List<File> passages = new ArrayList<File>();

	int wordvecLen = 0;

	int padding = 0;

	int passage_max_len = 0;

	public GigawordDataset(int wordvecLen, int padding, int passage_max_len) {
		this.wordvecLen = wordvecLen;
		this.padding = padding;
		this.passage_max_len = passage_max_len;
	}

	public int getSize() {
		return passages.size();
	}

	public Passage getPassage(int index) {
		File f = passages.get(index);
		return file2passage(f);
	}

	public Iterator<File> iter() {
		return passages.iterator();
	}

	private Passage file2passage(File f) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			try {
				String line;
				StringBuffer sb = new StringBuffer();
				String titleString = br.readLine();
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				String passage = sb.toString();
				List<double[]> ans = PassageHandler.passage2vec(passage);

				double[] tmp = new double[wordvecLen * (2 * padding + passage_max_len)];
				int j = padding * wordvecLen;
				for (int k = 0; k < ans.size() && j < tmp.length; k++) {
					double[] vec = ans.get(k);
					for (int l = 0; l < vec.length && j < tmp.length; l++) {
						tmp[j] = vec[l];
						j++;
					}
				}
				return new Passage(tmp, Title.getTitleVec(titleString));
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				br.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param wordvecLen
	 * @param passage_max_len
	 * @param padding
	 *            用于处理文本size normalization
	 */
	public static GigawordDataset loadGiga(int wordvecLen, int passage_max_len, int padding) {
		GigawordDataset dataset = new GigawordDataset(wordvecLen, padding, passage_max_len);
		String passagePath = CommandLine.getCorpusDir() + "newsourcetext/";
		System.out.println(passagePath);
		File file = new File(passagePath);
		File[] tempList = file.listFiles();
		int maxlen = 0;
		for (int i = 0; i < tempList.length; i++) {
			if (tempList[i].isFile() && tempList[i].getName().startsWith("AFP")) {
				// 读取某个文件夹下的所有文件
				System.out.println("(" + i + "/" + tempList.length + ")文件：" + tempList[i]);
				dataset.passages.add(tempList[i]);
			}
		}
		System.out.println("total passages num :" + dataset.passages.size());
		// System.out.println("maxlen:" + maxlen);
		return dataset;
	}

	public void clearAll() {
		this.passages.clear();
	}

	/**
	 * 清理某一篇文章，防止内存过度占用
	 * 
	 * @param index
	 */
	public void clear(int index) {
		this.passages.set(index, null);
	}

}
