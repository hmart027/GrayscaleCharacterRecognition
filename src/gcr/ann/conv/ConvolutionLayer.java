package gcr.ann.conv;

import gcr.ann.neuron.NeuronLayerInterface;

/**
 * Two Dimensional Convolution Layer with color bands
 * @author Harold Martin
 */
public class ConvolutionLayer extends NeuronLayerInterface{
	
	int numberOfDimensions;
	int[] dimensionSizes;

	public ConvolutionLayer(int inputWidth, int inputH, int kernelSize, int kernelCount){
		this.weights = new float[kernelCount][kernelSize*kernelSize];
		this.out 	=  new float[inputWidth*inputH*kernelCount];
		
	}
	
	public ConvolutionLayer(int[] inputSizes, int kernelSize, int kernelCount){
		this.weights = new float[kernelCount][kernelSize*kernelSize*inputSizes.length];
		int imgSize = inputSizes[0];
		for(int i = 1; i<inputSizes.length; i++){
			imgSize *= inputSizes[i];
		}
		
		this.out 	=  new float[kernelCount];
		
	}
	
	@Override
	public void setActThresholds(float upper, float lower) {
	}

	@Override
	public int[] calculate(int[] in) {
		return null;
	}

	@Override
	public int[] calculate(int[] in, int offset) {
		return null;
	}

	@Override
	public float[] calculateSigmoid(float[] in) {
		return null;
	}

	@Override
	public float[] calculateSigmoid(float[] in, int offset) {
		return null;
	}

	@Override
	public float[] backpropagate(float[] error) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float[] computeActivation(float[] in, int offset) {
		// TODO Auto-generated method stub
		return null;
	}

}
