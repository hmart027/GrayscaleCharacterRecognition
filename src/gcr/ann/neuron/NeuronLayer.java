package gcr.ann.neuron;

public class NeuronLayer extends NeuronLayerInterface{
	
	public static boolean threadsStarted = false;
	public static boolean multithread = true;
	public static int numberOfCores = Runtime.getRuntime().availableProcessors();
	public static MultithreadingNeuronLayer[] threads;
	
	float[] error;
	float[] errorInfTerms;
	float[] inputError;
	
	float a = 1;			//2
	float da = 1/a;
	float b = 0;			//1
		
	public NeuronLayer(int in, int out, boolean useMomentum){
		this.inputSize = in;
		this.outputSize = out;
		this.weights 	=  new float[out][in+1];
		this.yin 	=  new float[out];
		this.out 	=  new float[out];
		this.useMomentum = useMomentum;
		if(useMomentum){
			this.lastWeights 	=  new float[out][in+1];
		}
		startThreads();
	}
	
	public NeuronLayer(int in, int out){
		this(in, out, false);
	}
	
	public static synchronized void startThreads(){
		if (multithread && !threadsStarted) {
			threads = new MultithreadingNeuronLayer[numberOfCores];
			for (int i = 0; i < numberOfCores; i++) {
				threads[i] = new MultithreadingNeuronLayer(i);
				threads[i].start();
			}
			threadsStarted = true;
		}
	}
	
	public void setActThresholds(float upper, float lower){
		this.lowTH = lower;
		this.upTH = upper;
	}
	
	public int[] calculate(int[] in){
		return calculate(in, 0);
	}
	
	public int[] calculate(int[] in, int o){
		int output[] = new int[weights.length];
		for(int neuron = 0; neuron < weights.length; neuron++){
			float out = weights[neuron][0];
			for(int weight = 1; weight < weights[0].length; weight++){
				out += weights[neuron][weight]*in[weight-1+o];
			}
			output[neuron] = 0;
			if(out >= upTH) 	output[neuron] = 1;
			if(out < lowTH) 	output[neuron] = -1;
			yin[neuron] = out;
		}
		return output;
	}
	
	public float[] calculateSigmoid(float[] in){
		return calculateSigmoid(in, 0);
	}
	
	public float[] calculateSigmoid(float[] in, int o){
		prevIn = in;
		prevInOff = o;
		this.out = new float[outputSize];
		// If using multithreading set the current active layer.
		if(multithread){
			setActiveLayer(this);
		}
		if(!multithread){
			for(int neuron = 0; neuron < outputSize; neuron++){
				float out = weights[neuron][0];
				for(int weight = 1; weight < weights[0].length; weight++){
					out += weights[neuron][weight]*in[weight-1+o];
				}
				yin[neuron] = out;
				this.out[neuron] = (float) (a/(1d + Math.exp(-out)) + b);
			}
		}else{
			MultithreadingNeuronLayer.startSigmodComputation = true;
			boolean done = false;
			do {
				done = true;
				for (int i = 0; i < numberOfCores; i++) {
					if (!threads[i].done) {
						done = false;
					}
				}
			} while (!done);
			MultithreadingNeuronLayer.startSigmodComputation = false;
			for(int i=0; i<numberOfCores; i++)
				threads[i].done = false;
		}
		return this.out;
	}

