<%@page import="example.teste.*"%>
<%

	Boolean servlet = request.getAttribute("servlet") != null && (Boolean) request.getAttribute("servlet");

	if (!servlet) {
		response.sendRedirect("login");
	}


	Boolean loginError = request.getAttribute("loginError") != null && (Boolean) request.getAttribute("loginError");
%>
<jsp:useBean id="user" scope="session" class="example.teste.UserBean"/>
<jsp:setProperty name="user" property="*" />

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html >
    <head>
		<title>SayMore</title>
		<link rel="stylesheet" type="text/css" href="css/style.css" />
	<script src="js/login.js"></script>
    </head>
    <body>
		<div id="container">
			<%
			if (loginError) {
			%>
				<div id="errorMsg">Login invalid!</div>
			<%		    }
			%>
			<form id="form" action="login" method="post" id="login">
			Username: <input type="text" name="username" value="<%= (loginError ? user.getName() : "")%>" />
			<br>
			Password: <input type="password" name="password" />
			<br>
			<input class="button" type="submit" name="submit" value="Login" />  <a href="register" title="register">Haven't registered yet?</a>  

			</form>
		</div>
	</body>
</html>
