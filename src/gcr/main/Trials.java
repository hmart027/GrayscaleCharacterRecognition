package gcr.main;

import pilsner.fastica.BelowEVFilter;
import pilsner.fastica.FastICA;
import pilsner.fastica.FastICAConfig;
import pilsner.fastica.FastICAException;
import pilsner.fastica.TanhCFunction;
import pilsner.fastica.FastICAConfig.Approach;
import image.tools.ITools;
import image.tools.IViewer;
import img.ImageManipulation;

public class Trials {
	
	public Trials(){
		
		byte[][][] imgO = ImageManipulation.loadImage("/media/DATA/School/Research/BookReader/software/character DB/InftyCDB-1/InftyCDB-1/Images/00_003.png");
		
		byte[][] img = ITools.crop(0, 0, 56, 59, imgO[0]);
		long t0 = System.currentTimeMillis();
		float[][] res1 = processCharacter(img);
		long t1 = System.currentTimeMillis() - t0;
		
		t0 = System.currentTimeMillis();
		double[][] tout = ITools.byte2double(ITools.normilize(img));
		tout = ITools.getLaplacianMag(tout);
		int[] cent = GCRTools.getCentroidLocation(tout);
		float[][] res2 = GCRTools.square2sizeAndCenter(tout, 100, cent);
		long t2 = System.currentTimeMillis() - t0;
		
		System.out.println(res1.length+ ", "+ res2.length);
		
//		boolean eq = true;
//		int c = 0;
//		float md = 0;
//		for(int i=0; i<res1.length; i++){
//			if(res1[i]!=res2[i]){
//				float d = Math.abs(res2[i]-res1[i]);
//				if(d>md) md =d;
//				eq = false;
//				c++;
//			}
//		}
//		System.out.println(eq+", "+c+", "+md);
		System.out.println("T1: "+t1);
		System.out.println("T2: "+t2);
		
		new IViewer(ImageManipulation.getGrayBufferedImage(ITools.normalize(res1)));
		new IViewer(ImageManipulation.getGrayBufferedImage(ITools.normalize(res2)));
		
		
//		byte[][][] img = ImageManipulation.loadImage("/home/harold/Pictures/p7.jpg");
//		double[][] imgN = ITools.normalizeImage(ITools.byte2double(img[0]));
//		int[] s = new int[]{imgN.length, imgN[0].length};
//
////		new IViewer("Original", ImageManipulation.getGrayBufferedImage(img[0]));
//		double[][] k;
//		double[][] tE;
//		double[][] edges = new double[6][s[0]*s[1]];
//		byte[][] out;
//		
//		for(int i = 0; i<6; i++){
//			k = ITools.getEdgeKernel(i*30);
//			tE = ITools.abs(ITools.convolve(imgN, k));
//			edges[i] = imageToArray(tE);
//			out = ITools.normilize(tE);
//			ImageManipulation.writeImage(out, "/home/harold/Pictures/edges/K"+i+".png");
////			new IViewer("Angle:"+(i*10), ImageManipulation.getGrayBufferedImage(out));
//		}
//		
//		double[][] output = null;
//		double[][] separationMatrix = null;
//		try {
//			double[][] mMatrix = new double[edges.length][edges.length];
//			for (int v = 0; v < edges.length; v++) {
//				for (int u = 0; u < edges.length; u++) {
//					if (u == v)
//						mMatrix[v][u] = 1;
//					else
//						mMatrix[v][u] = 0;
//					System.out.print(mMatrix[v][u] + "\t");
//				}
//				System.out.println();
//			}
//			FastICA ica = new FastICA(edges, new FastICAConfig(mMatrix.length, Approach.SYMMETRIC, 1.0, 1.0e-12, 1000,
//					null), new TanhCFunction(1.0), new BelowEVFilter(1.0e-12, false), new ProgressListener());
//			separationMatrix = ica.getSeparatingMatrix();
//			output = ica.getICVectors();
//		} catch (FastICAException e) {
//			e.printStackTrace();
//		}
//		System.out.println();
//		for (int v = 0; v < separationMatrix.length; v++) {
//			for (int u = 0; u < separationMatrix[0].length; u++) {
//				System.out.print(separationMatrix[v][u] + "\t");
//			}
//			System.out.println();
//		}
//		for(int i = 0; i<output.length; i++){
//			out = ITools.normilize(arrayToImage(output[i], s[0], s[1]));
//			ImageManipulation.writeImage(out, "/home/harold/Pictures/edges/IC"+i+".png");
////			new IViewer("IC:"+i, ImageManipulation.getGrayBufferedImage(out));
//		}
	}
	
	public float[][] processCharacter(byte[][] img){
		// For this section change the zeroCentered variable to true and the size of the ANN to (100*100, 200, 26)
		int[] s  = {img.length, img[0].length};
		double[][] tout = ITools.byte2double(ITools.normilize(img));
		tout = ITools.getLaplacianMag(tout);
		int[] cent = GCRTools.getCentroidLocation(tout);
		int nW = s[1] + Math.abs(cent[0]), nH = s[0] + Math.abs(cent[1]);
		int nS = nH;
		if(nW>nS) nS = nW;
		int nX = 0, nY = 0, offX = (nS-s[1])/2, offY = (nS-s[0])/2;
		double[][] out = new double[nS][nS];
		for(int y=0; y<nS; y++){
			nY = y-cent[1]-offY;
			if(nY>=0 && nY<s[0]){
				for(int x=0; x<nS; x++){
					nX = x-cent[0]-offX;
					if(nX>=0 && nX<s[1]){
						out[y][x] = tout[nY][nX];
					}
				}
			}
		}
		return GCRTools.square2size(out, 100);
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
