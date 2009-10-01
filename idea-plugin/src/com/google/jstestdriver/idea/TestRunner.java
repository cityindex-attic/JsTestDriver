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
package com.google.jstestdriver.idea;

import com.google.jstestdriver.ActionRunner;
import com.google.jstestdriver.BrowserInfo;
import com.google.jstestdriver.ConfigurationParser;
import com.google.jstestdriver.DefaultPathRewriter;
import com.google.jstestdriver.DryRunInfo;
import com.google.jstestdriver.IDEPluginActionBuilder;
import com.google.jstestdriver.Response;
import com.google.jstestdriver.ResponseStream;
import com.google.jstestdriver.ResponseStreamFactory;
import com.google.jstestdriver.TestResult;
import com.google.jstestdriver.TestResultGenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;

/**
 * Run JSTD in its own process, and stream messages to a server that lives in the IDEA process,
 * which will update the UI with our results.
 * 
 * @author alexeagle@google.com (Alex Eagle)
 */
public class TestRunner {
  private final String serverURL;
  private final String settingsFile;
  private final ObjectOutputStream out;

  public TestRunner(String serverURL, String settingsFile, ObjectOutputStream out) {
    this.serverURL = serverURL;
    this.settingsFile = settingsFile;
    this.out = out;
  }

  public void execute() {
    ResponseStreamFactory responseStreamFactory = createResponseStreamFactory();
    final ActionRunner dryRunRunner =
        makeActionBuilder(responseStreamFactory)
          .dryRunFor(Arrays.asList("all")).build();
    final ActionRunner testRunner =
        makeActionBuilder(responseStreamFactory)
            .addAllTests().build();
    //TODO(alexeagle): support client-side reset action
    final ActionRunner resetRunner =
        makeActionBuilder(responseStreamFactory)
            .resetBrowsers().build();

    dryRunRunner.runActions();
    testRunner.runActions();
  }

  private ResponseStreamFactory createResponseStreamFactory() {
    return new ResponseStreamFactory() {
      public ResponseStream getRunTestsActionResponseStream(String s) {
        return new ResponseStream() {
          public void stream(Response response) {
            for (TestResult testResult : new TestResultGenerator().getTestResults(response)) {
              try {
                out.writeObject(TestResultProtocolMessage.fromTestResult(testResult));
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            }
          }

          public void finish() {
          }
        };
      }

      public ResponseStream getDryRunActionResponseStream() {
        return new ResponseStream() {
          public void stream(Response response) {
            BrowserInfo browser = response.getBrowser();
            DryRunInfo dryRunInfo = DryRunInfo.fromJson(response);
            for (String testName : dryRunInfo.getTestNames()) {
              try {
                out.writeObject(TestResultProtocolMessage.fromDryRun(testName, browser));
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            }
          }

          public void finish() {
          }
        };
      }

      public ResponseStream getEvalActionResponseStream() {
        return null;
      }

      public ResponseStream getResetActionResponseStream() {
        return null;
      }
    };
  }

  private IDEPluginActionBuilder makeActionBuilder(ResponseStreamFactory responseStreamFactory) {
    File configFile = new File(settingsFile);
    try {
      FileReader fileReader = new FileReader(settingsFile);
      ConfigurationParser configurationParser = new ConfigurationParser(configFile.getParentFile(),
          fileReader, new DefaultPathRewriter());
      return new IDEPluginActionBuilder(configurationParser, serverURL, responseStreamFactory);
    } catch (FileNotFoundException e) {
      throw new RuntimeException("Failed to read settings file " + settingsFile, e);
    }
  }

  public static void main(String[] args) throws IOException {
    String serverURL = args[0];
    String settingsFile = args[1];
    int port = Integer.parseInt(args[2]);
    Socket socket = new Socket();
    socket.connect(new InetSocketAddress(InetAddress.getLocalHost(), port));
    new TestRunner(serverURL, settingsFile, new ObjectOutputStream(socket.getOutputStream())).execute();
  }
}
