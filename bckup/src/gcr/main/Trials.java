package gcr.main;

import pilsner.fastica.BelowEVFilter;
import pilsner.fastica.FastICA;
import pilsner.fastica.FastICAConfig;
import pilsner.fastica.FastICAException;
import pilsner.fastica.TanhCFunction;
import pilsner.fastica.FastICAConfig.Approach;
import image.tools.ITools;
import img.ImageManipulation;

public class Trials {
	
	public Trials(){
		byte[][][] img = ImageManipulation.loadImage("/home/harold/Pictures/p7.jpg");
		double[][] imgN = ITools.normalizeImage(ITools.byte2double(img[0]));
		int[] s = new int[]{imgN.length, imgN[0].length};

//		new IViewer("Original", ImageManipulation.getGrayBufferedImage(img[0]));
		double[][] k;
		double[][] tE;
		double[][] edges = new double[6][s[0]*s[1]];
		byte[][] out;
		
		for(int i = 0; i<6; i++){
			k = ITools.getEdgeKernel(i*30);
			tE = ITools.abs(ITools.convolve(imgN, k));
			edges[i] = imageToArray(tE);
			out = ITools.normilize(tE);
			ImageManipulation.writeImage(out, "/home/harold/Pictures/edges/K"+i+".png");
//			new IViewer("Angle:"+(i*10), ImageManipulation.getGrayBufferedImage(out));
		}
		
		double[][] output = null;
		double[][] separationMatrix = null;
		try {
			double[][] mMatrix = new double[edges.length][edges.length];
			for (int v = 0; v < edges.length; v++) {
				for (int u = 0; u < edges.length; u++) {
					if (u == v)
						mMatrix[v][u] = 1;
					else
						mMatrix[v][u] = 0;
					System.out.print(mMatrix[v][u] + "\t");
				}
				System.out.println();
			}
			FastICA ica = new FastICA(edges, new FastICAConfig(mMatrix.length, Approach.SYMMETRIC, 1.0, 1.0e-12, 1000,
					null), new TanhCFunction(1.0), new BelowEVFilter(1.0e-12, false), new ProgressListener());
			separationMatrix = ica.getSeparatingMatrix();
			output = ica.getICVectors();
		} catch (FastICAException e) {
			e.printStackTrace();
		}
		System.out.println();
		for (int v = 0; v < separationMatrix.length; v++) {
			for (int u = 0; u < separationMatrix[0].length; u++) {
				System.out.print(separationMatrix[v][u] + "\t");
			}
			System.out.println();
		}
		for(int i = 0; i<output.length; i++){
			out = ITools.normilize(arrayToImage(output[i], s[0], s[1]));
			ImageManipulation.writeImage(out, "/home/harold/Pictures/edges/IC"+i+".png");
//			new IViewer("IC:"+i, ImageManipulation.getGrayBufferedImage(out));
		}
	}
	
	public static double[] imageToArray(double[][] img){
		double[] out = new double[img.length*img[0].length];
		int c = 0;
		for (int y = 0; y < img.length; y++) {
			for (int x = 0; x < img[0].length; x++) {
				out[c++] = img[y][x];
			}
		}
		return out;
	}
	
	public static double[][] arrayToImage(double[] img, int h, int w){
		double[][] out = new double[h][w];
		int c = 0;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				out[y][x] = img[c++];
			}
		}
		return out;
	}

	public static void main(String[] args) {
		new Trials();
	}
	
 	public class ProgressListener implements pilsner.fastica.ProgressListener{
		ComputationState pState = null;
		int pComp= -1;
		@Override
		public void progressMade(ComputationState state, int component,
				int iteration, int maxComps) {
			if(pState != state){
				System.out.print("\n"+state.name());
				pState = state;
				pComp= -1;
			}
			if (pComp != component) {
				System.out.print("\n"+component+": ");
				pComp = component;
			} else {
				System.out.print(".");
			}
		}
		
	}

}
