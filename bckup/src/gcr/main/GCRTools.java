package gcr.main;

import image.tools.ITools;

public class GCRTools {
	
	/**
	 * Computes the centroid of the provided image.
	 * @param img - the image of the character; first index is the y-axis, second is the x-axis.
	 * @return [0] x-axis center, [1] y-axis center
	 */
	public static int[] getCentroidLocation(byte[][] img){
		double xc=0, yc=0, v=0;
		int x0=img[0].length/2, y0=img.length/2; 
		for(int y=0; y<img.length; y++){
			for(int x=0; x<img[0].length; x++){
				v += (img[y][x] & 0x0FF);
				xc += (double)((x-x0))*(double)(img[y][x] & 0x0FF);
				yc += (double)((y0-y))*(double)(img[y][x] & 0x0FF);
			}
		}
		return new int[]{(int)(xc/v), (int)(yc/v)};
	}
	
	/**
	 * Computes the centroid of the provided image.
	 * @param img - the image of the character; first index is the y-axis, second is the x-axis.
	 * @return [0] x-axis center, [1] y-axis center
	 */
	public static int[] getCentroidLocation(double[][] img){
		double xc=0, yc=0, v=0;
		int x0=img[0].length/2, y0=img.length/2; 
		for(int y=0; y<img.length; y++){
			for(int x=0; x<img[0].length; x++){
				v += img[y][x];
				xc += (double)((x-x0))*img[y][x];
				yc += (double)((y0-y))*img[y][x];
			}
		}
		return new int[]{(int)(xc/v), (int)(yc/v)};
	}

	/**
	 * Draws the provided centroid onto the image.
	 * @param c - the centroid, obtained from getCentroid()
	 * @param img - the image of the character; first index is the y-axis, second is the x-axis.
	 */
	public static void drawCentroid(int[] c, byte[][] img){
		int xc = c[0]+img[0].length/2, yc = -c[1]+img.length/2;
		for(int y=0; y<img.length; y++){
			for(int x=0; x<img[0].length; x++){
				if(x>xc-1 && x<xc+1 && y>yc-1 && y<yc+1 ){
					img[y][x] = (byte) 255;
				}
			}
		}
	}
	
	/**
	 * CEnters the provided image onto its centroid. The centroid is obtained
	 * by calling the getCentroid() method.
	 * @param c - centroid of the image {x, y}
	 * @param img - the image to be centered
	 * @return the centered image
	 */
	public static byte[][] recenterImg(int[] c, byte[][] img){
		byte[][] out = new byte[img.length][img[0].length];
		int xd = c[0], yd = c[1];
		int nX = 0, nY = 0;
		for(int y=0; y<img.length; y++){
			nY = y-yd;
			if(nY>=0 && nY<img.length){
				for(int x=0; x<img[0].length; x++){
					nX = x-xd;
					if(nX>=0 && nX<img[0].length){
						out[y][x] = img[nY][nX];
					}
				}
			}
		}
		return out;
	}
	
	/**
	 * Makes the image a square of the provided size.
	 * @param img - the image to be squared
	 * @param nS - the lenght of one of the sides in pixels
	 * @return - an nS-by-nS image
	 */
	public static byte[][] square2size(byte[][] img, int nS){
		int oS = img.length;
		byte[][] out = new byte[nS][nS];
		double ratio = (double)oS/(double)nS;
		int nX = 0, nY = 0;
		for(int y=0; y<nS; y++){
			nY = (int) Math.round(y*ratio);
			if(nY>0 && nY<oS)
				for(int x=0; x<nS; x++){
					nX = (int) Math.round(x*ratio);
					if(nX>0 && nX<oS)
						out[y][x]=img[nY][nX];
				}
		}
		return out;
	}
	
	/**
	 * Makes the image a square of the provided size.
	 * @param img - the image to be squared
	 * @param nS - the lenght of one of the sides in pixels
	 * @return - an nS-by-nS image
	 */
	public static float[][] square2size(double[][] img, int nS){
		int oS = img.length;
		float[][] out = new float[nS][nS];
		double ratio = (double)oS/(double)nS;
		int nX = 0, nY = 0;
		for(int y=0; y<nS; y++){
			nY = (int) Math.round(y*ratio);
			if(nY>0 && nY<oS)
				for(int x=0; x<nS; x++){
					nX = (int) Math.round(x*ratio);
					if(nX>0 && nX<oS)
						out[y][x]=(float) img[nY][nX];
				}
		}
		return out;
	}
	
