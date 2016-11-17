package gcr.ann;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import gcr.ann.neuron.NeuronLayer;
import gcr.ann.neuron.NeuronLayerInterface;

public class ANN4 implements NeuralNetworkInterface{
	
	private boolean initialized = false;
	private int inputLayerSize;
	private int outLayerSize;
		
	private float learningRate = 0.02f; //.02
	private float momentum = 0.00f;
	private float maxError = 0.002f;//0.02, 0.002, 0.0005
	
	private float mwc = 0; //Maximum Weight Change
	
	private int[] layerSizes;
	private NeuronLayerInterface[] layers;
			
	public ANN4(int[] layerSizes){
		init(layerSizes);
	}
	
	private void init(int[] layerSizes){
		this.layerSizes 			= layerSizes;
		this.inputLayerSize 	= layerSizes[0];
		this.outLayerSize 		= layerSizes[layerSizes.length-1];
		this.layers = new NeuronLayerInterface[layerSizes.length-1];
		for(int i=0; i<layers.length; i++){
			this.layers[i] = new NeuronLayer(layerSizes[i],layerSizes[i+1]);
		}
		initialized = true;
	}
	
	public boolean isInitialized(){
		return initialized;
	}
	
	public void widrowInit(){
		for(int i=0; i<layers.length; i++){
			NeuronLayer.widrowInit(this.layers[i]);
		}
	}
	
