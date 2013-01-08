package example.teste;


import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/post")
public class PostServlet extends HttpServlet {
	
	ServerManager app;
	private static final long serialVersionUID = 1L;

	public PostServlet() {
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
    
    	String post= (String) request.getParameter("post");
    	System.out.println("post: " +post);
    	String image= (String) request.getParameter("image");
    	System.out.println("image: "+image);
    	String facebook= (String) request.getParameter("facebook");
    	System.out.println("facebook: "+facebook);
    	
    	if(!image.equals(""))
    	{
    		System.out.println("entrei no if da image");
    		this.app.image(image, user.getUser());
    		
    	}
    	else if(!facebook.equals(""))
    		
    	{
    		System.out.println("entrei no if do facebook");
    		this.app.postFacebook(facebook, user.getUser());
    		
    	}
    	else
    	{
        	this.app.post(post, user.getUser());
    	}
    	
 
    	response.sendRedirect("main");
    	
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException, IOException {
    	this.doGet(request, response);
    }
}