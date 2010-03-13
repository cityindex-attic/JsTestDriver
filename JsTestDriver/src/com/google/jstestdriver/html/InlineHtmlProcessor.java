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
package com.google.jstestdriver.html;

import com.google.inject.Inject;
import com.google.jstestdriver.FileInfo;
import com.google.jstestdriver.hooks.FileLoadPostProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Extract the inline html deocrators.
 * 
 * @author corysmith@google.com (Cory Smith)
 * 
 */
public class InlineHtmlProcessor implements FileLoadPostProcessor {
  private static final Logger LOGGER = LoggerFactory.getLogger(InlineHtmlProcessor.class);
  private final HtmlDocParser parser;
  private final HtmlDocLexer lexer;

  @Inject
  public InlineHtmlProcessor(HtmlDocParser parser, HtmlDocLexer lexer) {
    this.parser = parser;
    this.lexer = lexer;
  }
  
  public FileInfo process(FileInfo file) {
    try {
      LOGGER.debug("inlining html for " + file.getFileName());
      String source = file.getData();
      Writer writer = new CharArrayWriter();
      parser.parse(
          lexer.createStream(
              new ByteArrayInputStream(source.getBytes()))).write(writer);
      writer.flush();
      return new FileInfo(file.getFileName(),
                          file.getTimestamp(),
                          file.isPatch(),
                          file.isServeOnly(),
                          writer.toString());
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

}
