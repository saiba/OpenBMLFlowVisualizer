package saiba.bmlflowvisualizer.transformed;

import static org.mockito.Mockito.mock;

import javax.xml.transform.TransformerConfigurationException;

import org.junit.Test;

import asap.realizerport.RealizerPort;

public class GretaBMLFlowVisualizerPortTest
{
    private RealizerPort mockPort = mock(RealizerPort.class);
    
    @Test
    public void test() throws TransformerConfigurationException
    {
        GretaBMLFlowVisualizerPort port = new GretaBMLFlowVisualizerPort(mockPort);
        port.performBML("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<bml xmlns=\"http://www.mindmakers.org/projects/BML\" character=\"Greta\" composition=\"blend\" id=\"bml1\">\n"
                + "<gesture end=\"2.529\" id=\"p1_0\" lexeme=\"beat_Ges_R_Pointing\" start=\"-0.367\">\n"+
                 "<description priority=\"1\" type=\"gretabml\">\n"+
                        "<reference>beat=beat_Ges_R_Pointing</reference>\n"+
                        "<intensity>1.000</intensity>\n"+
                        "<SPC.value>0.500</SPC.value>\n"+
                        "<TMP.value>0.520</TMP.value>\n"+
                        "<FLD.value>1.000</FLD.value>\n"+
                        "<PWR.value>0.500</PWR.value>\n"+
                        "<REP.value>-0.100</REP.value>\n"+
                        "<OPN.value>0.000</OPN.value>\n"+
                        "<TEN.value>0.000</TEN.value>\n"+
                    "</description>"+
                "</gesture>\n"
            + "</bml>");
    }
}
