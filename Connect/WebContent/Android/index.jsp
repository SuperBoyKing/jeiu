<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.Enumeration"%>
<%@page import="com.oreilly.servlet.multipart.DefaultFileRenamePolicy"%>
<%@page import="com.oreilly.servlet.MultipartRequest"%>
	<%
		request.setCharacterEncoding("UTF-8");
		String name = new String();
		String fileName = new String();
		String path = "E:/JavaProject/Connect/WebContent/upload/";	
		int size = 1024 * 1024 * 10;
		
		try {
			MultipartRequest multi = new MultipartRequest(request, path, size, new DefaultFileRenamePolicy());
			Enumeration files = multi.getFileNames();
			
			if (files.hasMoreElements()) {
				name = (String) files.nextElement();
				fileName = multi.getFilesystemName(name);
			}
			System.out.println("파일 이름 : " + fileName);
			out.println("파일전송완료!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	%>