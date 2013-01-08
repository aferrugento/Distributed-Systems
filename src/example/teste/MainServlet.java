package example.teste;


import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet({"/index.html", "/main"})
public class MainServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private ServerManager app;

    public MainServlet() {
	super();
    }


    public void init(ServletConfig config)
	    throws ServletException {
	super.init(config);
	this.getServletContext();
 
	this.app = (ServerManager) this.getServletContext().getAttribute("app");
	if (app == null) {
	    this.app = new ServerManager();
	    this.getServletContext().setAttribute("app", this.app);
	}
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {
	UserBean user = LoginServlet.checkUser(request);
	if (!user.getLoginStat()) {
		response.sendRedirect("login");
	} else {
	  request.getRequestDispatcher("main.jsp").forward(request, response);
	}
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {
	this.doGet(request, response);
    }
}
