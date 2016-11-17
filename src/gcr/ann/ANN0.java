package gcr.ann;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import gcr.ann.neuron.NeuronLayer;
import gcr.ann.neuron.NeuronLayer2;
import gcr.ann.neuron.NeuronLayerInterface;

public class ANN0 implements NeuralNetworkInterface{
	public final int DEFAULT_INPUT_LAYER_SIZE = 100*100;
	public final int DEFAULT_HIDDEN_LAYER_SIZE = 200;
	public final int DEFAULT_OUTPUT_LAYER_SIZE = 26;

	public int inputLayerSize;
	public int hiddenLayerSize; //5000, 100, 200
	public int outLayerSize;
	
	public NeuronLayerInterface inputLayer;	// hidden layer
	public NeuronLayerInterface outputLayer;
	
	public float learningRate = 0.02f; //.02
	public float momentum = 0.00f;
	public float maxError = 0.002f;//0.02, 0.002, 0.0005
	
	public java.util.TreeMap<Integer, java.util.ArrayList<Float>> errors = new java.util.TreeMap<>();
	public java.util.ArrayList<Float> MSEs = new java.util.ArrayList<Float>();
	public java.util.ArrayList<Float> SSEs = new java.util.ArrayList<Float>();
	public java.util.ArrayList<Float> MWCs = new java.util.ArrayList<Float>();
	public float mwc = 0; //Maximum Weight Change
	
	public ANN0(){
		this(100*100, 200, 26);
	}
	
	public ANN0(int inputLayerSize, int hiddenLayerSize, int outputLayerSize){
		this.inputLayerSize = inputLayerSize;
		this.hiddenLayerSize = hiddenLayerSize;
		this.outLayerSize = outputLayerSize;
		inputLayer 	= new NeuronLayer2(inputLayerSize,hiddenLayerSize);	// hidden layer
		outputLayer = new NeuronLayer2(hiddenLayerSize,outLayerSize, true);
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
//				dEIT += outputErrorInfTerms[j]*outputLayer.weights[j][i+1];
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
	
	public static void randomInit(NeuronLayerInterface neurons){
		java.util.Random rand = new java.util.Random();
		for(float[] w: neurons.weights){
			for(int i = 0; i<w.length; i++)
				w[i]=(float)rand.nextInt(200)/100-.5f;
		}
	}
	
	public void widrowInit(){
		widrowInit(inputLayer);
		widrowInit(outputLayer);
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
		
	public boolean loadFromFile(String path){
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader( new FileInputStream(path+"_config.csv")));
			this.inputLayerSize = Integer.parseInt(input.readLine());
			this.hiddenLayerSize = Integer.parseInt(input.readLine());
			this.outLayerSize = Integer.parseInt(input.readLine());
			inputLayer 	= new NeuronLayer(inputLayerSize,hiddenLayerSize);	// hidden layer
			outputLayer = new NeuronLayer(hiddenLayerSize,outLayerSize, true);
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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
			int cline=0;
			int outOff = inputLayer.weights.length;
			while(input.ready()){
				String[] elements = input.readLine().split(",");
				if(cline<inputLayer.weights.length){
					for(int j = 0; j<inputLayer.weights[0].length; j++){
						inputLayer.weights[cline][j] = Float.parseFloat(elements[j]);
					}
				}
				if(cline>=inputLayer.weights.length && (cline-outOff)<outputLayer.weights.length){
					for(int j = 0; j<outputLayer.weights[0].length; j++){
						outputLayer.weights[cline-outOff][j] = Float.parseFloat(elements[j]);
					}
				}
				cline++;
			}
			input.close();
			return true;
		} catch (FileNotFoundException e) {
//			e.printStackTrace();
			System.out.println("File doesn't exist");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean writeToFile(String path){
		try {
			PrintWriter output = new PrintWriter(path+"_config.csv");
			output.write(inputLayerSize+"\n");
			output.write(hiddenLayerSize+"\n");
			output.write(outLayerSize+"\n");
			output.flush();
			output.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			PrintWriter output = new PrintWriter(path+".csv");
			for(int i = 0; i<inputLayer.weights.length; i++){
				for(int j = 0; j<inputLayer.weights[0].length; j++){
					output.write(inputLayer.weights[i][j]+", ");
				}
				output.write("\n");
				output.flush();
			}
			for(int i = 0; i<outputLayer.weights.length; i++){
				for(int j = 0; j<outputLayer.weights[0].length; j++){
					output.write(outputLayer.weights[i][j]+", ");
				}
				output.write("\n");
				output.flush();
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
	public float setLearningRate(float l) {
		learningRate = l;
		return 0;
	}

	@Override
	public float setMomentum(float m) {
		momentum = m;
		return 0;
	}

	@Override
	public float setMaxError(float e) {
		maxError = e;
		return 0;
	}

	@Override
	public void clearMaxWeightChange() {
		mwc = 0;	
	}

	@Override
	public float[] getLastResults() {
		return outputLayer.out;
	}

}
