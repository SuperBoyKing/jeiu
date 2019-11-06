<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import = "java.util.ArrayList" %>
<%@ page import="DB.*"%>
<%@ page import ="java.math.BigInteger, java.security.MessageDigest, java.security.NoSuchAlgorithmException" %> 

<%
	DB.UserDAO userDAO = DB.UserDAO.getInstance();
	DB.UserDTO userDTO = DB.UserDTO.getInstance();

	request.setCharacterEncoding("UTF-8");
	String email = request.getParameter("email");
	String password = request.getParameter("password");
	String name = request.getParameter("name");
	String type = request.getParameter("type");
	
	String returns = null;
	
	userDTO.setEmail(email);
	try {
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		byte[] messageDigest = md.digest(password.getBytes());
		BigInteger bigInteger = new BigInteger(1, messageDigest);
		String hashtext = bigInteger.toString(16);
		
		while (hashtext.length() < 32) {
			hashtext = "0" + hashtext;
		}
		
		userDTO.setPassword(hashtext);
	} catch (NoSuchAlgorithmException e) {
		throw new RuntimeException(e);
	}
	
	userDTO.setName(name);
	userDTO.setType(type);
	
	if (type.equals("login")) {
		returns = userDAO.signin(userDTO);
		out.println(returns);
	} else if (type.equals("signup")) {
		returns = userDAO.signup(userDTO);
		out.println(returns);
	} else {
		out.println("유효하지 않은 입력(type)");	
	}
%>

