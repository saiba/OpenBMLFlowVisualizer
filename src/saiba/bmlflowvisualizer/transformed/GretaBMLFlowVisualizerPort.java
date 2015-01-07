package saiba.bmlflowvisualizer.transformed;

import hmi.util.Resources;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.stream.StreamSource;

import asap.realizerport.RealizerPort;

public class GretaBMLFlowVisualizerPort extends TransformedBMLFlowVisualizerPort
{
    public GretaBMLFlowVisualizerPort(RealizerPort port) throws TransformerConfigurationException
    {
        super(port, new StreamSource(new Resources("").getInputStream("greta.xslt")));        
    }
}
