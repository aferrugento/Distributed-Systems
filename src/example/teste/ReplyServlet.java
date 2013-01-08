package example.teste;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/reply")
public class ReplyServlet extends HttpServlet {

	ServerManager app;
	private static final long serialVersionUID = 1L;

	public ReplyServlet() {
		super();
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		this.getServletContext();

		this.app = (ServerManager) this.getServletContext().getAttribute("app");
		if (app == null) {
			this.app = new ServerManager();
			this.getServletContext().setAttribute("app", this.app);
		}
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		UserBean user = LoginServlet.checkUser(request);
		String edit;
		String reply;
		
		if (!(edit = (String) request.getParameter("edition")).equals("*DELETE OR EDIT*")) {
			System.out.println(edit);
			if (!edit.equals("*DELETE*") && !edit.equals("*DELETER*") && !edit.equals("*EDITR*")) {
				String postID = (String) request.getParameter("formID");
				System.out.println("entrei no edit post, postID: " + postID+" edit: "+edit+" user: "+user.getUser().getName());

				this.app.editPost(postID, edit, user.getUser());
			}

			else if (edit.equals("*DELETER*")) {
				String postID = (String) request.getParameter("formID");
				String replyID = (String) request.getParameter("editionID");
				System.out.println("entrei no delete reply, postID: " + postID+" replyID: "+replyID);
				this.app.deleteReply(postID, replyID);
			}

			else if (edit.equals("*EDITR*")) {
				String postID = (String) request.getParameter("formID");
				String replyID = (String) request.getParameter("editionID");
				String newReply = (String) request.getParameter("editionR");
				
				System.out.println("entrei no edit reply, postID: " + postID+" replyID: "+replyID+ " newReply: "+ newReply);
				this.app.editReply(postID, replyID, newReply, user.getUser());
			} else {
				String postID = (String) request.getParameter("formID");
				System.out.println("entrei no delete post, postID: " + postID);
				this.app.deletePost(postID);
			}
		}

		else if ((reply = (String) request.getParameter("reply")) != null) {
			String postID = (String) request.getParameter("formID");

			this.app.reply(reply, postID, user.getUser());
		}

		response.sendRedirect("main");

	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}
}