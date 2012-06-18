package gov.usgs.ngwmn;

import gov.usgs.DownloadTokenFilterTests;
import gov.usgs.ngwmn.dm.DataManagerServletTests;
import gov.usgs.ngwmn.dm.cache.PipeStatisticsTest;
import gov.usgs.ngwmn.dm.cache.fs.FileCacheFilenameTest;
import gov.usgs.ngwmn.dm.harvest.UrlFactoryTests;
import gov.usgs.ngwmn.dm.harvest.WebRetrieverTests;
import gov.usgs.ngwmn.dm.io.CopyInvokerTest;
import gov.usgs.ngwmn.dm.io.FileInputInvokerTest;
import gov.usgs.ngwmn.dm.io.PipelineTest;
import gov.usgs.ngwmn.dm.io.SupplyZipOutputTests;
import gov.usgs.ngwmn.dm.io.aggregate.SequentialJoiningAggregatorTests;
import gov.usgs.ngwmn.dm.io.parse.DataRowParserTests;
import gov.usgs.ngwmn.dm.io.parse.ElementTests;
import gov.usgs.ngwmn.dm.io.transform.CsvOutputStreamTests;
import gov.usgs.ngwmn.dm.io.transform.ExcelXssfOutputStreamTest;
import gov.usgs.ngwmn.dm.io.transform.TransformEntrySupplierTests;
import gov.usgs.ngwmn.dm.io.transform.TransformSupplierTests;
import gov.usgs.ngwmn.dm.io.transform.TsvOutputStreamTests;
import gov.usgs.ngwmn.dm.spec.LatLongResolverTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	DownloadTokenFilterTests.class,
    FileInputInvokerTest.class,
    CsvOutputStreamTests.class,
    ElementTests.class,
    WellDataTypeTests.class,
    TransformEntrySupplierTests.class,
    CopyInvokerTest.class,
    WebRetrieverTests.class,
    DataManagerServletTests.class,
    SupplyZipOutputTests.class,
    TransformSupplierTests.class,
    LatLongResolverTests.class,
    FileCacheFilenameTest.class,
    SequentialJoiningAggregatorTests.class,
    PipelineTest.class,
    ExcelXssfOutputStreamTest.class,
    DataRowParserTests.class,
    PipeStatisticsTest.class,
    UrlFactoryTests.class,
    TsvOutputStreamTests.class
})
public class UnitTestSuite {

	// annotation suites have no need of methods
}
