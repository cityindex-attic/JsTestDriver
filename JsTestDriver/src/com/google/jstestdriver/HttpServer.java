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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class HttpServer implements Server {

  public String fetch(String url) {
    HttpURLConnection connection = null;

    try {
      connection = (HttpURLConnection) new URL(url).openConnection();

      connection.connect();
      return toString(connection.getInputStream());
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
  }

  private String toString(InputStream inputStream) throws IOException {
    StringBuilder sb = new StringBuilder();
    int ch;

    while ((ch = inputStream.read()) != -1) {
      sb.append((char) ch);
    }
    inputStream.close();
    return sb.toString();
  }

  public String post(String url, Map<String, String> params) {
    HttpURLConnection connection = null;

    try {
      String paramsString = convertParamsToString(params);

      connection = (HttpURLConnection) new URL(url).openConnection();
      connection.setRequestMethod("POST");
      connection.setDoOutput(true);
      connection.setDoInput(true);
      connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      connection.setRequestProperty("Content-Length", Integer
          .toString(paramsString.getBytes().length));
      OutputStreamWriter oWriter = new OutputStreamWriter(connection.getOutputStream());

      oWriter.write(paramsString);
      oWriter.close();
      connection.connect();
      return toString(connection.getInputStream());
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
  }

  public String convertParamsToString(Map<String, String> params)
      throws UnsupportedEncodingException {
    StringBuilder sb = new StringBuilder();
    Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();

    if (iterator.hasNext()) {
      Map.Entry<String, String> entry = iterator.next();

      sb.append(String.format("%s=%s", URLEncoder.encode(entry.getKey(), "UTF-8"),
          URLEncoder.encode(entry.getValue(), "UTF-8")));
      while (iterator.hasNext()) {
        entry = iterator.next();
        sb.append(String.format("&%s=%s", URLEncoder.encode(entry.getKey(), "UTF-8"),
            URLEncoder.encode(entry.getValue(), "UTF-8")));
      }
    }
    return sb.toString();
  }

  public String startSession(String baseUrl, String id) {
    return fetch(baseUrl + "/fileSet?id=" + id + "&session=start");
  }

  public void stopSession(String baseUrl, String id, String sessionId) {
    fetch(baseUrl + "/fileSet?id=" + id + "&session=stop" + "&sessionId=" + sessionId);
  }
}
