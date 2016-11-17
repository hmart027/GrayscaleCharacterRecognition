package gcr.ann;

import java.util.ArrayList;
import java.util.Collections;

public class ANNOutput implements Comparable<ANNOutput>{
	public char c;
	public float prob;
	
	public ANNOutput(char c, float p){
		this.c = c;
		this.prob = p;
	}
	
	@Override
	public int compareTo(ANNOutput a) { // inverse compare
		if(this.prob>a.prob) return -1;
		if(this.prob<a.prob) return 1;
		return 0;
	}
	
	public static ArrayList<ANNOutput> getOrderOutputs(float[] res){
		ArrayList<ANNOutput> out = new ArrayList<>();
		for(int i=0; i<res.length; i++){
			out.add(new ANNOutput((char)(i+97), res[i]));
		}
		Collections.sort(out);
		return out;
	}
}