	/**
	 * Makes the image a square of the provided size.
	 * @param img - the image to be squared
	 * @param nS - the lenght of one of the sides in pixels
	 * @return - an nS-by-nS image
	 */
	public static float[][][] square2size(double[][][] img, int nS){
		int oS = img[0].length;
		int kS = img.length;
		float[][][] out = new float[img.length][nS][nS];
		double ratio = (double)oS/(double)nS;
		int nX = 0, nY = 0;
		for(int y=0; y<nS; y++){
			nY = (int) Math.round(y*ratio);
			if(nY>0 && nY<oS)
				for(int x=0; x<nS; x++){
					nX = (int) Math.round(x*ratio);
					if(nX>0 && nX<oS){
						for(int k=0; k<kS; k++)
							out[k][y][x]=(float) img[k][nY][nX];
					}
				}
		}
		return out;
	}
	
	/**
	 * Converts a two dimensional image into a one-dimensional array of points. 
	 * It uses a simple row scanning method. The new point are in the range of -1 to 1
	 * @param img - the image
	 * @return - an array of all the points on the provided image in the -1 to +1 range.
	 */
	public static float[] imageToArray(byte[][] img, boolean zeroCentered){
		float[] out = new float[img.length*img[0].length];
		int c = 0;
		float f = 0;
		if(zeroCentered){
			f = (float) (2.0/255.0);
			for(int y=0; y<img.length; y++){
				for(int x=0; x<img[0].length; x++){
					out[c++]=(float) ((img[y][x] & 0x0FF)*f-1);
				}
			}
		}else{
			f = (float) (1.0/255.0);
			for(int y=0; y<img.length; y++){
				for(int x=0; x<img[0].length; x++){
					out[c++]=(float) ((img[y][x] & 0x0FF)*f);
				}
			}
		}
		return out;
	}
	
	/**
	 * Converts a two dimensional image into a one-dimensional array of points. 
	 * It uses a simple row scanning method. The new point are in the range of -1 to 1
	 * @param img - the image
	 * @return - an array of all the points on the provided image in the -1 to +1 range.
	 */
	public static float[] imageToArray(float[][] img, boolean zeroCentered){
		float[] out = new float[img.length*img[0].length];
		int c = 0;
		float f = (float) (2.0/255.0);
		if(zeroCentered){
			f = (float) (2.0/255.0);
			for(int y=0; y<img.length; y++){
				for(int x=0; x<img[0].length; x++){
					out[c++]=(float) (img[y][x]*f-1);
				}
			}
		}else{
			f = (float) (1.0/255.0);
			for(int y=0; y<img.length; y++){
				for(int x=0; x<img[0].length; x++){
					out[c++]=(float) (img[y][x]*f);
				}
			}
		}
		return out;
	}
	
	/**
	 * Converts a two dimensional image into a one-dimensional array of points. 
	 * It uses a simple row scanning method. The new point are in the range of -1 to 1
	 * @param img - the image. First index is the kernel
	 * @return - an array of all the points on the provided image in the -1 to +1 range.
	 */
	public static float[] imageToArray(float[][][] img, boolean zeroCentered){
		float[] out = new float[img.length*img[0].length*img[0][0].length];
		int c = 0;
		float f = (float) (2.0);
		if(zeroCentered){
			f = (float) (2.0);
			for(int k=0; k<img.length; k++){
				for(int y=0; y<img[0].length; y++){
					for(int x=0; x<img[0][0].length; x++){
						out[c++]=(float) (img[k][y][x]*f-1);
					}
				}
			}
		}else{
			f = (float) (1.0);
			for(int k=0; k<img.length; k++){
				for(int y=0; y<img[0].length; y++){
					for(int x=0; x<img[0][0].length; x++){
						out[c++]=(float) (img[k][y][x]*f);
					}
				}
			}
		}
		return out;
	}
	
