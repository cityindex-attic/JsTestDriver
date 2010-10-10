/*
 * Copyright 2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.jstestdriver.coverage;

import junit.framework.TestCase;

import com.google.jstestdriver.FileResult;
import com.google.jstestdriver.ResponseStream;
import com.google.jstestdriver.TestResult;
import com.google.jstestdriver.TestResultGenerator;
import com.google.jstestdriver.output.TestResultListener;


/**
 * @author corysmith
 * 
 */
public class CoverageResponseStreamFactoryTest extends TestCase {

  public final class TestResultListenerStub implements TestResultListener {
    public TestResult testResult;

    public void finish() {
    }

    public void onTestComplete(TestResult testResult) {
      this.testResult = testResult;
    }

    public void onFileLoad(String browser, FileResult fileResult) {
      
    }
  }

  final TestResultListener listener = new TestResultListenerStub();

  public void testGetRunTestsActionResponseStream() throws Exception {
    CoverageResponseStreamFactory factory = new CoverageResponseStreamFactory(null, new TestResultGenerator());

    ResponseStream responseStream = factory.getRunTestsActionResponseStream("browserId");
    assertNotNull(responseStream);
  }

  public void testGetResetActionResponseStream() throws Exception {
    CoverageResponseStreamFactory factory = new CoverageResponseStreamFactory(null, new TestResultGenerator());
    
    ResponseStream responseStream = factory.getResetActionResponseStream();
    assertEquals(CoverageResponseStreamFactory.NULL_RESPONSE_STREAM, responseStream);
  }
}
