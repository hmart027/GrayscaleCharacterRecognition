package gcr.ann;

public interface NeuralNetworkInterface {

	public final int DEFAULT_INPUT_LAYER_SIZE = 100*100;
	public final int DEFAULT_HIDDEN_LAYER_SIZE = 200;
	public final int DEFAULT_OUTPUT_LAYER_SIZE = 26;
	
	public java.util.TreeMap<Integer, java.util.ArrayList<Float>> errors = new java.util.TreeMap<>();
	public java.util.ArrayList<Float> MSEs = new java.util.ArrayList<Float>();
	public java.util.ArrayList<Float> SSEs = new java.util.ArrayList<Float>();
	public java.util.ArrayList<Float> MWCs = new java.util.ArrayList<Float>();
	
	//Getters
	public int getInputLayerSize();
	
	public int getOutputLayerSize();
	
	public float getLearningRate();
	
	public float getMomentum();
	
	public float getMaxError();
	
	public float getMaxWeightChange();
	
	//Setters
	public float setLearningRate(float l);
	
	public float setMomentum(float m);
	
	public float setMaxError(float e);
	
	public void clearMaxWeightChange();
		
	// Other Key Methods
	public int[] nonLinearAct(float[] in);
	
	public float[] feedForward(float[] in);
	
	public boolean train(float[][] training, float[][] target);
	
	public float[] learnPattern(float[] in, float[] target);
	
	public float[] getLastResults();
	
//	public boolean readConfigFile(InputStream in);
	
	public boolean loadFromFile(String path);
	
//	public boolean writeConfigFile(OutputStream out);
	
	public boolean writeToFile(String path);

}
