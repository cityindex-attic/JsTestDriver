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

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;

/**
 * Parsers the Flags from args.
 * @author corysmith
 *
 */
public class FlagsParser {

  public static class StringListOptionHandler extends OptionHandler<List<String>> {
  
    public StringListOptionHandler(CmdLineParser parser, OptionDef option,
        Setter<? super List<String>> setter) {
      super(parser, option, setter);
    }
  
    /**
     * @see org.kohsuke.args4j.spi.OptionHandler#getDefaultMetaVariable()
     */
    @Override
    public String getDefaultMetaVariable() {
      return "VAR";
    }
  
    @Override
    public int parseArguments(Parameters params) throws CmdLineException {
      setter.addValue(Arrays.asList(params.getParameter(0).split("(,|\\s)")));
      return 1;
    }
  }

  /**
   * Parses the Flags from a String[], throwing a exception either on '--help' or no args.
   */
  public Flags parseArgument(String[] strings) throws CmdLineException {
    FlagsImpl flags = new FlagsImpl();
    CmdLineParser.registerHandler(List.class, FlagsParser.StringListOptionHandler.class);
    CmdLineParser cmdLineParser = new CmdLineParser(flags);
    try {
      cmdLineParser.parseArgument(strings);
    } catch (CmdLineException e) {
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      cmdLineParser.printUsage(stream);
      throw new CmdLineException(e.getMessage() + "\n" + stream.toString());
    }
    if (strings.length == 0 || flags.getDisplayHelp()) {
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      cmdLineParser.printUsage(stream);
      throw new CmdLineException(stream.toString());
    }
    return flags;
  }
}
