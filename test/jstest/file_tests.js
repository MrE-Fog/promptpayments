dateValidOkTest("2000", "1", "1")
dateValidOkTest("2000", "12", "31")
dateValidOkTest("2000", "2", "29")
//dateValidOkTest("2000", "2", "")
//dateValidOkTest("2000", "", "29")
//dateValidOkTest("", "2", "29")

dateValidFailTest("2001", "2", "29", "invalid");
dateValidFailTest("2099", "1", "1", "future");
dateValidFailTest("0", "1", "1", "invalid");
dateValidFailTest("2000", "blue", "1", "invalid");
dateValidFailTest("2000", "1", "100", "invalid");

textPositiveIntegerOkTest("0");
textPositiveIntegerOkTest("0.0000000000");
textPositiveIntegerOkTest("1");
textPositiveIntegerOkTest("1.");
textPositiveIntegerOkTest("00100");
textPositiveIntegerOkTest("00100.000");
textPositiveIntegerOkTest("10 %");

textPositiveIntegerFailTest("0.1", "round");
textPositiveIntegerFailTest("-1", "negative");
textPositiveIntegerFailTest("jelly", "number");
textPositiveIntegerFailTest("1x2", "number");
textPositiveIntegerFailTest("%", "number");
textPositiveIntegerFailTest("x1", "number");
textPositiveIntegerFailTest("1 1", "number");

textPercentageOkTest("0");
textPercentageOkTest("  0.00");
textPercentageOkTest("1  ");
textPercentageOkTest("99");
textPercentageOkTest("100");
textPercentageOkTest("0100.");
textPercentageOkTest("0100.00");
textPercentageOkTest("0100.00%");

textPercentageFailTest("-1", "between");
textPercentageFailTest("101", "100");
textPercentageFailTest("50.5", "round");
textPercentageFailTest("gossamer", "number");

multiSum100OkTest("100", "0", "0");
multiSum100OkTest("0", "0", "100");
multiSum100OkTest("33", "33", "32");
multiSum100OkTest("34", "34", "34");

//ok if an individual one is not valid
multiSum100OkTest("-1", "100", "100");
multiSum100OkTest("100", "blue", "100");
multiSum100OkTest("100", "100", "100.5");

multiSum100FailTest("50", "50", "50", "add up");
multiSum100FailTest("10", "10", "10", "add up");

multiStartBeforeEndOkTest("2015", "1", "1", "2015", "12", "31");
multiStartBeforeEndOkTest("2015", "1", "1", "2015", "1", "1");

//ok if individual one is invalid
multiStartBeforeEndOkTest("2015", "2", "31", "2015", "1", "1");
multiStartBeforeEndOkTest("2015", "12", "31", "2015", "2", "31");
multiStartBeforeEndOkTest("2100", "1", "1", "2099", "12", "31");

multiStartBeforeEndFailTest("2015", "12", "31", "2015", "1", "1", "before");

QUnit.test("textfield validation", function(assert) {
    document.getElementById("qunit-fixture").innerHTML = "<fieldset id='pprtest-fieldset' class='form-group'><label><span id='pprtest-error' class='error-message'></span></label><input name='pprtest-input' id='pprtest-input' value='val'></fieldset>";

    var invalidation = function(val) {assert.ok(val === "val"); return "error message"};
    new Validation().validateTextInput("pprtest-input", function(val) {
        return invalidation(val);});

    var input = document.getElementById("pprtest-input"),
        message = document.getElementById("pprtest-error"),
        fieldset = document.getElementById("pprtest-fieldset");

    //test bad input
    input.onblur();
    assert.ok(message.innerHTML === "error message");
    assert.ok(fieldset.className.indexOf("error") > -1);

    //test edit
    input.onkeydown();
    assert.ok(message.innerHTML === "&nbsp;");
    assert.ok(fieldset.className.indexOf("error") === -1);

    //test good input
    invalidation = function(val) {assert.ok(val === "val"); return null};
    input.onblur();
    assert.ok(message.innerHTML === "&nbsp;");
    assert.ok(fieldset.className.indexOf("error") === -1);

    //test tolerates empty fields
    invalidation = function() {assert.notOk("shouldn't validate empty field")}
    input.setAttribute("value", "");
    input.onblur();
});

