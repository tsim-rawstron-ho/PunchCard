
// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
Parse.Cloud.define("hello", function(request, response) {
	response.success("Hello world!");
});

var Mailgun = require('mailgun');
Mailgun.initialize('sandbox11a24636c5a8432f8bd3e81fc2173c45.mailgun.org', 
	               'key-9e34e3d5564a99bd29a90774851594a0');


var generateTempPassword = function() {
	return Math.random().toString(36).slice(-8);
}

Parse.Cloud.define("add_employee", function(request, response) {
	// var company = request.params.company;
	var email = request.params.email;
	var firstName = request.params.firstName;
	var lastName = request.params.lastName;
	// Todo: Get the manager info from manager user id
	var managerFirstName = request.params.managerFirstName;
	var managerLastName = request.params.managerLastName;
	// Create new employee 
	var User = Parse.Object.extend("User");
	var employee = new User();
	var password = generateTempPassword();
	employee.set("firstName", firstName);
	employee.set("lastName", lastName);
	employee.set("username", email);
	employee.set("password", password);

	employee.save(null, {
	  success: function(employee) {
	    // Execute any logic that should take place after the object is saved.
	    alert('New object created with objectId: ' + employee.id);
	    	// Send a sign up email
		Mailgun.sendEmail({
		  to: email,
		  from: "Mailgun@CloudCode.com",
		  subject: "Hello from PunchCard",
		  text: managerFirstName + " " + managerLastName + " has invited to join PunchCard." 
		  		+ "Please login to your account using " + email + " with the temporary password: " 
		  		+ password
		}, {
		  success: function(httpResponse) {
		    console.log(httpResponse);
		    response.success("Email sent!");
		  },
		  error: function(httpResponse) {
		    console.error(httpResponse);
		    response.error("Uh oh, something went wrong");
		  }
		});
	  },
	  error: function(employee, error) {
	    // Execute any logic that should take place if the save fails.
	    // error is a Parse.Error with an error code and message.
	    alert('Failed to create new object, with error code: ' + error.message);
	   	response.error("Could not create user: " + error.message);
	  }
	});
});
