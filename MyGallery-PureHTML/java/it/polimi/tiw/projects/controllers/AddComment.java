package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.projects.beans.*;
import it.polimi.tiw.projects.dao.AlbumDAO;
import it.polimi.tiw.projects.dao.CommentDAO;
import it.polimi.tiw.projects.dao.ImageDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;

@WebServlet("/AddComment")
public class AddComment extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
   

    public AddComment() {
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
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		CommentDAO commentDao = new CommentDAO(connection);
		AlbumDAO albumDao = new AlbumDAO(connection);
		ImageDAO imageDao = new ImageDAO(connection);
		Image image = new Image();
		Album album = new Album();
		
		if (session.isNew() || user == null) {
			String loginpath = getServletContext().getContextPath() + "/index.html";
			response.sendRedirect(loginpath);
			return;
		}
		
		Integer imageId = Integer.parseInt(request.getParameter("imageId"));
		String creatorUsername = user.getUsername();
		String text = request.getParameter("commentText");
		
		if(imageId == null ||imageId < 1 ) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Image doesn't exist");
			return;
		}
	
		if(text == null ||text.equals("") ) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Comment can't be empty");
			return;
		}
		
		if(creatorUsername == null ||creatorUsername.equals("") ) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Username can't be empty");
			return;
		}
		
		try {
			image = imageDao.findImageById(imageId);
			album = albumDao.findAlbumById(image.getIdAlbum());
			System.out.println("Found album:  " + album.getId());
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failure while accessing database");
		}
		
		try {
			commentDao.createComment(imageId, creatorUsername, text);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,  "Error while adding comment");
			return;
		}
		
		String path = getServletContext().getContextPath() + "/GetAlbumPage?idAlbum=" +album.getId() + "&currentPage=1&imageSelected=" + imageId;
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
