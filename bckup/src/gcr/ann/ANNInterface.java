package gcr.ann;

import gcr.ann.neuron.NeuronLayer;
import gcr.ann.neuron.NeuronLayerInterface;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public abstract class ANNInterface {

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
	
//	public int getInputLayerSize();
	
	protected ANNInterface(){};
	
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
		float n = neurons.inputLayerSize;
		float p = neurons.outputLayerSize;
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
	
	public abstract boolean train(float[][] training, float[][] target);
	
	public abstract float[] learnPattern(float[] in, float[] target);
	
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

}
