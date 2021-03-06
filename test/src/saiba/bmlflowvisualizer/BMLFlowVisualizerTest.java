package saiba.bmlflowvisualizer;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import saiba.bmlflowvisualizer.BMLFlowVisualizerPort;
import asap.realizerport.RealizerPort;

/**
 * Unit tests for the AsapBMLFlowVisualizer
 * @author hvanwelbergen
 * 
 */
public class BMLFlowVisualizerTest
{
    private RealizerPort mockPort = mock(RealizerPort.class);
    private BMLFlowVisualizerPort vis;

    @Before
    public void before()
    {
        vis = new BMLFlowVisualizerPort(mockPort);
    }

    @Test
    public void testBMLSendThrough()
    {
        String bmlString = "<bml id=\"bml1\" xmlns=\"http://www.bml-initiative.org/bml/bml-1.0\"></bml>";
        vis.performBML(bmlString);
        verify(mockPort).performBML(bmlString);
    }

    @Test
    public void testAddToPlanning()
    {
        String bmlString = "<bml id=\"bml1\" xmlns=\"http://www.bml-initiative.org/bml/bml-1.0\"></bml>";
        vis.performBML(bmlString);
    }

    @Test
    public void testFinished()
    {
        String feedbackString = "<blockProgress id=\"bml1:end\" globalTime=\"10\" xmlns=\"http://www.bml-initiative.org/bml/bml-1.0\"/>";
        vis.feedback(feedbackString);
    }

    @Test
    @Ignore
    public void testPredictionFeedback()
    {
        String feedbackString = "<predictionFeedback xmlns=\"http://www.bml-initiative.org/bml/bml-1.0\">"
                + "<bml id=\"bml1\" globalStart=\"1\" globalEnd=\"7\"/></predictionFeedback>";
        vis.feedback(feedbackString);
    }

    @Test
    public void testStartFeedback()
    {
        String feedbackString = "<blockProgress id=\"bml1:start\" globalTime=\"10\" xmlns=\"http://www.bml-initiative.org/bml/bml-1.0\"/>";
        vis.feedback(feedbackString);
    }

    @Test
    public void testClear()
    {
        String bmlString = "<bml id=\"bml1\" composition=\"REPLACE\" xmlns=\"http://www.bml-initiative.org/bml/bml-1.0\"/>";
        vis.performBML(bmlString);
    }

    @Test
    public void testInvalidBML()
    {
        String bmlString = "<bml id=\"bml1\" xmlns=\"http://www.bml-initiative.org/bml/bml-1.0\" "
                + "xmlns:pe=\"http://hmi.ewi.utwente.nl/pictureengine\"> "
                + "<pe:addAnimationXML id=\"anim\" layer=\"5\" start=\"0\" end=\"5\" "
                + "filePath=\"pictureengine/example/animations/\" fileName=\"speak.xml\"/>" + "</bml>";
        vis.performBML(bmlString);
    }

    @Test
    public void testInvalidBML2()
    {
        vis.performBML("<invalid>");
        verify(mockPort).performBML("<invalid>");
    }

    @Test
    public void testInvalidFeedback2()
    {
        vis.feedback("<invalid>");
    }

    @Test
    public void testInvalidFeedback()
    {
        String feedbackString = "<blockProgress id=\"bml1:start\" globalTime=\"10\" xmlns=\"http://www.bml-initiative.org/bml/bml-1.0\">";
        vis.feedback(feedbackString);
    }
}
