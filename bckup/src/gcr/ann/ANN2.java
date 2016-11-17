package gcr.ann;

import gcr.ann.neuron.NeuronLayer;
import gcr.ann.neuron.SuperNeuronLayer;

public class ANN2 extends ANNInterface{

	public int subLayerCount	=	16;
	public float[] lastResults = null;
		
	public ANN2(){
		this( 100*100, 16, 200, 26);
	}
	
	public ANN2(int inputLayerSize, int inputLayerCount, int hiddenLayerSize, int outputLayerSize){
		this.inputLayerSize = inputLayerSize;
		this.hiddenLayerSize = hiddenLayerSize;
		this.outLayerSize = outputLayerSize;
		this.subLayerCount = inputLayerCount;
		inputLayer 	= new SuperNeuronLayer(inputLayerSize,hiddenLayerSize, inputLayerCount);	// hidden layer
		outputLayer = new NeuronLayer(hiddenLayerSize*subLayerCount,outLayerSize, true);
	}
	
	public int[] nonLinearAct(float[] in){
		int[] out = new int[in.length];
		for(int i = 0; i < in.length; i++){
			if(in[i]<0) out[i] = -1;
			if(in[i]>=0) out[i] = 1;
		}
		return out;
	}
	
	public float[] feedForward(float[] in){
		float[] inputLayerReults = inputLayer.calculateSigmoid(in);
		float[] outputLayerResults = outputLayer.calculateSigmoid(inputLayerReults);
		return outputLayerResults;
	}
	
	public float[] learnPattern(float[] in, float[] target){
		float[] error = new float[target.length];
		float[] iResults = inputLayer.calculateSigmoid(in);
		float[] oResults = outputLayer.calculateSigmoid(iResults);
		lastResults = oResults;
		
		float[] outputErrorInfTerms = new float[oResults.length];
		float[] inputErrorInfTerms = new float[iResults.length];
		//Output Error Information Term		
		for(int i = 0; i<oResults.length; i++){
			error[i] = (target[i] - oResults[i]);
			outputErrorInfTerms[i] = error[i]*0.5f*(1-oResults[i]*oResults[i]);		//Using Sigmoid Function
		}
		//Hidden Layer Error Information term
		for(int i = 0; i<iResults.length; i++){
			float dEIT = 0 ;
			for(int j = 0; j<outputErrorInfTerms.length; j++){
				dEIT += outputErrorInfTerms[j]*outputLayer.weights[j][i];
			}
			inputErrorInfTerms[i] = dEIT*0.5f*(1-iResults[i]*iResults[i]);
		}
		//Update Output Layer Weights
		for(int i = 0; i<outputLayer.weights.length; i++){
			float d = learningRate *  outputErrorInfTerms[i];
			outputLayer.weights[i][0] += d; 
			if(Math.abs(d)>Math.abs(mwc)) mwc = d;					//Check for maximum weight change
			for(int j = 1; j<outputLayer.weights[0].length; j++){
				float dW = d * iResults[j-1] + momentum * (outputLayer.weights[i][j] - outputLayer.lastWeights[i][j]); 
				outputLayer.weights[i][j] += dW;
				outputLayer.lastWeights[i][j] = outputLayer.weights[i][j];
				if(Math.abs(dW)>Math.abs(mwc)) mwc = dW;			//Check for maximum weight change
			}
		}
		//Update Input Layer Weights
		int chunckLength = (int) (((SuperNeuronLayer)inputLayer).weights.length*((SuperNeuronLayer)inputLayer).reverseScaleFactor);
		int offset = 0;
		for(int i = 0; i<inputLayer.weights.length; i++){
			float d = learningRate *  inputErrorInfTerms[i];
			inputLayer.weights[i][0] += d; 
			if(Math.abs(d)>Math.abs(mwc)) mwc = d;					//Check for maximum weight change
			for(int j = 1; j<inputLayer.weights[0].length; j++){
				float dW = d * in[j-1+offset];
				inputLayer.weights[i][j] += dW;
				if(Math.abs(dW)>Math.abs(mwc)) mwc = dW;			//Check for maximum weight change
			}
			if(i%chunckLength == 0 && i>0){
				offset+=((SuperNeuronLayer)inputLayer).inputLayerSize;
			}
		}
		return error;
	}

	
	@Override
	public boolean train(float[][] training, float[][] target) {
		// TODO Auto-generated method stub
		return false;
	}

}
