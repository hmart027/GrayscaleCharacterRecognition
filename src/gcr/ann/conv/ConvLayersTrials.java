package gcr.ann.conv;

import image.tools.IViewer;
import img.ImageManipulation;

public class ConvLayersTrials {
	
	ConvLayersTrials(){
//		byte[][][] img = ImageManipulation.loadImage("/home/harold/Pictures/eye.png");
		byte[][][] img = ImageManipulation.loadImage("/home/harold/Pictures/f16cp.jpg");
		System.out.println(img[0].length + ", " + img[0][0].length);
		new IViewer(ImageManipulation.getBufferedImage(img)); 
		MaxPoolLayer pool = MaxPoolLayer.createMaxPoolLayer(1024, 1024, 3, 2, 2);
		float[] out = pool.computeActivation(getImgArray(img), 0);
		
		new IViewer(ImageManipulation.getBufferedImage(getImg(out, pool.getOutputHeight()))); 
		
	}

	public static void main(String[] args) {
		new ConvLayersTrials();
	}
	
	public float[] getImgArray(byte[][][] img){
		float[] out = new float[1024*1024*3];
		int index = 0;
		for(int d=0; d<3; d++){
			index = d*1024*1024;
			for(int h=0; h<img[0].length; h++){
				for(int w=0; w<img[0][0].length; w++){
					out[index++] = img[d][h][w] & 0x0FF;
				}
			}
		}
		return out;
	}
	
	public byte[][][] getImg(float[] img, int n){
		byte[][][] out = new byte[3][n][n];
		int index = 0;
		for(int d=0; d<3; d++){
			for(int h=0; h<n; h++){
				for(int w=0; w<n; w++){
					out[d][h][w] = (byte) img[index++];
				}
			}
		}
		return out;
	}

}
