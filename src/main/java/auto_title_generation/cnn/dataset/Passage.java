package auto_title_generation.cnn.dataset;

import java.util.List;

public class Passage {

	double[] attrs;
	int[] titleVec;

	public Passage(double[] passageVecs, int[] titlevec) {
		this.titleVec = titlevec;
		this.attrs = passageVecs;
	}
	
	public double[] getAttrs(){
		return attrs;
	}
	
	public int[] getTitleVec(){
		return titleVec;
	}
	
}
