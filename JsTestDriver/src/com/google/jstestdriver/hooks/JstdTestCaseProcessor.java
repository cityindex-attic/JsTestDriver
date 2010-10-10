package com.google.jstestdriver.hooks;

import java.util.Iterator;
import java.util.List;

import com.google.jstestdriver.model.JstdTestCase;

public interface JstdTestCaseProcessor {
  List<JstdTestCase> process(Iterator<JstdTestCase> testCase);
}
