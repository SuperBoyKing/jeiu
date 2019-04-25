package stockmovement;

public class StockMovementDTO {
	private int id;
	private String productNumber;
	private String releaseWarehouseName;
	private String storeWarehouseName;
	private String releaseDate;
	private int releaseProductQuantity;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getReleaseProductQuantity() {
		return releaseProductQuantity;
	}
	public void setReleaseProductQuantity(int releaseProductQuantity) {
		this.releaseProductQuantity = releaseProductQuantity;
	}
	public String getProductNumber() {
		return productNumber;
	}
	public void setProductNumber(String productNumber) {
		this.productNumber = productNumber;
	}
	public String getReleaseWarehouseName() {
		return releaseWarehouseName;
	}
	public void setReleaseWarehouseName(String releaseWarehouseName) {
		this.releaseWarehouseName = releaseWarehouseName;
	}
	public String getStoreWarehouseName() {
		return storeWarehouseName;
	}
	public void setStoreWarehouseName(String storeWarehouseName) {
		this.storeWarehouseName = storeWarehouseName;
	}
	public String getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}
	
	
}