	public boolean train(float[][] training, float[][] target){

		System.out.println("Starting Training");
		long t0 = System.nanoTime();
		widrowInit();
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
	
	public int[] nonLinearAct(float[] in){
		int[] out = new int[in.length];
		for(int i = 0; i < in.length; i++){
			if(in[i]<0) out[i] = -1;
			if(in[i]>=0) out[i] = 1;
		}
		return out;
	}
	
	public float[] feedForward(float[] in){
		float[] res = in;
		for(int i = 0; i< layers.length; i++)
			res = layers[i].calculateSigmoid(res);
		return res;
	}
	
/*	public float[] learnPattern(float[] in, float[] target){
		float[] error = new float[target.length];
		float[] iResults = layers[0].calculateSigmoid(in);
		float[] oResults = layers[1].calculateSigmoid(iResults);
		
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
				dEIT += outputErrorInfTerms[j]*layers[1].weights[j][i+1];
			}
			inputErrorInfTerms[i] = dEIT*0.5f*(1+iResults[i])*(1-iResults[i]);
		}
		//Update Output Layer Weights
		for(int i = 0; i<layers[1].weights.length; i++){
			float d = learningRate *  outputErrorInfTerms[i];
			layers[1].weights[i][0] += d; 
			if(Math.abs(d)>mwc) mwc = Math.abs(d);					//Check for maximum weight change
			for(int j = 1; j<layers[1].weights[0].length; j++){
//				float dW = d * iResults[j-1] + momentum * (layers[1].weights[i][j] - layers[1].lastWeights[i][j]); 
				float dW = d * iResults[j-1]; 
				layers[1].weights[i][j] += dW;
//				layers[1].lastWeights[i][j] = layers[1].weights[i][j];
				if(Math.abs(dW)>mwc) mwc = Math.abs(dW);			//Check for maximum weight change
			}
		}
		//Update Input Layer Weights
		for(int i = 0; i<layers[0].weights.length; i++){
			float d = learningRate *  inputErrorInfTerms[i];
			layers[0].weights[i][0] += d; 
			if(Math.abs(d)>mwc) mwc = Math.abs(d);					//Check for maximum weight change
			for(int j = 1; j<layers[0].weights[0].length; j++){
				float dW = d * in[j-1];
				layers[0].weights[i][j] += dW;
				if(Math.abs(dW)>mwc) mwc = Math.abs(dW);			//Check for maximum weight change
			}
		}
		return error;
	}*/
	
	public float[] learnPattern(float[] in, float[] target) {
		float[] error = new float[outLayerSize];
		float[] res = feedForward(in);
		// Output Error Information Term
		for (int i = 0; i < outLayerSize; i++) {
			error[i] = (target[i] - res[i]);
		}
		float[] tE = error;
		for (int i = layers.length - 1; i >= 0; i--) {
			tE = layers[i].backpropagate(tE);
			if (layers[i].mwc > mwc) mwc = layers[i].mwc;
		}
		return error;
	}
	
	private void backPropagationLearning(float[][] in, float[][] target){
		float SSE = 0;
		int count = 0;
		float MSE = 0;
		float epoch = 0;
		//Start Trainning
		do{
			clearMaxWeightChange();
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
	
	public boolean loadFromFile(String path){
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader( new FileInputStream(path+"_config.csv")));
			if(!input.readLine().trim().equals(this.getClass().toString().trim())){
				System.out.println("Configuration file does not match this Class: ANN4");
				input.close();
				return false;
			}
			int lC = Integer.parseInt(input.readLine());
			int[] layerSizes = new int[lC];
			String[] sizes = input.readLine().trim().split(",", lC+1);
			for(int i=0; i<lC; i++){
				layerSizes[i] = Integer.parseInt(sizes[i]);
			}
			init(layerSizes);
			setLearningRate(learningRate);
			input.close();
		} catch (FileNotFoundException e) {
			System.out.println("Configuration File Missing...");
			return false;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader( new FileInputStream(path+".csv")));
			for(int l=0; l<layers.length; l++){
				NeuronLayerInterface nl = layers[l];
				for(int n=0; n<nl.weights.length; n++){
					String[] elements = input.readLine().split(",");
					for(int j = 0; j<nl.weights[0].length; j++){
						nl.weights[n][j] = Float.parseFloat(elements[j]);
					}
				}
			}
			input.close();
			return true;
		} catch (FileNotFoundException e) {
			System.out.println("File doesn't exist");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean writeToFile(String path){
		try {
			PrintWriter output = new PrintWriter(path+"_config.csv");
			output.write(this.getClass()+"\n");
			output.write(layerSizes.length+"\n");
			for(int i = 0; i< layerSizes.length; i++){
				output.write(layerSizes[i]+",");
			}
			output.write("\n");
			output.flush();
			output.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			PrintWriter output = new PrintWriter(path+".csv");
			for(int l = 0; l< layers.length; l++){
				NeuronLayerInterface nL = layers[l];
				for(int i = 0; i<nL.weights.length; i++){
					for(int j = 0; j<nL.weights[0].length; j++){
						output.write(nL.weights[i][j]+", ");
					}
					output.write("\n");
					output.flush();
				}
			}
			output.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public int getInputLayerSize() {
		return inputLayerSize;
	}

	@Override
	public int getOutputLayerSize() {
		return outLayerSize;
	}

	@Override
	public float getLearningRate() {
		return learningRate;
	}

	@Override
	public float getMomentum() {
		return momentum;
	}

	@Override
	public float getMaxError() {
		return maxError;
	}

	@Override
	public float getMaxWeightChange() {
		return mwc;
	}
	
	@Override
	public void clearMaxWeightChange() {
		this.mwc = 0;
		for(int i = 0; i<layers.length; i++){
			layers[i].mwc = 0;
		}
	}
	
	@Override
	public float setLearningRate(float l) {
		float tl = learningRate;
		learningRate = l;
		for(int i = 0; i<layers.length; i++){
			layers[i].learningRate = l;
		}
		return tl;
	}

	@Override
	public float setMomentum(float m) {
		float tm = momentum;
		momentum = m;
		return tm;
	}
	
	@Override
	public float setMaxError(float e) {
		float te = maxError;
		maxError = e;
		return te;
	}

	@Override
	public float[] getLastResults() {
		return layers[layers.length-1].out;
	}

}
