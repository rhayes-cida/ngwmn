package gov.usgs.ngwmn.dm.parse;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.util.StreamReaderDelegate;

import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLOutputFactory2;

import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;

public abstract class USGS_StAXUtils {
	public static final int SKIP_EVENTS = 2;
	public static final String upsideDownQuestionMark="\u00BF";
	public static final boolean IS_ESCAPE_BAD_OUTPUT = true;

	public static Map<Integer, String> eventNames;
	public static Location defaultLocation;
	public static NamespaceContext defaultNSContext;

	static {
		initializeEventNames();
		initializeDefaults();
	}

	private static void initializeDefaults() {
		// See XMLStreamReader.getLocation() javadocs for why this location acts
		// the way it does
		defaultLocation = new Location() {

			public int getCharacterOffset() {
				return -1;
			}

			public int getColumnNumber() {
				return -1;
			}

			public int getLineNumber() {
				return -1;
			}

			public String getPublicId() {
				return null;
			}

			public String getSystemId() {
				return null;
			}

		};

		defaultNSContext = new NamespaceContext() {

			public String getNamespaceURI(String prefix) {
				if (prefix == null) {
					throw new IllegalArgumentException("null namespace prefix not allowed");
				}
				return null;
			}

			public String getPrefix(String namespaceURI) {
				// TODO Auto-generated method stub
				return null;
			}

			public Iterator<String> getPrefixes(String namespaceURI) {
				// TODO Auto-generated method stub
				return null;
			}

		};
	}

	private static void initializeEventNames() {
		Map<Integer, String> names = new HashMap<Integer, String>();
		names.put(0, "NOT_YET_BEGUN_PARSING");
		names.put(XMLStreamConstants.START_ELEMENT, "START_ELEMENT");
		names.put(XMLStreamConstants.END_ELEMENT, "END_ELEMENT");
		names.put(XMLStreamConstants.PROCESSING_INSTRUCTION,
		"PROCESSING_INSTRUCTION");
		names.put(XMLStreamConstants.CHARACTERS, "CHARACTERS");
		names.put(XMLStreamConstants.COMMENT, "COMMENT");
		names.put(XMLStreamConstants.SPACE, "SPACE");
		names.put(XMLStreamConstants.START_DOCUMENT, "START_DOCUMENT");
		names.put(XMLStreamConstants.END_DOCUMENT, "END_DOCUMENT");
		names.put(XMLStreamConstants.ENTITY_REFERENCE, "ENTITY_REFERENCE");
		names.put(XMLStreamConstants.ATTRIBUTE, "ATTRIBUTE");
		names.put(XMLStreamConstants.DTD, "DTD");
		names.put(XMLStreamConstants.CDATA, "CDATA");
		names.put(XMLStreamConstants.NAMESPACE, "NAMESPACE");
		names.put(XMLStreamConstants.NOTATION_DECLARATION,
		"NOTATION_DECLARATION");
		names.put(XMLStreamConstants.ENTITY_DECLARATION,
		"ENTITY_DECLARATION");
		eventNames = names;
	}

	public static void printEventInfo(XMLStreamReader reader) throws XMLStreamException {
		int eventCode = reader.next();
		switch (eventCode) {
			case 1 :
				System.out.println("event = START_ELEMENT");
				System.out.println("Localname = "+reader.getLocalName());
				break;
			case 2 :
				System.out.println("event = END_ELEMENT");
				System.out.println("Localname = "+reader.getLocalName());
				break;
			case 3 :
				System.out.println("event = PROCESSING_INSTRUCTION");
				System.out.println("PIData = " + reader.getPIData());
				break;
			case 4 :
				System.out.println("event = CHARACTERS");
				System.out.println("Characters = " + reader.getText());
				break;
			case 5 :
				System.out.println("event = COMMENT");
				System.out.println("Comment = " + reader.getText());
				break;
			case 6 :
				System.out.println("event = SPACE");
				System.out.println("Space = " + reader.getText());
				break;
			case 7 :
				System.out.println("event = START_DOCUMENT");
				System.out.println("Document Started.");
				break;
			case 8 :
				System.out.println("event = END_DOCUMENT");
				System.out.println("Document Ended");
				break;
			case 9 :
				System.out.println("event = ENTITY_REFERENCE");
				System.out.println("Text = " + reader.getText());
				break;
			case 11 :
				System.out.println("event = DTD");
				System.out.println("DTD = " + reader.getText());

				break;
			case 12 :
				System.out.println("event = CDATA");
				System.out.println("CDATA = " + reader.getText());
				break;
		}
	}

