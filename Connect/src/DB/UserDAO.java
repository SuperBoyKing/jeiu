package DB;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
	String falied = "false";
	String success = "true";
	
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
	
	public String signup(UserDTO userDTO) {
		connect();
		String sql = "insert into User(email, password, name) values(?, ?, ?)";
		String select = "select email from User where email = ?";
		
		try {			
			pstmt = conn.prepareStatement(select);
			pstmt.setString(1, userDTO.getEmail());
			rs = pstmt.executeQuery();
			if(rs.next()) {
				if (rs.getString(1).equals(userDTO.getEmail())) {
					return falied;
				}
			} else {
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, userDTO.getEmail());
				pstmt.setString(2, userDTO.getPassword());
				pstmt.setString(3, userDTO.getName());
				pstmt.executeUpdate();
				
				return success;
			}
		} catch (Exception e) {
			e.getStackTrace();
		} finally {
			disconnect();
		}
		return falied;
	}
		
	public String signin(UserDTO userDTO) {
		connect();
		String sql = "select password, name from User where email = ?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userDTO.getEmail());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				if (rs.getString(1).equals(userDTO.getPassword())) {
					return rs.getString("name");
				}
			}
			return falied;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnect();
		}
		
		return falied;
	}
	
	public String createRoom(UserDTO userDTO) {
		connect();
		String sql = "insert into Room(room, email) values(?, ?)";
		String select = "select room from Room where room = ?";
		
		try {			
			pstmt = conn.prepareStatement(select);
			pstmt.setString(1, userDTO.getRoom());
			rs = pstmt.executeQuery();
			if(rs.next()) {
				if (rs.getString(1).equals(userDTO.getRoom())) {
					return "false";
				}
			} else {
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, userDTO.getRoom());
				pstmt.setString(2, userDTO.getEmail());
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
	
	public ArrayList<UserDTO> showList() {
		connect();
		ArrayList<UserDTO> roomList = new ArrayList<UserDTO>();
		String sql = "select room from Room";
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				UserDTO userDTO = new UserDTO();
				userDTO.setRoom(rs.getString("room"));			
				roomList.add(userDTO);
			}
			rs.close();
					
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnect();
		}
		return roomList;
	}

}
