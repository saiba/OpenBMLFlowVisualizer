package saiba.bmlflowvisualizer;

import hmi.xml.XMLScanException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import saiba.bml.core.Behaviour;
import saiba.bml.core.BehaviourBlock;
import saiba.bml.core.CoreComposition;
import saiba.bml.feedback.BMLFeedback;
import saiba.bml.feedback.BMLSyncPointProgressFeedback;
import saiba.bml.feedback.BMLWarningFeedback;
import saiba.bmlflowvisualizer.gui.BMLFlowVisualization;
import saiba.bmlflowvisualizer.utils.BMLBlock;
import saiba.bmlflowvisualizer.utils.BMLFileFilter;
import saiba.bmlflowvisualizer.utils.BMLInformation;
import saiba.bmlflowvisualizer.utils.BMLInformationType;
import asap.bml.ext.bmla.BMLABMLBehaviorAttributes;
import asap.bml.ext.bmla.BMLAInterruptBehaviour;
import asap.bml.ext.bmla.feedback.BMLABlockPredictionFeedback;
import asap.bml.ext.bmla.feedback.BMLABlockProgressFeedback;
import asap.bml.ext.bmla.feedback.BMLAFeedbackParser;
import asap.bml.ext.bmla.feedback.BMLAPredictionFeedback;
import asap.realizerport.BMLFeedbackListener;
import asap.realizerport.RealizerPort;

/**
 * Handles incoming realizer feedback and manages the bmlBlocks.
 * 
 * @author jpoeppel
 * 
 */
public class BMLFlowVisualizerPort implements RealizerPort, BMLFeedbackListener {
	private final RealizerPort realizerPort;

	private BMLFlowVisualization panel;

	// Data storage
	private List<BMLInformation> information = Collections
			.synchronizedList(new ArrayList<BMLInformation>());
	private Map<String, BMLBlock> bmlBlocks = Collections
			.synchronizedMap(new HashMap<String, BMLBlock>());

	// Auxiliary variables
	private long firstTimestamp;

	private boolean firstTimestampKnown;

