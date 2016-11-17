package gcr.main;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.TreeMap;

import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.ITessAPI.TessBaseAPI;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import gcr.ann.ANN;
import gcr.ann.ANN2;
import gcr.ann.ANNOutput;
import gcr.gui.ErrorGraph;
import gcr.gui.GCRGui;
import gcr.gui.GCRHandler;
import gcr.gui.HistogramViewer;
import image.tools.ITools;
import image.tools.IViewer;
import img.ImageManipulation;
import inftycdb.InftyCDB;
import inftycdb.InftyCDBEntry;

public class GCRMain {
	
	public static final String OS_DRIVE = "/media/DATA";
//	public static final String OS_DRIVE = "E:";
	public static String IMAGE_PATH = OS_DRIVE+"/School/Research/BookReader/software/character DB/InftyCDB-1/InftyCDB-1/Images/";
	
	GCRGui gui;
	ANN2 ann = new ANN2(100*100, 16, 200, 26); //100*100, 200, 26
	double tSetC = 0;
//	ErrorGraph graph = new ErrorGraph("Error");
	double[] lastItError = null;
	int[] totalCharCount = new int[26];
	int[] totalCharErrors = new int[26];

	static int charCount = 0;
	String dataSet = "load24";
	
	boolean zeroCentered = true;
	
	Random rand = new Random();
	boolean randomNoise = false;
	double a = 0.05;
	
	ITesseract tessaract = new Tesseract();
	
	public GCRMain(){

		ann.learningRate = 0.02f;
//		ann.momentum = 0.5f;
		GCRHandler h = new GCRHandler();
		h.ann = ann;
		gui = new GCRGui();
		gui.setGCRHandler(h);

		InftyCDB db = InftyCDB.getDatabaseInstance("admin", "elechar");
		
		ArrayList<InftyCDBEntry> charsA = null;
		byte[][][] page1 = null;
		
//		new ImageViewer("Original",ImageManipulation.getBufferedImage(page));
		
		//Load 10 was trained with noisy characters
//		if(!ann.loadFromFile(OS_DRIVE+"/School/Research/BookReader/GrayscaleCharacterRecognition/"+dataSet)){
//			randomNoise = false;
//			gui.setSelectedTab(1);
//			trainANN(db, true);
//			randomNoise = false;
//		}
		
//		//Temporary
//		trainANN(db, false);
//		System.out.println("Retraining:");
//		trainANN(chars, page1[0]);
		
//		trainANN2(db, true);
		
//		tessaract.setTessVariable("variable tessedit_write_images", "true");
		tessaract.setOcrEngineMode(ITessAPI.TessOcrEngineMode.OEM_TESSERACT_ONLY);
		tessaract.setPageSegMode(ITessAPI.TessPageSegMode.PSM_SINGLE_CHAR);
				
		int eCount = 0;
		int sampleCount = 0;		
		int pCount = 0;
		for(String p: db.getPages()){
			gui.testingResults.append(p+"\n");
			charsA = (ArrayList<InftyCDBEntry>) db.getCharactersFromImage(p);
			page1 = ImageManipulation.loadImage(IMAGE_PATH+p);
//			int[] r = processPage(charsA, page1[0], true);
			int[] r = processPageWithTessaract(charsA, page1[0], true);
			eCount += r[0];
			sampleCount += r[1];
			pCount++;
			
			gui.testingResults.append("\tPage Errors: "+r[0]+", "+(r[0]/(double)r[1]*100.0)+"%\n");
//			if(pCount>=100){
//				break;
//			}
		}
		for(int i=0; i<26; i++){
			gui.testingResults.append((char)(i+97)+": "+totalCharErrors[i]+" of "+totalCharCount[i]+",\t"+(totalCharErrors[i]/(double)totalCharCount[i]*100.0)+"\n");
		}
		gui.testingResults.append("Total Errors: "+eCount+", "+(eCount/(double)sampleCount*100.0)+"%\n");	
		
//		page1 = ImageManipulation.loadImage(OS_DRIVE+"/School/Research/BookReader/GrayscaleCharacterRecognition/s.png");
//		processLeter(page1[0]);
		
	}

	
	/**
	 * Process the given image of the letter. The first step is to call the process character 
	 * method and then feed it to the ANN feedforward method. The last step is to order the outputs
	 * according to their probability value. Finally the 3 most likely characters are displayed. 
	 * @param img - the image of the character; first index is the y-axis, second is the x-axis.
	 * @return the most likely character.
	 */
	public char processLeter(byte[][] img){
		float[] p = null;
		p = processCharacter(img);
//		new IViewer("Char",ImageManipulation.getGrayBufferedImage(ITools.normalize(p)));
//		p = ITools.atmosphericTurbulence(ITools.normalize(p), 10);	// Noise
		ArrayList<ANNOutput> result = ANNOutput.getOrderOutputs(ann.feedForward(p));
		for (int x = 0; x < 3; x++) {
			ANNOutput r = result.get(x);
			System.out.println(r.c + ": " + r.prob);
		}
		return result.get(0).c;
	}
	
