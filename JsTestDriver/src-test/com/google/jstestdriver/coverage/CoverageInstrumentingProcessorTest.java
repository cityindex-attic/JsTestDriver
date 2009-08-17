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

import com.google.jstestdriver.FileInfo;

import junit.framework.TestCase;

/**
 * @author corysmith@google.com (Cory Smith)
 *
 */
public class CoverageInstrumentingProcessorTest extends TestCase {

  public void testInstrument() throws Exception {
    FileInfo fileInfo = new FileInfo("foo.js", 0, true, false, "var a = 1;");
    String expected = "decorated";
    FileNameHasher hasher = new FileNameHasher();
    Code code = new Code(fileInfo.getFileName(),
                         hasher.hash(fileInfo.getFileName()),
                         fileInfo.getData());
    FileInfo decorated =
      new CoverageInstrumentingProcessor(new DecoratorStub(expected, code), hasher).process(fileInfo);
    assertEquals(expected, decorated.getData());
    assertEquals(fileInfo.getFileName(), decorated.getFileName());
    assertEquals(fileInfo.getTimestamp(), decorated.getTimestamp());
    assertEquals(fileInfo.isServeOnly(), decorated.isServeOnly());
  }

  public void testSkipInstrument() throws Exception {
    FileInfo lcov = new FileInfo("LCOV.js", 0, true, false, "var a = 1;");
    FileInfo serveOnly = new FileInfo("someData.dat", 0, true, true, "scally{wag}");
    FileInfo remote = new FileInfo("https://foobar", 0, true, false, null);
    CoverageInstrumentingProcessor processor = new CoverageInstrumentingProcessor(null, null);
    assertSame(lcov, processor.process(lcov));
    assertSame(serveOnly, processor.process(serveOnly));
    assertSame(remote, processor.process(remote));
  }
  
  static class DecoratorStub extends CodeCoverageDecorator {
    private final String decorated;
    private final Code expectedCode;
    public DecoratorStub(String decorated, Code expectedCode) {
      this.decorated = decorated;
      this.expectedCode = expectedCode;
    }

    @Override
    public String decorate(Code code) {
      assertEquals(expectedCode, code);
      return decorated;
    }
  }
}
