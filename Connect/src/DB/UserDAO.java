package DB;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
	
	private static UserDAO instance = new UserDAO();
	
	public static UserDAO getInstance() {
		return instance;
	}
	
	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	
	String jdbc_driver = "com.mysql.cj.jdbc.Driver";
	String jdbc_url = "jdbc:mysql://pjs.chgycbit5egq.ap-northeast-2.rds.amazonaws.com/FunnyChat?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC"; 
	
	void connect() {
		try {
			Class.forName(jdbc_driver);
			
			conn = DriverManager.getConnection(jdbc_url, "admin", "mysql7612");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	void disconnect() {
		if(pstmt != null) {
			try {
				pstmt.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		if(conn != null) {
			try {
				conn.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}

	
	public String signup(String email, String password, String name) {
		connect();
		String sql = "insert into User(email, password, name) values(?, ?, ?)";
		String select = "select email from User where email = ?";
		
		try {			
			pstmt = conn.prepareStatement(select);
			pstmt.setString(1, email);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				if (rs.getString(1).equals(email)) {
					return "false";
				}
			} else {
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, email);
				pstmt.setString(2, password);
				pstmt.setString(3, name);
				pstmt.executeUpdate();
				
				return "true";
			}
		} catch (Exception e) {
			e.getStackTrace();
		} finally {
			disconnect();
		}
		return "false";
	}
	
	public String signin(String email, String password) {
		connect();
		String sql = "select password, name from User where email = ?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, email);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				if (rs.getString(1).equals(password)) {
					return rs.getString("name");
				}
			}
			return "false";
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnect();
		}
		
		return "false";
	}

}
