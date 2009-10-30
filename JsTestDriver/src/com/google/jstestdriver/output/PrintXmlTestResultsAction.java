package com.google.jstestdriver.output;

import com.google.jstestdriver.Action;

/**
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class PrintXmlTestResultsAction implements Action {
  private final XmlPrinter xmlPrinter;

  public PrintXmlTestResultsAction(XmlPrinter xmlPrinter) {
    this.xmlPrinter = xmlPrinter;
  }

  public void run() {
    xmlPrinter.writeXmlReportFiles();
  }
}
