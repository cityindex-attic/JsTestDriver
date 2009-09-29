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
import java.io.PrintStream;
import java.util.Arrays;

/**
 * Run JSTD and print messages using the TeamCity stuff, see
 * http://www.jetbrains.net/confluence/display/TCD4/Build+Script+Interaction+with+TeamCity
 * 
 * @author alexeagle@google.com (Alex Eagle)
 */
public class TestRunner {
  // The scala plugin does this...
  // http://svn.jetbrains.org/idea/BRANCHES/scala/diana/scala/Runners/src/org/jetbrains/plugins/scala/testingSupport/scalaTest/ScalaTestReporter.scala
  public static final String MAGIC_IDEA_PREFIX = "\n##teamcity";
  private final String serverURL;
  private final String settingsFile;
  private final PrintStream out;

  public TestRunner(String serverURL, String settingsFile, PrintStream out) {
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
              String testName = escapeString(testResult.getTestCaseName() + "." + testResult.getTestName());
              if (testResult.getResult() == TestResult.Result.passed) {
                out.println(MAGIC_IDEA_PREFIX + "[testFinished name='" + escapeString(testName)
                    + "' duration='" + testResult.getTime() +"']");
              } else {
                StringBuilder res = new StringBuilder(MAGIC_IDEA_PREFIX)
                    .append("[testFailed ")
                    .append("name='").append(testName)
                    .append("' message='").append(escapeString(testResult.getParsedMessage()))
                    .append("' details='")
                    .append(escapeString(testResult.getLog() + "\n" + testResult.getStack()))
                    .append("'")
                    .append(" duration='").append(testResult.getTime()).append("']");
                out.println(res.toString());
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
              out.println(MAGIC_IDEA_PREFIX + "[testStarted name='" + escapeString(testName) +
                  "' captureStandardOutput='true']");
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

  private String escapeString(String s) {
    return s.replaceAll("[|]", "||")
        .replaceAll("[']", "|'")
        .replaceAll("[\n]", "|n")
        .replaceAll("[\r]", "|r")
        .replaceAll("]","|]");
  }

  public static void main(String[] args) {
    String serverURL = args[0];
    String settingsFile = args[1];
    new TestRunner(serverURL, settingsFile, System.out).execute();
  }
}
