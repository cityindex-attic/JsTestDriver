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
var consoleTest = jstestdriver.testCaseManager.TestCase('consoleTest');

consoleTest.prototype.testLog = function() {
  var console = new jstestdriver.Console();

  console.log("Hello %s", "world");
  assertEquals("[LOG] Hello world", console.getAndResetLog());
  console.log("This is some log", "and more");
  assertEquals("[LOG] This is some log and more", console.getAndResetLog());
  console.log("A number %d", 42);
  assertEquals("[LOG] A number 42", console.getAndResetLog());
  console.log("A", "B");
  console.log("C %s", "D"); 
  assertEquals("[LOG] A B\n[LOG] C D", console.getAndResetLog());
  console.log("log"); 
  console.debug("debug");
  assertEquals("[LOG] log\n[DEBUG] debug", console.getAndResetLog());
  console.log("log");
  console.debug("debug");
  console.info("info");
  console.warn("warn");
  console.error("error");
  assertEquals("[LOG] log\n[DEBUG] debug\n[INFO] info\n[WARN] warn\n[ERROR] error",
      console.getAndResetLog());
}