	@Override
	public float[] backpropagate(float[] error) {		
		this.error = error;
		errorInfTerms = new float[outputSize];
		inputError = new float[inputSize];
		
		if(multithread){
			setActiveLayer(this);
		}
		
		if(!multithread){
			//Output Error Information Term		
			for(int i = 0; i<outputSize; i++){
				errorInfTerms[i] = error[i]*da*(out[i]-b)*(1-out[i]);
			}
			//Input Layer Error
			for(int i = 0; i<inputSize; i++){
				float dE = 0 ;
				for(int j = 0; j<errorInfTerms.length; j++){
					dE += errorInfTerms[j]*weights[j][i+1];
				}
				inputError[i] = dE;
			}
			//Update Weights
			if(useMomentum){
				for(int i = 0; i<outputSize; i++){
					float d = learningRate * errorInfTerms[i];
					weights[i][0] += d; 
					if(Math.abs(d)>mwc) mwc = Math.abs(d);					//Check for maximum weight change
					for(int j = 1; j<weights[0].length; j++){
						float dW = d * prevIn[j-1] + momentum * (weights[i][j] - lastWeights[i][j]); 
						weights[i][j] += dW;
						lastWeights[i][j] = weights[i][j];
						if(Math.abs(dW)>mwc) mwc = Math.abs(dW);			//Check for maximum weight change
					}
				}
			}else{
				for(int i = 0; i<outputSize; i++){
					float d = learningRate * errorInfTerms[i];
					weights[i][0] += d; 
					if(Math.abs(d)>mwc) mwc = Math.abs(d);					//Check for maximum weight change
					for(int j = 1; j<weights[0].length; j++){
						float dW = d * prevIn[j-1]; 
						weights[i][j] += dW;
						if(Math.abs(dW)>mwc) mwc = Math.abs(dW);			//Check for maximum weight change
					}
				}
			}
		}else{
			// Computing Error Information Term
			boolean done = false;
			MultithreadingNeuronLayer.startEITComp = true;
			do{
				done = true;
				for(int i=0; i<numberOfCores; i++){
					if(!threads[i].done){
						done = false;
					}
				}
			}while(!done);
			MultithreadingNeuronLayer.startEITComp = false;
			for(int i=0; i<numberOfCores; i++)
				threads[i].done = false;
			// Computing Input Error
			MultithreadingNeuronLayer.startInputErrorComp = true;
			done = false;
			do{
				done = true;
				for(int i=0; i<numberOfCores; i++){
					if(!threads[i].done){
						done = false;
					}
				}
			}while(!done);
			MultithreadingNeuronLayer.startInputErrorComp = false;
			for(int i=0; i<numberOfCores; i++)
				threads[i].done = false;
			// Update Weights
			MultithreadingNeuronLayer.startWeightUpdate = true;
			done = false;
			do{
				done = true;
				for(int i=0; i<numberOfCores; i++){
					if(!threads[i].done){
						done = false;
					}
				}
			}while(!done);
			MultithreadingNeuronLayer.startWeightUpdate = false;
			for(int i=0; i<numberOfCores; i++)
				threads[i].done = false;
			
		}
		return inputError;
	}
	
	public static void setActiveLayer(NeuronLayer cLayer){
		MultithreadingNeuronLayer.cLayer = cLayer;
		for(int i=0; i<numberOfCores; i++){
			threads[i].computeOffsets();
		}
	}
	
	public static class MultithreadingNeuronLayer extends Thread{
		
		private static volatile boolean startSigmodComputation 	= false;
		private static volatile boolean startEITComp 					= false;
		private static volatile boolean startInputErrorComp 		= false;
		private static volatile boolean startWeightUpdate			= false;
		private static volatile NeuronLayer cLayer;
		
		public final int threadID;
				
		public int threadOffset;
		public int lastIndex;		
		public int inputLayerOffset;
		public int inputLayerLastIndex;
		
		public volatile boolean done = false;
		
		MultithreadingNeuronLayer(int threadID){
			this.threadID = threadID;
		}
		
