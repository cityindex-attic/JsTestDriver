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
var utilsTest = jstestdriver.testCaseManager.TestCase('utilsTest');

utilsTest.prototype.testStringFormat = function() {
  assertEquals("Hello world", jstestdriver.formatString("Hello %s", "world"));
  assertEquals("Hello 1", jstestdriver.formatString("Hello %d", 1));
  assertEquals("Hello world, number 1 and number 2 a float 1.5 ahah",
      jstestdriver.formatString("Hello %s, number %d and number %i a float %f", "world", 1, 2, 1.5,
      "ahah"));
  assertEquals("Hello world 42", jstestdriver.formatString("Hello", "world", 42));
  assertEquals("Hello {\"property\":\"value\"}", jstestdriver.formatString("Hello %o", {property:
    'value'}));
  assertEquals("Hello {\"property\":\"value\"}", jstestdriver.formatString("Hello", {property:
    'value'}));
};


utilsTest.prototype.testToHtml = function() {
  var node = jstestdriver.toHtml('<div id="foo"></div>', window.document);
  assertNotNull(node);
  assertEquals('foo', node.id);
  assertEquals('div', node.tagName.toLowerCase());
};


utilsTest.prototype.testToHtmlMultipleNodes = function() {
  var fragment = jstestdriver.toHtml('<div id="foo"></div><div id="bar"></div>', window.document);
  var node = fragment.firstChild;
  assertNotNull(node);
  assertEquals('foo', node.id);
  assertEquals('div', node.tagName.toLowerCase());
};


utilsTest.prototype.testStripHtmlComments = function() {
  var html = "<div><!-- foobar -->\n<p> zib zab</p><!-- bar --></div>";
  assertEquals("<div>\n<p> zib zab</p></div>",
      jstestdriver.stripHtmlComments(html));
};


utilsTest.prototype.testAppendHtml = function() {
  jstestdriver.appendHtml('<div id="foo"></div>', window.document);
  var node = document.getElementById('foo');
  assertNotNull(node);
  assertEquals('foo', node.id);
  assertEquals('div', node.tagName.toLowerCase());
};


utilsTest.prototype.testAppendHtmlMultipleNodes = function() {
  jstestdriver.appendHtml('<div id="foo"></div><div class="bar"></div>', window.document);
  var node = document.getElementById('foo');
  assertNotNull(node);
  assertEquals('foo', node.id);
  assertEquals('div', node.tagName.toLowerCase());
};


utilsTest.prototype.testAppendHtmlLotsOfHtml = function() {
  jstestdriver.appendHtml('<div id="foo"></div><div class="bar"></div>', window.document);
  var node = document.getElementById('foo');
  assertNotNull(node);
  assertEquals('foo', node.id);
  assertEquals('div', node.tagName.toLowerCase());
};
