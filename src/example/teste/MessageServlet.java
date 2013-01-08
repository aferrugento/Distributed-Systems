package example.teste;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/message")
public class MessageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ServerManager app;
       

    public MessageServlet() {
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
            	
            	if((String) request.getParameter("receivedMessages") != null)
            	{
            		request.getRequestDispatcher("messages.jsp").forward(request, response);
            	}
            	else{
	            	String messageT = (String) request.getParameter("messageTitle");
	            	String messageS= (String) request.getParameter("messageSubject");
	            	String messageC= (String) request.getParameter("messageContent");
	            	String destination = (String) request.getParameter("dest");
	            	
	            	 this.app.message(messageT, messageS, messageC,  user.getUser(), destination);
	            	 response.sendRedirect("main");
            	}
            	
            }

            protected void doPost(HttpServletRequest request, HttpServletResponse response)
        	    throws ServletException, IOException {
            	this.doGet(request, response);
            }

}
