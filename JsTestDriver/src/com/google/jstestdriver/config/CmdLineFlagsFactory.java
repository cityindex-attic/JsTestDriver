package com.google.jstestdriver.config;

import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;

/**
 * Poorman's flag parser. This will take a String[] and translate it into a
 * List<CmdLineFlags>, a very lightweight representation of flag objects.
 * @author corysmith@google.com (Cory Smith)
 */
public class CmdLineFlagsFactory {
  public CmdFlags create(String[] args) {
    CmdLineFlagsFactory.CmdLineFlagIterator iterator = new CmdLineFlagIterator(args);
    List<CmdLineFlag> flags = Lists.newLinkedList();
    while (iterator.hasNext()) {
      flags.add(iterator.next());
    }
    return new CmdFlags(flags);
  }

  /**
   * Iterates over an array of String[] returning CmdLineFlags object. Poor
   * mans flag parser, really. This is used to extract flags as a precursor 
   * until we can use the heavy weight flag parsing machinery.
   */
  private static class CmdLineFlagIterator implements Iterator<CmdLineFlag> {
    private final String[] args;
    private int pos = 0;

    public CmdLineFlagIterator(String[] args) {
      this.args = args;
    }

    public boolean hasNext() {
      while (pos < args.length) {
        if (args[pos].startsWith("--")) {
          return true;
        }
        pos++;
      }
      return false;
    }

    public CmdLineFlag next() {
      int current = pos++;
      int next = pos;
      if (next >= args.length || args[next].startsWith("--")) {
        if (args[current].contains("=")) {
          String[] flagValue = args[current].split("=");
          return new CmdLineFlag(flagValue[0], flagValue[1]);
        }
        return new CmdLineFlag(args[current], null);
      }
      pos++; // consume the next because it's the value.
      return new CmdLineFlag(args[current], args[next]);
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
}