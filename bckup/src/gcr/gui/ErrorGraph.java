package gcr.gui;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JPanel;

import main.Window;
import plotter.GradientPane;

public class ErrorGraph {

	private Window window; 			// The application window
	public GradientPane pane = null; 		// Pane containing filled rectangles
            
    public ErrorGraph(String title){
    	if(title==null)
    		title = "EEG-ICA";
		window = new Window(title); 			// Create the app window
    	this.creatGUI();

    	pane.setBackgroundColor(java.awt.Color.WHITE);
    	pane.setMaxY(10);
    	pane.setMinY(-10);
    	pane.setdeltaY(.5);
    	
    	pane.setMinX(0);
    	pane.setMaxX(20);
    	pane.setdeltaX(1);

    }
    
    public void setTitle(String title){
    	this.window.setTitle(title);
    }
	
	// Method to create the application GUI
	private void creatGUI() {
		
		Toolkit theKit = window.getToolkit(); 		// Get the window toolkit
		Dimension wndSize = theKit.getScreenSize(); // Get screen size
		
		int width = wndSize.width;
		int height = wndSize.height;
		
//		System.out.println(height+", "+width);
		
		double sizeFactor = 0.80;
		
		height = height-50;
		
//		if(width>height){
//			width = height;
//			height = height-50;
//		}
//		if(height>width){
//			height = width;
//		}
		
		Dimension prefdim = new Dimension(width,height);//Preffered dimensions
		
		// Set the position & size of window
		window.setBounds(0, 0, 	// Position
				(int)(width*sizeFactor), (int)(height*sizeFactor)); 				// Size
		window.setPreferredSize(prefdim);

		window.setVisible(true);		// Shows window
		window.pack();					// Packs window
		
		//Sends the size to the class
		Dimension dim = window.getContentPane().getSize();
		pane = new GradientPane(dim.width,dim.height-2); // Pane containing filled rectangles
						
		//Adds the background picture for the first time
		JPanel panel = new JPanel();
		panel.setPreferredSize(dim);
		panel.add(pane);
		
		window.getContentPane().add(panel); 
		window.setResizable(false);		// Prevents resizing
		window.pack();
				
//		System.out.println("Width:  "+pane.dim.width);
//		System.out.println("Heigth: "+pane.dim.height);

	}
	
}
