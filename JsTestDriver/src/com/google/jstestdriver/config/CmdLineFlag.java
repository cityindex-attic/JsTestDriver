package com.google.jstestdriver.config;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * An extremely simple flag object. It only support the name and the value
 * as a simple string, or as a boolean.
 * @author corysmith@google.com (Cory Smith)
 *
 */
public class CmdLineFlag {
  public final String flag;
  public final String value;

  public CmdLineFlag(String flag, String value) {
    this.flag = flag;
    this.value = value;
  }

  public String toCmdArg() {
    return value == null ? flag : flag + "=" + value;
  }

  public String safeValue() {
    return value == null ? "" : value;
  }

  public List<String> valuesList() {
    if (value == null) {
      return Collections.<String>emptyList();
    }
    List<String> values =  Lists.<String>newLinkedList();
    for (String string : value.split(",")) {
      values.add(string);
    }
    return values;
  }

  public void addToArgs(List<String> args) {
    args.add(flag);
    if (value != null) {
      args.add(value);
    }
  }
}