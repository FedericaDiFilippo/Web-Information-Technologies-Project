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

import it.polimi.tiw.projects.beans.Comment;
import it.polimi.tiw.projects.dao.CommentDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;


@WebServlet("/GetImageComments")
public class GetImageComments extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

    public GetImageComments() {
        super();
        // TODO Auto-generated constructor stub
    }
    
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Finding images comments..." );

		Integer idImage;
		idImage = Integer.parseInt(request.getParameter("idImage"));
		CommentDAO commentDao = new CommentDAO(connection); 
		List<Comment> comments = new ArrayList<Comment>();
		
		try {
			comments = commentDao.findAllCommentsById(idImage);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().println("Resource not found");
			return;
		}
		if(comments.size() == 0) {
			System.out.println("No comments");
		}
		for(Comment comment: comments) {
			System.out.println(comment.getText());
		}
		
		String json = new Gson().toJson(comments);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
