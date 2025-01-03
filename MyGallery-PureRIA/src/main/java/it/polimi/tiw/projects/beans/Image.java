package it.polimi.tiw.projects.beans;

import java.util.Date;

public class Image {

	private int id;
	private int idAlbum;
	private String title;
	private Date date;
	private String descriptionText;
	private String path;
	private String creatorUsername;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	
	public int getIdAlbum() {
		return idAlbum;
	}
	public void setIdAlbum(int idAlbum) {
		this.idAlbum = idAlbum;
	}
	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	
		public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	
	public String getDescriptionText() {
		return descriptionText;
	}
	public void setDescriptionText(String descriptionText) {
		this.descriptionText = descriptionText;
	}
	
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getCreatorUsername() {
		return creatorUsername;
	}
	public void setCreatorUsername(String creatorUsername) {
		this.creatorUsername = creatorUsername;
	}

}
