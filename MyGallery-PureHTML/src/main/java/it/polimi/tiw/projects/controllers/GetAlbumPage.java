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
import it.polimi.tiw.projects.beans.Comment;
import it.polimi.tiw.projects.beans.Image;
import it.polimi.tiw.projects.beans.User;
import it.polimi.tiw.projects.dao.AlbumDAO;
import it.polimi.tiw.projects.dao.CommentDAO;
import it.polimi.tiw.projects.dao.ImageDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;

@WebServlet("/GetAlbumPage")
public class GetAlbumPage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection;

	public GetAlbumPage() {
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

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("Finding all albums...");
		String loginpath = getServletContext().getContextPath() + "/index.html";
		HttpSession s = request.getSession();
		Integer idAlbum;
		Integer currentPage;
		Integer imageSelected;
		boolean hasNext = false;
		boolean hasPrevious = false;
		
		if (s.isNew() || s.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		} else {
			User u = (User) s.getAttribute("user");
			System.out.println(u.getUsername());
			idAlbum = Integer.parseInt(request.getParameter("idAlbum"));
			System.out.println("Album:" + idAlbum);
			currentPage = Integer.parseInt(request.getParameter("currentPage"));
			imageSelected = Integer.parseInt(request.getParameter("imageSelected"));
			System.out.println("imageSelected: " + imageSelected);
		}

		ImageDAO imageDao = new ImageDAO(connection);
		AlbumDAO albumDao = new AlbumDAO(connection);
		CommentDAO commentDao =new CommentDAO(connection);
		List<Image> images = new ArrayList<Image>();
		List<Comment> comments = new ArrayList<Comment>();
		Album album = new Album();
		try {
			album = albumDao.findAlbumById(idAlbum);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			images = imageDao.findFiveImagesByAlbumID(idAlbum, currentPage);
		} catch (SQLException e) {
			// throw new ServletException(e);
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, " Failure in album's database extraction!");
			return;
		}

		try {
			if (currentPage < albumDao.findNumberPagesInAlbum(idAlbum)) {
				hasNext = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, " Failure in album's database extraction!");
			return;

		}

		if (currentPage > 1) {
			hasPrevious = true;
		}
		
		for(Image i : images) {
			if(i.getId() == imageSelected) {
				try {
					comments = commentDao.findAllCommentsById(imageSelected);
				} catch (SQLException e) {
					response.sendError(HttpServletResponse.SC_BAD_GATEWAY, " Failure in comments research");
					return;
				}
				imageSelected = images.indexOf(i);
			}
			
		}


		String path = "/WEB-INF/AlbumPage.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("images", images);
		ctx.setVariable("album", album);
		ctx.setVariable("hasNext", hasNext);		
		ctx.setVariable("hasPrevious", hasPrevious);
		ctx.setVariable("currentPage", currentPage);
		ctx.setVariable("imageSelected", imageSelected);
		ctx.setVariable("comments", comments);
		
		templateEngine.process(path, ctx, response.getWriter());

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
