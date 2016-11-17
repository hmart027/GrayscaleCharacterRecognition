package gcr.ann;

import edu.fiu.cate.ann.NeuralNetwork;

public class ANN3 extends NeuralNetwork implements NeuralNetworkInterface {

	int[] layerSizes;
	
	public ANN3(int[] layerSizes) {
		super(layerSizes);
		this.layerSizes = layerSizes;
	}

	@Override
	public int getInputLayerSize() {
		return layerSizes[0];
	}

	@Override
	public int getOutputLayerSize() {
		return getOutputSize();
	}

	@Override
	public int[] nonLinearAct(float[] in) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean train(float[][] training, float[][] target) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean loadFromFile(String path) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean writeToFile(String path) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clearMaxWeightChange() {
		// TODO Auto-generated method stub
	}

}
