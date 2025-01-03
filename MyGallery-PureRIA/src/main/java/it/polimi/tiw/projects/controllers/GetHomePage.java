package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.lang.String;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.projects.beans.Album;
import it.polimi.tiw.projects.beans.User;
import it.polimi.tiw.projects.dao.AlbumDAO;
import it.polimi.tiw.projects.dao.UserDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;

@WebServlet("/GetHomePage")
public class GetHomePage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

	public GetHomePage() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("Opening home page...");
		AlbumDAO albumDao = new AlbumDAO(connection);
		List<Album> allAlbums = new ArrayList<Album>();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		UserDAO userDao = new UserDAO(connection);
		Album album = new Album();
		List<Album> othersAlbums = new ArrayList<Album>();
		String order = null;
		
		//check if there was previous order saved
		try {
			order = userDao.getOrderedAlbums(user.getUsername());
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//if there wasn't any order saved
		if (order == null) {
			System.out.println("No previous orders saved" );
			try {
				allAlbums = albumDao.findAllAlbums();
			} catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("Not possible to recover albums");
			}
		}else { //if there was
			 System.out.println("Found previous order:");
			 String[] arrOfStr = order.split(",");
			 for(String x : arrOfStr) {
				 int id = Integer.parseInt(x);
				 try {
					album = albumDao.findAlbumById(id);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 allAlbums.add(album);	 
			 }
			 try {
				othersAlbums = albumDao.findOthersAlbums(user.getUsername());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 for(Album a: othersAlbums) {
				 allAlbums.add(a);
			 }
			 
		}

		Gson gson = new GsonBuilder().setDateFormat("yyyy MMM dd").create();
		String json = gson.toJson(allAlbums);

		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);

		System.out.println("Done");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}

}
