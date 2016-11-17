package gcr.ann;

import java.util.ArrayList;
import java.util.Collections;

public class NeuralNetworkOutput implements Comparable<NeuralNetworkOutput>{
	public char c;
	public float prob;
	
	public NeuralNetworkOutput(char c, float p){
		this.c = c;
		this.prob = p;
	}
	
	@Override
	public int compareTo(NeuralNetworkOutput a) { // inverse compare
		if(this.prob>a.prob) return -1;
		if(this.prob<a.prob) return 1;
		return 0;
	}
	
	public static ArrayList<NeuralNetworkOutput> getOrderOutputs(float[] res){
		ArrayList<NeuralNetworkOutput> out = new ArrayList<>();
		for(int i=0; i<res.length; i++){
			out.add(new NeuralNetworkOutput((char)(i+97), res[i]));
		}
		Collections.sort(out);
		return out;
	}
}