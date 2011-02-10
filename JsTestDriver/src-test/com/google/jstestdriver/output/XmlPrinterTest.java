package com.google.jstestdriver.output;

import static com.google.jstestdriver.TestResult.Result.error;
import static com.google.jstestdriver.TestResult.Result.passed;
import static java.io.File.createTempFile;
import static java.util.Arrays.asList;

import java.io.File;
import java.util.Arrays;

import junit.framework.TestCase;

import com.google.jstestdriver.BrowserInfo;
import com.google.jstestdriver.TestResult;

/**
 * @author alexeagle@google.com (Alex Eagle)
 */
public class XmlPrinterTest extends TestCase {
  TestResultHolder data = new TestResultHolder();
  File tempFile;
  BrowserInfo firefox = makeBrowser("Firefox", "Linux", "2.5", 1);
  BrowserInfo firefox2 = makeBrowser("Firefox", "Linux", "2.5", 2);
  BrowserInfo safari = makeBrowser("Safari", "MacIntel", "3", 3);

  String stdout = "Some standard out\n logging";
  TestResult firefoxPassed1 =
      new TestResult(firefox, passed.toString(), "", stdout, "testCase1", "test1", 1.0f);
  TestResult firefoxPassed2 =
      new TestResult(firefox2, passed.toString(), "", "", "testCase1", "test1", 2.0f);
  TestResult firefoxPassed3 =
      new TestResult(firefox, passed.toString(), "", stdout, "testCase1", "test2", 1.0f);
  TestResult safariError1 =
      new TestResult(safari, error.toString(), "", "", "testCase1", "test4", 4.0f);
  TestResult safariPassed1 =
      new TestResult(safari, passed.toString(), "", "", "testCase2", "test4", 4.0f);

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    tempFile = createTempFile(getName(), "tmp");
    tempFile.delete();
    tempFile.mkdirs();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    for (File file : tempFile.listFiles()) {
      file.delete();
    }
    tempFile.delete();
  }

  public void testTestSuiteFileNameIsNice() throws Exception {
    data.onTestComplete(firefoxPassed1);
    XmlPrinter printer = new XmlPrinterImpl(data, tempFile.getAbsolutePath(), new FileNameFormatter());
    printer.writeXmlReportFiles();
    File[] files = tempFile.listFiles();

    assertEquals(1, files.length);
    assertEquals("TEST-Firefox_25_Linux.testCase1.xml", files[0].getName());
  }

  public void testSeveralTestCasesInSeveralBrowsers() throws Exception {
    for (TestResult testResult : asList(firefoxPassed1, firefoxPassed2, firefoxPassed3,
        safariError1, safariPassed1)) {
      data.onTestComplete(testResult);
    }
    XmlPrinter printer = new XmlPrinterImpl(data, tempFile.getAbsolutePath(), new FileNameFormatter());
    printer.writeXmlReportFiles();
    String[] files = tempFile.list();

    assertEquals(4, files.length);
    Arrays.sort(files);
    assertEquals("TEST-Firefox_25_Linux.testCase1.xml", files[0]);
    assertEquals("TEST-Firefox_25_Linux_2.testCase1.xml", files[1]);
    assertEquals("TEST-Safari_3_MacIntel.testCase1.xml", files[2]);
    assertEquals("TEST-Safari_3_MacIntel.testCase2.xml", files[3]);
  }

  private BrowserInfo makeBrowser(String name, String os, String version, long id) {
    BrowserInfo info = new BrowserInfo();
    info.setName(name);
    info.setOs(os);
    info.setVersion(version);
    info.setId(id);
    return info;
  }
}
