package gov.usgs.ngwmn.dm.io.aggregate;


import static org.junit.Assert.*;

import gov.usgs.ngwmn.WellDataType;
import gov.usgs.ngwmn.dm.io.SimpleSupplier;
import gov.usgs.ngwmn.dm.io.Supplier;
import gov.usgs.ngwmn.dm.io.SupplyZipOutput;
import gov.usgs.ngwmn.dm.io.aggregate.Flow;
import gov.usgs.ngwmn.dm.io.aggregate.FlowFactory;
import gov.usgs.ngwmn.dm.io.aggregate.SequentialJoiningAggregator;
import gov.usgs.ngwmn.dm.spec.Encoding;
import gov.usgs.ngwmn.dm.spec.Specification;
import gov.usgs.ngwmn.dm.spec.Specifier;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.Test;


public class SequentialJoiningAggregatorTests {
	public static final String ELEMENTS1
		= "<get><TimeValuePair><Id>1234</Id><Type>water</Type></TimeValuePair><TimeValuePair><Id>5678</Id><Type>clay</Type></TimeValuePair></get>";
	public static final String ELEMENTS2
		= "<get><TimeValuePair><Id>8765</Id><Type>water</Type></TimeValuePair><TimeValuePair><Id>4321</Id><Type>sandstone</Type></TimeValuePair></get>";
	public static final String ELEMENTS3
		= "<get><TimeValuePair><Id>7777</Id><Type>H2O</Type></TimeValuePair><TimeValuePair><Id>8888</Id><Type>gray</Type></TimeValuePair></get>";
	public static final String ELEMENTS4
		= "<get><TimeValuePair><Id>6666</Id><Type>saltwater</Type></TimeValuePair><TimeValuePair><Id>9999</Id><Type>purple</Type></TimeValuePair></get>";

	@Test
	public void test() throws Exception {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Supplier<OutputStream> upstream;
		upstream = new SimpleSupplier<OutputStream>(baos);
		upstream = new SupplyZipOutput(upstream);

		
		FlowFactory fac = new FlowFactory() {
			@Override
			public Flow makeFlow(final Specifier spec, final Supplier<OutputStream> out) throws IOException {
				return new Flow() {
					@Override
					public Void call() throws Exception {
						OutputStream os = null;
						boolean   threw = true;
						try {
							os = out.begin();
							
							String elements="";
							
							switch (spec.getFeatureID().charAt( spec.getFeatureID().length()-1  )) {
								case 'a': elements = ELEMENTS1;
									break;
								case 'b': elements = ELEMENTS2;
									break;
								case 'c': elements = ELEMENTS3;
									break;
								case 'd': elements = ELEMENTS4;
									break;
							}
							
							os.write( elements.getBytes() );
							threw = false;
						} finally {
							out.end(threw);
						}
						return null;
					}
				};
			}
		};
		
		Specification spect;
		
		spect = new Specification();
		spect.setEncode( Encoding.CSV );
		
		spect.addWell( new Specifier("a","1234567a",WellDataType.QUALITY) );
		spect.addWell( new Specifier("a","1234567b",WellDataType.QUALITY) );
		
		spect.addWell( new Specifier("a","1234567c",WellDataType.LOG) );
		spect.addWell( new Specifier("a","1234567d",WellDataType.LOG) );
		
		new SequentialJoiningAggregator(fac,spect,upstream).call();
		
		File file = new File("/tmp","dataSJA.zip");
		FileOutputStream fos = new FileOutputStream(file);
		fos.write( baos.toByteArray() );
		fos.flush();
		fos.close();
		
		assertEquals(395, baos.toByteArray().length );
		
		Set<String> expectedNames = new HashSet<String>();
		expectedNames.add("LOG.csv");
		expectedNames.add("QUALITY.csv");
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		ZipInputStream zis = new ZipInputStream(bais);
		for (String name : expectedNames) {
			ZipEntry entry = zis.getNextEntry();
			name = entry.getName(); // name as a loop it just to control the loop count
			assertTrue("Expected ["+name+"] in collection "+expectedNames, 
					expectedNames.contains(name) );
		}
		zis.close();

	}
}