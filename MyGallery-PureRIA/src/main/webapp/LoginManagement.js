/**
 * Login management
 */

(function() { // avoid variables ending up in the global scope

	document.getElementById("loginbutton").addEventListener('click', (e) => {
		document.getElementById("signuperrormessage").innerHTML="";
		var form = e.target.closest("form");
		if (form.checkValidity()) {
			makeCall("POST", 'CheckLogin', e.target.closest("form"),
				function(x) {
					if (x.readyState == XMLHttpRequest.DONE) {
						var message = x.responseText;
						switch (x.status) {
							case 200:
								sessionStorage.setItem('username', message);
								window.location.href = "Home.html";
								break;
							case 400: // bad request
								document.getElementById("loginerrormessage").textContent = message;
								break;
							case 401: // unauthorized
								document.getElementById("loginerrormessage").textContent = message;
								break;
							case 500: // server error
								document.getElementById("loginerrormessage").textContent = message;
								break;
						}
					}
				}
			);
		} else {
			form.reportValidity();
		}
	});

	document.getElementById("signupbutton").addEventListener('click', (e) => {
    document.getElementById("loginerrormessage").innerHTML="";

		var password = document.getElementById("password_input");
		var confirmPassword = document.getElementById("confirmPassword_input");
		if (password.value === confirmPassword.value) {
			var form = e.target.closest("form");
			if (form.checkValidity()) {
				makeCall("POST", 'SignUp', e.target.closest("form"),
					function(x) {
						if (x.readyState == XMLHttpRequest.DONE) {
							var message = x.responseText;
							switch (x.status) {
								case 200:
									sessionStorage.setItem('username', message);
									window.location.href = "Home.html";
									break;
								case 400: // bad request
									document.getElementById("signuperrormessage").textContent = message;
									break;
								case 401: // unauthorized
									document.getElementById("signuperrormessage").textContent = message;
									break;
								case 500: // server error
									document.getElementById("signuperrormessage").textContent = message;
									break;
							}
						}
					}
				);
			} else {
				form.reportValidity();
			}
		} else {
			document.getElementById("signuperrormessage").textContent = "passwords don't match";
		}
	});

})();