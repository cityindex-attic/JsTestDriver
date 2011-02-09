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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((flag == null) ? 0 : flag.hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    CmdLineFlag other = (CmdLineFlag) obj;
    if (flag == null) {
      if (other.flag != null) return false;
    } else if (!flag.equals(other.flag)) return false;
    if (value == null) {
      if (other.value != null) return false;
    } else if (!value.equals(other.value)) return false;
    return true;
  }
}