	public BMLFlowVisualizerPort(RealizerPort port) {
		realizerPort = port;
		realizerPort.addListeners(this);
		firstTimestampKnown = false;
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					panel = new BMLFlowVisualization(BMLFlowVisualizerPort.this, bmlBlocks);
				}
			});

		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open a file chooser and saves all received at the chosen location. 
	 * If the chosen file extension does not match BMLFileFilter.bmlFileFormat, then
	 * the fileFormat is added to the filename.
	 */
	public void saveAs() {
		JFileChooser chooser;

		chooser = new JFileChooser();
		chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.addChoosableFileFilter(new BMLFileFilter());
		chooser.setDialogTitle("Save as...");
		chooser.setVisible(true);

		int result = chooser.showSaveDialog(panel);

		if (result == JFileChooser.APPROVE_OPTION) {

			String path = chooser.getSelectedFile().toString();
			try {
				String ext = BMLFileFilter.getExtension(path);
				if (ext == null || !ext.equals(BMLFileFilter.bmlFileFormat)) {
					path = path +"."+ BMLFileFilter.bmlFileFormat;
				}
				FileOutputStream fileOut = new FileOutputStream(path);
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				out.writeObject(new ArrayList<BMLInformation>(information));
				out.close();
				fileOut.close();
			} catch (IOException i) {
				i.printStackTrace();
			}
		}
		chooser.setVisible(false);
	}

	/**
	 * Opens a file chooser and tries to load the selected file as a list of bmlInformation.
	 */
	@SuppressWarnings("unchecked")
	public void load() {
		JFileChooser chooser;

		chooser = new JFileChooser();
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.addChoosableFileFilter(new BMLFileFilter());
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		chooser.setDialogTitle("Load BML Information...");
		chooser.setVisible(true);
		int result = chooser.showOpenDialog(panel);

		if (result == JFileChooser.APPROVE_OPTION) {

			String path = chooser.getSelectedFile().toString();
			try {
				FileInputStream fileIn = new FileInputStream(path);
				ObjectInputStream in = new ObjectInputStream(fileIn);
				information = Collections
						.synchronizedList((ArrayList<BMLInformation>) in
								.readObject());
				in.close();
				fileIn.close();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Unable to load " + path + ". Corrupt file?");
				return;
			} catch (ClassNotFoundException e) {
				System.out.println("Class not found Exception. Something went terribly wrong.");
				return;
			} 
			bmlBlocks.clear();
			firstTimestampKnown = false;
			chooser.setVisible(false);
			for (BMLInformation info : information) {
				switch (info.getType()) {
				case BML_BLOCK:
					presentBML(info, info.getTimestamp());
					break;
				case FEEDBACK:
					presentFeedback(info, info.getTimestamp());
					break;
				}
				panel.updateHistory(info.getTimestamp() + 1000);
			}
			panel.updateVisualisation();
		}
		
	}

	public static JComponent createBMLFlowVisualizerPortUI(RealizerPort rp) {
		BMLFlowVisualizerPort port = new BMLFlowVisualizerPort(rp);
		return port.getVisualization();
	}


	@Override
	public void feedback(String feedback) {
		if (!firstTimestampKnown) {
			firstTimestamp = System.currentTimeMillis();
			firstTimestampKnown = true;
		}
		long time = System.currentTimeMillis() - firstTimestamp;

		BMLInformation info = new BMLInformation(time, feedback,
				BMLInformationType.FEEDBACK);

		information.add(info);
		presentFeedback(info, time);
		panel.update(time);

	}

	/**
	 * Presents the given information as Feedback to the system at the given
	 * time. The feedback is not delayed until the time but rather the timestamp
	 * of the feedback will be set to the given time.
	 * 
	 * @param info
	 *            The feedback to be presented
	 * @param time
	 *            The time at which this feedback is to be presented
	 */
	private void presentFeedback(BMLInformation info, long time) {
		if (!firstTimestampKnown) {
			firstTimestamp = System.currentTimeMillis();
			firstTimestampKnown = true;
		}
		BMLFeedback fb;
		try {
			fb = BMLAFeedbackParser.parseFeedback(info.getInformation());

		} catch (IOException e) {
			// shouldn't happen since we parse strings
			throw new AssertionError(e);
		} catch (XMLScanException e) {
			return;
		}
		synchronized (bmlBlocks) {

			if (fb instanceof BMLABlockProgressFeedback) {
				// States: DONE, IN_EXEC,
				BMLABlockProgressFeedback fbBlock = (BMLABlockProgressFeedback) fb;
				if (bmlBlocks.containsKey(fbBlock.getBmlId())) {
					bmlBlocks.get(fbBlock.getBmlId()).addFeedback(info);
					if (fbBlock.getSyncId().equals("end")) {
						bmlBlocks.get(fbBlock.getBmlId()).end(fbBlock, time);

					} else if (fbBlock.getSyncId().equals("start")) {

						bmlBlocks.get(fbBlock.getBmlId()).start(time);
					}
				} else {
					System.out.println("BMLABlockProgressFeedback with id: " + fbBlock.getBmlId()+":"+fbBlock.getSyncId()
							+ " not present for feedback.");
				}
			}
			if (fb instanceof BMLAPredictionFeedback) {
				// States: IN_PREP, PENDING, LURKING, IN_EXEC
				BMLAPredictionFeedback pf = (BMLAPredictionFeedback) fb;
				for (BMLABlockPredictionFeedback bbp : pf
						.getBMLABlockPredictions()) {
					if (!bmlBlocks.containsKey(bbp.getId())) {

						bmlBlocks.put(bbp.getId(), new BMLBlock(bbp.getId()));
					}
					bmlBlocks.get(bbp.getId()).addFeedback(info);
					bmlBlocks.get(bbp.getId()).update(bbp, time);
				}
				for (Behaviour b : pf.getBmlBehaviorPredictions()) {
					if (bmlBlocks.containsKey(b.getBmlId())) {
						bmlBlocks.get(b.getBmlId())
								.updateBehaviourSyncPoints(b);
					} else {
						System.out.println("BMLAPredictionFeedback of behavior" + b.getBmlId()+":"+b.id
								+ " not present for feedback.");
					}
				}
			}
			if (fb instanceof BMLWarningFeedback) {
				BMLWarningFeedback wf = (BMLWarningFeedback) fb;
				if (bmlBlocks.containsKey(wf.getId())) {
					bmlBlocks.get(wf.getId()).addFeedback(info);
				} else {
					System.out.println("BMLWarningFeedback with id: " + wf.getId()
							+ " not present for feedback.");
				}
			}
			if (fb instanceof BMLSyncPointProgressFeedback) {
				BMLSyncPointProgressFeedback sf = (BMLSyncPointProgressFeedback) fb;
				if (bmlBlocks.containsKey(sf.getBMLId())) {
					bmlBlocks.get(sf.getBMLId()).addFeedback(info);
				} else {
					System.out.println("BMLSyncPointProgressFeedback with id: " + sf.getBMLId() +":"+sf.getBehaviourId()
							+ " not present for feedback.");
				}
			}

		}
	}

	@Override
	public void addListeners(BMLFeedbackListener... listeners) {
		realizerPort.addListeners(listeners);
	}

	@Override
	public void removeAllListeners() {
		realizerPort.removeAllListeners();
	}

	@Override
	public void removeListener(BMLFeedbackListener l) {
		realizerPort.removeListener(l);
	}

	@Override
	public void performBML(String bmlString) {
		if (!firstTimestampKnown) {
			firstTimestamp = System.currentTimeMillis();
			firstTimestampKnown = true;
		}
		long time = System.currentTimeMillis() - firstTimestamp;
		BMLInformation info = new BMLInformation(time, bmlString,
				BMLInformationType.BML_BLOCK);
		information.add(info);
		presentBML(info, time);
		
		panel.update(time);
		realizerPort.performBML(bmlString); // Does not do anything for this
											// port.
	}

	/**
	 * Presents the given information as a BMLBlock information to the system at
	 * the given time. The block is not delayed until the time but rather the
	 * timestamp of the block will be set to the given time.
	 * 
	 * @param info
	 *            The information about the block to be presented
	 * @param time
	 *            The time at which this block is to be presented
	 */
	private void presentBML(BMLInformation info, long time) {

		BehaviourBlock bb = new BehaviourBlock(false, new BMLABMLBehaviorAttributes());
		synchronized (bmlBlocks) {
			try {
				bb.readXML(info.getInformation());

				if (bb.getComposition().equals(CoreComposition.REPLACE)) {
					System.out.println("Reset");
					bmlBlocks.clear();
					firstTimestamp = System.currentTimeMillis();
					info.setTimestamp(0);
					panel.reset();
				}

				if (bmlBlocks.containsKey(bb.getBmlId())) {
					System.out.println("Setting block: " + bb.getBmlId());
					bmlBlocks.get(bb.getBmlId()).setBb(bb, time);
				} else {
					BMLBlock block = new BMLBlock(bb.getBmlId(), bb, time);
					bmlBlocks.put(block.getId(), block);
				}
				// Check for interrupt
				// With interrupt behaviour
				for (Behaviour b : bb.behaviours) {
					if (b instanceof BMLAInterruptBehaviour) {
						bmlBlocks.get(((BMLAInterruptBehaviour) b).getTarget())
								.interrupt(time);
						//7.12.14 Visualization of interrupting blocks not desired  
						//bmlBlocks.get(bb.getBmlId()).isInterruptBlock();
					}
				}
				// With interrupt attribute
				BMLABMLBehaviorAttributes bmlaAttr = bb
						.getBMLBehaviorAttributeExtension(BMLABMLBehaviorAttributes.class);
				if (bmlaAttr != null) {
					for (String block : bmlaAttr.getInterruptList()) {
						bmlBlocks.get(bb.getBmlId()).isInterruptBlock();
						if (bmlBlocks.containsKey(block)) {
							bmlBlocks.get(block).interrupt(time);
						}
					}
				}

			} catch (XMLScanException e) {
				e.printStackTrace();
			}
		}
	}

	public JComponent getVisualization() {
		return panel;
	}

	public boolean isFirstTimestampKnown() {
		return firstTimestampKnown;
	}

	public long getFirstTimestamp() {
		if (!firstTimestampKnown) {
			firstTimestamp = System.currentTimeMillis();
			firstTimestampKnown = true;
		}
		return firstTimestamp;
	}

}
