<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="DB.ConnectDB"%>
<%
	request.setCharacterEncoding("UTF-8");
	String email = request.getParameter("email");
	String password = request.getParameter("password");
	String name = request.getParameter("name");
	String type = request.getParameter("type");
	
	ConnectDB connectDB = ConnectDB.getInstance();
	String returns = null;
	
	if (type.equals("login")) {
		returns = connectDB.signin(email, password);
		out.println(returns);
	} else if (type.equals("signup")){
		returns = connectDB.signup(email, password, name);
		out.println(returns);
	} else {
		out.println("유효하지 않은 입력(type)");	
	}

%>

