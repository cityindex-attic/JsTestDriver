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
package com.google.jstestdriver;

import junit.framework.TestCase;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author corysmith@google.com (Cory Smith)
 *
 */
public class BrowserStartupActionTest extends TestCase {

  public void testProcessRun() throws Exception {
    CountDownLatchFake latch = new CountDownLatchFake(1, false, true);
    final String browserPath = "path/to/ff";
    final String serverAddress = "http://foo:8080";
    final BrowserStartupAction action = new BrowserStartupAction(Arrays.asList(browserPath),
                                                           serverAddress,
                                                           new ProcessFactory(){
      public Process start(String ... commands) {
        return new ProcessStub(commands);
      }
    }, latch);

    action.run();

    assertFalse(action.getProcesses().isEmpty());
    assertEquals(new ProcessStub(new String[] {browserPath, serverAddress + "/capture"}),
      action.getProcesses().get(0));
  }
  
  public void testProcessStartFailure() throws Exception {
    CountDownLatchFake latch = new CountDownLatchFake(1, false, true);
    final String browserPath = "path/to/ff";
    final String nonexistant = "nonexistant";
    final String serverAddress = "http://foo:8080";
    final BrowserStartupAction action = new BrowserStartupAction(
      Arrays.asList(browserPath, nonexistant, browserPath),
      serverAddress,
      new ProcessFactory(){
        public Process start(String ... commands) throws IOException{
          if (commands[0].equals(nonexistant)) {
            throw new IOException();
          }
          return new ProcessStub(commands);
        }
    }, latch);
    
    action.run();
    
    ArrayList<ProcessStub> expected = new ArrayList<ProcessStub>(Arrays.asList(
      new ProcessStub(new String[] {browserPath, serverAddress + "/capture"}),
      new ProcessStub(new String[] {browserPath, serverAddress + "/capture"})
    ));

    assertFalse(action.getProcesses().isEmpty());
    assertEquals(expected,
                 action.getProcesses());
  }

  private final static class ProcessStub extends Process {

    public final String[] commands;

    public ProcessStub(String[] commands) {
      this.commands = commands;
    }
    @Override
    public void destroy() {
    }

    @Override
    public int exitValue() {
      return 0;
    }

    @Override
    public InputStream getErrorStream() {
      return null;
    }

    @Override
    public InputStream getInputStream() {
      return null;
    }

    @Override
    public OutputStream getOutputStream() {
      return null;
    }

    @Override
    public int waitFor() {
      return 0;
    }
    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + Arrays.hashCode(commands);
      return result;
    }
    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      ProcessStub other = (ProcessStub) obj;
      if (!Arrays.equals(commands, other.commands))
        return false;
      return true;
    }
    @Override
    public String toString() {
      return String.format("%s(%s)", getClass().getSimpleName(), Arrays.toString(commands));
    }
  }

  static class CountDownLatchFake extends CountDownLatch{
    private final boolean awaitResponse;
    private int count;

    public CountDownLatchFake(int count, boolean wait, boolean awaitResponse) {
      super(0);
      this.count = count;
      this.awaitResponse = awaitResponse;
    }

    @Override
    public boolean await(long timeoutPassed, TimeUnit unit) {
      return awaitResponse;
    }

    @Override
    public long getCount() {
      return count;
    }

    @Override
    public void countDown() {
      count--;
      //super.countDown();
    }
  }
}
