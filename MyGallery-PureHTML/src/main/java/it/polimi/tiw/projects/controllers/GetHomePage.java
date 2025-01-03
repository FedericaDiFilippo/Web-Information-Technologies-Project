package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.projects.beans.Album;
import it.polimi.tiw.projects.beans.User;
import it.polimi.tiw.projects.dao.AlbumDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;


@WebServlet("/GetHomePage")
public class GetHomePage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection;
       

    public GetHomePage() {
        super();
        // TODO Auto-generated constructor stub
    }
    
	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		connection = ConnectionHandler.getConnection(servletContext);
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
			
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Opening home page...");
		String loginpath = getServletContext().getContextPath() + "/index.html";
		String username =null;
		HttpSession s = request.getSession();
		if (s.isNew() || s.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		}else {
			User u= (User) s.getAttribute("user");
			username = u.getUsername();
			
		}
		System.out.println(connection.toString());
		AlbumDAO albumDao = new AlbumDAO(connection);
		List<Album> allAlbums =  new ArrayList<Album>();
		List<Album> allAlbumFromUser = new ArrayList<Album>();
		List<Album> allOtherAlbum =  new ArrayList<Album>();
		try {
			
			allAlbums = albumDao.findAllAlbums();
		} catch (SQLException e) {
			// throw new ServletException(e);
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, " Failure in album's database extraction!");
		}
		
		for(Album a : allAlbums) {
			if(a.getCreator().equals(username)) {
				allAlbumFromUser.add(a);
			}else {
				allOtherAlbum.add(a);
			}
		}
		
		String path = "/WEB-INF/HomePage.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("allAlbumFromUser", allAlbumFromUser);
		ctx.setVariable("allOtherAlbum", allOtherAlbum);
		templateEngine.process(path, ctx, response.getWriter());
	}


	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException sqle) {
		}
	}

}
