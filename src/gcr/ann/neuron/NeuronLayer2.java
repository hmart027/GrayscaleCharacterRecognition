package gcr.ann.neuron;

public class NeuronLayer2 extends NeuronLayerInterface{
	
	public NeuronLayer2(int in, int out, boolean keepLastWeights){
		this.inputSize = in;
		this.outputSize = out;
		this.weights 	=  new float[out][in+1];
		this.yin 	=  new float[out];
		this.out 	=  new float[out];
		this.useMomentum = keepLastWeights;
		if(keepLastWeights){
			this.lastWeights 	=  new float[out][in+1];
		}
	}
	
	public NeuronLayer2(int in, int out){
		this(in, out, false);
	}
	
	public void setActThresholds(float upper, float lower){
		this.lowTH = lower;
		this.upTH = upper;
	}
	
	public int[] calculate(int[] in){
		return calculate(in, 0);
	}
	
	public int[] calculate(int[] in, int o){
		int output[] = new int[weights.length];
		for(int neuron = 0; neuron < weights.length; neuron++){
			float out = weights[neuron][0];
			for(int weight = 1; weight < weights[0].length; weight++){
				out += weights[neuron][weight]*in[weight-1+o];
			}
			output[neuron] = 0;
			if(out >= upTH) 	output[neuron] = 1;
			if(out < lowTH) 	output[neuron] = -1;
			yin[neuron] = out;
		}
		return output;
	}
	
	public float[] calculateSigmoid(float[] in){
		return calculateSigmoid(in, 0);
	}
	
	public float[] calculateSigmoid(float[] in, int o){
		float output[] = new float[weights.length];
		for(int neuron = 0; neuron < weights.length; neuron++){
			float out = weights[neuron][0];
			for(int weight = 1; weight < weights[0].length; weight++){
				out += weights[neuron][weight]*in[weight-1+o];
			}
			yin[neuron] = out;
			output[neuron] = (float) (2d/(1d + Math.exp(-out))-1d);
		}
		return output;
	}

	@Override
	public float[] backpropagate(float[] error) {		
		float[] outputErrorInfTerms = new float[outputSize];
		float[] inputErrorInfTerms = new float[inputSize];
		//Output Error Information Term		
		for(int i = 0; i<outputSize; i++){
			outputErrorInfTerms[i] = error[i]*0.5f*(1+out[i])*(1-out[i]);
		}
		//Input Layer Error
		for(int i = 0; i<inputSize; i++){
			float dE = 0 ;
			for(int j = 0; j<outputErrorInfTerms.length; j++){
				dE += outputErrorInfTerms[j]*weights[j][i];
			}
			inputErrorInfTerms[i] = dE;
		}
//		//Update Output Layer Weights
//		for(int i = 0; i<outputLayerSize; i++){
//			float d = learningRate *  outputErrorInfTerms[i];
//			weights[i][0] += d; 
//			if(Math.abs(d)>mwc) mwc = Math.abs(d);					//Check for maximum weight change
//			for(int j = 1; j<weights[0].length; j++){
//				float dW = d * iResults[j-1] + momentum * (outputLayer.weights[i][j] - outputLayer.lastWeights[i][j]); 
//				outputLayer.weights[i][j] += dW;
//				outputLayer.lastWeights[i][j] = outputLayer.weights[i][j];
//				if(Math.abs(dW)>mwc) mwc = Math.abs(dW);			//Check for maximum weight change
//			}
//		}
		return inputErrorInfTerms;
	}

	@Override
	public float[] computeActivation(float[] in, int offset) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
