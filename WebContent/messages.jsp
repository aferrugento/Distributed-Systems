<%@page import="example.teste.*"%>
<%@page import="java.util.Vector"%>
<%@page import="java.lang.String"%>


<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
 <jsp:useBean id="user" scope="session" class="example.teste.UserBean" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>SayMore</title>
<link rel="stylesheet" type="text/css" href="css/style.css" />

 <script type="text/javascript">

 var websocket;

 window.onload = function() { // execute once the page loads
     initialize();

 }

 function initialize() { 
     connect('ws://'+window.location.host+'/_SD2/messageAndOnline');
 }

 function connect(host) { // connect to the host websocket servlet
     if ('WebSocket' in window)
         websocket = new WebSocket(host);
     else if ('MozWebSocket' in window)
         websocket = new MozWebSocket(host);
     else {
        alert("Get a real browser which supports WebSocket.");
         return;
     }

     websocket.onopen    = onOpen; // set the event listeners below
     websocket.onclose   = onClose;
     websocket.onmessage = onMessage;
     websocket.onerror   = onError;
 }

 function onOpen(event) {


 }

 function onClose(event) {


 }

 function onMessage(message) { // print the received message

 	if(!(message.data.slice(-18, -1) == "just disconnected" || message.data.slice(-17, -1) == "is now connected"))
	 	alert(message.data);

 }

 function onError(event) {
     alert("WebSocket error (" + event.data + ").");

 }


    </script>
</head>
<body>

<% 
	ServerManager app = new ServerManager();
	Vector<String> list=app.listMessages(user.getName());
	for(int i=0; i<list.size(); i++)
	{
		%>
		<form>
		<% out.println(list.get(i)); %>
		</form>
		
				<% 
	}

	
%>

</body>
</html>