/*
 * Copyright 2010 Google Inc.
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
package com.google.jstestdriver.util;

import com.google.common.collect.Lists;
import com.google.jstestdriver.model.HandlerPathPrefix;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * A simple method to easy writing html. Replace with a proper templating
 * system.
 *
 * @author corbinrsmith@gmail.com (Cory Smith)
 */
public class HtmlWriter {
  public static
      String FRAMESET =
          "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Frameset//EN\" \"http://www.w3.org/TR/html4/frameset.dtd\">";
  public static
      String QUIRKS =
          "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">";
  public static
      String STRICT =
          "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/DTD/strict.dtd\">";
  private final Writer writer;
  private final HandlerPathPrefix prefix;
  private String dtd = null;
  private List<String> body = Lists.newLinkedList();

  public HtmlWriter(Writer writer, HandlerPathPrefix prefix) {
    this.writer = writer;
    this.prefix = prefix;
  }

  public HtmlWriter writeStrictDtd() {
    dtd = STRICT;
    return this;
  }

  public HtmlWriter writeQuirksDtd() {
    dtd = QUIRKS;
    return this;
  }

  public HtmlWriter startHead() {
    body.add("<html>");
    body.add("<head>");
    return this;
  }

  public HtmlWriter writeTitle(String title) {
    body.add("<title>");
    body.add(title);
    body.add("</title>");
    return this;
  }

  public HtmlWriter writeStyleSheet(String path) {
    body.add("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
    body.add(prefix.prefixPath(path));
    body.add("\"/>");
    return this;
  }

  public HtmlWriter finishHead() {
    body.add("</head>");
    return this;
  }

  public HtmlWriter startBody() {
    body.add("<body>");
    return this;
  }

  public HtmlWriter writeIframe(String id, String src) {
    body.add("<iframe id=\"");
    body.add(id);
    body.add("\" src=\"");
    body.add(prefix.prefixPath(src));
    body.add("\" frameborder=\"0\"></iframe>");
    return this;
  }

  public HtmlWriter finishBody() {
    body.add("</body>");
    return this;
  }

  public HtmlWriter writeExternalScript(String path) {
    body.add("<script src=\"");
    body.add(prefix.prefixPath(path));
    body.add("\" type=\"text/javascript\"></script>");
    return this;
  }

  public void flush() throws IOException {
    body.add("</html>");
    writer.append(dtd);
    for (String fragment : body) {
      writer.write(fragment);
    }
    writer.flush();
  }

  public HtmlWriter writeScript(String script) {
    body.add("<script type=\"text/javascript\">");
    body.add(script);
    body.add("</script>");
    return this;
  }

  public HtmlWriter startFrameSet() {
    dtd = FRAMESET;
    body.add("<frameset rows='80,*' border=\"1\">");
    return this;
  }

  public HtmlWriter writeFrame(String id, String src) {
    body.add("<frame id=\"");
    body.add(id);
    body.add("\" src=\"");
    body.add(src);
    body.add("\" />");
    return this;
  }

  public HtmlWriter finishFrameSet() {
    body.add("</frameset>");
    return this;
  }
}
