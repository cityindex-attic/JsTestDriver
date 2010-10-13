package com.google.jstestdriver.hooks;

import com.google.jstestdriver.model.JstdTestCase;

import java.util.Iterator;
import java.util.List;

public interface JstdTestCaseProcessor {
  List<JstdTestCase> process(Iterator<JstdTestCase> testCase);
}
