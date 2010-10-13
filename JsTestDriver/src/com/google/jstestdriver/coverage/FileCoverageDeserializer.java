package com.google.jstestdriver.coverage;

import com.google.common.collect.Lists;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Deserializes a file coverage prototbuffer. Not the prettiest solution
 * but workable.
 * @author corysmith@google.com (Cory Smith)
 */
public class FileCoverageDeserializer {

  // TODO(corysmith): clean this up into a good oo structure...
  public FileCoverage deserializeCoverage(InputStream in) throws IOException {
    int fileId = parseToIntEndingWith(in, ',');
    verifyAndConsumeToken(in, '[');
    List<CoveredLine> lines = deserializeLines(in);
    return new FileCoverage(fileId, lines);
  }
  
  public List<FileCoverage> deserializeCoverages(InputStream in) throws IOException {
    verifyAndConsumeToken(in, '[');
    List<FileCoverage> coverages = Lists.newLinkedList();
    char next = consumeToken(in);
    while(next == '[') {
      coverages.add(deserializeCoverage(in));
      next = conditionalConsumeToken(in, ',');
    }
    verifyToken(next, ']');
    return coverages;
  }

  public List<CoveredLine> deserializeLines(InputStream in) throws IOException {
    List<CoveredLine> lines = Lists.newLinkedList();
    char next = consumeToken(in);
    while(next == '[') {
      lines.add(deserializeLine(in));
      next = consumeToken(in);
      next = conditionalConsumeToken(in, ',');
    }
    verifyToken(next, ']');
    return lines;
  }
  
  public CoveredLine deserializeLine(InputStream in) throws IOException {
    int lineNumber = parseToIntEndingWith(in, ','); 
    int executedNumber = parseToIntEndingWith(in, ']');
    return new CoveredLine(lineNumber, executedNumber);
  }
  
  // TODO(corysmith): replce this with a utility class?
  private char conditionalConsumeToken(InputStream in, char consumable) throws IOException {
    char next = consumeToken(in);
    if (next == consumable) {
      next = consumeToken(in);
    }
    return next;
  }
  
  private void verifyToken(char next, char expected) {
    if (next != expected) {
      throw new RuntimeException(
          String.format("unrecognized format, expected %s  was %s",
              expected, next));
    }
  }
  
  private int parseToIntEndingWith(InputStream in, char delim) throws IOException {
    StringBuilder intBuilder = new StringBuilder();
    char token = consumeToken(in);
    while (token != delim) {
      intBuilder.append(token);
      token = consumeToken(in);
    }
    return Integer.parseInt(intBuilder.toString());
  }

  private void verifyAndConsumeToken(InputStream in, char expected) throws IOException {
    char token = consumeToken(in);
    if (token != expected) {
      throw new RuntimeException(
          String.format("unrecognized format, expected %s  was %s",
              expected, token));
    }
  }

  private char consumeToken(InputStream in) throws IOException {
    char token = (char) in.read();
    while (token == ' ') {
      token = (char) in.read();
    }
    return token;
  }
}
