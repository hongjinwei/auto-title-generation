package auto_title_generation.cnn.core;

import java.util.Arrays;

import auto_title_generation.cnn.core.CNN.LayerBuilder;
import auto_title_generation.cnn.core.Layer.Size;
import auto_title_generation.cnn.dataset.Dataset;
import auto_title_generation.cnn.dataset.GigawordDataset;
import auto_title_generation.cnn.utils.ConcurenceRunner;
import auto_title_generation.cnn.utils.TimedTest;
import auto_title_generation.cnn.utils.TimedTest.TestTask;
import auto_title_generation.corpus.Title;

public class RunCNN {

	private static final int WORD_VEC_LEN = 100;
	private static final int PADDING = 50;
	private static final int MAX_PASSAGE_LEN = 4000;

	private static final int REPEAT = 3;

	public static void runCnn() {
		// 创建一个卷积神经网络
		// LayerBuilder builder = new LayerBuilder();
		// builder.addLayer(Layer.buildInputLayer(new Size(28, 28)));
		// builder.addLayer(Layer.buildConvLayer(6, new Size(5, 5)));
		// builder.addLayer(Layer.buildSampLayer(new Size(2, 2)));
		// builder.addLayer(Layer.buildConvLayer(12, new Size(5, 5)));
		// builder.addLayer(Layer.buildSampLayer(new Size(2, 2)));
		// builder.addLayer(Layer.buildOutputLayer(10));

		LayerBuilder builder = new LayerBuilder();
		// 1100*100
		builder.addLayer(Layer.buildInputLayer(new Size(MAX_PASSAGE_LEN + 2 * PADDING, WORD_VEC_LEN)));
		builder.addLayer(Layer.buildConvLayer(6, new Size(5, WORD_VEC_LEN)));
		builder.addLayer(Layer.buildSampLayer(new Size(3, 1)));
		builder.addLayer(Layer.buildConvLayer(12, new Size(5, 1)));
		builder.addLayer(Layer.buildSampLayer(new Size(2, 1)));
		builder.addLayer(Layer.buildOutputLayer(Title.getLableSize()));

		CNN cnn = new CNN(builder, 50);

		// 导入数据集
		// String fileName = "dataset/train.format";
		// Dataset dataset = Dataset.load(fileName, ",", 784);// 784=28*28
		GigawordDataset dataset = GigawordDataset.loadGiga(WORD_VEC_LEN, MAX_PASSAGE_LEN, PADDING);
		cnn.train(dataset, REPEAT);// iteration times 100
		String modelName = "model.cnn";
		cnn.saveModel(modelName);
		dataset.clearAll();
		dataset = null;

		// 预测
		// CNN cnn = CNN.loadModel(modelName);

		// Dataset testset = Dataset.load("dataset/test.format", ",", -1);
		// cnn.predict(testset, "dataset/test.predict");
	}

	public static void testPredict() {
		CNN cnn = CNN.loadModel("model.cnn");

		// Dataset dataset = Dataset.load("dataset/train.format", ",", 784);//
		// 784=28*28
		GigawordDataset dataset = GigawordDataset.loadGiga(WORD_VEC_LEN, MAX_PASSAGE_LEN, PADDING);
		cnn.testPredict(dataset, 100);
	}

	public static void run() {

		new TimedTest(new TestTask() {

			@Override
			public void process() {
				runCnn();
			}
		}, 1).test();
		ConcurenceRunner.stop();

		// testPredict();
	}

}