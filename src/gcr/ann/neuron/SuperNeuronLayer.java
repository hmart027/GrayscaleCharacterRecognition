package gcr.ann.neuron;

public class SuperNeuronLayer extends NeuronLayerInterface {
	
	public float reverseScaleFactor;

	public SuperNeuronLayer(int inSize, int outSize,  int upScaleBy){
		this(inSize, outSize, upScaleBy, false);
	}
	
	/**
	 * Creates a SuperNeuronLayer which could be split into many sub nets.
	 * @param inSize - Size of the input Layer
	 * @param outSize - Size of the output Layer
	 * @param upScaleBy - creates this many sub networks of the above specified sizes.
	 * @param keepLastWeights - whether to keep the weights of the last itteration.
	 */
	public SuperNeuronLayer(int inSize, int outSize,  int upScaleBy, boolean useMomentum){
		if(upScaleBy<1) upScaleBy=1;
		reverseScaleFactor = 1.0f/(float)upScaleBy;
		this.inputSize = inSize;
		this.outputSize = outSize;
		this.weights 	=  new float[outSize*upScaleBy][inSize+1];
		this.yin 	=  new float[outSize*upScaleBy];
		this.useMomentum = useMomentum;
		if(useMomentum){
			this.lastWeights 	=  new float[outputSize*upScaleBy][inSize+1];
		}
	}
	
	public void setActThresholds(float upper, float lower){
		this.lowTH = lower;
		this.upTH = upper;
	}
	
	public int[] calculate(int[] in){
		return calculate(in, 0);
	}
	
	public int[] calculate(int[] in, int offset){
		int output[] = new int[weights.length];
		int chunckLength = (int) (weights.length*reverseScaleFactor);
		for(int neuron = 0; neuron < weights.length; neuron++){
			float out = weights[neuron][0];
			for(int weight = 1; weight < weights[0].length; weight++){
				out += weights[neuron][weight]*in[weight-1+offset];
			}
			output[neuron] = 0;
			if(out >= upTH) 	output[neuron] = 1;
			if(out < lowTH) 	output[neuron] = -1;
			yin[neuron] = out;
			if(neuron%chunckLength == 0 && neuron>0){
				offset+=inputSize;
			}
		}
		return output;
	}
	
	public float[] calculateSigmoid(float[] in){
		return calculateSigmoid(in, 0);
	}
	
	public float[] calculateSigmoid(float[] in, int offset){
		float output[] = new float[weights.length];
		int chunckLength = (int) (weights.length*reverseScaleFactor);
		for(int neuron = 0; neuron < weights.length; neuron++){
			float out = weights[neuron][0];
			for(int weight = 1; weight < weights[0].length; weight++){
				out += weights[neuron][weight]*in[weight-1+offset];
			}
			yin[neuron] = out;
			output[neuron] = (float) (2d/(1d + Math.exp(-out))-1d);
			if(neuron%chunckLength == 0 && neuron>0){
				offset+=inputSize;
			}
		}
		return output;
	}

	@Override
	public float[] backpropagate(float[] eit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float[] computeActivation(float[] in, int offset) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
