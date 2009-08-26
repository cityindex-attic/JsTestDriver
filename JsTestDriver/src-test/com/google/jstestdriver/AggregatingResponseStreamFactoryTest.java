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

import junit.framework.TestCase;

import com.google.common.collect.Sets;

/**
 * @author corysmith@google.com (Cory Smith)
 */
public class AggregatingResponseStreamFactoryTest extends TestCase {

  private final class MockResponseStream implements ResponseStream {
    private Response response;
    private String browserId;
    private boolean finished = false;

    public void stream(Response response) {
      this.response = response;
    }

    public Response getResponse() {
      return response;
    }

    public void finish() {
      finished  = true;
    }

    public String getBrowserId() {
      return browserId;
    }

    public void setBrowserId(String browserId) {
      this.browserId = browserId;
    }

    public boolean isFinished() {
      return finished;
    }
  }

  private final class ResponseStreamFactoryStub implements
      ResponseStreamFactory {
    private final MockResponseStream responseStream;

    public ResponseStreamFactoryStub(MockResponseStream responseStream) {
      this.responseStream = responseStream;
    }

    public ResponseStream getDryRunActionResponseStream() {
      return responseStream;
    }

    public ResponseStream getEvalActionResponseStream() {
      return responseStream;
    }

    public ResponseStream getResetActionResponseStream() {
      return responseStream;
    }

    public ResponseStream getRunTestsActionResponseStream(String browserId) {
      responseStream.setBrowserId(browserId);
      return responseStream;
    }
  }

  public void testGetRunTestsActionResponseStream() throws Exception {
    String browserId = "foo";
    MockResponseStream streamOne = new MockResponseStream();
    MockResponseStream streamTwo = new MockResponseStream();
    ResponseStream stream = new AggregatingResponseStreamFactory(
        Sets.<ResponseStreamFactory> newHashSet(
            new ResponseStreamFactoryStub(streamOne),
            new ResponseStreamFactoryStub(streamTwo))).getRunTestsActionResponseStream(browserId);
    
    Response response = new Response();
    stream.stream(response);
    stream.finish();
    
    assertEquals(response, streamOne.getResponse());
    assertEquals(response, streamTwo.getResponse());
    assertEquals(browserId, streamOne.getBrowserId());
    assertEquals(browserId, streamTwo.getBrowserId());
    assertTrue(streamOne.isFinished());
    assertTrue(streamTwo.isFinished());
  }

  public void testGetEvalActionResponseStream() throws Exception {
    MockResponseStream streamOne = new MockResponseStream();
    MockResponseStream streamTwo = new MockResponseStream();
    ResponseStream stream = new AggregatingResponseStreamFactory(
      Sets.<ResponseStreamFactory> newHashSet(
        new ResponseStreamFactoryStub(streamOne),
        new ResponseStreamFactoryStub(streamTwo))).getEvalActionResponseStream();
    
    Response response = new Response();
    stream.stream(response);
    stream.finish();
    
    assertEquals(response, streamOne.getResponse());
    assertEquals(response, streamTwo.getResponse());
    assertTrue(streamOne.isFinished());
    assertTrue(streamTwo.isFinished());
  }
  
  public void testGetDryRunActionResponseStream() throws Exception {
    MockResponseStream streamOne = new MockResponseStream();
    MockResponseStream streamTwo = new MockResponseStream();
    ResponseStream stream = new AggregatingResponseStreamFactory(
      Sets.<ResponseStreamFactory> newHashSet(
        new ResponseStreamFactoryStub(streamOne),
        new ResponseStreamFactoryStub(streamTwo))).getDryRunActionResponseStream();
    
    Response response = new Response();
    stream.stream(response);
    stream.finish();
    
    assertEquals(response, streamOne.getResponse());
    assertEquals(response, streamTwo.getResponse());
    assertTrue(streamOne.isFinished());
    assertTrue(streamTwo.isFinished());
  }
  
  public void testGetResetActionResponseStream() throws Exception {
    MockResponseStream streamOne = new MockResponseStream();
    MockResponseStream streamTwo = new MockResponseStream();
    ResponseStream stream = new AggregatingResponseStreamFactory(
      Sets.<ResponseStreamFactory> newHashSet(
        new ResponseStreamFactoryStub(streamOne),
        new ResponseStreamFactoryStub(streamTwo))).getResetActionResponseStream();
    
    Response response = new Response();
    stream.stream(response);
    stream.finish();
    
    assertEquals(response, streamOne.getResponse());
    assertEquals(response, streamTwo.getResponse());
    assertTrue(streamOne.isFinished());
    assertTrue(streamTwo.isFinished());
  }
}
