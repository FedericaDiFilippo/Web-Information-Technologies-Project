package it.polimi.tiw.projects.beans;

import java.util.Date;

public class Album {

	private int id;
	private String title;
	private String creator;
	private Date creationDate;
	
	public int getId() {
		return id;
	}
	public void setId(int idAlbum) {
		this.id = idAlbum;
	}
	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	
	
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

}
