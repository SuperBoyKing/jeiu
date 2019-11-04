<%@page import="com.google.gson.JsonObject"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import = "java.util.ArrayList" %>
<%@ page import = "org.json.JSONObject" %>
<%@ page import = "java.util.*" %>
<%@ page import = "com.google.gson.Gson" %>
<%@ page import = "DB.*"%>

<%
	DB.UserDAO userDAO = DB.UserDAO.getInstance();
	DB.UserDTO userDTO = DB.UserDTO.getInstance();
	
	request.setCharacterEncoding("UTF-8");
	String email = request.getParameter("email");
	String room = request.getParameter("room");
	String type = request.getParameter("type");

	userDTO.setEmail(email);
	userDTO.setRoom(room);
	userDTO.setType(type);
	
	String returns = null;
	
	if (type.equals("create")) {
		returns = userDAO.createRoom(userDTO);
		out.println(returns);
	} else {
		returns = new Gson().toJson(userDAO.showList());
		out.println(returns);
	}
%>