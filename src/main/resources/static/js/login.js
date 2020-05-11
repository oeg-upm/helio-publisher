function login() {
	$('#login_error').css("display", "none");
	var xhr = new XMLHttpRequest();
	var url = "/helio-api/login";
	xhr.open("POST", url, true);
	xhr.setRequestHeader("Content-Type", "application/json");
	xhr.setRequestHeader("Accept", "application/json");
	xhr.onreadystatechange = function (data) {
	  if (xhr.readyState === 4) {
	      if(xhr.status==200){
	       	location.replace("/helio-api/dashboard");
	      }else if(xhr.status==401){
	        $('#login_error').css("display", "inline-block");
	      }
	      
	   }
	};
	var username = $("#username").val();
	var password = $("#password").val();
	var payload = "{\"username\":\""+username+"\", \"password\":\""+password+"\"}";
	xhr.send(payload);
}