	/**
	 * Copies the reader to the writer. The start and end document methods must
	 * be handled on the writer manually. This is copied from STAXUtils but with
	 * a modification to not completely ignore SPACE events as was done in the
	 * original
	 *
	 * TODO: if the namespace on the reader has been declared previously to
	 * where we are in the stream, this probably won't work.
	 *
	 * @param reader
	 * @param writer
	 * @throws XMLStreamException
	 */
	public static void copy( XMLStreamReader reader, XMLStreamWriter writer )
	throws XMLStreamException
	{
		int read = 0; // number of elements read in
		int eventCount = 0;

		while ( reader.hasNext() )
		{
			int event = reader.next();
			eventCount++;
			switch( event )
			{
				case XMLStreamConstants.START_ELEMENT:
					read++;
					USGS_StAXUtils.writeStartElement( reader, writer );
					break;
				case XMLStreamConstants.END_ELEMENT:
					writer.writeEndElement();
					read--;
					if ( read <= 0 )
						return;
					break;
				case XMLStreamConstants.CHARACTERS:
					String content = reader.getText();
					writeWithBestEffort(writer, content);
					break;
				case XMLStreamConstants.START_DOCUMENT:
				case XMLStreamConstants.END_DOCUMENT:
				case XMLStreamConstants.ATTRIBUTE:
				case XMLStreamConstants.NAMESPACE:
					break;
				case XMLStreamConstants.CDATA:
					writer.writeCData(reader.getText());
					break;
				case XMLStreamConstants.SPACE:
					// SPACE events are generally ignored. However, a carriage
					// return needs to be printed every once in a while to
					// prevent excessive line lengths
					if (eventCount > SKIP_EVENTS) {
						writer.writeCharacters(reader.getText());
						eventCount = 0;
					}
					break;
				case XMLStreamConstants.COMMENT:
					writer.writeComment(reader.getText());
					break;
				default:
					break;
			}
		}
	}

	private static void writeWithBestEffort(XMLStreamWriter writer,
			String content) throws XMLStreamException {
		if (IS_ESCAPE_BAD_OUTPUT) {
			try {
				writer.writeCharacters( content );
			} catch (XMLStreamException e) {
				// Make a best effort to output the bad output text, 
				// replacing problematic characters by upsideDownQuestionMark
				char[] charText = content.toCharArray();
				int i=0;

				for ( i=0; i<content.length(); i++) {
					try {
						writer.writeCharacters( charText, i,1 );
					} catch (XMLStreamException ex) {
						System.err.println("FUBAR, ERROR: " + charText[i]);
						ex.printStackTrace();
						writer.writeCharacters(upsideDownQuestionMark);
					}
				}
				System.err.println("YO YO MA, ERROR: " + content);
				e.printStackTrace();
			}
		} else {
			writer.writeCharacters( content );
		}
	}

