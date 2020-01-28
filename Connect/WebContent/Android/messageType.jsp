<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import = "java.util.ArrayList" %>
<%@ page import="DB.*"%>

<%
	DB.UserDAO userDAO = DB.UserDAO.getInstance();
	DB.UserDTO userDTO = DB.UserDTO.getInstance();

	request.setCharacterEncoding("UTF-8");
	String email = request.getParameter("email");
	String message = request.getParameter("message");
	
	
	userDTO.setEmail(email);
	userDTO.setMessage(message);
	
	userDAO.saveChat(userDTO);
	out.println("실행 중...");
%>

