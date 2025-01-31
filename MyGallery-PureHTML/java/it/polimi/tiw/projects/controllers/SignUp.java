package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.projects.beans.User;
import it.polimi.tiw.projects.dao.UserDAO;
import it.polimi.tiw.projects.utils.ConnectionHandler;

@WebServlet("/SignUp")
public class SignUp extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public SignUp() {
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
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String username = request.getParameter("username");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String confirmPassword = request.getParameter("confirmPassword");
		UserDAO userDao = new UserDAO(connection);

		User u = new User();
		u.setEmail(email);
		u.setUsername(username);
		u.setPassword(confirmPassword);
		if (username == null || username.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Username can't be empty");
			return;
		}
		if (email == null || email.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email can't be empty");
			return;
		}

		//controlla che non sia gi� presente un utente con lo stesso username
		User check = new User();
		try {
			check = userDao.findUserByName(username);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (check != null) {
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMsgSignUp", "Username already taken");
			String path = "/WEB-INF/SignUpPage.html";
			templateEngine.process(path, ctx, response.getWriter());
			return;
		}

		if (password == null || !password.equals(confirmPassword)) {
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMsgSignUp", "Passwords don't match");
			String path = "/WEB-INF/SignUpPage.html";
			templateEngine.process(path, ctx, response.getWriter());
			return;
		}

		try {
			userDao.signUp(username, email, password);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Database failure");
			return;
		}

		String path = getServletContext().getContextPath() + "/GetHomePage";
		request.getSession().setAttribute("user", u);
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
