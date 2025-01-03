package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import it.polimi.tiw.projects.beans.Image;
import it.polimi.tiw.projects.dao.ImageDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;

@WebServlet("/GetAlbumPage")
public class GetAlbumPage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

	public GetAlbumPage() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("Finding all albums...");	
		Integer idAlbum;

		idAlbum = Integer.parseInt(request.getParameter("idAlbum"));
		System.out.println("Album: " + idAlbum);	

		ImageDAO imageDao = new ImageDAO(connection);
		List<Image> images = new ArrayList<Image>();
		
		try {
			images = imageDao.findAllImagesByAlbumId(idAlbum);
		} catch (SQLException e1) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().println("Failure in album's database extraction!");
			return;
		}

		String json = new Gson().toJson(images);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException sqle) {
		}
	}

}
