package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

import it.polimi.tiw.projects.beans.User;
import it.polimi.tiw.projects.dao.UserDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;


@WebServlet("/SignUp")
@MultipartConfig
public class SignUp extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
       
    public SignUp() {
        super();
        // TODO Auto-generated constructor stub
    }
    
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		String username = null;
		String password = null;
		String email = null;
		String confirmPassword = null;
		username =StringEscapeUtils.escapeJava(request.getParameter("username"));
		email = StringEscapeUtils.escapeJava(request.getParameter("email"));
		password = StringEscapeUtils.escapeJava(request.getParameter("pwd"));
		confirmPassword = StringEscapeUtils.escapeJava(request.getParameter("confirmPassword"));
		UserDAO userDao = new UserDAO(connection);
	
		User check = new User();
		
		try {
			check = userDao.findUserByName(username);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(check != null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Username already taken");
			return;
		}
		User u = new User();
		u.setEmail(email);
		u.setUsername(username);
		u.setPassword(confirmPassword);
		if(username == null || username.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Username mustn't be empty");
			return;
		}
		if(email == null ||email.isEmpty() ) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email can't be empty");
			return;
		}
		
		if(password == null || !password.equals(confirmPassword)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Passwords don't match");
			return;
		}
		
		try{
			userDao.signUp(username, email, password);	
		}catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Database failure");
			return;
		}
		
		request.getSession().setAttribute("user", u);
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(username);
		
	}
	
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