	public static void writeStartElement(XMLStreamReader reader, XMLStreamWriter writer)
	throws XMLStreamException
	{
		String local = reader.getLocalName();
		String uri = reader.getNamespaceURI();
		String prefix = reader.getPrefix();
		if (prefix == null)
		{
			prefix = "";
		}

		String boundPrefix = writer.getPrefix(uri);
		boolean writeElementNS = false;
		if ( boundPrefix == null || !prefix.equals(boundPrefix) )
		{
			writeElementNS = true;
		}

		// Write out the element name
		if (uri != null && uri.length() > 0)
		{
			if (prefix.length() == 0)
			{

				writer.writeStartElement(local);
				writer.setDefaultNamespace(uri);

			}
			else
			{
				writer.writeStartElement(prefix, local, uri);
				writer.setPrefix(prefix, uri);
			}
		}
		else
		{
			writer.writeStartElement( reader.getLocalName() );
		}

		// Write out the namespaces
		for ( int i = 0; i < reader.getNamespaceCount(); i++ )
		{
			String nsURI = reader.getNamespaceURI(i);
			String nsPrefix = reader.getNamespacePrefix(i);

			// Why oh why does the RI suck so much?
			if (nsURI == null) nsURI = "";
			if (nsPrefix == null) nsPrefix = "";

			if ( nsPrefix.length() ==  0 )
			{
				writer.writeDefaultNamespace(nsURI);
			}
			else
			{
				writer.writeNamespace(nsPrefix, nsURI);
			}

			if (uri != null && nsURI.equals(uri) && nsPrefix.equals(prefix))
			{
				writeElementNS = false;
			}
		}

		// Check if the namespace still needs to be written.
		// We need this check because namespace writing works
		// different on Woodstox and the RI.
		if (writeElementNS && uri != null)
		{
			if ( prefix == null || prefix.length() ==  0 )
			{
				writer.writeDefaultNamespace(uri);
			}
			else
			{
				writer.writeNamespace(prefix, uri);
			}
		}

		// Write out attributes
		for ( int i = 0; i < reader.getAttributeCount(); i++ )
		{
			String ns = reader.getAttributeNamespace(i);
			String nsPrefix = reader.getAttributePrefix(i);
			if ( ns == null || ns.length() == 0 ){
				writer.writeAttribute(
						reader.getAttributeLocalName(i),
						reader.getAttributeValue(i));
			}
			else if (nsPrefix == null || nsPrefix.length() == 0)
			{
				writer.writeAttribute(
						reader.getAttributeNamespace(i),
						reader.getAttributeLocalName(i),
						reader.getAttributeValue(i));
			}
			else
			{
				writer.writeAttribute(reader.getAttributePrefix(i),
						reader.getAttributeNamespace(i),
						reader.getAttributeLocalName(i),
						reader.getAttributeValue(i));
			}
		}
	}

	public static XMLStreamReader wrapXMLStreamReaderIgnoreNamespaces(final XMLStreamReader xReader) {
		return new StreamReaderDelegate(xReader) {

            @Override
			public String getAttributeNamespace(int index) {
				// IGNORING NAMESPACES
				return XMLConstants.NULL_NS_URI;
			}

            @Override
			public String getAttributePrefix(int index) {
				// IGNORING NAMESPACES
				return XMLConstants.DEFAULT_NS_PREFIX;
			}

            @Override
			public String getNamespaceURI() {
				// IGNORING NAMESPACES
				return XMLConstants.NULL_NS_URI;
			}

            @Override
			public String getNamespaceURI(String prefix) {
				// IGNORING NAMESPACES
				return XMLConstants.NULL_NS_URI;
			}

            @Override
			public String getNamespaceURI(int index) {
				// IGNORING NAMESPACES
				return XMLConstants.NULL_NS_URI;
			}

            @Override
			public String getPrefix() {
				return XMLConstants.DEFAULT_NS_PREFIX;
			}
		};
	}

	private static XMLOutputFactory xmlOutputFactory;
	public static synchronized XMLOutputFactory getXMLOutputFactory() {
		if (xmlOutputFactory == null) {
			XMLOutputFactory2 xmlOutputFactory2 = new WstxOutputFactory();
			xmlOutputFactory2.configureForSpeed();
			xmlOutputFactory2.setProperty(XMLOutputFactory2.IS_REPAIRING_NAMESPACES, Boolean.FALSE);
			xmlOutputFactory = xmlOutputFactory2;
		}
		return xmlOutputFactory;
	}

	private static XMLInputFactory xmlInputFactory;
	public static synchronized XMLInputFactory getXMLInputFactory() {
		if (xmlInputFactory == null) {
			XMLInputFactory2 xmlInputFactory2 = new WstxInputFactory();
			xmlInputFactory2.configureForSpeed();
			xmlInputFactory = xmlInputFactory2;
		}
		return xmlInputFactory;
	}

}
