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


function dateValidOkTest(year, month, day) {
    QUnit.test( "dateValid for " +year + " " + month + " " + day, function( assert ) {
      assert.ok(!new Validation().validations.dateValid(year,month,day));
    });
}

function dateValidFailTest(year, month, day, messageFragment) {
    QUnit.test( "dateValid fails for " +year + " " + month + " " + day, function( assert ) {
      assert.ok(new Validation().validations.dateValid(year,month,day).indexOf(messageFragment) > -1);
    });
}