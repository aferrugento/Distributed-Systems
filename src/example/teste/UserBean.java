package example.teste;

import example.server.User;



public class UserBean  {

	private String name;
	private String pass;
	private boolean loginStat;
	private User user;
	


	public UserBean() {

		this.loginStat=false;

	}

	public User getUser()
	{
		return this.user;
	}
	
	public void setUser(User user)
	{
		this.user=user;
		
	}
	
	public boolean getLoginStat() {
		return loginStat;
	}

	public void setLoginStat(boolean loginStat) {
		this.loginStat = loginStat;
	}
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPass() {
		return this.pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}
}