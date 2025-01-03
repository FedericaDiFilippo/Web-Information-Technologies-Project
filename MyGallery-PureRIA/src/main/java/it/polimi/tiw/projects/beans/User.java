package it.polimi.tiw.projects.beans;

public class User {
	private String username;
	private String email;
	private String password;
	private String albumsOrder;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getAlbumsOrder() {
		return albumsOrder;
	}
	public void setAlbumsOrder(String albumsOrder) {
		this.albumsOrder = albumsOrder;
	}


}