QUnit.test("date validation",  function(assert) {
    document.getElementById("qunit-fixture").innerHTML = "<fieldset id='pprtest-fieldset' class='form-group'><label><span id='pprtest-error' class='error-message'></span></label><div><input name='pprtest-input-year' id='pprtest-input-year' value='year'><input name='pprtest-input-month' id='pprtest-input-month' value='month'><input name='pprtest-input-day' id='pprtest-input-day' value='day'></div></fieldset>";

    var invalidation = function(year, month, day) {
       assert.ok(year === "year");
       assert.ok(month === "month");
       assert.ok(day === "day");
       return "error message";
   };
    new Validation().validateDateInput("pprtest-input-", function(year, month, day) {
        return invalidation(year, month, day);
    });

    document.getElementById("pprtest-input-year").onblur();
    assert.ok(document.getElementById("pprtest-error").innerHTML === "error message");
    assert.ok(document.getElementById("pprtest-fieldset").className.indexOf("error") > -1);

    document.getElementById("pprtest-input-month").onkeydown();
    assert.ok(document.getElementById("pprtest-error").innerHTML === "&nbsp;");
    assert.ok(document.getElementById("pprtest-fieldset").className.indexOf("error") === -1);

    invalidation = function() {return null;};
    document.getElementById("pprtest-input-month").onblur();
    assert.ok(document.getElementById("pprtest-error").innerHTML === "&nbsp;");
    assert.ok(document.getElementById("pprtest-fieldset").className.indexOf("error") === -1);

    invalidation = function() {return "new error message";};
    document.getElementById("pprtest-input-day").onblur();
    assert.ok(document.getElementById("pprtest-error").innerHTML === "new error message");
    assert.ok(document.getElementById("pprtest-fieldset").className.indexOf("error") > -1);

    invalidation = function() {assert.notOk("shouldn't validate when a field is empty")}
    document.getElementById("pprtest-input-day").setAttribute("value","");
    document.getElementById("pprtest-input-day").onblur();

});

QUnit.test("multi validation", function(assert) {
    document.getElementById("qunit-fixture").innerHTML = "<fieldset id='pprtest-fieldset' class='form-group'><label><span id='pprtest-error' class='error-message'></span></label></fieldset><div><input name='pprtest-input-one' id='pprtest-input-one' value='one'><input name='pprtest-input-two' id='pprtest-input-two' value='two'></div>";

    var invalidation = function() {return "error message"};
    new Validation().validateMultiple(["pprtest-input-one", "pprtest-input-two"],
        document.getElementById("pprtest-fieldset"),
        function(x) {
            assert.ok(x[0] === "one");
            assert.ok(x[1] === "two");
            return invalidation();
        });

    document.getElementById("pprtest-input-one").onblur();
    assert.ok(document.getElementById("pprtest-error").innerHTML === "error message");
    assert.ok(document.getElementById("pprtest-fieldset").className.indexOf("error") > -1);

    document.getElementById("pprtest-input-one").onkeydown();
    assert.ok(document.getElementById("pprtest-error").innerHTML === "&nbsp;");
    assert.ok(document.getElementById("pprtest-fieldset").className.indexOf("error") === -1);


    invalidation = function() {return null;};
    document.getElementById("pprtest-input-two").onblur();
    assert.ok(document.getElementById("pprtest-error").innerHTML === "&nbsp;");
    assert.ok(document.getElementById("pprtest-fieldset").className.indexOf("error") === -1);

    invalidation = function() {assert.notOk("when one of the fields is empty, it shouldn't validate")}
    document.getElementById("pprtest-input-two").setAttribute("value", "");
    document.getElementById("pprtest-input-two").onblur();
});


function dateValidOkTest(year, month, day) { testValidationOk("dateValid", function() {return new Validation().validations.dateValid(year,month,day) }) };
function dateValidFailTest(year, month, day, messageFragment) { testValidationFail("dateValid", function() {return new Validation().validations.dateValid(year,month,day) }, messageFragment) };

function textPositiveIntegerOkTest(text) { testValidationOk("textPostiveInteger " + text, function() {return new Validation().validations.textPositiveInteger(text)}) };
function textPositiveIntegerFailTest(text, fragment) { testValidationFail("textPostiveInteger " + text, function() {return new Validation().validations.textPositiveInteger(text)}, fragment) };

function textPercentageOkTest(text) { testValidationOk("textPostiveInteger " + text, function() {return new Validation().validations.textPercentage(text)}) };
function textPercentageFailTest(text, fragment) { testValidationFail("textPostiveInteger " + text, function() {return new Validation().validations.textPercentage(text)}, fragment) };

function multiSum100OkTest(one, two, three) {testValidationOk("multiSum100 " + one + " " + two + " " + three, function() {return new Validation().validations.multiSumTo100([one, two, three])})};
function multiSum100FailTest(one, two, three, fragment) {testValidationFail("multiSum100 " + one + " " + two + " " + three, function() {return new Validation().validations.multiSumTo100([one, two, three])}, fragment)};

function multiStartBeforeEndOkTest(y1, m1, d1, y2, m2, d2) {testValidationOk("multiStartBeforeEnd", function() {return new Validation().validations.multiStartBeforeEnd([y1, m1, d1, y2, m2, d2])})};
function multiStartBeforeEndFailTest(y1, m1, d1, y2, m2, d2, fragment) {testValidationFail("multiStartBeforeEnd", function() {return new Validation().validations.multiStartBeforeEnd([y1, m1, d1, y2, m2, d2])}, fragment)};

function testValidationOk(name, doValidation) {
    QUnit.test( name + " ok", function( assert ) {
      assert.ok(!doValidation());
    });
}

function testValidationFail(name, doValidation, fragment) {
    QUnit.test( name + " fails with " + fragment, function( assert ) {
      var res = doValidation();
      assert.ok(res, res);
      assert.ok(res.indexOf(fragment) > -1);
    });
}
