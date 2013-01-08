package example.teste;


import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import example.server.User;


@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private ServerManager app;

	public RegisterServlet() {
		super();
	}

	public void init(ServletConfig config)
			throws ServletException {
		super.init(config);
		this.app = (ServerManager) this.getServletContext().getAttribute("app");
		if (app == null) {
			this.app = new ServerManager();
			this.getServletContext().setAttribute("app", this.app);
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LoginServlet.checkUser(request);
		request.getRequestDispatcher("register.jsp").forward(request, response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		UserBean userBean = LoginServlet.checkUser(request);

		String username = (String) request.getParameter("username");
		String password = (String) request.getParameter("password");
		User temp;

		RequestDispatcher dispatcher = request.getRequestDispatcher("register.jsp"); 

		if (username != null && password != null) {
			userBean.setName(username);
			if ((temp=this.app.register(username, password))!= null) {
				userBean.setLoginStat(true);
				userBean.setUser(temp);
				dispatcher = request.getRequestDispatcher("main");
			} else {
				request.setAttribute("registerError", true);
			}
		}
		dispatcher.forward(request, response);
	}
}
