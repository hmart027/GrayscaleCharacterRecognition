package gcr.ann.neuron;

public abstract class NeuronLayerInterface {

	public int inputLayerSize;
	public int outputLayerSize;
	
	public boolean keepLastWeights = false;
	public float weights[][] = null; // index 1 = neuron; 2 = weight
	public float lastWeights[][] = null; // index 1 = neuron; 2 = weight
	public float yin[] = null;
	public float out[] = null;
	public float upTH = 0;
	public float lowTH = 0;
	
	public float alpha = 1;	// for sigmoid function
	public float learningRate;
	public float momentum;
	public float mwc;
	
	/**
	 * Set the activation Thresholds
	 * @param upper
	 * @param lower
	 */
	public abstract void setActThresholds(float upper, float lower);
	
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
}