		@Override
		public void run(){
			while(true){
				if(!done){
					if(startSigmodComputation){
						calculateSigmoid();
						done = true;
					}
					if(startEITComp){
						computeOutputErrorInformationTerm();
						done=true;
					}
					if(startInputErrorComp){
						computeInputLayerError();
						done=true;
					}
					if(startWeightUpdate){
						updateWeights();
						done=true;
					}
				}
				
				try {
					Thread.sleep(0, 100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		public void computeOffsets(){
			int processCount = cLayer.outputSize/numberOfCores;
			if(cLayer.outputSize%numberOfCores > 0) processCount ++;
			this.threadOffset = threadID*processCount;
			this.lastIndex = threadOffset + processCount;
			if(this.lastIndex > cLayer.outputSize) this.lastIndex =cLayer.outputSize;
				
			processCount = cLayer.inputSize/numberOfCores;
			if(cLayer.inputSize%numberOfCores > 0) processCount ++;
			this.inputLayerOffset = threadID*processCount;
			this.inputLayerLastIndex = inputLayerOffset + processCount;
			if(this.inputLayerLastIndex > cLayer.inputSize) this.inputLayerLastIndex = cLayer.inputSize;
		}
		
		public void calculateSigmoid(){
			int lastIndex = this.lastIndex;
			for(int neuron = threadOffset; neuron < lastIndex; neuron++){
				float tout = cLayer.weights[neuron][0];
				for(int weight = 1; weight < cLayer.weights[0].length; weight++){
					tout += cLayer.weights[neuron][weight]*cLayer.prevIn[weight-1];
				}
				cLayer.yin[neuron] = tout;
				cLayer.out[neuron] = (float) ( cLayer.a/(1d + Math.exp(-tout))+ cLayer.b);
			}
		}
		
		public void computeOutputErrorInformationTerm(){
			for(int i = threadOffset; i<lastIndex; i++){
				cLayer.errorInfTerms[i] = cLayer.error[i]*cLayer.da*(cLayer.out[i]-cLayer.b)*(1-cLayer.out[i]);
			}
		}
		
		public void computeInputLayerError(){
			
//			for(int j = 0; j<cLayer.errorInfTerms.length; j++){
//				cLayer.inputErrorInfTerms[inputLayerOffset] = cLayer.errorInfTerms[j]*cLayer.weights[j][inputLayerOffset+1];
//				for(int i = inputLayerOffset+1; i<inputLayerLastIndex; i++){
//					cLayer.inputErrorInfTerms[i] += cLayer.errorInfTerms[j]*cLayer.weights[j][i+1];
//				}
//			}
			
			for(int i = inputLayerOffset; i<inputLayerLastIndex; i++){
				float dE = 0 ;
				for(int j = 0; j<cLayer.errorInfTerms.length; j++){
//					dE += cLayer.errorInfTerms[j]*cLayer.weights[j][i];
					dE += cLayer.errorInfTerms[j]*cLayer.weights[j][i+1];
				}
				cLayer.inputError[i] = dE;
			}
		}

		public void updateWeights(){
			float tMWC = cLayer.mwc;
			if(cLayer.useMomentum){
				for(int i = threadOffset; i<lastIndex; i++){
					float d = cLayer.learningRate *  cLayer.errorInfTerms[i];
					cLayer.weights[i][0] += d; 
					if(Math.abs(d)>tMWC) tMWC = Math.abs(d);					//Check for maximum weight change
					for(int j = 1; j<cLayer.weights[0].length; j++){
						float dW = d * cLayer.prevIn[j-1] + cLayer.momentum * (cLayer.weights[i][j] - cLayer.lastWeights[i][j]); 
						cLayer.weights[i][j] += dW;
						cLayer.lastWeights[i][j] = cLayer.weights[i][j];
						if(Math.abs(dW)>tMWC) tMWC = Math.abs(dW);			//Check for maximum weight change
					}
				}
			}else{
				for(int i = threadOffset; i<lastIndex; i++){
					float d = cLayer.learningRate *  cLayer.errorInfTerms[i];
					cLayer.weights[i][0] += d; 
					if(Math.abs(d)>tMWC) tMWC = Math.abs(d);					//Check for maximum weight change
					for(int j = 1; j<cLayer.weights[0].length; j++){
						float dW = d * cLayer.prevIn[j-1]; 
						cLayer.weights[i][j] += dW;
						if(Math.abs(dW)>tMWC) tMWC = Math.abs(dW);			//Check for maximum weight change
					}
				}
			}
			if(tMWC > cLayer.mwc) cLayer.mwc = tMWC;
		}
		
	}

	@Override
	public float[] computeActivation(float[] in, int offset) {
		// TODO Auto-generated method stub
		return null;
	}
}
