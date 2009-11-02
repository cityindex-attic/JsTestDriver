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
expectAsserts = function(count){
  jstestdriver.expectedAssertCount = count;
};


fail = function(msg) {
  var err = new Error(msg);

  err.name = 'AssertError';
  throw err;
};


isBoolean_ = function(bool) {
  if (typeof(bool) != 'boolean') {
    fail('Not a boolean: ' + this.prettyPrintEntity_(bool));
  }
};


prettyPrintEntity_ = function(entity) {
  var str = JSON.stringify(entity);
  if (!str) {
    if (entity.toString().indexOf('function')) {
      return "[function]";
    }
    return "[" + typeof entity + "]";
  }
  return str;
};


assertTrue = function(msg, actual) {
  if (arguments.length == 1) {
    actual = msg;
    msg = '';
  } else {
    msg += ' ';
  }
  jstestdriver.assertCount++;

  isBoolean_(actual);
  if (actual != true) {
    fail(msg + 'expected true but was ' + this.prettyPrintEntity_(actual) + '');
  }
  return true;
};


assertFalse = function(msg, actual) {
  if (arguments.length == 1) {
    actual = msg;
    msg = '';
  } else {
    msg += ' ';
  }
  jstestdriver.assertCount++;

  isBoolean_(actual);
  if (actual != false) {
    fail(msg + 'expected false but was ' + this.prettyPrintEntity_(actual) + '');
  }
  return true;
};


assertEquals = function(msg, expected, actual) {
  if (arguments.length == 2) {
    var tmp = expected;

    expected = msg;
    actual = tmp;
    msg = '';
  } else {
    msg += ' ';
  }
  jstestdriver.assertCount++;
    
  // The local compare function closure is needed to allow easy recursive
  // checking without destroying a meaninful assertion message on failure.
  var compare = function(expected, actual) {
    var key = null;

    // Do we compare an Array or an Object?
    // 'null' and 'undefined' are of type 'object' therefore the extra check is
    // required.
    if (expected !== undefined && typeof(expected) == 'object') {
      var actualLength   = 0;
      var expectedLength = 0;
      // If an array is expected the length of actual should be simple to
      // determine. If it is not it is undefined.
      if (expected instanceof [].constructor) {
          actualLength = actual.length;
      }
      else {
          // In case it is an object it is a little bit more complicated to get the
          // length.
          for(key in actual) {
            if (!actual.hasOwnProperty(key)) {
                continue;
            }
            ++actualLength;
          }
      }

      for (key in expected) {
        if (!expected.hasOwnProperty(key)) {
          // Do not run up the inheritance chain
          continue;
        }
        if (!compare(expected[key], actual[key])) {
          return false;
        }
        ++expectedLength;
      }

      if (expectedLength !== actualLength) {
          return false;
      }
        
      return true;
    }

    // We are dealing with a scalar here. Simple comparison should do the trick
    if (actual != expected) {
      return false;
    }

    // actual and expected are equal
    return true;
  };

  if (!compare(expected, actual)) {
    fail(msg + 'expected ' + this.prettyPrintEntity_(expected) + ' but was ' +
        this.prettyPrintEntity_(actual) + '');
  }
  return true;
};


assertSame = function(msg, expected, actual) {
  if (arguments.length == 2) {
    var tmp = expected;

    expected = msg;
    actual = tmp;
    msg = '';
  } else {
    msg += ' ';
  }
  jstestdriver.assertCount++;

  if (!isSame_(actual, expected)) {
    fail(msg + 'expected ' + this.prettyPrintEntity_(expected) + ' but was ' +
        this.prettyPrintEntity_(actual) + '');
  }
  return true;
};


assertNotSame = function(msg, expected, actual) {
  if (arguments.length == 2) {
    var tmp = expected;

    expected = msg;
    actual = tmp;
    msg = '';
  } else {
    msg += ' ';
  }  
  jstestdriver.assertCount++;

  if (isSame_(actual, expected)) {
    fail(msg + 'expected not same as ' + this.prettyPrintEntity_(expected) + ' but was ' +
        this.prettyPrintEntity_(actual) + '');
  }
  return true;
};


isSame_ = function(expected, actual) {
  return actual === expected;
};


assertNull = function(msg, actual) {
  if (arguments.length == 1) {
    actual = msg;
    msg = '';
  } else {
    msg += ' ';
  }
  jstestdriver.assertCount++;

  if (actual !== null) {
    fail(msg + 'expected null but was ' + this.prettyPrintEntity_(actual) + '');
  }
  return true;
};


assertNotNull = function(msg, actual) {
  if (arguments.length == 1) {
    actual = msg;
    msg = '';
  } else {
    msg += ' ';
  }
  jstestdriver.assertCount++;

  if (actual === null) {
    fail(msg + 'expected not null but was ' + this.prettyPrintEntity_(actual) + '');
  }
  return true;
};
