package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.polimi.tiw.projects.beans.Album;

public class AlbumDAO {
	private Connection connection;

	public AlbumDAO(Connection connection) {
		this.connection = connection;
	}

	public void createAlbum(String title, String creator, Date creationDate) throws SQLException {
		String query = "INSERT INTO album (title, creator, creationDate)   VALUES(?, ?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, title);
			pstatement.setString(2, creator);
			pstatement.setObject(3, creationDate.toInstant().atZone(ZoneId.of("Europe/Rome")).toLocalDate());
			System.out.println(creationDate.toInstant().atZone(ZoneId.of("Europe/Rome")).toLocalDate());
			pstatement.executeUpdate();
		}
	}

	public Album findAlbumById(int albumID) throws SQLException {
		String query = "SELECT * FROM album WHERE id = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, albumID);
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next())
					return this.dbToAlbum(result);
				return null;
			}
		}
	}

	
	public List<Album> findAllAlbums() throws SQLException {
		List<Album> allAlbum = new ArrayList<Album>();
		String query = "SELECT id, title, creationDate, creator FROM album  ORDER BY creationDate DESC";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			System.out.println(pstatement.toString());
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Album album = dbToAlbum(result);
					allAlbum.add(album);
				}
			}
		}
		return allAlbum;
	}

	public Album dbToAlbum(ResultSet result) throws SQLException {
		Album album = new Album();
		album.setId(result.getInt("id"));
		System.out.println(album.getId());
		album.setTitle(result.getString("title"));
		System.out.println(album.getTitle());
		album.setCreationDate(result.getDate("creationDate"));
		System.out.println(album.getCreationDate());
		album.setCreator(result.getString("creator"));
		System.out.println(album.getCreator());
		return album;
	}

	public int findNumberPagesInAlbum(int idAlbum) throws SQLException {
		String query = "SELECT CEILING(COUNT(*)/5) FROM image WHERE idalbum = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, idAlbum);
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next())
					return result.getInt(1);
				return 1;
			}
		}
	}
		
		
	}

