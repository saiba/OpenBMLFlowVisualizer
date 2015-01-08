package saiba.bmlflowvisualizer.demo;

import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.xml.transform.TransformerConfigurationException;

import saiba.bmlflowvisualizer.BMLFlowVisualizerPort;
import saiba.bmlflowvisualizer.transformed.GretaBMLFlowVisualizerPort;
import asap.realizerport.BMLFeedbackListener;
import asap.realizerport.RealizerPort;

/**
 * Try out submitting BML + feedback through a UI
 * @author hvanwelbergen
 *
 */
public class BMLFlowVisualizerDemo
{
    public static void main(String args[]) throws TransformerConfigurationException
    {
        JFrame jf = new JFrame("BMLFlowVisualizerDemo");
        BMLFlowVisualizerPort realizerPort = new GretaBMLFlowVisualizerPort(new RealizerPort()
        {
            @Override
            public void addListeners(BMLFeedbackListener... listeners)
            {
            }

            @Override
            public void removeListener(BMLFeedbackListener l)
            {
            }

            @Override
            public void removeAllListeners()
            {
            }

            @Override
            public void performBML(String bmlString)
            {
            }
        });
        jf.setLayout(new FlowLayout());
        jf.add(realizerPort.getVisualization());
        jf.add(new BMLAndFeedbackSubmissionUI(realizerPort,realizerPort).getPanel());
        jf.setSize(1600,768);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);
    }
}
