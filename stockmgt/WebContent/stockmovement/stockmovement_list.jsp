<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ page errorPage="stockmovement_error.jsp" %>
<%@ page import="stockmovement.*" %>
<%@ page import="java.util.ArrayList" %>

<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>재고이동 목록</title>
	<link rel="stylesheet" href="stockmovement.css" type="text/css" media="screen">
		<script>
			function revealList() {
				document.list.action.value = "list";
				document.list.submit();
			}
		</script>
</head>

<jsp:useBean id="stockmovementList" class="java.util.ArrayList" scope="request" />

<body>
	<div align="center">
		<h2>재고이동 조회</h2>
		<hr>
		<a href="stockmovement_control.jsp?action=add">재고이동 등록</a>
		<form name="list" action="stockmovement_control.jsp" method="post">
		<input type="hidden" name="action" value="action">
			<table border="1">
				<tr>
					<td colspan="6" align="right">
						<input type="button" value="조회" onClick="revealList()">
					</td>
				</tr>
				<tr>
					<th>id</th>
					<th>제품번호</th>
					<th>출고창고</th>
					<th>입고창고</th>
					<th>출고일자</th>
					<th>출고수량</th>
				</tr>
				<%
					if(stockmovementList != null) {
						for(StockMovementDTO stockmovementDTO : (ArrayList<StockMovementDTO>) stockmovementList){
				%>
							<tr>
								<td><%=stockmovementDTO.getId() %></td>
								<td><%=stockmovementDTO.getProductNumber() %></td>
								<td><%=stockmovementDTO.getReleaseWarehouseName() %></td>
								<td><%=stockmovementDTO.getStoreWarehouseName() %></td>
								<td><%=stockmovementDTO.getReleaseDate() %></td>
								<td><%=stockmovementDTO.getReleaseProductQuantity() %></td>
							</tr>
				<%
						}
					}
				%>
			</table>
		</form>
	</div>
</body>
</html>