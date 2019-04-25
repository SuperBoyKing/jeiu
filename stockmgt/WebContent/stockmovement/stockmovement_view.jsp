<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<jsp:useBean id="stockmovementDTO" class="stockmovement.StockMovementDTO" scope="request"></jsp:useBean>

<!DOCTYPE html>
<html>
<head>
	<link rel="stylesheet" href="stockmovement.css" type="text/css" media="screen" /> 
	<meta charset="UTF-8">
	<title>재고이동 등록</title>
	<script>
		
		function insertCheck(){
			document.view.action.value = "insert";
			document.view.submit();
		}
		
	</script>
</head>
<body>
	<div align="center">
		<h2>재고이동등록</h2>
		<hr>
		<a href="stockmovement_control.jsp?action=list">재고이동 목록 조회</a>
		
		<form name="view" action="stockmovement_control.jsp" method="post">
		<%
			String action = request.getParameter("action");
		
			if(action.equals("add")){
				stockmovementDTO.setProductNumber(""); 
				stockmovementDTO.setReleaseWarehouseName(""); 
				stockmovementDTO.setStoreWarehouseName(""); 
				stockmovementDTO.setReleaseDate(""); 
				stockmovementDTO.setReleaseProductQuantity(0); 
			}
		%>
		
		<input type="hidden" name="action" value="<%= action %>">
		<input type="hidden" name="id" value="<%= stockmovementDTO.getId() %>">
			<table border=1>
				<tr>
					<td>제품번호</td>
					<td><input type=text size=20 name=productNumber value="<%= stockmovementDTO.getProductNumber()%>"></td>
				</tr>
				<tr>
					<td>출고창고</td>
					<td><input type=text size=20 name=releaseWarehouseName value="<%= stockmovementDTO.getReleaseWarehouseName()%>"></td>
				</tr>
				<tr>
					<td>입고창고</td>
					<td><input type=text size=20 name=storeWarehouseName value="<%= stockmovementDTO.getStoreWarehouseName()%>"></td>
				</tr>
				<tr>
					<td>출고일자</td>
					<td><input type=date size=20 name=releaseDate value="<%= stockmovementDTO.getReleaseDate()%>"></td>
				</tr>
				<tr>
					<td>출고수량</td>
					<td><input type=text size=20 name=releaseProductQuantity value="<%= stockmovementDTO.getReleaseProductQuantity()%>"></td>
				</tr>
				<tr>
					<td colspan=2 align=center>
						<input type="button" id="insert" value="입력" onclick="insertCheck()">
					</td>
				</tr>
			</table>
		</form>
	</div>
</body>
</html>