package gcr.gui;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JPanel;

import main.Window;
import plotter.GraphPanel;

public class HistogramViewer extends GraphPanel{

	private static final long serialVersionUID = -6607071461968685823L;
	private Window window; 			// The application window
            
    public HistogramViewer(String title){
    	if(title==null)
    		title = "Histogram";
		window = new Window(title); 			// Create the app window
    	this.creatGUI();

    	this.setBackgroundColor(java.awt.Color.WHITE);
    	this.setCursorColor(java.awt.Color.BLACK, true);
    	this.setMaxY(10);
    	this.setMinY(-10);
    	this.setdeltaY(.5);
    	
    	this.setMinX(0);
    	this.setMaxX(20);
    	this.setdeltaX(1);

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
		
		double sizeFactor = 0.30;
		
		height = height-50;
		
//		if(width>height){
//			width = height;
//			height = height-50;
//		}
//		if(height>width){
//			height = width;
//		}
		
		width 	*= sizeFactor;
		height 	*= sizeFactor;
		
		Dimension prefdim = new Dimension(width,height);//Preffered dimensions
		
		// Set the position & size of window
		window.setBounds(0, 0, 	// Position
				width, height); 				// Size
		window.setPreferredSize(prefdim);

		window.setVisible(true);		// Shows window
		window.pack();					// Packs window
		
		//Sends the size to the class
		Dimension dim = window.getContentPane().getSize();
		
		this.init(dim.width,dim.height-2); // Pane containing filled rectangles
						
		//Adds the background picture for the first time
		JPanel panel = new JPanel();
		panel.setPreferredSize(dim);
		panel.add(this);
		
		window.getContentPane().add(panel); 
		window.setResizable(false);		// Prevents resizing
		window.pack();
				
//		System.out.println("Width:  "+pane.dim.width);
//		System.out.println("Heigth: "+pane.dim.height);

	}
	
}
