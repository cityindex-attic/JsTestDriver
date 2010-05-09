package com.google.jstestdriver;

import org.kohsuke.args4j.CmdLineException;



public interface FlagsParser {

  /**
   * Parses the Flags from a String[], throwing a exception either on '--help' or no args.
   */
  public abstract Flags parseArgument(String[] strings) throws CmdLineException;

}
