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

import it.polimi.tiw.projects.beans.User;
import it.polimi.tiw.projects.dao.AlbumDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;

@WebServlet("/CreateAlbum")
public class CreateAlbum extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public CreateAlbum() {
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
		User user= (User) s.getAttribute("user");
		System.out.println("Creating new album:");
		System.out.println("Title: " + title);
		String creator =user.getUsername();
		System.out.println("Creator: " + creator);
		String cd = request.getParameter("creationDate");
		System.out.println("CreationDate: " + cd);
		Date creationDate = null;
		AlbumDAO albumDao = new AlbumDAO(connection);

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


		if (title == null || title.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Title can't be empty");
			return;
		}
		if (cd == null || cd.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Date can't be empty");
			return;
		}

		try {
			 albumDao.createAlbum(title, creator, creationDate);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Database failure");
			return;
		}

		String path = getServletContext().getContextPath() + "/GetHomePage";
		request.getSession().setAttribute("user", user);
		response.sendRedirect(path);
	}

	
	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {
		}
	}

}
