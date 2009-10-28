package com.google.jstestdriver.guice;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import static com.google.inject.multibindings.Multibinder.newSetBinder;
import com.google.inject.name.Names;
import com.google.jstestdriver.DefaultResponseStreamFactory;
import com.google.jstestdriver.ResponseStreamFactory;
import com.google.jstestdriver.output.MultiResponsePrinterFactory;
import com.google.jstestdriver.output.PrintStreamResponsePrinterFactory;
import com.google.jstestdriver.output.ResponsePrinterFactory;
import com.google.jstestdriver.output.XmlResponsePrinterFactory;

import java.io.PrintStream;

/**
 * Configuration for outputting test results. If a testOutput flag was
 * provided, then XML result files will be written to that directory.
 * A text report is always written to the provided PrintStream.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class TestResultPrintingModule extends AbstractModule {

  private final PrintStream out;
  private final String testOutput;

  public TestResultPrintingModule(PrintStream out, String testOutput) {
    this.out = out;
    this.testOutput = testOutput;
  }

  protected void configure() {
    bind(PrintStream.class).annotatedWith(Names.named("outputStream")).toInstance(out);

    Multibinder<ResponsePrinterFactory> responsePrinterFactories =
        newSetBinder(binder(), ResponsePrinterFactory.class);
    if (testOutput.length() > 0) {
      responsePrinterFactories.addBinding().to(XmlResponsePrinterFactory.class);
    }
    responsePrinterFactories.addBinding().to(PrintStreamResponsePrinterFactory.class);
    bind(ResponsePrinterFactory.class).to(MultiResponsePrinterFactory.class);
    newSetBinder(binder(),
        ResponseStreamFactory.class).addBinding().to(DefaultResponseStreamFactory.class);
  }
}
