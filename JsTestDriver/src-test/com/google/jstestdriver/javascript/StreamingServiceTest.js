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

var StreamingServiceTest = TestCase('StreamingServiceTest');


StreamingServiceTest.prototype.setUp = function() {
  var posts = this.posts = [];
  this.now = 1;
  var testCase = this;
  this.streamingService = new jstestdriver.StreamingService("/Q/1",
          function(){ return testCase.now++; },
          function (url, data, callback, type){
    posts.push({
      url : url,
      data : data,
      callback : callback,
      type : type
    });
  });
};


StreamingServiceTest.prototype.testStreamAndClose = function() {
  function callback() {}
  var response = new jstestdriver.Response('test', '1', {}, null);
  var finalResponse = new jstestdriver.Response('test', '2', {}, null);

  this.streamingService.stream(response, callback);

  var streamPost = this.posts.pop();
  assertEquals(callback, streamPost.callback);
  assertFalse("Streaming should not be a final reponse.", streamPost.data.done);
  assertEquals(response, streamPost.data.response);
  assertEquals(this.now - 1, streamPost.data.responseId);

  var streamQuery = this.posts.pop();
  assertNotNull("Didn't ask the server for a list of responseIds", streamQuery);
  // first callback from the server, for the stream
  this.streamingService.streamAcknowledged([streamPost.data.responseId]);

  this.streamingService.close(finalResponse, callback);
  var finalResponsePost = this.posts.pop();
  assertNotNull("Final response should be sent when the server confirms resposneId",
                finalResponsePost);
  assertTrue("Final response should be done.", finalResponsePost.data.done);
  assertEquals(finalResponse, finalResponsePost.data.response);
  assertEquals("finalResponses do not have responseIds",
               null, finalResponsePost.data.responseId);

  // second callback from the server for the query
  this.streamingService.streamAcknowledged([streamPost.data.responseId]);
  assertEquals("Shouldn't be any posts after the final response.",
               0, this.posts.length);
};


StreamingServiceTest.prototype.testStreamOutOfOrderAndClose = function() {
  function callback() {}
  var responseOne = new jstestdriver.Response('test', '1', {}, null);
  var responseTwo = new jstestdriver.Response('test', '2', {}, null);
  var finalResponse = new jstestdriver.Response('test', '3', {}, null);

  this.streamingService.stream(responseTwo, callback);
  var streamTwoPost = this.posts.pop();

  this.streamingService.stream(responseOne, callback);
  var streamOnePost = this.posts.pop();

  this.streamingService.close(finalResponse, callback);
  var streamQueryOnClose = this.posts.pop();

  // first ack, no responses on the server
  this.streamingService.streamAcknowledged([]);
  streamQueryOnClose = this.posts.pop();
  assertEquals("Should be a query", null, streamQueryOnClose.data.response);
  assertEquals("Should not be done", false, streamQueryOnClose.data.done);

  // second ack, one response  on the server
  this.streamingService.streamAcknowledged([streamTwoPost.data.responseId]);
  streamQueryOnClose = this.posts.pop();
  assertEquals("Should be a query", null, streamQueryOnClose.data.response);
  assertEquals("Should not be done", false, streamQueryOnClose.data.done);
  assertEquals("Final response should not be sent", 0, this.posts.length);

  // third ack, two responses on the server
  this.streamingService.streamAcknowledged([streamTwoPost.data.responseId,
                                            streamOnePost.data.responseId]);
  var finalPost = this.posts.pop();
  assertNotNull("Final response should be sent", finalPost);
  assertEquals("Should contain final response", finalResponse, finalPost.data.response);
  assertEquals("Should be done", true, finalPost.data.done);
};


StreamingServiceTest.prototype.testClose = function() {
  function callback() {}
  var finalResponse = new jstestdriver.Response('test', '3', {}, null);
  this.streamingService.close(finalResponse, callback);
  var closePost = this.posts.pop();
  assertNotNull('final response should be sent immediately', closePost);
};
