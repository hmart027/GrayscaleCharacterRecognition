package gcr.ann.conv;

import gcr.ann.neuron.NeuronLayerInterface;

/**
 * 2D pooling layer.
 * @author Harold Martin
 *
 */
public class MaxPoolLayer extends NeuronLayerInterface{
	
	int widthIn;
	int heightIn;
	int depth;
	int k;
	int stride;
	int widthOut;
	int heightOut;
	int[] maxLocation;
	
	/**
	 * Creates a max pooling layer with the given parameters. Commonly used values
	 * are f=3, s=2 or f=2, s=2.
	 * @param w width of image
	 * @param h height of image
	 * @param d depth (number of layers in the input)
	 * @param f kernel size
	 * @param s stride size
	 */
	public MaxPoolLayer(int w, int h, int d, int f, int s){
		this.inputSize = w*h*d;
		this.widthIn = w;
		this.heightIn = h;
		this.depth = d;
		this.k = f;
		this.stride = s;
		this.widthOut = (w-f)/s+1;
		this.heightOut = (h-f)/s+1;
		System.out.println("hO: "+heightOut);
		System.out.println("wO: "+widthOut);
		this.out = new float[this.widthOut*this.heightOut*this.depth];
		this.maxLocation = new int[out.length*2]; // times 2 is for the x and y coordinates of the max point;
	}

	public static MaxPoolLayer createMaxPoolLayer(int w, int h, int d, int f, int s){
//		int outH
		int hdiff = (h-f)%s;
		int wdiff = (w-f)%s;
		if(hdiff!=0 || wdiff!=0)
			return null;
		return new MaxPoolLayer(w, h, d, f, s);
	}
	
	@Override
	public void setActThresholds(float upper, float lower) {	}

	@Override
	public float[] computeActivation(float[] in, int offset) {
		int w=0, h=0, d=0;
		int kW=0, kH=0;
		int cIndex=offset;
		float[][][] inT = new float[depth][heightIn][widthIn];
		for (d = 0; d<depth; d++){
			for(h=0; h<heightIn; h++){
				for(w=0; w<widthIn; w++){
					inT[d][h][w]=in[cIndex++];
				}
			}
		}
		float max;
		float current;
		int dOutOffset, hOutOffset, wOutOffset;
		int dOutInc = widthOut*heightOut, hOutInc = widthOut, wOutInc = 1;	//Increment sizes
		dOutOffset = 0;
		for (d = 0; d<depth; d++){
			hOutOffset = 0;
			for(h=0; h<heightIn; h+=stride){
				wOutOffset = 0;
				for(w=0; w<widthIn; w+=stride){
					max=inT[d][h][w];
					for(kW=1; kW<k; kW++){
						current = inT[d][h][w+kW];
						if(current>max){
							max = current;
						}
					}
					for(kH=1; kH<k; kH++){
						for(kW=0; kW<k; kW++){
							current = inT[d][h+kW][w+kW];
							if(current>max){
								max = current;
							}
						}
					}
					out[dOutOffset+hOutOffset+wOutOffset]=max;
					wOutOffset += wOutInc;
				}
				hOutOffset += hOutInc;
			}
			dOutOffset += dOutInc;
		}
		return this.out;
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
		
		return null;
	}

	public int getOutputWidth(){
		return widthOut;
	}
	
	public int getOutputHeight(){
		return heightOut;
	}
}
