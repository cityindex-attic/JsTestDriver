/**
 * 
 */
package com.google.jstestdriver.server.handlers.pages;

import java.io.IOException;

import com.google.jstestdriver.util.HtmlWriter;

/**
 * Common interface for runner pages.
 * 
 * @author corbinrsmith@gmail.com (Cory Smith)
 */
public interface Page {
  void render(HtmlWriter writer, SlavePageRequest request) throws IOException;
}