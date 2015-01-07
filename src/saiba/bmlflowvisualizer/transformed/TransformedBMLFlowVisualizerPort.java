package saiba.bmlflowvisualizer.transformed;

import hmi.xml.XMLScanException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import asap.realizerport.RealizerPort;
import saiba.bmlflowvisualizer.BMLFlowVisualizerPort;

/**
 * Uses an xslt transform to transform the BML string sent in perform BML into a valid BML 1.0 String, which is then forwarded to the BMLFlowVisualizerPort.
 * @author hvanwelbergen
 */
public class TransformedBMLFlowVisualizerPort extends BMLFlowVisualizerPort
{
    private final Transformer bmlTransformer;
    private DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    
    public TransformedBMLFlowVisualizerPort(RealizerPort port, StreamSource bmlTransform) throws TransformerConfigurationException
    {
        super(port);
        TransformerFactory tFactory = TransformerFactory.newInstance();
        bmlTransformer = tFactory.newTransformer(bmlTransform);        
    }

    @Override
    public void performBML(String bmlString) 
    {
        try
        {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(bmlString)));
            ByteArrayOutputStream bmlOut = new ByteArrayOutputStream();
            StreamResult result = new StreamResult(bmlOut);
            bmlTransformer.transform(new DOMSource(doc), result);
            System.out.println(bmlOut.toString());
            super.performBML(bmlOut.toString());
        }
        catch (SAXException e)
        {
            throw new XMLScanException("",e);
        }
        catch (IOException e)
        {
            throw new XMLScanException("",e);
        }
        catch (ParserConfigurationException e)
        {
            throw new XMLScanException("",e);
        }
        catch (TransformerException e)
        {
            throw new XMLScanException("",e);
        }
    }
}
