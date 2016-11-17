package gcr.gui.tabs;

import java.awt.Font;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import gcr.gui.GCRHandler;

import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ANNSettingsTab extends JPanel{
	
	private static final long serialVersionUID = -4699531109483380318L;

	private JTextField widthField;
	private JTextField heightField;
	private JTextField channelField;
	private JTextField outputSize;
	private JTextField hiddenSize;
	private JTextField learnigRateField;
	private JTextField momentumField;
	private JTextField maxErrorField;
	private JTextField trainningFilePath;
	
	private GCRHandler handler;
	
	public String defaultDataPath = "/School/Research/BookReader/GrayscaleCharacterRecognition/";

	public ANNSettingsTab(){
		JLabel lblMaxError = new JLabel("Max Error:");
		
		learnigRateField = new JTextField();
		learnigRateField.setColumns(10);
		
		JLabel lblLearningRate = new JLabel("Learning Rate:");
		
		JLabel lblMomentum = new JLabel("Momentum:");
		
		momentumField = new JTextField();
		momentumField.setColumns(10);
		
		maxErrorField = new JTextField();
		maxErrorField.setColumns(10);
		
		
		JLabel lblInputLayer = new JLabel("Input Layer:");
		lblInputLayer.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		JLabel lblWidth = new JLabel("Width:");
		
		widthField = new JTextField();
		widthField.setColumns(10);
		
		JLabel lblHeight = new JLabel("Height:");
		
		heightField = new JTextField();
		heightField.setColumns(10);
		
		JLabel lblHiddenLayer = new JLabel("Hidden Layer:");
		lblHiddenLayer.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		JSeparator separator_1 = new JSeparator();
		
		JSeparator separator_2 = new JSeparator();
		
		JLabel lblSize = new JLabel("Size:");
		
		hiddenSize = new JTextField();
		hiddenSize.setColumns(10);
		
		JLabel lblCh = new JLabel("CH:");
		
		channelField = new JTextField();
		channelField.setColumns(10);
		
		JLabel lblLearningParams = new JLabel("Learning Params:");
		lblLearningParams.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		
		trainningFilePath = new JTextField();
		trainningFilePath.setColumns(10);
		
		JLabel lblTrainingData = new JLabel("Training Data:");
		lblTrainingData.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		JButton btnLoad = new JButton("...");
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(handler!=null){
					JFileChooser fc = new JFileChooser(handler.OS_DRIVE+defaultDataPath);
					FileNameExtensionFilter filter = new FileNameExtensionFilter("csv files", "csv", "txt");
					fc.setFileFilter(filter);
					int returnVal = fc.showOpenDialog(null);
			        if (returnVal == JFileChooser.APPROVE_OPTION) {
			            java.io.File file = fc.getSelectedFile();
			            String path = file.getPath();
			            trainningFilePath.setText(path);
						handler.ann.loadFromFile(path);
			        }
				}
			}
		});
		
		JSeparator separator_3 = new JSeparator();
		separator_3.setOrientation(SwingConstants.VERTICAL);
		
		JLabel lblOutputLayer = new JLabel("Output Layer:");
		lblOutputLayer.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		JLabel label_1 = new JLabel("Size:");
		
		outputSize = new JTextField();
		outputSize.setColumns(10);
		
		GroupLayout gl_annSettingsTab = new GroupLayout(this);
		gl_annSettingsTab.setHorizontalGroup(
			gl_annSettingsTab.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_annSettingsTab.createSequentialGroup()
					.addGroup(gl_annSettingsTab.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_annSettingsTab.createSequentialGroup()
							.addGap(23)
							.addComponent(lblWidth)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(widthField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(lblHeight)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(heightField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(lblCh)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(channelField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_annSettingsTab.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblInputLayer))
						.addGroup(gl_annSettingsTab.createSequentialGroup()
							.addContainerGap()
							.addComponent(separator_1, GroupLayout.PREFERRED_SIZE, 465, Short.MAX_VALUE))
						.addGroup(gl_annSettingsTab.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_annSettingsTab.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_annSettingsTab.createSequentialGroup()
									.addGap(10)
									.addComponent(lblSize)
									.addGap(18)
									.addComponent(hiddenSize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addComponent(lblHiddenLayer))
							.addGap(48)
							.addComponent(separator_3, GroupLayout.PREFERRED_SIZE, 6, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addGroup(gl_annSettingsTab.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_annSettingsTab.createSequentialGroup()
									.addComponent(label_1, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(outputSize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addComponent(lblOutputLayer, GroupLayout.PREFERRED_SIZE, 102, GroupLayout.PREFERRED_SIZE))
							.addGap(129))
						.addGroup(gl_annSettingsTab.createSequentialGroup()
							.addContainerGap()
							.addComponent(separator_2, GroupLayout.PREFERRED_SIZE, 465, Short.MAX_VALUE))
						.addGroup(gl_annSettingsTab.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_annSettingsTab.createParallelGroup(Alignment.LEADING)
								.addComponent(lblLearningParams, GroupLayout.PREFERRED_SIZE, 124, GroupLayout.PREFERRED_SIZE)
								.addGroup(gl_annSettingsTab.createSequentialGroup()
									.addGroup(gl_annSettingsTab.createParallelGroup(Alignment.LEADING)
										.addComponent(lblLearningRate)
										.addComponent(lblMomentum)
										.addComponent(lblMaxError))
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addGroup(gl_annSettingsTab.createParallelGroup(Alignment.LEADING)
										.addComponent(maxErrorField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(momentumField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(learnigRateField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
							.addGap(18)
							.addComponent(separator, GroupLayout.PREFERRED_SIZE, 11, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addGroup(gl_annSettingsTab.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_annSettingsTab.createSequentialGroup()
									.addComponent(trainningFilePath, GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btnLoad)
									.addGap(11))
								.addComponent(lblTrainingData, GroupLayout.PREFERRED_SIZE, 124, GroupLayout.PREFERRED_SIZE))))
					.addContainerGap())
		);
		gl_annSettingsTab.setVerticalGroup(
			gl_annSettingsTab.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_annSettingsTab.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblInputLayer)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_annSettingsTab.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblWidth)
						.addComponent(widthField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblHeight)
						.addComponent(heightField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblCh)
						.addComponent(channelField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(14)
					.addComponent(separator_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_annSettingsTab.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_annSettingsTab.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_annSettingsTab.createSequentialGroup()
								.addComponent(lblHiddenLayer, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addGroup(gl_annSettingsTab.createParallelGroup(Alignment.BASELINE)
									.addComponent(lblSize)
									.addComponent(hiddenSize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
							.addGroup(gl_annSettingsTab.createSequentialGroup()
								.addGap(28)
								.addGroup(gl_annSettingsTab.createParallelGroup(Alignment.BASELINE)
									.addComponent(label_1)
									.addComponent(outputSize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
						.addGroup(gl_annSettingsTab.createParallelGroup(Alignment.LEADING)
							.addComponent(lblOutputLayer, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
							.addComponent(separator_3, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(separator_2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGroup(gl_annSettingsTab.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_annSettingsTab.createSequentialGroup()
							.addGap(18)
							.addComponent(lblLearningParams, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(gl_annSettingsTab.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblLearningRate)
								.addComponent(learnigRateField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_annSettingsTab.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblMomentum)
								.addComponent(momentumField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_annSettingsTab.createParallelGroup(Alignment.BASELINE)
								.addComponent(maxErrorField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblMaxError)))
						.addGroup(gl_annSettingsTab.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(separator, GroupLayout.PREFERRED_SIZE, 146, Short.MAX_VALUE))
						.addGroup(gl_annSettingsTab.createSequentialGroup()
							.addGap(18)
							.addComponent(lblTrainingData, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
							.addGap(14)
							.addGroup(gl_annSettingsTab.createParallelGroup(Alignment.BASELINE)
								.addComponent(trainningFilePath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnLoad))))
					.addContainerGap())
		);
		this.setLayout(gl_annSettingsTab);
	}
	
	public void setGCRHandler(GCRHandler h){
		if(h==null) return;
		this.handler = h;
		
		widthField.setText(10+"");
		heightField.setText(10+"");
		channelField.setText(1+"");
		hiddenSize.setText(300+"");
		outputSize.setText(h.ann.getOutputLayerSize()+"");
		
		learnigRateField.setText(h.ann.getLearningRate()+"");
		momentumField.setText(h.ann.getMomentum()+"");
		maxErrorField.setText(h.ann.getMaxError()+"");
		
	}
}
