package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class Feedback {
//	A common method to connect to the DB
	private Connection connect() {
		Connection con = null;

		try {
			Class.forName("com.mysql.jdbc.Driver");
			// Provide the correct details: DBServer/DBName, username, password
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/electrogrid_clientside", "root", "");

			// For testing
			System.out.print("Successfully connected");

		} catch (Exception e) {
			e.printStackTrace();
		}

		return con;
	}
	
	public String readFeedback() {
		String output = "";

		try {
			Connection con = connect();
			if (con == null) {
				return "Error while connecting to the database for reading.";
			}

			// Prepare the html table to be displayed
			output = "<table border='1'><tr><th>Customer Name</th>" + "<th>Customer Email</th><th>Rate</th>"
					+ "<th>FeedbackNotes</th>" + "<th>Update</th><th>Remove</th></tr>";

			String query = "select * from feedback";
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			// iterate through the rows in the result set
			while (rs.next()) {

				String FeedbackID = Integer.toString(rs.getInt("FeedbackID"));
				String CustomerName = rs.getString("CustomerName");
				String CustomerEmail = rs.getString("CustomerEmail");
				String Rate = rs.getString("Rate");
				String FeedbackNotes = rs.getString("FeedbackNotes");

				// Add into the html table

				output += "<tr><td><input id='hidFeedbackIDUpdate' name='hidFeedbackIDUpdate' type='hidden' value='"
						+ FeedbackID + "'>" + CustomerName + "</td>";

				output += "<td>" + CustomerEmail + "</td>";
				output += "<td>" + Rate + "</td>";
				output += "<td>" + FeedbackNotes + "</td>";

				// buttons
				output += "<td><input name='btnUpdate' type='button' value='Update' class='btnUpdate btn btn-secondary'></td>"
						+ "<td><input name='btnRemove' type='button' value='Remove' class='btnRemove btn btn-danger' data-FeedbackID='"
						+ FeedbackID + "'>" + "</td></tr>";

			}

			con.close();

			// Complete the html table
			output += "</table>";
		} catch (Exception e) {
			output = "Error while reading the Feedbacks Details.";
			System.err.println(e.getMessage());
		}

		return output;
	}
	
	// Insert Feedback
		public String insertFeedback(String CustomerName, String CustomerEmail, String Rate,
				String FeedbackNotes) {
			String output = "";

			try {
				Connection con = connect();

				if (con == null) {
					return "Error while connecting to the database";
				}

				// create a prepared statement
				String query = " insert into feedback (`FeedbackID`,`CustomerName`,`CustomerEmail`,`Rate`,`FeedbackNotes`)"
						+ " values (?, ?, ?, ?, ?)";

				PreparedStatement preparedStmt = con.prepareStatement(query);

				// binding values
				preparedStmt.setInt(1, 0);
				preparedStmt.setString(2, CustomerName);
				preparedStmt.setString(3, CustomerEmail);
				preparedStmt.setString(4, Rate);
				preparedStmt.setString(5, FeedbackNotes);

				// execute the statement
				preparedStmt.execute();
				con.close();

				// Create JSON Object to show successful msg.
				String newFeedback = readFeedback();
				output = "{\"status\":\"success\", \"data\": \"" + newFeedback + "\"}";
			} catch (Exception e) {
				// Create JSON Object to show Error msg.
				output = "{\"status\":\"error\", \"data\": \"Error while Inserting Feedbackr.\"}";
				System.err.println(e.getMessage());
			}

			return output;
		}
		
		// Update Feedback
		public String updateFeedback(String FeedbackID, String CustomerName, String CustomerEmail, String Rate,
				String FeedbackNotes) {
			String output = "";

			try {
				Connection con = connect();

				if (con == null) {
					return "Error while connecting to the database for updating.";
				}

				// create a prepared statement
				String query = "UPDATE feedback SET CustomerName=?,CustomerEmail=?,Rate=?,FeedbackNotes=? WHERE FeedbackID=?";

				PreparedStatement preparedStmt = con.prepareStatement(query);

				// binding values
				preparedStmt.setString(1, CustomerName);
				preparedStmt.setString(2, CustomerEmail);
				preparedStmt.setString(3, Rate);
				preparedStmt.setString(4,FeedbackNotes);
				preparedStmt.setInt(5, Integer.parseInt(FeedbackID));

				// execute the statement
				preparedStmt.execute();
				con.close();

				// create JSON object to show successful msg
				String newFeedback = readFeedback();
				output = "{\"status\":\"success\", \"data\": \"" + newFeedback + "\"}";
			} catch (Exception e) {
				output = "{\"status\":\"error\", \"data\": \"Error while Updating Feedback Details.\"}";
				System.err.println(e.getMessage());
			}

			return output;
		}
		
		public String deleteFeedback(String FeedbackID) {
			String output = "";

			try {
				Connection con = connect();

				if (con == null) {
					return "Error while connecting to the database for deleting.";
				}

				// create a prepared statement
				String query = "DELETE FROM feedback WHERE FeedbackID=?";

				PreparedStatement preparedStmt = con.prepareStatement(query);

				// binding values
				preparedStmt.setInt(1, Integer.parseInt(FeedbackID));
				// execute the statement
				preparedStmt.execute();
				con.close();

				// create JSON Object
				String newFeedback = readFeedback();
				output = "{\"status\":\"success\", \"data\": \"" + newFeedback + "\"}";
			} catch (Exception e) {
				// Create JSON object
				output = "{\"status\":\"error\", \"data\": \"Error while Deleting Feedback.\"}";
				System.err.println(e.getMessage());

			}

			return output;
		}
}
