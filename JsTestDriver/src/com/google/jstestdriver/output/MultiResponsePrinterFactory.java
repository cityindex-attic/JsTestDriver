package com.google.jstestdriver.output;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * A factory that produces a MultiTestResultPrinter which delegates to
 * the product from a number of ResponsePrinterFactory's.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class MultiResponsePrinterFactory implements ResponsePrinterFactory {
  private final Collection<ResponsePrinterFactory> delegates;

  @Inject
  public MultiResponsePrinterFactory(Set<ResponsePrinterFactory> delegates) {
    this.delegates = delegates;
  }

  public TestResultPrinter getResponsePrinter(String xmlFile) {
    List<TestResultPrinter> printDelegates = Lists.newLinkedList();
    for (ResponsePrinterFactory delegate : delegates) {
      printDelegates.add(delegate.getResponsePrinter(xmlFile));
    }
    return new MultiTestResultPrinter(printDelegates);
  }
}
