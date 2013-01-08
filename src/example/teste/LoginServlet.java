package example.teste;


import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import example.server.User;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private ServerManager app;

	public LoginServlet() {
		super();
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	  
		this.app = (ServerManager) this.getServletContext().getAttribute("app");
		if (app == null) {
			this.app = new ServerManager();
			this.getServletContext().setAttribute("app", this.app);
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String logout = (String) request.getParameter("logout");
		UserBean userBean =	LoginServlet.checkUser(request);
		
		if (logout != null) {
			app.logout(userBean.getUser());
			request.getSession().invalidate();
		}
	
		request.getRequestDispatcher("login.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		UserBean userBean = LoginServlet.checkUser(request);


		User temp=null;
		String username = (String) request.getParameter("username");
		String password = (String) request.getParameter("password");

		if (username != null && password != null) {
			userBean.setName(username);
			userBean.setPass(password);

			if ((temp=this.app.loginRequest(username, password))!=null) { //associar um User ao userBean, para poder definir o user ao fazer publicacoes e comentarios
				userBean.setUser(temp);
				userBean.setLoginStat(true);
				System.out.println("entrei no login request");
				response.sendRedirect("main");
			} else {
				request.setAttribute("loginError", true);
				request.getRequestDispatcher("login.jsp").forward(request, response);
			}
		} else {
			request.getRequestDispatcher("login.jsp").forward(request, response);
		}
	}

	public static UserBean checkUser(HttpServletRequest request) {
		UserBean userBean = (UserBean) request.getSession().getAttribute("user");
		if (userBean == null) {
			userBean = new UserBean();
			request.getSession().setAttribute("user", userBean);
		}
		request.setAttribute("servlet", true);
		return userBean;
	}
}
