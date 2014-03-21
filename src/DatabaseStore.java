import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class DatabaseStore {
	// declare a connection by using Connection interface
	Connection connection;
	/* Create string of connection url within specified format with machine 
	name, port number and database name. Here machine name id localhost 
	and database name is mahendra. */
	String connectionURL = "jdbc:mysql://localhost:3306/ImageStore";
    // Prepared Statement
	PreparedStatement psmnt; 
	
	public DatabaseStore() throws SQLException{
		this.connectionURL = "jdbc:mysql://localhost:3306/ImageStore";
		/*declare a resultSet that works as a table resulted by execute a specified 
		sql query. */
		ResultSet rs = null;
		// Declare prepare statement.
		PreparedStatement psmnt = null;		
	}
	public void storeImage (InputStream imageStream, String url, int imageSize) throws SQLException{
		// declare FileInputStream object to store binary stream of given image.
		FileInputStream fis;
		try {
			// Load JDBC driver "com.mysql.jdbc.Driver"
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			/* Create a connection by using getConnection() method that takes 
			parameters of string type connection url, user name and password to 
			connect to database. */
			this.connection = DriverManager.getConnection(connectionURL, "root", "toot");
			/* prepareStatement() is used for create statement object that is 
			used for sending sql statements to the specified database. */
			this.psmnt = connection.prepareStatement 
				("insert into imagesTable(url, image) "+ "values(?,?)");
			psmnt.setString(1,url);			
			psmnt.setBinaryStream(2, imageStream, imageSize);
			/* executeUpdate() method execute specified sql query. Here this query 
			insert data and image from specified address. */ 
			int s = psmnt.executeUpdate();
			if(s>0) {
				System.out.println("Uploaded successfully !");
			}
			else {
				System.out.println("unsucessfull to upload image.");
			}
			psmnt.close();
		}
		// catch if found any exception during rum time.
		catch (Exception ex) {
		System.out.println("Found some error : "+ex);
		}
		finally {
		// close all the connections.
		if (this.connection != null)
			this.connection.close();
		if (this.psmnt != null)
			this.psmnt.close();
		}	
	}
}