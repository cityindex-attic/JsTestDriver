module('Asserts');

test('OK true succeeds', function() {
	expect(1);
	ok(true);
});

// test('OK false fails', function() {
// 	ok(false);
// });

// test('OK false fails with correct failure message', function() {
// 	ok(false, 'MESSAGE');
// });


// test('Expect fails', function() {
// 	expect(2);
// 	ok(true);
// });

test('Equals succeeds', function() {
	expect(1);
	equals(2, 2);
});

// test('Equals fails', function() {
// 	equals(1, 2);
// });

// test('Equals fails with correct failure message', function() {
// 	equals(1, 2, 'MESSAGE');
// });

// test('Stop and Start functions are not allowed, so fails', function() {
// 	expect(1);
// 	stop();
// 	start();
// });

test('Same assert succeeds', function() {
	expect(1);
	
	var actual = {a: 1};
	same(actual, {a: 1}, "must pass, same content, but different object");
});

// test('Same assert fails, with correct failure message', function() {
// 	expect(1);
// 	
// 	var actual = {a: 1};
// 	same(actual, {a: "1"}, "must fail, expected value is a string, actual a number");
// });

module('DOM');

// test('jQuery incldued', function() {
// 	expect(1);
// 	ok($, 'This will fail in JS Test Driver as jQuery is not included by default');
// });

// test('No DOM to manipulate', function() {
// 	expect(1);
// 	ok(document.getElementById('main'), 'This will fail is JS Test Driver as there is no <div id="main">');
// });


module('Lifecycle', {
  setup: function() {
    ok(true, "once extra assert per test");
  },
  teardown: function() {
	ok(true, "and one extra assert after each test");
	window.testGlobal = 1;
  }
});

test('Setup and Teardown are run, and can contain assertions', function() {
	expect(2);
});

module('Lifecycle', {});

test('', function() {
	expect(1);
	ok(true, 'tests still run successfully even if Setup and Teardown are undefined');
});

// test('Teardown still run even with exception (must run next test to check)', function() {
// 	expect(2);
// 	window.testGlobal = 2;
// 	throw('test exception');
// });
// 
// test('Check global state after previous test raised exception', function() {
// 	expect(3);
// 	equals(1, window.testGlobal);
// });





