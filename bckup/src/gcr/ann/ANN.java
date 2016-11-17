package gcr.ann;

import gcr.ann.neuron.NeuronLayer;

public class ANN extends ANNInterface{
			
	public ANN(){
		this(100*100, 200, 26);
	}
	
	public ANN(int inputLayerSize, int hiddenLayerSize, int outputLayerSize){
		this.inputLayerSize = inputLayerSize;
		this.hiddenLayerSize = hiddenLayerSize;
		this.outLayerSize = outputLayerSize;
		inputLayer 	= new NeuronLayer(inputLayerSize,hiddenLayerSize);	// hidden layer
		outputLayer = new NeuronLayer(hiddenLayerSize,outLayerSize, true);
	}
	
	public boolean train(float[][] training, float[][] target){

		System.out.println("Starting Training");
		long t0 = System.nanoTime();
		widrowInit(inputLayer);
		widrowInit(outputLayer);
//		initializeWeights(inputLayer);
//		initializeWeights(outputLayer);
		backPropagationLearning(training, target);
		long t2 = System.nanoTime();
		System.out.println("Done Training; took: "+(t2-t0)/1000 +" us");
		return true;			
	}
	
	public void initializeWeights(NeuronLayer neurons){
		java.util.Random rand = new java.util.Random();
		for(float[] w: neurons.weights){
			for(int i = 0; i<w.length; i++)
				w[i]=(float)rand.nextInt(100)/100-.5f;
		}
	}

	public char getLetter(int[] in){
		char out = ' ';
		if(in[0] == -1 && in[1] == -1 && in[2] ==  1) out = 'A';
		if(in[0] == -1 && in[1] ==  1 && in[2] == -1) out = 'E';
		if(in[0] == -1 && in[1] ==  1 && in[2] ==  1) out = 'I';
		if(in[0] ==  1 && in[1] == -1 && in[2] == -1) out = 'O';
		if(in[0] ==  1 && in[1] == -1 && in[2] ==  1) out = 'U';
		return out;
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
		
		float[] outputErrorInfTerms = new float[oResults.length];
		float[] inputErrorInfTerms = new float[iResults.length];
		//Output Error Information Term		
		for(int i = 0; i<oResults.length; i++){
			error[i] = (target[i] - oResults[i]);
			outputErrorInfTerms[i] = error[i]*0.5f*(1+oResults[i])*(1-oResults[i]);
		}
		//Hidden Layer Error Information term
		for(int i = 0; i<iResults.length; i++){
			float dEIT = 0 ;
			for(int j = 0; j<outputErrorInfTerms.length; j++){
				dEIT += outputErrorInfTerms[j]*outputLayer.weights[j][i];
			}
			inputErrorInfTerms[i] = dEIT*0.5f*(1+iResults[i])*(1-iResults[i]);
		}
		//Update Output Layer Weights
		for(int i = 0; i<outputLayer.weights.length; i++){
			float d = learningRate *  outputErrorInfTerms[i];
			outputLayer.weights[i][0] += d; 
			if(Math.abs(d)>mwc) mwc = Math.abs(d);					//Check for maximum weight change
			for(int j = 1; j<outputLayer.weights[0].length; j++){
				float dW = d * iResults[j-1] + momentum * (outputLayer.weights[i][j] - outputLayer.lastWeights[i][j]); 
				outputLayer.weights[i][j] += dW;
				outputLayer.lastWeights[i][j] = outputLayer.weights[i][j];
				if(Math.abs(dW)>mwc) mwc = Math.abs(dW);			//Check for maximum weight change
			}
		}
		//Update Input Layer Weights
		for(int i = 0; i<inputLayer.weights.length; i++){
			float d = learningRate *  inputErrorInfTerms[i];
			inputLayer.weights[i][0] += d; 
			if(Math.abs(d)>mwc) mwc = Math.abs(d);					//Check for maximum weight change
			for(int j = 1; j<inputLayer.weights[0].length; j++){
				float dW = d * in[j-1];
				inputLayer.weights[i][j] += dW;
				if(Math.abs(dW)>mwc) mwc = Math.abs(dW);			//Check for maximum weight change
			}
		}
		return error;
	}
	
	public void backPropagationLearning(float[][] in, float[][] target){
		float SSE = 0;
		int count = 0;
		float MSE = 0;
		float epoch = 0;
		//Start Trainning
		do{
			mwc = 0;
			SSE = 0;
			count = 0;
			for(int i = 0; i < in.length; i++){
				for(float f:learnPattern(in[i], target[i])){
					SSE += Math.pow(f, 2);
					count++;
				}
			}
			MSE = SSE/(float)count;
			epoch ++ ;
			SSEs.add(SSE);
			MSEs.add(MSE);
			MWCs.add(mwc);
		}while(MSE > maxError);
		System.out.println("Took: "+epoch+" epochs");
	}

}