	public static float[] imageToArray(float[][] img1, float[][] img2, boolean zeroCentered){
		float[] out = new float[img1.length*img1[0].length*2];
		int c = 0;
		float f = (float) (2.0/255.0);
		float f1 = (float) (2.0/180.0);
		float maxA = img2[0][0], minA = img2[0][0];
		if(zeroCentered){
			f = (float) (2.0/255.0);
			f1 = (float) (2.0/180.0);
			maxA = img2[0][0]*f1-1;  minA = maxA;
			for(int y=0; y<img1.length; y++){
				for(int x=0; x<img1[0].length; x++){
					out[c++]=(float) (img1[y][x]*f-1);
					out[c++]=(float) (img2[y][x]*f1-1);
					if(out[c-1]>maxA) maxA = out[c-1];
					if(out[c-1]<minA) minA = out[c-1];
				}
			}
		}else{
			f = (float) (1.0/255.0);
			f1 = (float) (1.0/180.0);
			maxA = img2[0][0]*f1-1;  minA = maxA;
			for(int y=0; y<img1.length; y++){
				for(int x=0; x<img1[0].length; x++){
					out[c++]=(float) (img1[y][x]*f);
					out[c++]=(float) (img2[y][x]*f1-1);
					if(out[c-1]>maxA) maxA = out[c-1];
					if(out[c-1]<minA) minA = out[c-1];
				}
			}
		}
		System.out.println("Max angle: "+maxA);
		System.out.println("Min angle: "+minA);
		System.out.println();
		return out;
	}
	
 	
 	public static double[][][] getEdges(double[][] img){
		double[][] k;
		double[][][] imgEdge = new double[2][img.length][img[0].length];
		double[][] conv;
		img = ITools.normalizeImage(img);
		for(int i=0; i<180; i+=10){//361
			k = ITools.getEdgeKernel(i);
//			conv = ITools.convolveCell(img, k);
			conv = ITools.abs(ITools.convolve(img, k));
			imgEdge = findMax(imgEdge, conv, i);
		}
		return imgEdge;
	}
 	
 	public static double[][][] getAllEdges(double[][] img, int nKernels){
 		double interval = 180.0/(double)nKernels;
		double[][] k;
		double[][][] imgEdge = new double[nKernels][img.length][img[0].length];
		img = ITools.normalizeImage(img);
		for(int i=0; i<nKernels; i++){//361
			k = ITools.getEdgeKernel(i*interval);
			imgEdge[i] = ITools.abs(ITools.convolve(img, k));
		}
		return imgEdge;
	}
 	
 	
	public static double[][][] findMax(double[][][] m1, double[][] m2, double a){
		int[] l = new int[]{m2.length,m2[0].length};
		double out[][][] = new double[2][l[0]][l[1]];
		for(int y=0; y<l[0]; y++){
			for(int x=0; x<l[1]; x++){
				if(m1[0][y][x]>m2[y][x]){
					out[0][y][x] = m1[0][y][x];
					out[1][y][x] = m1[1][y][x];
				}else{
					out[0][y][x] = m2[y][x];
					out[1][y][x] = (a+90)*Math.PI/180.0;
				}
			}
		}
		return out;		
	}
 	
	/**
	 * Converts an array of floats into an array of doubles.
	 * @param f - the primitive array of floats to be converted
	 * @return the converted primitive array
	 */
 	public static double[] float2double(float[] f){
 		double[] out = new double[f.length];
 		for(int i=0; i<f.length; i++)
 			out[i] = f[i];
 		return out;
 	}
 	
 	/**
	 * Converts a two-dimensional array of floats into a two-dimensional array of doubles.
	 * @param f - the primitive array of floats to be converted
	 * @return the converted primitive array
	 */
 	public static double[][] float2double(float[][] f){
 		double[][] out = new double[f.length][f[0].length];
 		for(int y=0; y<f.length; y++)
 			for(int x=0; x<f[0].length; x++)
 			out[y][x] = f[y][x];
 		return out;
 	}

 	public static float[] concatenate(float[] a1, float[] a2){
 		float[] out = new float[a1.length + a2.length];
 		for(int i = 0; i<a1.length; i++){
 			out[i] = a1[i];
 		}
 		int off = a1.length;
 		for(int i = 0; i<a2.length; i++){
 			out[i+off] = a2[i];
 		}
 		return out;
 	}

}
