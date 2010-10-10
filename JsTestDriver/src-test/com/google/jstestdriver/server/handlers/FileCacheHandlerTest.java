// Copyright 2010 Google Inc. All Rights Reserved.
package com.google.jstestdriver.server.handlers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.jstestdriver.FileInfo;
import com.google.jstestdriver.FileSetCacheStrategy;

/**
 * @author rdionne@google.com (Robert Dionne)
 */
public class FileCacheHandlerTest extends junit.framework.TestCase {

  private final Gson gson = new Gson();

  public void testFileCache() throws IOException, ServletException {
    HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
    HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
    FileCacheHandler handler = new FileCacheHandler(
        request, response, gson, new HashSet<FileInfo>(), new FileSetCacheStrategy());
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PrintWriter writer = new PrintWriter(out);

    FileInfo info = new FileInfo("asdf", 1, false, true, "data");

    EasyMock.expect(request.getParameter("fileSet"))
        .andReturn(gson.toJson(new FileInfo[] {info}));
    EasyMock.expect(response.getWriter()).andReturn(writer);

    EasyMock.replay(request, response);

    handler.handleIt();
    writer.flush();
    Collection<FileInfo> infos = gson.fromJson(out.toString(),
        new TypeToken<Collection<FileInfo>>() {}.getType());
    assertEquals(info, infos.iterator().next());

    EasyMock.verify(request, response);
  }
}