	public int[] processPage(ArrayList<InftyCDBEntry> chars, byte[][] img, boolean saveErros){	
		int eCount = 0, sampleCount =0; 
		float[] p = null;
		int[] charErrors = new int[26];
		for(int i=0; i<chars.size(); i++){//charsA.size()
			InftyCDBEntry e = chars.get(i);
			String entity = e.entity.toLowerCase().trim();
			if(entity.length()>1) continue;	// Continue if string is more than one character long;
			char c = entity.charAt(0);
			p = processCharacter(ITools.crop(e.left, e.top, e.right, e.bottom, img));
//			p = ImageTools.atmosphericTurbulence(p, 10);	// Noise
			ArrayList<ANNOutput> result = ANNOutput.getOrderOutputs(ann.feedForward(p));
//			if(!(result.get(0).c == c)){	// Highest is target
			if(!(result.get(0).c == c || result.get(1).c == c || result.get(2).c == c)){ // one of the highest 3 is target
//				System.out.println(e.entity.trim()+": ");
				charErrors[c-97]++;
				totalCharErrors[c-97]++;
//				if(saveErros){
//					ImageManipulation.saveGrayArrayAsImage(ITools.normalize(p),
//							OS_DRIVE+"/School/Research/BookReader/GrayscaleCharacterRecognition/lastErrors/"
//									+e.charID+"_"+result.get(0).c+"_"+result.get(1).c+"_"+result.get(2).c
//									+".png", "png");
//				}
				
//				for(int x=0; x<3; x++){
//					ANNOutput r = result.get(x);
//					System.out.println("\t"+r.c+": "+r.prob);
//				}
//				if(e.entity.trim().charAt(0)=='h'){
//					new ImageViewer("h",ImageManipulation.getGrayBufferedImage(p));
//				}
				eCount++;
			}
			sampleCount++;
			totalCharCount[c-97]++;
		}
		return new int[]{eCount, sampleCount};
	}

	int lastP = 0;
	double percent = 0;
	public int[] processPageWithTessaract(ArrayList<InftyCDBEntry> chars, byte[][] img, boolean saveErros){	
		int eCount = 0, sampleCount =0; 
		byte[][] p = null, pt = null;
		int[] charErrors = new int[26];
		for(int i=0; i<chars.size(); i++){//charsA.size()
			InftyCDBEntry e = chars.get(i);
			String entity = e.entity.toLowerCase().trim();
			if(entity.length()>1) continue;	// Continue if string is more than one character long;
			//Center Img
			pt = ITools.crop(e.left, e.top, e.right, e.bottom, img);
			int[] s  = {pt.length, pt[0].length};
			int[] cent = GCRTools.getCentroidLocation(pt);
			int nW = s[1] + Math.abs(cent[0]), nH = s[0] + Math.abs(cent[1]);
			int nS = nH;
			if(nW>nS) nS = nW;
			int nX = 0, nY = 0, offX = (nS-s[1])/2, offY = (nS-s[0])/2;
			p = new byte[nS][nS];
			for(int y=0; y<nS; y++){
				nY = y-cent[1]-offY;
				if(nY>=0 && nY<s[0]){
					for(int x=0; x<nS; x++){
						nX = x-cent[0]-offX;
						if(nX>=0 && nX<s[1]){
							p[y][x] = pt[nY][nX];
						}
					}
				}
			}
			//End center Img
			char c = entity.charAt(0);
			try {
				String result = tessaract.doOCR(ImageManipulation.getGrayBufferedImage(pt));
				result = result.trim();
				if(result.toCharArray().length==0 || result.toCharArray().length>1 || result.charAt(0)!=c){
					eCount++;
				}
			} catch (TesseractException e1) {
				e1.printStackTrace();
			}
			if((int)percent != lastP){
				lastP = (int)percent;
//				gui.testingResults.append(lastP+"%\n");
			}
			sampleCount++;
			totalCharCount[c-97]++;
			percent = sampleCount*100.0f/(float)chars.size();
		}
		return new int[]{eCount, sampleCount};
	}
	
/*	// Old method
   public byte[][] processCharacter(byte[][] img){
		int[] s  = {img.length, img[0].length};
		byte[][] tout = ITools.normilize(ITools.getNegative(img));
		int[] cent = getCentroidLocation(tout);
		int nW = s[1] + Math.abs(cent[0]), nH = s[0] + Math.abs(cent[1]);
		int nS = nH;
		if(nW>nS) nS = nW;
		int nX = 0, nY = 0, offX = (nS-s[1])/2, offY = (nS-s[0])/2;
		byte[][] out = new byte[nS][nS];
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
		return square2size(out, 100);
	}*/
		
