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
if (!jstestdriver) {
  jstestdriver = {};
}


jstestdriver.MockDOM = function() {
  this.dom_ = [];
};


jstestdriver.MockDOM.prototype.createElement = function(element) {
  var node = new jstestdriver.MockNode(element);

  this.dom_.push(node);
  return node;
};


jstestdriver.MockDOM.prototype.getElementsByTagName = function(tagName) {
  var size = this.dom_.length;
  var elems = [];

  for (var i = 0; i < size; i++) {
    var node = this.dom_[i];

    if (node.nodeName == tagName) {
      elems.push(node);
    }
  }
  return elems;
};


jstestdriver.MockNode = function(name) {
  this.nodeName = name;
  this.childNodes = [];
};


jstestdriver.MockNode.prototype.onload = {};


jstestdriver.MockNode.prototype.appendChild = function(tag) {
  this.childNodes.push(tag);
};


jstestdriver.MockNode.prototype.removeChild = function(node) {
  var size = this.childNodes.length;
  var tmpChildNodes = [];

  for (var i = 0; i < size; i++) {
    var childNode = this.childNodes[i];

    if (childNode != node) {
      tmpChildNodes.push(childNode);
    }
  }
  this.childNodes = tmpChildNodes;
};
