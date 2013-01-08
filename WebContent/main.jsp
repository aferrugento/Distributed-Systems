
<%@page import="example.teste.*"%>
<%@page import="example.server.*"%>
<%@page import="java.util.concurrent.*"%>
<%@page import="java.util.Vector"%>
<%@page import="java.lang.String"%>

<jsp:useBean id="user" scope="session" class="example.teste.UserBean" />
<jsp:useBean id="app" scope="application"
	class="example.teste.ServerManager" />

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">

<html>
<head>
<title>SayMore</title>
<link rel="stylesheet" type="text/css" href="css/style.css" />
<script src="js/main.js"></script>
</head>


<body>
	<div id="top">
		<span id="username"><%=user.getName()%> | <a
			href="login?logout=true">Logout</a></span>
	</div>

	<div>
		<div id="container">
			<div id="online" style="text-align: right">
				ONLINE USERS: <br>
				<%
					out.println(app.onlineList());
				%>
			</div>
		</div>
	</div>


	<form action="message" method="post">

		Menu:
		<br>
		<br>
		<input name="Message" type="button" value="Send a direct message"
			onclick="message(this.form);" />
		<input id="messageTitle" type="hidden" name="messageTitle" value="" />
		<input id="messageSubject" type="hidden" name="messageSubject"
			value="" />
		<input id="messageContent" type="hidden" name="messageContent"
			value="" />
		<input id="dest" type="hidden" name="dest" value="" />
		<input name="receivedMessages" type="submit"
			value="See received messages" "/>
		<a href="http://localhost:8080/_SD2/chat.html" title="chat"> <br>
			____________________ or join a chatroom
		</a>
	</form>

	<br>


	<form id="form" action="post" method="post">
		<div>

			What are you doing? <br> <input type="text" name="post"
				size="50"> <input name="post" type="submit" value="Post" />
			<br>
			<br> <input type="text" name="facebook" size="30"> <input
				name="facebook" type="submit" value="Post on Facebook" /><br>
			<input type="text" name="image" size="30"> <input
				name="image" type="submit" value="Publish a picture" /> <br> <br>
		</div>
		<form></form>
		<%!public String formIdentifier(int num) {
		return String.format(
				"<input type =\"hidden\" name=\"formID\" value=\"%d\" />", num);
	}%>

		<%!public String replyIdentifier(int num) {

		return String
				.format("<input name=\"Edit\" type=\"button\" value=\"Edit\" onclick=\"editReply(this.form, %d);\"/> <input name=\"Delete\" type=\"button\" value=\"Delete\" onclick=\"deleteReply(this.form, %d);\"/>",
						num, num);
	}%>

		<%!public String image(String url, String name) {

	
		//return String.format("<img src=\"%s\" alt=\"%s\" width=\"200\" height = \"200\" border=\"0\">", url, name);
		return String.format("<br>   %s:<br> <input type=\"image\" src=\"%s\" width=\"200\"	height=\"200\" ><br>", name, url);
	}%>




		<%
			int i = 0, outro_counter = 0;
			ConcurrentHashMap<Integer, Post> posts = app.listPosts();
			while (outro_counter < posts.size()) {
				if (posts.containsKey(i)) {
					outro_counter++;
		%>

		<form id="form2" action="reply" method="post">
			<div>
				<br>
				<%=formIdentifier(i)%>
				<input id="edition" type="hidden" name="edition"
					value="*DELETE OR EDIT*" /> <input id="editionID" type="hidden"
					name="editionID" value="*DELETE OR EDIT*" /> <input id="editionR"
					type="hidden" name="editionR" value="" />
				<%
					if (posts.get(i).getFacebook() != null) {
				%>
				<input type="image" src="FACEBOOK3.gif" alt="Submit" width="21"
					height="21" name="facebookPost">
				<%
					}
							out.println(posts.get(i).getAuthor().getName() + ": "
									+ posts.get(i).getPost());

							if (posts.get(i).getAuthor().getName()
									.equals(user.getUser().getName())
									&& posts.get(i).getFacebook() == null) {
				%>
				<input id="Edit" name="Edit" type="button" value="Edit"
					onclick="editPost(this.form);" />
				<%
					}
							if (posts.get(i).getAuthor().getName()
									.equals(user.getUser().getName())) {
				%>
				<input name="Delete" type="submit" value="Delete"
					onclick="deletePost(this.form);" />
				<%
					}
				%>
				<br> <input type="text" name=reply size="50"
					value="Type the reply."> <input name="Reply" type="submit"
					value="Reply" />
			</div>
			<%
				Vector<Reply> comments = app.listComments(i);
						for (int j = 0; j < comments.size(); j++) {
			%>
			<div>
				<%
					out.println(comments.get(j).getAuthor().getName()
										+ ": " + comments.get(j).getComment());
				%>
				<%
					if (comments.get(j).getAuthor().getName()
										.equals(user.getUser().getName())) {
				%>
				<%=replyIdentifier(j)%>
				<%
					}
				%>
			</div>
			<%
				}
			%><form></form>
			<%
				}
			%>
			<%
				i++;
				}
			%>
		</form>
		<br>
		<form>
			IMAGES:
			<br><br>
			<%
				app.addImage();
				for (String key : app.imagesURL.keySet()) {
			%>
			<%=image(key, app.imagesURL.get(key))%>
			<%
				}
			%>

		</form>
</body>
</html>