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
var assertsTest = jstestdriver.testCaseManager.TestCase('assertsTest');


assertsTest.prototype.testAssertTrue = function() {
  try {
    assertTrue(true);
    assertTrue(false);
    fail('assertTrue did not throw an exception');
  } catch (e) {
    assertEquals('expected true but was false', e.message);
    assertEquals('AssertError', e.name);
  }
};


assertsTest.prototype.testAssertTrueWithMsg = function() {
  try {
    assertTrue(true);
    assertTrue('this is a message', false);
    fail('assertTrue did not throw an exception');
  } catch (e) {
    assertEquals('this is a message expected true but was false', e.message);
    assertEquals('AssertError', e.name);
  }
};


assertsTest.prototype.testAssertFalse = function() {
  try {
    assertFalse(false);
    assertFalse(true);
    fail('assertFalse did not throw an exception');
  } catch (e) {
    assertEquals('expected false but was true', e.message);
    assertEquals('AssertError', e.name);
  }
};


assertsTest.prototype.testAssertFalseWithMsg = function() {
  try {
    assertFalse(false);
    assertFalse('this is a message', true);
    fail('assertFalse did not throw an exception');
  } catch (e) {
    assertEquals('this is a message expected false but was true', e.message);
    assertEquals('AssertError', e.name);
  }
};


assertsTest.prototype.testAssertEquals = function() {
  try {
    assertEquals("hello", "hello");
    assertEquals("hello", "world");
    fail('assertEquals did not throw an exception');
  } catch (e) {
    assertEquals('expected "hello" but was "world"', e.message);
    assertEquals('AssertError', e.name);
  }
  try {
    assertEquals(new Array('mooh', 'meuh'), new Array('mooh', 'meuh'));
    assertEquals(new Array('mooh', 'meuh'), new Array('meuh'));
    fail('assertEquals did not throw an exception');
  } catch (e) {
    assertEquals('expected ["mooh","meuh"] but was ["meuh"]', e.message);
    assertEquals('AssertError', e.name);
  }
};


assertsTest.prototype.testAssertEqualsWithMsg = function() {
  try {
    assertEquals("hello", "hello");
    assertEquals('this is a message', "hello", "world");
    fail('assertEquals did not throw an exception');
  } catch (e) {
    assertEquals('this is a message expected "hello" but was "world"', e.message);
    assertEquals('AssertError', e.name);
  }
};


assertsTest.prototype.testAssertSame = function() {
  try {
    var obj1 = { data: 'data' };
    var obj2 = { data: 'data' };
    assertSame(obj1, obj1);
    assertSame(obj1, obj2);
    fail('assertSame did not throw an exception');
  } catch (e) {
    assertEquals('expected {"data":"data"} but was {"data":"data"}', e.message);
    assertEquals('AssertError', e.name);
  }
};


assertsTest.prototype.testAssertSameWithMsg = function() {
  try {
    var obj1 = { data: 'data' };
    var obj2 = { data: 'data' };
    assertSame(obj1, obj1);
    assertSame('this is a message', obj1, obj2);
    fail('assertSame did not throw an exception');
  } catch (e) {
    assertEquals('this is a message expected {"data":"data"} but was {"data":"data"}',
        e.message);
    assertEquals('AssertError', e.name);
  }
};


assertsTest.prototype.testAssertNotSame = function() {
  try {
    var obj1 = { data: 'data' };
    var obj2 = { data: 'data' };
    assertNotSame(obj1, obj2);
    assertNotSame(obj1, obj1);
    fail('assertNotSame did not throw an exception');
  } catch (e) {
    assertEquals('expected not same as {"data":"data"} but was {"data":"data"}', e.message);
    assertEquals('AssertError', e.name);
  }
};


assertsTest.prototype.testAssertNotSameWithMsg = function() {
  try {
    var obj1 = { data: 'data' };
    var obj2 = { data: 'data' };
    assertNotSame(obj1, obj2);
    assertNotSame('this is a message', obj1, obj1);
    fail('assertNotSame did not throw an exception');
  } catch (e) {
    assertEquals('this is a message expected not same as {"data":"data"} but was ' + 
        '{"data":"data"}', e.message);
    assertEquals('AssertError', e.name);
  }
};


assertsTest.prototype.testAssertNull = function() {
  try {
    assertNull(null);
    assertNull({});
    fail('assertNull did not throw an exception');
  } catch (e) {
    assertEquals('expected null but was {}', e.message);
    assertEquals('AssertError', e.name);
  }
};


assertsTest.prototype.testAssertNullWithMsg = function() {
  try {
    assertNull(null);
    assertNull('this is a message', {});
    fail('assertNull did not throw an exception');
  } catch (e) {
    assertEquals('this is a message expected null but was {}', e.message);
    assertEquals('AssertError', e.name);
  }
};


assertsTest.prototype.testAssertNotNull = function() {
  try {
    assertNotNull({});
    assertNotNull(null);
    fail('assertNotNull did not throw an exception');
  } catch (e) {
    assertEquals('expected not null but was null', e.message);
    assertEquals('AssertError', e.name);
  }
};


assertsTest.prototype.testAssertNotNullWithMsg = function() {
  try {
    assertNotNull({});
    assertNotNull('this is a message', null);
    fail('assertNotNull did not throw an exception');
  } catch (e) {
    assertEquals('this is a message expected not null but was null', e.message);
    assertEquals('AssertError', e.name);
  }
};


assertsTest.prototype.testExpectAsserts = function() {
  expectAsserts(4);
  assertEquals(4, jstestdriver.expectedAssertCount);
  expectAsserts(-1);
};


assertsTest.prototype.testAssertCount = function() {
  assertTrue(true);
  assertFalse(false);
  assertEquals("hello", "hello");
  assertEquals(3, jstestdriver.assertCount);
};
