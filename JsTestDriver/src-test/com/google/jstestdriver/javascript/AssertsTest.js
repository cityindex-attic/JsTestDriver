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
  try {
    assertTrue(undefined);
	fail("assertTrue did not throw an exception");
  }catch (e){
    assertEquals('Not a boolean: [undefined]', e.message);
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
  try {
    assertTrue("This should fail",undefined);
	fail("assertTrue did not throw an exception");
  }catch (e){
    assertEquals('Not a boolean: [undefined]', e.message);
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
  try {
    assertFalse(undefined);
	fail("assertFalse did not throw an exception");
  }catch (e){
    assertEquals('Not a boolean: [undefined]', e.message);
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
  try {
    assertFalse("This should fail",undefined);
	fail("assertFalse did not throw an exception");
  }catch (e){
    assertEquals('Not a boolean: [undefined]', e.message);
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
  try{
	assertEquals(true,undefined);
	fail('assertEquals did not throw an exception when testing against undefined');
  }catch (e){
	assertEquals('AssertError',e.name);
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
  try{
	assertEquals("This should fail since true !== undefined'",true,undefined);
	fail('assertEquals did not throw an exception when testing against undefined');
  }catch (e){
	assertEquals('AssertError',e.name);
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
  try{
    var trueObj = { tre: true}; 
	assertSame(trueObj,undefined);
	fail('assertSame did not throw an exception');
  }catch (e){
	assertEquals('AssertError',e.name);
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
  try{
    var trueObj = { tre: true}; 
	assertSame("This should fail because object !== undefined",trueObj,undefined);
	fail('assertSame did not throw an exception');
  }catch (e){
	assertEquals('AssertError',e.name);
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
  var trueObj = { tre: true}; 
  assertNotSame(trueObj,undefined);
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
  var trueObj = { tre: true}; 
  assertNotSame("This is a message",trueObj,undefined);
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
  try{
	assertNull(undefined);
	fail('assertNull did not throw an exception');
  } catch (e) {
	assertEquals('expected null but was [undefined]', e.message);
	assertEquals('AssertError',e.name);
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
  try{
	assertNull("This should fail",undefined);
	fail('assertNull did not throw an exception');
  } catch (e) {
	assertEquals('This should fail expected null but was [undefined]', e.message);
	assertEquals('AssertError',e.name);
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
	assertNotNull(undefined);
};

/*
assertsTest.prototype.testAssertNullWithFunction = function() {
  try {
    assertNull(function(){});
    fail('assertNotNull did not throw an exception');
  } catch (e) {
    assertEquals('expected  null but was [function]', e.message);
    assertEquals('AssertError', e.name);
  }
};
*/


assertsTest.prototype.testAssertNotNullWithMsg = function() {
  try {
    assertNotNull({});
    assertNotNull('this is a message', null);
    fail('assertNotNull did not throw an exception');
  } catch (e) {
    assertEquals('this is a message expected not null but was null', e.message);
    assertEquals('AssertError', e.name);
  }
  assertNotNull("This is a message",undefined);
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

(function () {
  var GLOBAL = this;

  TestCase("AssertNotEqualsTest", {
    setUp: function () {
      this.assertEquals = GLOBAL.assertEquals;
    },

    tearDown: function () {
      GLOBAL.assertEquals = this.assertEquals;
    },

    "test should call assertEquals to compare arguments": function () {
      GLOBAL.assertEquals = function () { GLOBAL.assertEquals.called = true; };

      try {
        assertNotEquals(1, 2);
      } catch (e) {}

      assertTrue(GLOBAL.assertEquals.called);
    },

    "test should call assertEquals with three arguments": function () {
      var args = [], expected = 1, actual = 2;
      GLOBAL.assertEquals = function () { args = arguments; };

      try {
        assertNotEquals(expected, actual);
      } catch (e) {}

      GLOBAL.assertEquals, 2, args.length);
      GLOBAL.assertEquals, expected, args[0]);
      GLOBAL.assertEquals, actual, args[1]);
    },

    "test should call assertEquals with original message": function () {
      var args = [], msg = "oh noes!", expected = 1, actual = 2;
      GLOBAL.assertEquals = function () { args = arguments; };

      try {
        assertNotEquals(msg, expected, actual);
      } catch (e) {}

      GLOBAL.assertEquals, 3, args.length);
      GLOBAL.assertEquals, msg, args[0]);
    },

    "test should fail when assertEquals passes": function () {
      GLOBAL.assertEquals = function () {};

      try {
        assertNotEquals("string1", "string1");
        fail("assertNotEquals should fail on equal strings");
      } catch (e) {
        GLOBAL.assertEquals, "AssertError", e.name);
        GLOBAL.assertEquals,
            "expected \"string1\" not to be equal to \"string1\"", e.message);
      }
    },

    "test should pass when assertEquals fails": function () {
      GLOBAL.assertEquals = function () {
        var error = new Error("fail");
        error.name = "AssertError";
        throw error;
      };

      try {
        assertNotEquals("string2", "string1");
      } catch (e) {
        fail("assertNotEquals should pass for unequal strings");
      }
    }
  });

  TestCase("AssertNotNullTest", {
    "test should fail when null is passed": function () {
      try {
        assertNotNull(null);
        fail("assertNotNull should fail for null");
      } catch (e) {
        assertEquals("AssertError", e.name);
        assertEquals("expected not null but was null", e.message);
      }
    },

    "test should fail with message when message and null is passed": function () {
      try {
        assertNotNull("some value", null);
        fail("assertNotNull should fail for null");
      } catch (e) {
        assertEquals("AssertError", e.name);
        assertEquals("some value expected not null but was null", e.message);
      }
    },

    "test should pass for undefined": function () {
      assertNotNull(undefined);
    },

    "test should pass for object": function () {
      assertNotNull({});
    }
  });

  TestCase("AssertNotUndefinedTest", {
    "test should fail when undefined is passed": function () {
      try {
        assertNotUndefined(undefined);
        fail("assertNotUndefined should fail for undefined");
      } catch (e) {
        assertEquals("AssertError", e.name);
        assertEquals("expected not undefined but was undefined", e.message);
      }
    },

    "test should fail with message when message and undefined is passed": function () {
      try {
        assertNotUndefined("some value", undefined);
        fail("assertNotUndefined should fail for undefined");
      } catch (e) {
        assertEquals("AssertError", e.name);
        assertEquals("some value expected not undefined but was undefined", e.message);
      }
    },

    "test should pass for null": function () {
      assertNotUndefined(null);
    },

    "test should pass for object": function () {
      assertNotUndefined({});
    },

    "test should pass for object with message": function () {
      assertNotUndefined("object", {});
    }
  });

  TestCase("AssertNaNTest", {
    "test should pass when NaN is passed": function () {
      assertNaN(10/"o");
    },

    "test should pass when NaN and message is passed": function () {
      assertNaN("hope it's NaN", 10/"o");
    },

    "test should fail when number not NaN is passed": function () {
      try {
        assertNaN(10);
        fail("assertNaN should fail on numbers not NaN");
      } catch (e) {
        assertEquals("AssertError", e.name);
        assertEquals("expected to be NaN but was 10", e.message);
      }
    },

    "test should fail when message and number not NaN is passed": function () {
      try {
        assertNaN("Hope it's NaN!", 10);
        fail("assertNaN should fail on numbers not NaN");
      } catch (e) {
        assertEquals("AssertError", e.name);
        assertEquals("Hope it's NaN! expected to be NaN but was 10", e.message);
      }
    }
  });

  TestCase("AssertNotNaNTest", {
    "test should pass when number is passed": function () {
      assertNotNaN(10);
    },

    "test should pass when number and message is passed": function () {
      assertNotNaN("hope it's not NaN", 10);
    },

    "test should fail when NaN is passed": function () {
      try {
        assertNotNaN(10/"o");
        fail("assertNotNaN should fail on NaN");
      } catch (e) {
        assertEquals("AssertError", e.name);
        assertEquals("expected not to be NaN", e.message);
      }
    },

    "test should fail when message and NaN is passed": function () {
      try {
        assertNotNaN("Hope it's not NaN!", 10/"o");
        fail("assertNotNaN should fail on NaN");
      } catch (e) {
        assertEquals("AssertError", e.name);
        assertEquals("Hope it's not NaN! expected not to be NaN", e.message);
      }
    }
  });

  TestCase("AssertExceptionTest", {
    "test should pass when callback throws exception": function () {
      assertException(function () {
        throw new Error("Fail!");
      });
    },

    "test should pass with message when callback throws exception": function () {
      assertException("Should throw fail error", function () {
        throw new Error("Fail!");
      });
    },

    "test should pass when callback throws correct exception": function () {
      assertException(function () {
        throw new Error("Fail!");
      }, "Error");
    },

    "test should pass with message when callback throws correct exception": function () {
      assertException("Should throw fail error", function () {
        throw new Error("Fail!");
      }, "Error");
    },

    "test should fail when callback does not throw exception": function () {
      try {
        assertException(function () {});
        fail("assertException should fail when callback does not throw error");
      } catch (e) {
        assertEquals("AssertError", e.name);
        assertEquals("expected to throw exception", e.message);
      }
    },

    "test should fail with message when callback does not throw exception": function () {
      try {
        assertException("It should fail", function () {});
        fail("assertException should fail when callback does not throw error");
      } catch (e) {
        assertEquals("AssertError", e.name);
        assertEquals("It should fail expected to throw exception", e.message);
      }
    },

    "test should fail with message and exception type when callback does not throw exception": function () {
      try {
        assertException("It should fail", function () {}, "Error");
        fail("assertException should fail when callback does not throw error");
      } catch (e) {
        assertEquals("AssertError", e.name);
        assertEquals("It should fail expected to throw exception", e.message);
      }
    },

    "test should fail when exception thrown is not of correct type": function () {
      try {
        assertException(function () { throw new TypeError(); }, "Error");
        fail("assertException should fail when callback does not throw error");
      } catch (e) {
        assertEquals("AssertError", e.name);
        assertEquals("expected to throw Error but threw TypeError", e.message);
      }
    },

    "test should fail with message when exception thrown is not of correct type": function () {
      try {
        assertException("It should fail",
            function () { throw new TypeError(); }, "Error");
        fail("assertException should fail when callback does not throw error");
      } catch (e) {
        assertEquals("AssertError", e.name);
        assertEquals(
            "It should fail expected to throw Error but threw TypeError",
            e.message);
      }
    }
  });

  TestCase("AssertNoExceptionTest", {
    "test should pass when callback does not throw exception": function () {
      assertNoException(function () {});
    },

    "test should pass with message when callback does not throw exception": function () {
      assertNoException("It should pass", function () {});
    },

    "test should fail when callback throws exception": function () {
      try {
        assertNoException(function () { throw new Error("Oh no!"); });
        fail("assertNoException should fail when callback throws error");
      } catch (e) {
        assertEquals("AssertError", e.name);
        assertEquals(
            "expected not to throw exception, but threw Error (Oh no!)",
            e.message);
      }
    },

    "test should fail with message when callback throws exception": function () {
      try {
        assertNoException("It should pass",
            function () { throw new Error("Oh no!"); });
        fail("assertNoException should fail when callback throws error");
      } catch (e) {
        assertEquals("AssertError", e.name);
        assertEquals(
            "It should pass expected not to throw exception, but threw Error (Oh no!)",
            e.message);
      }
    }
  });

  TestCase("AssertArrayTest", {
    setUp: function () {
      this.isArray = jstestdriver.jQuery.isArray;
    },

    tearDown: function () {
      jstestdriver.jQuery.isArray = this.isArray;
    },

    "test should check arrayness with jstestdriver.jQuery.isArray": function () {
      var called;
      jstestdriver.jQuery.isArray = function () { return (called = true); };
      assertArray([]);

      assertTrue(called);
    },

    "test should pass when isArray returns true": function () {
      jstestdriver.jQuery.isArray = function () { return true; };
      assertArray([]);
    },

    "test should pass with message when isArray returns true": function () {
      jstestdriver.jQuery.isArray = function () { return true; };
      assertArray("should be array", []);
    },

    "test should fail when isArray returns false": function () {
      jstestdriver.jQuery.isArray = function () { return false; };

      try {
        assertArray({});
        fail("assertArray should fail when isArray returns false");
      } catch (e) {
        assertEquals("AssertError", e.name);
        assertEquals("expected to be array but was {}");
      }
    },

    "test should fail with message when isArray returns false": function () {
      jstestdriver.jQuery.isArray = function () { return false; };

      try {
        assertArray("no array", {});
        fail("assertArray should fail when isArray returns false");
      } catch (e) {
        assertEquals("AssertError", e.name);
        assertEquals("no array expected to be array but was {}");
      }
    }
  });

  TestCase("AssertTypeOfTest", {
    "test pass for correct type": function () {
      assertTypeOf("string", "string string string");
    },

    "test pass with message for correct type": function () {
      assertTypeOf("string should be, well, string", "string", "string string string");
    },

    "test fail for wrong type": function () {
      try {
        assertTypeOf("object", "string string string");
        fail("assertTypeOf should fail for wrong type");
      } catch (e) {
        assertEquals("AssertError", e.name);
        assertEquals("expected to be object but was string");
      }
    },

    "test fail with message for wrong type": function () {
      try {
        assertTypeOf("should fail", "object", "string string string");
        fail("assertTypeOf should fail for wrong type");
      } catch (e) {
        assertEquals("AssertError", e.name);
        assertEquals("should fail expected to be object but was string", e.message);
      }
    }
  });

  TestCase("AssertTypeOfApplicationsTest", {
    setUp: function () {
      this.assertTypeOf = GLOBAL.assertTypeOf;
    },

    tearDown: function () {
      GLOBAL.assertTypeOf = this.assertTypeOf;
    },

    "test assertBoolean should delegate to assertTypeOf": function () {
      var args, actual = "some value";
      GLOBAL.assertTypeOf = function () { args = [].slice.call(arguments); };
      assertBoolean(actual);

      assertEquals(["", "boolean", actual], args);
    },

    "test assertBoolean should delegate to assertTypeOf with message": function () {
      var args, actual = "some value", msg = "should be boolean";
      GLOBAL.assertTypeOf = function () { args = [].slice.call(arguments); };
      assertBoolean(msg, actual);

      assertEquals([msg + " ", "boolean", actual], args);
    },

    "test assertFunction should delegate to assertTypeOf": function () {
      var args, actual = "some value";
      GLOBAL.assertTypeOf = function () { args = [].slice.call(arguments); };
      assertFunction(actual);

      assertEquals(["", "function", actual], args);
    },

    "test assertFunction should delegate to assertTypeOf with message": function () {
      var args, actual = "some value", msg = "should be function";
      GLOBAL.assertTypeOf = function () { args = [].slice.call(arguments); };
      assertFunction(msg, actual);

      assertEquals([msg + " ", "function", actual], args);
    },

    "test assertNumber should delegate to assertTypeOf": function () {
      var args, actual = "some value";
      GLOBAL.assertTypeOf = function () { args = [].slice.call(arguments); };
      assertNumber(actual);

      assertEquals(["", "number", actual], args);
    },

    "test assertNumber should delegate to assertTypeOf with message": function () {
      var args, actual = "some value", msg = "should be number";
      GLOBAL.assertTypeOf = function () { args = [].slice.call(arguments); };
      assertNumber(msg, actual);

      assertEquals([msg + " ", "number", actual], args);
    },

    "test assertObject should delegate to assertTypeOf": function () {
      var args, actual = "some value";
      GLOBAL.assertTypeOf = function () { args = [].slice.call(arguments); };
      assertObject(actual);

      assertEquals(["", "object", actual], args);
    },

    "test assertObject should delegate to assertTypeOf with message": function () {
      var args, actual = "some value", msg = "should be object";
      GLOBAL.assertTypeOf = function () { args = [].slice.call(arguments); };
      assertObject(msg, actual);

      assertEquals([msg + " ", "object", actual], args);
    },

    "test assertString should delegate to assertTypeOf": function () {
      var args, actual = "some value";
      GLOBAL.assertTypeOf = function () { args = [].slice.call(arguments); };
      assertString(actual);

      assertEquals(["", "string", actual], args);
    },

    "test assertString should delegate to assertTypeOf with message": function () {
      var args, actual = "some value", msg = "should be string";
      GLOBAL.assertTypeOf = function () { args = [].slice.call(arguments); };
      assertString(msg, actual);

      assertEquals([msg + " ", "string", actual], args);
    }
  });

  TestCase("AssertMatchTest", {
    "test should pass when string matches regexp": function () {
      assertMatch(/hello/, "oh, hello there");
    },

    "test should pass with message when string matches regexp": function () {
      assertMatch("should match hello", /hello/, "oh, hello there");
    },

    "test should fail if actual argument is not given": function () {
      try {
        assertMatch(/oh no/);
        fail("assertMatch should fail when missing string");
      } catch (e) {
        assertEquals("AssertError", e.name);
        assertEquals("expected atleast 2 arguments, got 1", e.message);
      }
    },

    "test should fail if actual argument is undefined": function () {
      try {
        assertMatch(/oh no/, undefined);
        fail("assertMatch should fail when missing string");
      } catch (e) {
        assertEquals("AssertError", e.name);
        assertEquals("expected undefined to match /oh no/", e.message);
      }
    },

    "test should fail when string does not match regexp": function () {
      try {
        assertMatch(/oh no/, "hell yes");
        fail("assertMatch should fail when string doesn't match regexp");
      } catch (e) {
        assertEquals("AssertError", e.name);
        assertEquals("expected \"hell yes\" to match /oh no/", e.message);
      }
    },

    "test should fail with message when string does not match regexp": function () {
      try {
        assertMatch("will fail", /oh no/, "hell yes");
        fail("assertMatch should fail when string doesn't match regexp");
      } catch (e) {
        assertEquals("AssertError", e.name);
        assertEquals("will fail expected \"hell yes\" to match /oh no/", e.message);
      }
    }
  });

  TestCase("AssertNoMatchTest", {
    "test should pass when string does not matches regexp": function () {
      assertNoMatch(/hello/, "oh, y'ello there");
    },

    "test should pass with message when string does not matches regexp": function () {
      assertNoMatch("should match hello", /hello/, "oh, y'ello there");
    },

    "test should pass when actual argument is undefined": function () {
      assertNoMatch("should match hello", /hello/, undefined);
    },

    "test should fail when string matches regexp": function () {
      try {
        assertNoMatch(/oh no/, "oh no");
        fail("assertNoMatch should fail when string matches regexp");
      } catch (e) {
        assertEquals("AssertError", e.name);
        assertEquals("expected \"oh no\" not to match /oh no/", e.message);
      }
    },

    "test should fail with message when string matches regexp": function () {
      try {
        assertNoMatch("will fail", /oh no/, "oh no");
        fail("assertNoMatch should fail when string matches regexp");
      } catch (e) {
        assertEquals("AssertError", e.name);
        assertEquals("will fail expected \"oh no\" not to match /oh no/", e.message);
      }
    }
  });

  TestCase("AssertTagNameTest", {
    setUp: function () {
      /*:DOC += <div id="el"></div> */
      this.element = document.getElementById("el");
    },

    "test should pass when tag name is correct": function () {
      assertTagName("div", this.element);
    },

    "test should pass tag name regardless of casing": function () {
      assertTagName("DIV", this.element);
    },

    "test should pass with message when tag name is correct": function () {
      assertTagName("should be div", "div", this.element);
    },

    "test should fail for wrong tag name": function () {
      try {
        assertTagName("span", this.element);
        fail("assertTagName should fail for wrong tag name");
      } catch (e) {
        assertEquals("AssertError", e.name);
        assertEquals("expected tagName to be span but was DIV", e.message);
      }
    },

    "test should fail with message for wrong tag name": function () {
      try {
        assertTagName("should be span", "span", this.element);
        fail("assertTagName should fail for wrong tag name");
      } catch (e) {
        assertEquals("AssertError", e.name);
        assertEquals("should be span expected tagName to be span but was DIV", e.message);
      }
    }
  });

  TestCase("AssertClassNameTest", {
    setUp: function () {
      /*:DOC += <div id="el" class="something"></div> */
      /*:DOC += <div id="el2" class="something other"></div> */
      /*:DOC += <div id="el3"></div> */
      this.element = document.getElementById("el");
      this.elementMultipleClasses = document.getElementById("el2");
      this.elementNoClass = document.getElementById("el3");
    },

    "test should pass when class name is correct": function () {
      assertClassName("something", this.element);
    },

    "test should pass with message when class name is correct": function () {
      assertClassName("should have something", "something", this.element);
    },

    "test should pass when class name contains expected class name": function () {
      assertClassName("should have something", "something", this.elementMultipleClasses);
      assertClassName("should have something", "other", this.elementMultipleClasses);
    },

    "test should fail for missing class name": function () {
      try {
        assertClassName("hola", this.element);
        fail("assertClassName should fail for missing class name");
      } catch (e) {
        assertEquals("AssertError", e.name);
        assertEquals("expected class name to include \"hola\" but was \"something\"", e.message);
      }
    },

    "test should fail with message for missing class name": function () {
      try {
        assertClassName("msg", "hola", this.element);
        fail("assertClassName should fail for missing class name");
      } catch (e) {
        assertEquals("AssertError", e.name);
        assertEquals("msg expected class name to include \"hola\" but was \"something\"", e.message);
      }
    },

    "test should fail when element has no classes": function () {
      try {
        assertClassName("hola", this.elementNoClass);
        fail("assertClassName should fail for missing class name");
      } catch (e) {
        assertEquals("AssertError", e.name);
        assertEquals("expected class name to include \"hola\" but was \"\"", e.message);
      }
    },

    "test should fail with message when element has no classes": function () {
      try {
        assertClassName("msg", "hola", this.elementNoClass);
        fail("assertClassName should fail for missing class name");
      } catch (e) {
        assertEquals("AssertError", e.name);
        assertEquals("msg expected class name to include \"hola\" but was \"\"", e.message);
      }
    }
  });

  TestCase("AssertElementIdTest", {
    setUp: function () {
      /*:DOC += <div id="el"></div> */
      this.element = document.getElementById("el");
    },

    "test should pass when id is correct": function () {
      assertElementId("el", this.element);
    },

    "test should pass with message when id is correct": function () {
      assertElementId("should be el", "el", this.element);
    },

    "test should fail for wrong id": function () {
      try {
        assertElementId("other", this.element);
        fail("assertElementId should fail for wrong id");
      } catch (e) {
        assertEquals("AssertError", e.name);
        assertEquals("expected id to be other but was el", e.message);
      }
    },

    "test should fail with message for wrong id": function () {
      try {
        assertElementId("should be span", "span", this.element);
        fail("assertElementId should fail for wrong id");
      } catch (e) {
        assertEquals("AssertError", e.name);
        assertEquals("should be span expected id to be span but was el", e.message);
      }
    }
  });

  TestCase("AssertInstanceOfTest", {
    "test should pass when object is instance of constructor": function () {
      assertInstanceOf(String, "a string");
    },

    "test should pass with message when object is instance of constructor": function () {
      assertInstanceOf("should be string", String, "a string");
    },

    "test should fail if object is not instance of constructor": function () {
      function MyConstructor () {};

      try {
        assertInstanceOf(MyConstructor, {});
        fail("assertInstanceOf should fail if object is not instance of constructor");
      } catch (e) {
        assertEquals("AssertError", e.name);
        assertMatch(/expected {} to be instance of .*MyConstructor.*/, e.message);
      }
    },

    "test should fail with message if object is not instance of constructor": function () {
      function MyConstructor () {};

      try {
        assertInstanceOf("msg", MyConstructor, {});
        fail("assertInstanceOf should fail if object is not instance of constructor");
      } catch (e) {
        assertEquals("AssertError", e.name);
        assertMatch(/msg expected {} to be instance of .*MyConstructor.*/, e.message);
      }
    }
  });

  TestCase("AssertNotInstanceOfTest", {
    "test should pass when object is not instance of constructor": function () {
      assertNotInstanceOf(Array, "a string");
    },

    "test should pass with message when object is not instance of constructor": function () {
      assertNotInstanceOf("should be string", Array, "a string");
    },

    "test should fail if object is instance of constructor": function () {
      try {
        assertNotInstanceOf(Object, {});
        fail("assertNotInstanceOf should fail if object is instance of constructor");
      } catch (e) {
        assertEquals("AssertError", e.name);
        assertMatch(/expected {} not to be instance of /, e.message);
        assertMatch(/Object/, e.message);
      }
    },

    "test should fail with message if object is instance of constructor": function () {
      try {
        assertNotInstanceOf("msg", Object, {});
        fail("assertNotInstanceOf should fail if object is instance of constructor");
      } catch (e) {
        assertEquals("AssertError", e.name);
        assertMatch(/msg expected {} not to be instance of /, e.message);
        assertMatch(/Object/, e.message);
      }
    }
  });

  TestCase("AssertTest", {
    "test should alias assertTrue as assert": function () {
      assertEquals(assertTrue, assert);
    }
  });
}());