	boolean c = false;
	public float[] processCharacter(byte[][] img){
		// For this section change the zeroCentered variable to true and the size of the ANN to (100*100, 200, 26)
		int[] s  = {img.length, img[0].length};
		double[][] tout = ITools.byte2double(ITools.normilize(img));
		if(randomNoise && rand.nextBoolean()){
			tout = ITools.atmosphericTurbulence(tout, a);
		}
		int kCount = 16;
		double[][][] tout2 = GCRTools.getAllEdges(tout, kCount);
		
		tout = ITools.getLaplacianMag(tout);
		int[] cent = GCRTools.getCentroidLocation(tout);
		int nW = s[1] + Math.abs(cent[0]), nH = s[0] + Math.abs(cent[1]);
		int nS = nH;
		if(nW>nS) nS = nW;
		int nX = 0, nY = 0, offX = (nS-s[1])/2, offY = (nS-s[0])/2;
//		double[][] out = new double[nS][nS];
//		for(int y=0; y<nS; y++){
//			nY = y-cent[1]-offY;
//			if(nY>=0 && nY<s[0]){
//				for(int x=0; x<nS; x++){
//					nX = x-cent[0]-offX;
//					if(nX>=0 && nX<s[1]){
//						out[y][x] = tout[nY][nX];
//					}
//				}
//			}
//		}
//		return GCRTools.imageToArray(GCRTools.square2size(out, 100), zeroCentered);
		
		double[][][] out = new double[tout.length][nS][nS];
		for(int k=0; k<kCount; k++){
			for(int y=0; y<nS; y++){
				nY = y-cent[1]-offY;
				if(nY>=0 && nY<s[0]){
					for(int x=0; x<nS; x++){
						nX = x-cent[0]-offX;
						if(nX>=0 && nX<s[1]){
							out[k][y][x] = tout2[k][nY][nX];
						}
					}
				}
			}
		}
		return GCRTools.imageToArray(GCRTools.square2size(out, 100), zeroCentered);
		
//		int[] s  = {img.length, img[0].length};
//		double[][][] tout = getEdges(ITools.byte2double(ITools.normilize(img)));
//		int[] cent = getCentroidLocation(tout[0]);
//		int nW = s[1] + Math.abs(cent[0]), nH = s[0] + Math.abs(cent[1]);
//		int nS = nH;
//		if(nW>nS) nS = nW;
//		int nX = 0, nY = 0, offX = (nS-s[1])/2, offY = (nS-s[0])/2;
//		double[][][] out = new double[2][nS][nS];
//		for(int y=0; y<nS; y++){
//			nY = y-cent[1]-offY;
//			if(nY>=0 && nY<s[0]){
//				for(int x=0; x<nS; x++){
//					nX = x-cent[0]-offX;
//					if(nX>=0 && nX<s[1]){
//						out[0][y][x] = tout[0][nY][nX];
//						out[1][y][x] = tout[1][nY][nX];
//					}
//				}
//			}
//		}
//		return imageToArray(square2size(out[0], 100), square2size(out[1], 100));
	}
	
	/**
	 * Creates the target ANN output used for ANN training. The target char get a +1
	 * and all others get -1.
	 * @param c - the target character
	 * @return a float array containing the target outputs of the ANN network.
	 */
	public float[] target(char c){
		float[] out = new float[26];
		for(int i = 0; i<26; i++){
			if(i==(c-97)){
				out[i] = 1;
			}else{
				out[i] = -1;
			}
		}
		return out;
	}
			
