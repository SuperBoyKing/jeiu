package stockmovement;

import java.sql.*;
import java.util.ArrayList;

public class StockMovementDAO {
	Connection conn = null;
	PreparedStatement pstmt = null;
	
	String jdbc_driver = "com.mysql.jdbc.Driver";
	String jdbc_url = "jdbc:mysql://localhost/jspdb?serverTimezone=UTC";
	
	void connect() {
		try {
			Class.forName(jdbc_driver);
			conn = DriverManager.getConnection(jdbc_url, "jspdb", "1234");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void disconnect() {
		if(pstmt != null) {
			try {
				pstmt.close();
			}catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(pstmt != null) {
			try {
				conn.close();
			}catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	
	public ArrayList<StockMovementDTO> getDBList() {
		connect();
		
		ArrayList<StockMovementDTO> stockMovementList = new ArrayList<StockMovementDTO>();
		
		String sql = "select id, productNumber, releaseWarehouseName, storeWarehouseName, releaseDate, releaseProductQuantity from Stockmovement";
		
		try {
			pstmt = conn.prepareStatement(sql);
			
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()) {
				StockMovementDTO stockmovementDTO = new StockMovementDTO();
				
				stockmovementDTO.setId(rs.getInt("id"));
				stockmovementDTO.setProductNumber(rs.getString("productNumber"));
				stockmovementDTO.setReleaseWarehouseName(rs.getString("releaseWarehouseName"));
				stockmovementDTO.setStoreWarehouseName(rs.getString("storeWarehouseName"));
				stockmovementDTO.setReleaseDate(rs.getString("releaseDate"));
				stockmovementDTO.setReleaseProductQuantity(rs.getInt("releaseProductQuantity"));
				
				stockMovementList.add(stockmovementDTO);
			}
			rs.close();
			
		}catch (SQLException e) {
			e.printStackTrace();
		}finally {
			disconnect();
		}
		
		return stockMovementList;
	}
	
	public boolean insertDB(StockMovementDTO stockmovementDTO) {
		connect();
		
		String sql = "insert into Stockmovement(productNumber, releaseWarehouseName, storeWarehouseName, releaseDate, releaseProductQuantity) values(?,?,?,?,?)";
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, stockmovementDTO.getProductNumber());
			pstmt.setString(2, stockmovementDTO.getReleaseWarehouseName());
			pstmt.setString(3, stockmovementDTO.getStoreWarehouseName());
			pstmt.setString(4, stockmovementDTO.getReleaseDate());
			pstmt.setInt(5, stockmovementDTO.getReleaseProductQuantity());
			
			pstmt.executeUpdate();
		}catch (SQLException e) {
			e.printStackTrace();
			return false;
		}finally {
			disconnect();
		}
		return true;
	}
}
