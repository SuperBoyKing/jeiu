<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ page errorPage="stockmovement_error.jsp" %>

<%@ page import="stockmovement.*" %>
<%@ page import="java.util.ArrayList"%>

<% request.setCharacterEncoding("utf-8"); %>

<jsp:useBean id="stockmovementDTO" class="stockmovement.StockMovementDTO" scope="request"></jsp:useBean>

<jsp:setProperty property="*" name="stockmovementDTO"/>

<jsp:useBean id="stockmovementDAO" class="stockmovement.StockMovementDAO" scope="request"></jsp:useBean>

<%
	String action = request.getParameter("action");

	if(action.equals("list")) {
		
		ArrayList<StockMovementDTO> stockmovementList = stockmovementDAO.getDBList();
		
		request.setAttribute("stockmovementList", stockmovementList);
		pageContext.forward("stockmovement_list.jsp");
		
	} else if(action.equals("add")){
		pageContext.forward("stockmovement_view.jsp?action=add");
		
	} else if(action.equals("insert")){
			if(stockmovementDAO.insertDB(stockmovementDTO))
				pageContext.forward("stockmovement_control.jsp?action=list");
		
	} else {
		throw new Exception("DB 입력오류");
	}
%>

