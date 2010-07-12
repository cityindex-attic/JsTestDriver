package com.google.jstestdriver.output;

import com.google.jstestdriver.Action;
import com.google.jstestdriver.model.RunData;

/**
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class PrintXmlTestResultsAction implements Action {
  private final XmlPrinter xmlPrinter;

  public PrintXmlTestResultsAction(XmlPrinter xmlPrinter) {
    this.xmlPrinter = xmlPrinter;
  }

  public RunData run(RunData testCase) {
    xmlPrinter.writeXmlReportFiles();
    return testCase;
  }
}
