package com.google.jstestdriver.output;

import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static com.google.common.collect.Multimaps.newMultimap;
import static com.google.common.collect.Multimaps.synchronizedMultimap;

import com.google.common.base.Supplier;
import com.google.common.collect.Multimap;
import com.google.inject.Singleton;
import com.google.jstestdriver.BrowserInfo;
import com.google.jstestdriver.FileResult;
import com.google.jstestdriver.TestResult;

import java.util.Collection;
import java.util.Map;

/**
 * A data storage for test results. It listens on each browser for incoming test results,
 * on multiple threads. Then the XmlPrinter can use it to find the test results, so it can
 * produce our XML output files.
 * It should be bound as a Singleton to be sure the data is shared between these classes.
 * @author alexeagle@google.com (Alex Eagle)
 */
@Singleton
public class TestResultHolder implements TestResultListener {
  private static final class LinkListSupplier implements
      Supplier<Collection<TestResult>> {
    public Collection<TestResult> get() {
          return newLinkedList();
        }
  }

  private final Multimap<BrowserInfo, TestResult> results;

  public TestResultHolder() {
    Map<BrowserInfo, Collection<TestResult>> map = newLinkedHashMap();
    results = synchronizedMultimap(createMultiMap(map));
  }

  private Multimap<BrowserInfo, TestResult> createMultiMap(
      Map<BrowserInfo, Collection<TestResult>> map) {
    Supplier<Collection<TestResult>> collectionSupplier = new LinkListSupplier();
    Multimap<BrowserInfo, TestResult> resultMultimap =
        newMultimap(map, collectionSupplier);
    return resultMultimap;
  }

  /**
   * @return a map of browser name to test results from that browser
   */
  public Multimap<BrowserInfo, TestResult> getResults() {
    return results;
  }

  public void onTestComplete(TestResult testResult) {
    results.put(testResult.getBrowserInfo(), testResult);
  }

  public void finish() {
  }

  public void onFileLoad(String browser, FileResult fileResult) {
    // TODO(corysmith): Add load error to the xml results
  }
}
