package semanticgateway.model;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;

public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	@NotEmpty
	private String username;
	@NotEmpty
	private String password;

	public User() {
		
	}
	
	public User(HelioUser user) {
		this.username = user.getUsername();
		this.password = user.getPassword();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "User [username=" + username + ", password=" + password + "]";
	}
	
	
	
}
