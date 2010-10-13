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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.TimerTask;


// TODO(jeremiele): Fill in the javadoc.
/**
 * The client portion of the browser heartbeat.
 * 
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
class HeartBeat extends TimerTask {
  private static final Logger logger = LoggerFactory.getLogger(HeartBeat.class);

  private final String url;

  public HeartBeat(String url) {
    this.url = url;
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

  @Override
  public void run() {
    HttpURLConnection connection = null;

    try {
      connection = (HttpURLConnection) new URL(url).openConnection();

      connection.connect();
      String result = toString(connection.getInputStream());
      if (result.length() > 0) {
        logger.debug("Client Heatbeat: {}",
                     result);
      }
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
}
