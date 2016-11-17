package gcr.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import gcr.gui.tabs.ANNSettingsTab;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.Font;

import javax.swing.JProgressBar;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextField;

import plotter.GraphPanel;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JTextArea;

public class GCRGui extends JFrame {

	private static final long serialVersionUID = -8014266346016088051L;
	private JPanel contentPane;
	private JProgressBar epochProgressBar;
	private JTextField epochNumbre;
	
	private GraphPanel msePlotter;
	private JTextField elapsedTime;
	private JTextField lastMSE;
	private JButton btnStart;
	private JButton btnStop;
	
	private boolean tRunning 		= true;
	private boolean saveAfterDone 	= true;
	
	public JTextArea testingResults;
	
	private GCRHandler handler;
	private JTabbedPane tabbedPane;
	private ANNSettingsTab settingsTab;
	
	/**
	 * Create the frame.
	 */
	public GCRGui() {
		setTitle("Neural Network Workbench");
		setBackground(new Color(240, 240, 240));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 522, 459);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		JScrollPane scrollPane = new JScrollPane(tabbedPane);
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		settingsTab = new ANNSettingsTab();
		tabbedPane.addTab("ANN Settings", null, settingsTab, null);

		
		JPanel annTrainingTab = new JPanel();
		tabbedPane.addTab("ANN Training", null, annTrainingTab, null);
		
		JLabel lblAnnSettings = new JLabel("Epoch #");
		lblAnnSettings.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblAnnSettings.setHorizontalAlignment(SwingConstants.LEFT);
		
		epochProgressBar = new JProgressBar(0, 1000);
		epochProgressBar.setStringPainted(true);
		
		epochNumbre = new JTextField();
		epochNumbre.setEditable(false);
		epochNumbre.setFont(new Font("Tahoma", Font.PLAIN, 18));
		epochNumbre.setColumns(10);
		
		msePlotter = new GraphPanel();
		msePlotter.setMinX(0);
		msePlotter.setMaxX(15);
		msePlotter.setdeltaX(1.0);
		msePlotter.setMinY(0);
		msePlotter.setMaxY(1);
		msePlotter.setdeltaY(0.1);
		msePlotter.useHorizontalCursor(false);
		msePlotter.useHorizontalCursorLabler(false);
		msePlotter.useVerticalCursor(true);
		msePlotter.useVerticalCursorLabler(true);
		
		JLabel lblEllapsedTime = new JLabel("Elapsed Time:");
		
		elapsedTime = new JTextField();
		elapsedTime.setEditable(false);
		elapsedTime.setColumns(10);
		
		btnStart = new JButton("Start");
		btnStop = new JButton("Stop");
		
		btnStart.setEnabled(!tRunning);
		btnStop.setEnabled(tRunning);
		
		btnStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				btnStart.setEnabled(false);
				btnStop.setEnabled(true);
				tRunning = true;
			}
		});
		btnStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				btnStop.setEnabled(false);
				btnStart.setEnabled(true);
				tRunning = false;
				int s = JOptionPane.showConfirmDialog(null, "Would you like to save the training file after training is done?", 
						"Save after Training", JOptionPane.YES_NO_OPTION);
				if(s==JOptionPane.YES_OPTION){
					saveAfterDone = true;
				}else{
					saveAfterDone = false;
				}
			}
		});
		
		JLabel lblLastMse = new JLabel("Last MSE:");
		
		lastMSE = new JTextField();
		lastMSE.setEditable(false);
		lastMSE.setColumns(10);
		GroupLayout gl_annTrainingTab = new GroupLayout(annTrainingTab);
		gl_annTrainingTab.setHorizontalGroup(
			gl_annTrainingTab.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_annTrainingTab.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_annTrainingTab.createParallelGroup(Alignment.LEADING)
						.addComponent(msePlotter, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
						.addGroup(gl_annTrainingTab.createSequentialGroup()
							.addComponent(lblAnnSettings)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(epochNumbre, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 132, Short.MAX_VALUE)
							.addComponent(lblEllapsedTime)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(elapsedTime, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addComponent(epochProgressBar, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
						.addGroup(Alignment.TRAILING, gl_annTrainingTab.createSequentialGroup()
							.addComponent(btnStart, GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 76, Short.MAX_VALUE)
							.addComponent(lblLastMse)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lastMSE, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(72)
							.addComponent(btnStop)))
					.addContainerGap())
		);
		gl_annTrainingTab.setVerticalGroup(
			gl_annTrainingTab.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_annTrainingTab.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_annTrainingTab.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblAnnSettings)
						.addComponent(epochNumbre, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(elapsedTime, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblEllapsedTime))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(epochProgressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(msePlotter, GroupLayout.PREFERRED_SIZE, 244, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addGroup(gl_annTrainingTab.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnStop)
						.addComponent(btnStart)
						.addComponent(lblLastMse)
						.addComponent(lastMSE, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		annTrainingTab.setLayout(gl_annTrainingTab);
		
		JPanel testingTab = new JPanel();
		tabbedPane.addTab("Testing Results", null, testingTab, null);
		testingResults = new JTextArea();
		JScrollPane scrollPane_1 = new JScrollPane(testingResults);
		GroupLayout gl_testingTab = new GroupLayout(testingTab);
		gl_testingTab.setHorizontalGroup(
			gl_testingTab.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_testingTab.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_testingTab.setVerticalGroup(
			gl_testingTab.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_testingTab.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)
					.addContainerGap())
		);
		testingTab.setLayout(gl_testingTab);
				
		pack();
		setVisible(true);
	}
	
	/**
	 * Set the progress for the Epoch Progress Bar. p must be between 0 and 1.
	 * @param p -the progress of the bar; must be between 0 and 1.
	 */
	public void setEpochProgress(double p){
		if(p<0 || p>1) return;
		epochProgressBar.setValue((int)(p*1000));
	}
	
	/**
	 * Sets the epoch number in the epoch field
	 * @param e -epoch number
	 */
	public void setEpochNumber(int e){
		epochNumbre.setText(e+"");
	}
	
	/**
	 * Adds a new point to the MSE plot and sets the last MSE value.
	 * @param mse -the MSE value between 0 and 1
	 * @param e -the Epoch number
	 */
	public void addMSEtoPlot(double mse, double e){
		msePlotter.addPoint(e, mse);
		lastMSE.setText(mse+"");
	}
	
	/**
	 * Sets the last MSE values field.
	 * @param mse -the MSE value
	 */
	public void setLastMSE(double mse){
		lastMSE.setText(mse+"");
	}
	
	/**
	 * Set the elapsed time since training started.
	 * @param eTime -elapsed time since the beginning of training in ms.
	 */
	public void setElapsedTime(long eTime){
		String t = String.format("%02d:%02d:%02d", 
				TimeUnit.MILLISECONDS.toHours(eTime),
				TimeUnit.MILLISECONDS.toMinutes(eTime) -  
				TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(eTime)), // The change is in this line
				TimeUnit.MILLISECONDS.toSeconds(eTime) - 
				TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(eTime)));   
		elapsedTime.setText(t);
	}

	/**
	 * Returns whether the training is running or not/
	 * @return
	 */
	public boolean isTrainingRunning(){
		return tRunning;
	}

	/**
	 * Returns whether the user wants to save after training is done.
	 * @return
	 */
	public boolean isSaveAfterDone(){
		return saveAfterDone;
	}
	
	public void addStartActionListener(ActionListener a){
		btnStart.addActionListener(a);
	}

	public void setGCRHandler(GCRHandler h){
		if(h==null) return;
		this.handler = h;
		settingsTab.setGCRHandler(h);
	}
	
	public void setSelectedTab(int index){
		tabbedPane.setSelectedIndex(index);
	}
}
