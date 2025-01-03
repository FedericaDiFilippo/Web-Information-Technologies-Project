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
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;

import it.polimi.tiw.projects.beans.User;
import it.polimi.tiw.projects.dao.UserDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;


@WebServlet("/SaveNewAlbumsOrder")
@MultipartConfig
public class SaveNewAlbumsOrder extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;   

    public SaveNewAlbumsOrder() {
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
		System.out.println("Saving current order...");
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		UserDAO userDao =new UserDAO(connection);
		String order = StringEscapeUtils.escapeJava(request.getParameter("newOrder"));

		System.out.println("New order: " + order);
		
		try {
			userDao.updateAlbumsOrder(order, user.getUsername());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to update order");
			return;
		}
		
	}

}
