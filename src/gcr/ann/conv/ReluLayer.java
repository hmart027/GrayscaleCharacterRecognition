package gcr.ann.conv;

import gcr.ann.neuron.NeuronLayerInterface;

public class ReluLayer extends NeuronLayerInterface{
	
	private boolean[] zeroed;
	float[] error;
	float[] inputError;
	
	public ReluLayer(int inputSize){
		this.inputSize = inputSize;
		this.outputSize = inputSize;
		this.out = new float[this.outputSize];
		this.zeroed = new boolean[inputSize];
		this.inputError = new float[inputSize];
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
		this.error = error;
		float e;
		for(int i = 0; i<inputSize; i++){
			e = error[i];
			if(zeroed[i]) e = 0;
			inputError[i] = e;
		}
		return inputError;
	}

	@Override
	public float[] computeActivation(float[] in, int offset) {
		this.prevIn = in;
		this.prevInOff = offset;
		float t;
		boolean zeroed;
		for(int i = 0; i<outputSize; i++){
			t = in[offset+i];
			zeroed = false;
			if(t<0){
				t = 0;
				zeroed = true;
			}
			this.zeroed[i] = zeroed;
			this.out[i] = t;
		}
		return this.out;
	}

}