	/**
	 * Creates a random set of character to be used during the training of the ANN.
	 * The random set is created by obtaining the ID's of all the ROMAN style characters in the 
	 * database and randomly sorting them. Once the randomization is done a subset of the size
	 * specified by the percentSplit parameter is selected.
	 * @param db - database object to be used.
	 * @param percentSplit - percent of the dataset to be used for training 0.0-to-1.0
	 * @return the randomly selected training data.
	 */
 	public ArrayList<InftyCDBEntry> getRandomTrainingSet(InftyCDB db, double percentSplit){
 		ArrayList<InftyCDBEntry> entries = new ArrayList<>();
 		ArrayList<Integer> charIDs = db.getUniqueCharIDList();
 		Collections.shuffle(charIDs, new java.util.Random());
 		int sampleSize = (int) (charIDs.size()*percentSplit);
 		int id;
 		for(int i = 0; i<sampleSize; i++){
 			id = charIDs.get(i);
 	 		InftyCDBEntry e = db.getEntryByID(id);
 	 		if(e==null)
 	 			System.out.println("Invalid entry for ID: ");
 	 		else
 	 			entries.add(e);
 		} 		
 		return entries;
 	}
 	
/* 	public ArrayList<InftyCDBEntry> getNonRandomTrainingSet(InftyCDB db, double percentSplit){
		for(char c='a'; c<='z'; c++){
			TreeMap<String, ArrayList<InftyCDBEntry>> nM = db.getCharacter2PageMap(c, 50);
			for(String p: nM.keySet()){
				ArrayList<InftyCDBEntry> oM = page2char.get(p);
				if(oM==null) oM = new ArrayList<>();
				oM.addAll(nM.get(p));
				page2char.put(p, oM);
				}
		}
		for(char c='A'; c<='Z'; c++){
			TreeMap<String, ArrayList<InftyCDBEntry>> nM = db.getCharacter2PageMap(c, 50);
			for(String p: nM.keySet()){
				p=p.toLowerCase();
				ArrayList<InftyCDBEntry> oM = page2char.get(p);
				if(oM==null) oM = new ArrayList<>();
				oM.addAll(nM.get(p));
				page2char.put(p, oM);
			}
		}
		//shuffle the carachters withicn each page;
		for(ArrayList<InftyCDBEntry> l: page2char.values()){
			Collections.shuffle(l);
		}	
 		return entries;
 	}*/
 	
 	public void trainANN(InftyCDB db, boolean save){
 		System.out.println("Starting to Train ANN!!!");
 		// key is Page name
		TreeMap<String, ArrayList<InftyCDBEntry>> page2char = new TreeMap<>();
		
		//Random training set
		ArrayList<InftyCDBEntry> tSet = getRandomTrainingSet(db, 0.03); // 3% of database
		System.out.println("Trainning set size: "+tSet.size());
		tSetC = 1.0/(double)tSet.size();
		for(InftyCDBEntry e : tSet){	// Arrange the data per page
			ArrayList<InftyCDBEntry> oM = page2char.get(e.imageName);
			if(oM==null) oM = new ArrayList<>();
			oM.add(e);
			page2char.put(e.imageName, oM);
		}
		
		System.out.println("Initializing Weights.");
		ann.widrowInit();
		System.out.println("Trainning ANN.");
		trainANN(page2char);
//		graph.pane.addPlot(lastItError, java.awt.Color.BLUE);
		for(int i=0; i<26; i++){
			System.out.println((char)(i+97)+": "+lastItError[i]);
		}
		if(save && gui.isSaveAfterDone()){
			System.out.println("Saving ANN Weights.");
			ann.writeToFile(OS_DRIVE+"/School/Research/BookReader/GrayscaleCharacterRecognition/"+dataSet);
		}
 		
 	}
 	
