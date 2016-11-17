package gcr.ann.neuron;

public abstract class NeuronLayerInterface {

	public int inputSize;
	public int outputSize;
	
	public boolean useMomentum = false;
	public float weights[][] = null; // index 1 = neuron; 2 = weight
	public float lastWeights[][] = null; // index 1 = neuron; 2 = weight
	public float prevIn[] = null;
	public int prevInOff = 0;
	public float yin[] = null;
	public float out[] = null;
	public float upTH = 0;
	public float lowTH = 0;
	
	public float alpha = 1;	// for sigmoid function
	public float learningRate;
	public float momentum;
	public volatile float mwc;
	
	/**
	 * Set the activation Thresholds
	 * @param upper
	 * @param lower
	 */
	public abstract void setActThresholds(float upper, float lower);
	
	public abstract float[] computeActivation(float[] in, int offset);
	
	public abstract int[] calculate(int[] in);
	
	public abstract int[] calculate(int[] in, int offset);
	
	public abstract float[] calculateSigmoid(float[] in);
	
	public abstract float[] calculateSigmoid(float[] in, int offset);
	
	/**
	 * Backpropagates the error trough the current layer.
	 * @param error - Error from the upper layers
	 * @return - the Error for the lower layers.
	 */
	public abstract float[] backpropagate(float[] error);

	public static void randomInit(NeuronLayerInterface neurons){
		java.util.Random rand = new java.util.Random();
		for(float[] w: neurons.weights){
			for(int i = 0; i<w.length; i++)
				w[i]=(float)rand.nextInt(200)/100-.5f;
		}
	}
	
	public static  void widrowInit(NeuronLayerInterface neurons){
		java.util.Random rand = new java.util.Random();
		float n = neurons.inputSize;
		float p = neurons.outputSize;
		float b = (float) (0.7*Math.pow(p,1f/n));
		float mag = 0;
		float c = 0;
		randomInit(neurons);
		for(float[] w: neurons.weights){
			for(int i = 0; i<w.length; i++)
				mag += (float) Math.pow(w[i], 2);
		}
		mag = (float) Math.pow(mag, 0.5);
		c = b/mag;
		for(float[] w: neurons.weights){
			for(int i = 0; i<w.length; i++)
				w[i] *= c;
		}
		for(float[] w: neurons.weights){
			w[0] = (float)rand.nextInt(100)/100*2*b-b;
		}
	}
}
