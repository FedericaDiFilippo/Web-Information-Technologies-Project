package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.projects.beans.Album;
import it.polimi.tiw.projects.beans.Image;
import it.polimi.tiw.projects.beans.User;
import it.polimi.tiw.projects.dao.ImageDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;

@WebServlet("/AddImageToAlbum")
public class AddImageToAlbum extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public AddImageToAlbum() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession s = request.getSession();
		String title = request.getParameter("title");
		System.out.println("Inserting new image: " + title);
		System.out.println("Title: " + title);
		User user = (User) s.getAttribute("user");
		String creatorUsername = request.getParameter("creatorUsername");
		System.out.println("Creator: " + creatorUsername);
		String pathImg = request.getParameter("path");
		String descriptionText = request.getParameter("descriptionText");
		System.out.println("Description: " + descriptionText);
		//String cd = request.getParameter("creationDate");

		Integer idAlbum = Integer.parseInt(request.getParameter("idAlbum"));
		Date creationDate = null;
		ImageDAO imageDao = new ImageDAO(connection);

		/**
		// check the correct input for the date parameter
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		try {
			creationDate = new SimpleDateFormat("yyyy-MM-dd").parse(cd);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		*/
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(new Date());
		try {
			creationDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
		} catch (ParseException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}  

		if (idAlbum == null || idAlbum < 1) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error while searching for album");
			return;
		}

		if (title == null || title.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Title can't be empty");
			return;
		}
		/*
		if (cd == null || cd.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Date can't be empty");
			return;
		}
		*/

		if (s.isNew() || user == null) {
			String loginpath = getServletContext().getContextPath() + "/index.html";
			response.sendRedirect(loginpath);
			return;
		}

		if (descriptionText == null || descriptionText.equals("")) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Description can't be empty");
			return;
		}

		Image checkTitle = new Image();
		Image checkPath = new Image();

		//controlla se è gia presente un immagine con questo titolo
		try {
			checkTitle = imageDao.findImageByTitle(title, idAlbum);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String path = null;

		if (checkTitle != null) {
			request.getSession().setAttribute("user", user);
			String error = "This title is already in use";
			request.getSession().setAttribute("errorMsgCreateImage", error);
			path = getServletContext().getContextPath() + "/GetAlbumPage?idAlbum=" + idAlbum
					+ "&currentPage=1&imageSelected=-1";
			response.sendRedirect(path);
			return;
		}

		//controlla se questa immmagine è presente nel database
		try {
			checkPath = imageDao.findImageByPath(pathImg);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (checkPath == null) {
			request.getSession().setAttribute("user", user);
			String error = "This image isn't in the database";
			request.getSession().setAttribute("errorMsgCreateImage", error);
			path = getServletContext().getContextPath() + "/GetAlbumPage?idAlbum=" + idAlbum
					+ "&currentPage=1&imageSelected=-1";
			response.sendRedirect(path);
			return;
		}

		try {
			imageDao.createImage(idAlbum, title, creationDate, descriptionText, pathImg, creatorUsername);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Database failure");
			return;
		}
		request.getSession().setAttribute("errorMsgCreateImage", "");
		path = getServletContext().getContextPath() + "/GetAlbumPage?idAlbum=" + idAlbum
				+ "&currentPage=1&imageSelected=-1";
		request.getSession().setAttribute("user", user);
		response.sendRedirect(path);
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
