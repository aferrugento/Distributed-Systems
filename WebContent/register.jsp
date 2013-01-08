<%@page import="example.teste.*"%>
<%

	boolean servlet = request.getAttribute("servlet") != null && (Boolean)request.getAttribute("servlet");

	if(!servlet)
		response.sendRedirect("register");
	
	boolean registerError = request.getAttribute("registerError") != null && (Boolean)request.getAttribute("registerError");
%>
<jsp:useBean id="user" scope="session" class="example.teste.UserBean" />

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
	<head>
		<title>SayMore</title>
		<link rel="stylesheet" type="text/css" href="css/style.css" />
		<script src="js/register.js"></script>
	</head>
<body>
	<div id="container">
	<%
		if(registerError)
		{
		%>
			<div id="errorMsg">Fail to register!</div>
		<%
		}
		%>
		<form id="form" action="register" method="post" id="register" onsubmit="return validForm(this);">
			
			Username: <input type="text" name="username" value="<%= (registerError ? user.getName() : "") %>" />
			<br>
			Password: <input type="password" name="password" />
			<br>
			Confirm: <input type="password" name="password2" />
			<br>
			<input class="button" type="submit" name="submit" value="Register" />
			
		</form>
	</div>
</body>
</html>