 	public void trainANN(TreeMap<String, ArrayList<InftyCDBEntry>> data){
		float SSE = 0;
		int count = 0;
		float MSE = 0;
		float epoch = 0;	
		int eCharCount = 0;
		long eStartTime;
		
		int hittCount = 0;
		boolean rightAnswerInTopThree;
		ArrayList<ANNOutput> result;
		float mwc = 0;
		//Start Training
		do{
			float[] err = null;
			mwc = 0;
			hittCount = 0;
			SSE = 0;
			count = 0;
			eCharCount = 0;
			System.out.println("Epoch: "+(epoch+1));
			gui.setEpochNumber((int) (epoch+1));
			eStartTime = System.currentTimeMillis();
			lastItError = new double[26];
			
			for(String page: data.keySet()){
				ArrayList<InftyCDBEntry> chars = data.get(page);
				byte[][] img = ImageManipulation.loadImage(IMAGE_PATH+page)[0];
				for(int i=0; i<chars.size(); i++){
					ann.mwc = 0;
					charCount++;
					eCharCount++;
					gui.setEpochProgress(eCharCount*tSetC);
//					System.out.print(".");
					InftyCDBEntry e = chars.get(i);
					float[] p = processCharacter(ITools.crop(e.left, e.top, e.right, e.bottom, img));
//					ImageManipulation.saveGrayArrayAsImage(p, OS_DRIVE+"/School/Research/BookReader/software/character DB/InftyCDB-1/"
//							+ "InftyCDB-1/annTSetLoad8/"+e.entity.trim()+"_"+e.charID+".png", "png");
					char c = e.entity.toLowerCase().charAt(0);
					err = ann.learnPattern(p, target(c));
					for(int eindex = 0; eindex<26; eindex++){
						float f = err[eindex];
						SSE += Math.pow(f, 2);
						if(c==(char)(eindex+97)){
							if(f>0.5 || f<0)
								lastItError[eindex] += 1;
						}
						count++;
					}
					
					//Check top 3 results
					result = ANNOutput.getOrderOutputs(ann.lastResults);
					rightAnswerInTopThree = false;
					for (int x = 0; x < 3; x++) {
						ANNOutput r = result.get(x);
						if(r.c==c){
							rightAnswerInTopThree = true;
						}
					}
					if(rightAnswerInTopThree){
						hittCount++;
					}
					
//					//Learn Noisy pattern
//					p = ITools.atmosphericTurbulence(p, 10);	// Noise
//					err = ann.learnPattern(imageToArray(p), target(c));
//					for(int eindex = 0; eindex<26; eindex++){
//						float f = err[eindex];
//						SSE += Math.pow(f, 2);
//						if(c==(char)(eindex+97)){
//							if(f>0.5 || f<0)
//								lastItError[eindex] += 1;
//						}
//						count++;
//					}
					//End Learning
//					gui.testingResults.append("\t mwc: "+ann.mwc+"\n");
					if(Math.abs(ann.mwc)>Math.abs(mwc)) mwc = ann.mwc;
				}
			}
			System.out.println("\tmwc: "+mwc);
			System.out.println("\tTotal HitCount on top 3: "+hittCount);
			System.out.println("\tMisses: "+(1-(float)hittCount/(float)eCharCount)*100f + "%");
//			for(int eindex = 0; eindex<26; eindex++){
//				lastItError[eindex] /= (double)100;
//			}
			MSE = SSE/(float)count;
			System.out.println("MSE: "+MSE);
			epoch ++ ;
			ann.SSEs.add(SSE);
			ann.MSEs.add(MSE);
			ann.MWCs.add(ann.mwc);
			gui.addMSEtoPlot(MSE, epoch);
			gui.setElapsedTime(System.currentTimeMillis() - eStartTime);
		}while(MSE > ann.maxError && gui.isTrainingRunning());
		System.out.println("Took: "+epoch+" epochs");
 	}
 	
/* 	public void trainANN(ArrayList<InftyCDBEntry> chars, byte[][] img){
		float SSE = 0;
		int count = 0;
		float MSE = 0;
		float epoch = 0;
		//Start Trainning
		do{
			ann.mwc = 0;
			SSE = 0;
			count = 0;
			System.out.println("Epoch: "+(epoch+1));
			for(int i=0; i<chars.size(); i++){//chars.size()
				System.out.print(".");
				InftyCDBEntry e = chars.get(i);
				float[][] p = processCharacter(ITools.crop(e.left, e.top, e.right, e.bottom, img));
				for(float f:ann.learnPattern(imageToArray(p), target(e.entity.toLowerCase().charAt(0)))){
					SSE += Math.pow(f, 2);
					count++;
				}
			}
			System.out.println();
			MSE = SSE/(float)count;
			System.out.println("MSE: "+MSE);
			epoch ++ ;
			ann.SSEs.add(SSE);
			ann.MSEs.add(MSE);
			ann.MWCs.add(ann.mwc);
		}while(MSE > ann.maxError);
		System.out.println("Took: "+epoch+" epochs");
 	}*/
 	
/* 	public void trainANN2(InftyCDB db, boolean save){
		TreeMap<String, ArrayList<InftyCDBEntry>> page2char = new TreeMap<>();
		for(char c='a'; c<='z'; c++){
			TreeMap<String, ArrayList<InftyCDBEntry>> nM = db.getCharacter2PageMap(c, 100);
			for(String p: nM.keySet()){
				ArrayList<InftyCDBEntry> oM = page2char.get(p);
				if(oM==null) oM = new ArrayList<>();
				oM.addAll(nM.get(p));
				page2char.put(p, oM);
			}
		}
		for(char c='A'; c<='Z'; c++){
			TreeMap<String, ArrayList<InftyCDBEntry>> nM = db.getCharacter2PageMap(c, 100);
			for(String p: nM.keySet()){
				p=p.toLowerCase();
				ArrayList<InftyCDBEntry> oM = page2char.get(p);
				if(oM==null) oM = new ArrayList<>();
				oM.addAll(nM.get(p));
				page2char.put(p, oM);
			}
		}
		for(ArrayList<InftyCDBEntry> l: page2char.values()){
			Collections.shuffle(l);
		}
		System.out.println("Trainning ANN2.");
		trainANN2(page2char);
		if(save){
			System.out.println("Saving ANN Weights.");
			ann.writeToFile(OS_DRIVE+"/School/Research/BookReader/GrayscaleCharacterRecognition/load12.csv");
		}
 	}
 	
 	public void trainANN2(TreeMap<String, ArrayList<InftyCDBEntry>> data){
 		ArrayList<InftyCDBEntry> chars = null;
 		byte[][] img = null;
 		int tErrorCount = 0, pErrorCount = 0;
// 		do{
 			tErrorCount = 0;
		for (String page : data.keySet()) {
			System.out.println("On page: " + page);
			chars = data.get(page);
			img = ImageManipulation
					.loadImage(IMAGE_PATH + page)[0];
			do{
				pErrorCount = 0;
				for (int i = 0; i < chars.size(); i++) {
					InftyCDBEntry e = chars.get(i);
					float[][] p = processCharacter(ITools.crop(e.left, e.top,
							e.right, e.bottom, img));
					float[] imgArray = imageToArray(p);
					char c = e.entity.toLowerCase().charAt(0);
					int rInt = 0;
					float[] pR = ann.feedForward(imgArray);
					ArrayList<ANNOutput> result = ANNOutput.getOrderOutputs(pR);
					while(result.get(0).c!=c){
						ann.learnPattern(imageToArray(p), target2(c, pR, result));
						pR 		= ann.feedForward(imgArray);
						result 	= ANNOutput.getOrderOutputs(pR);
						if(rInt==0)
							System.out.print("\tRetrainning: "+c);
						else{
							System.out.print(".");
						}
						rInt++;
					}
					if(rInt>0){	
						System.out.println();
						pErrorCount++;
					}
				}
				if(pErrorCount>0){
					tErrorCount++;
				}
			}while(pErrorCount>0);
		}
 //		}while(tErrorCount>0);
 	}
 	
 	public float[] target2(char c, float[] p, ArrayList<ANNOutput> results){
 		boolean found = false;
 		for(ANNOutput r: results){
 			if(!found){
 				if(r.c==97){
 					p[c-97] = 1;
 					found = true;
 				}else{
 					p[c-97] = -1;
 				}
 			}
 		}
		return p;
	}*/
 	
 	/**
 	 * Main method. Program start point
 	 * @param args
 	 */
 	public static void main(String[] args) {
		new GCRMain();
 		
//		byte[][][] page1 = ImageManipulation.loadImage(OS_DRIVE+"/School/Research/BookReader/GrayscaleCharacterRecognition/s.png");
//		
//		float[] hist = ITools.getHistogram(page1[0]);
//		byte[][] histEQ = ITools.equalizeHistogram(page1[0]);
//		
//		HistogramViewer histViewer = new HistogramViewer(null);
//		histViewer.addPlot(float2double(hist), java.awt.Color.BLUE);
//		histViewer.addPlot(float2double(ITools.getHistogram(histEQ)), java.awt.Color.RED);
//		histViewer.setMinY(-0.01);
//		
//		new IViewer(ImageManipulation.getGrayBufferedImage(page1[0]));
//		new IViewer(ImageManipulation.getGrayBufferedImage(histEQ));
//		new IViewer(ImageManipulation.getGrayBufferedImage(ITools.normilize(
//				getEdges(ITools.byte2double(page1[0]))[0])));
	}

}
