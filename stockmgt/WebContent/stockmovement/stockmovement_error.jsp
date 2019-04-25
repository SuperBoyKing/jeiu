<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isErrorPage="true"%>
<!DOCTYPE html>
<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>계좌이체</title>
	<link rel="stylesheet" href="stockmovement.css" type="text/css" media="screen" />
</head>

<body>
	<div align="center">
	<H2>계좌이체 오류</H2>
	<HR>
	<table>
		<tr>
			<td>
				계좌이체 처리중 에러가 발생 했습니다.<BR>
				관리자에게 문의해 주세요..<BR>
				빠른시일내 복구하겠습니다.<BR>
				<HR>
				에러내용 : <%= exception%>
				<HR>
			</td>
		</tr>
	</table>
	</div>
</body>
</html>