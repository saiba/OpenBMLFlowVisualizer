package saiba.bmlflowvisualizer.demo;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import asap.realizerport.BMLFeedbackListener;
import asap.realizerport.RealizerPort;

/**
 * Simple UI to submit BML and BML feedback through two textareas
 * @author hvanwelbergen
 */
public class BMLAndFeedbackSubmissionUI
{
    private final JPanel panel = new JPanel();

    public BMLAndFeedbackSubmissionUI(RealizerPort rp, BMLFeedbackListener fbListener)
    {
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(constructBMLArea(rp));
        panel.add(constructFeedbackArea(fbListener));
    }

    private JPanel constructBMLArea(final RealizerPort rp)
    {
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));
        jp.add(new JLabel("BML"));        
        final JTextArea bmlArea = new JTextArea();
        JScrollPane sp = new JScrollPane(bmlArea);
        sp.setPreferredSize(new Dimension(500, 80));
        jp.add(sp);
        JButton submit = new JButton("Submit");
        jp.add(submit);
        submit.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                rp.performBML(bmlArea.getText());
            }
        });
        return jp;
    }

    private JPanel constructFeedbackArea(final BMLFeedbackListener fbListener)
    {
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));
        jp.add(new JLabel("feedback"));
        final JTextArea feedbackArea = new JTextArea();
        JScrollPane sp = new JScrollPane(feedbackArea);
        sp.setPreferredSize(new Dimension(500, 80));
        jp.add(sp);
        JButton submit = new JButton("Submit");
        jp.add(submit);
        submit.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                fbListener.feedback(feedbackArea.getText());
            }
        });
        return jp;
    }

    public JPanel getPanel()
    {
        return panel;
    }
}
