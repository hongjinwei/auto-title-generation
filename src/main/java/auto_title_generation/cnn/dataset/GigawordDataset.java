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

	private List<Passage> passages = new ArrayList<Passage>();
	private String[] fileList;

	public int getDataNum() {
		return passages.size();
	}

	public Passage getPassage(int index) {
		return passages.get(index);
	}

	public Iterator<Passage> iter() {
		return passages.iterator();
	}

	/**
	 * 
	 * @param wordvecLen
	 * @param passage_max_len
	 * @param padding
	 *            用于处理文本size normalization
	 */
	public static GigawordDataset loadGiga(int wordvecLen, int passage_max_len, int padding) {
		GigawordDataset dataset = new GigawordDataset();
		String passagePath = CommandLine.getCorpusDir() + "newsourcetext/";
		System.out.println(passagePath);
		File file = new File(passagePath);
		File[] tempList = file.listFiles();
		int maxlen = 0;
		for (int i = 0; i < tempList.length; i++) {
			if (tempList[i].isFile() && tempList[i].getName().startsWith("AFP")) {
				// 读取某个文件夹下的所有文件
				System.out.println("(" + i + "/" + tempList.length + ")文件：" + tempList[i]);
				try {
					BufferedReader br = new BufferedReader(new FileReader(tempList[i]));
					try {
						String line;
						StringBuffer sb = new StringBuffer();
						String titleString = br.readLine();
						while ((line = br.readLine()) != null) {
							sb.append(line);
						}
						String passage = sb.toString();
						List<double[]> ans = PassageHandler.passage2vec(passage);
						if (maxlen < ans.size()) {
							maxlen = ans.size();
						}

						double[] tmp = new double[wordvecLen * (2 * padding + passage_max_len)];
						int j = padding * wordvecLen;
						for (int k = 0; k < ans.size() && j < tmp.length; k++) {
							double[] vec = ans.get(k);
							for (int l = 0; l < vec.length && j < tmp.length; l++) {
								tmp[j] = vec[l];
								j++;
							}
						}
						dataset.passages.add(new Passage(tmp, Title.getTitleVec(titleString)));
					} catch (Exception e) {
						// TODO: handle exception
					}
				} catch (Exception e) {

				}
			}
		}
		Dictionary.getDictionary().clear();
		System.out.println("maxlen:" + maxlen);
		return dataset;
	}

	public void clear() {
		this.passages.clear();
	}

}
