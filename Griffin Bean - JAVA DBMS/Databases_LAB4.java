// ---------------------------------------------------------------------------------------------------------------------------------
// Griffin Bean
// Databases_LAB4.java
// Basic DBMS and SQL command builder
// ---------------------------------------------------------------------------------------------------------------------------------
package databaseTest;
import java.sql.*;
import java.util.*;

public class Databases_LAB4 
{
	static String query = "";
	static Scanner scan = new Scanner (System.in);
	public static void main(String[] args)

	{
		String response = "";
		System.out.println("Perform action? (Y/N)");
		response = scan.nextLine().toUpperCase();
		
		while (response.equals("Y"))
		{
			System.out.println("Would you like to CREATE, DROP, DELETE, UPDATE, INSERT, QUERY, or VIEWMETADATA?");
			query = scan.nextLine().toUpperCase();
			switch (query)
			{
				case ("CREATE"):
					create();
					break;
				case ("DROP"):
					drop();
					break;
				case ("INSERT"):
					insert();
					break;
				case ("UPDATE"):
					update();
					break;
				case ("DELETE"):
					delete();
					break;
				case ("QUERY"):
					query = "SELECT ";
					query();
					break;
				case ("VIEWMETADATA"):
					query = "SHOW ";
					view();
					break;
				default:
					System.out.println("Invalid Selection");	
			}
			System.out.println("Perform action? (Y/N)");
			response = scan.nextLine().toUpperCase();
		}
	}
	
	
	
	
	// -------------------------------------------------------------------- CREATE METHOD ------------------------------------------
	public static String create()
	{
		Connection conn = getConnection();
		Statement statement = null;
		String stmt = "";
		String dbname = "";
		String tname = "";
		String attr = "";
		String response = "";
		int count = 0;
		int i = 0;
		System.out.println("Would you like to create a DATABASE, or a TABLE?");
		stmt = scan.nextLine().toUpperCase();
		query = query + " " + stmt;
		if (stmt.equals("DATABASE"))
		{
			System.out.println("What would you like to call your database?");
			dbname = scan.nextLine().toUpperCase();
			query = query + " `" + dbname + "`";
		}
		if (stmt.equals("TABLE"))
		{
			System.out.println("What is the name of the database you want to create the table in?");
			dbname = scan.nextLine().toUpperCase();
			query = query + " " + dbname + ".";
			System.out.println("What would you like to call your table?");
			tname = scan.nextLine().toUpperCase();
			query = query + tname + "(";
			System.out.println("How many attributes are there for your table?");
			count = scan.nextInt();
			scan.nextLine();
			if (count > 1)
			{
				do
				{
					System.out.println("What is one of the attributes? (This should include the name, the data type, and any "
							+ "limits (Ex: yourattr varchar(maxlength))");
					attr = scan.nextLine().toUpperCase();
					query = query + attr + ", ";
					i++;
				} while (i != count-1);
				i = 0;
				System.out.println("What is one of the attributes? (This should include the name, the data type, and any "
							+ "limits (Ex: yourattr varchar(maxlength))");
				attr = scan.nextLine().toUpperCase();
				query = query + attr + ", ";
			}
			else
			{
				System.out.println("What is one of the attributes? (This should include the name, the data type, and any "
							+ "limits (Ex: yourattr varchar(maxlength))");
				attr = scan.nextLine().toUpperCase();
				query = query + attr + ", ";
			}
			System.out.println("Which of the previous attributes is the primary key?");
			attr = scan.nextLine().toUpperCase();
			query = query + "PRIMARY KEY (" + attr + "))";
		}
		System.out.println("Does this query or update look correct? (Y/N)");
		System.out.println();
		System.out.println(query);
		response = scan.nextLine().toUpperCase();
		if (response.equals("Y"))
		{
			try 
			{
				statement = conn.createStatement();
				statement.executeUpdate(query);
			} 
			catch (SQLException e) 
			{
				System.out.println("There is already a database or table with this name.");
				e.printStackTrace();
			}
		}
		else
		{
			query = "CREATE";
			create();
		}
		return "";
	}
	// -----------------------------------------------------------------------------------------------------------------------------
	
	
	
	
	// -------------------------------------------------------------------- DROP METHOD --------------------------------------------
	public static String drop()
	{
		Connection conn = getConnection();
		Statement statement = null;
		String stmt = "";
		String name = "";
		String response = "";
		System.out.println("What would you like to drop?");
		stmt = scan.nextLine().toUpperCase();
		query = query + " " + stmt;
		System.out.println("What is then name of the item you want to drop?");
		name = scan.nextLine().toUpperCase();
		query = query + " `" + name + "`";
		System.out.println("Does this query or update look correct? (Y/N)");
		System.out.println();
		System.out.println(query);
		response = scan.nextLine().toUpperCase();
		if (response.equals("Y"))
		{
			try 
			{
				statement = conn.createStatement();
				statement.executeUpdate(query);
			} 
			catch (SQLException e) 
			{
				System.out.println("There is not a database or table with this name.");
				e.printStackTrace();
			}
		}
		else
		{
			query = "DROP";
			drop();
		}
		return "";
	}
	// -----------------------------------------------------------------------------------------------------------------------------
	
	
	
	// -------------------------------------------------------------------- INSERT METHOD ------------------------------------------
	public static String insert()
	{
		Connection conn = getConnection();
		Statement statement = null;
		String dbname = "";
		String tname = "";
		String value = "";
		String response = "";
		int count = 0;
		int i = 0;
		System.out.println("What is the name of the database you want to insert into?");
		dbname = scan.nextLine().toUpperCase();
		query = query + " " + dbname + ".";
		System.out.println("What is the name of the table in this database?");
		tname = scan.nextLine().toUpperCase();
		query = query + tname + " VALUES ('";
		System.out.println("How many attributes are in this table?");
		count = scan.nextInt();
		scan.nextLine();
		if (count > 1)
		{
			do
			{
				System.out.println("Enter the values for the attributes in order, make sure the data types are correct.");
				value = scan.nextLine();
				query = query + value + "', '";
				i++;
			} while (i != count-1);
			i = 0;
			System.out.println("Enter the values for the attributes in order, make sure the data types are correct.");
			value = scan.nextLine();
			query = query + value + "')";
		}
		else
		{
			System.out.println("Enter the values for the attributes in order, make sure the data types are correct.");
			value = scan.nextLine();
			query = query + value + "')";
		}
		System.out.println("Does this query or update look correct? (Y/N)");
		System.out.println();
		System.out.println(query);
		response = scan.nextLine().toUpperCase();
		if (response.equals("Y"))
		{
			try 
			{
				statement = conn.createStatement();
				statement.executeUpdate(query);
			} 
			catch (SQLException e) 
			{
				System.out.println("There may already be a tuple in the database with the same primary key value.");
			}
		}
		else
		{
			query = "INSERT";
			insert();
		}
		return "";
	}
	// -----------------------------------------------------------------------------------------------------------------------------
	
	
	
	// -------------------------------------------------------------------- DELETE METHOD ------------------------------------------
	public static String delete()
	{
		Connection conn = getConnection();
		Statement statement = null;
		String dbname = "";
		String tname = "";
		String wattr = "";
		String value = "";
		String response  = "";
		int count = 0;
		int i = 0;
		System.out.println("What is the name of the database you would like to delete from?");
		dbname = scan.nextLine().toUpperCase();
		query = query + " FROM " + dbname + ".";
		System.out.println("What is the name of the table this value exists in?");
		tname = scan.nextLine().toUpperCase();
		query = query + tname + " WHERE ";
		System.out.println("How many attributes would you like to use in the WHERE clause?");
		count = scan.nextInt();
		scan.nextLine();
		if (count > 1)
		{
			do
			{
				System.out.println("What attribute would you like to use in the WHERE clause?");
				wattr = scan.nextLine().toUpperCase();
				query = query + wattr + " = '";
				System.out.println("Enter the value for that attribute for the tuple you would like to delete.");
				value = scan.nextLine().toUpperCase();
				query = query + value + "' AND ";
				i++;
			} while (i != count-1);
			i = 0;
			System.out.println("What attribute would you like to use in the WHERE clause?");
			wattr = scan.nextLine().toUpperCase();
			query = query + wattr + " = '";
			System.out.println("Enter the value for that attribute for the tuple you would like to delete.");
			value = scan.nextLine();
			query = query + value + "'";
		}
		else
		{
			System.out.println("What attribute would you like to use in the WHERE clause?");
			wattr = scan.nextLine().toUpperCase();
			query = query + wattr + " = '";
			System.out.println("Enter the value for that attribute for the tuple you would like to delete.");
			value = scan.nextLine();
			query = query + value + "'";
		}
		System.out.println("Does this query or update look correct?");
		System.out.println();
		System.out.println(query);
		response = scan.nextLine().toUpperCase();
		if (response.equals("Y"))
		{
			System.out.println("Are you sure you want to delete this tuple? (Y/N)");
			response = scan.nextLine().toUpperCase();
			if (response.equals("Y"))
			{
				try 
				{
					statement = conn.createStatement();
					statement.executeUpdate(query);
				} 
				catch (SQLException e) 
				{
					System.out.println("Attempting to delete this may have violated a foreign key constraint, or the tuple"
							+ "did not exist in the table.");
				}
			}
			else
			{
				query = "DELETE";
				delete();
			}
		}
		else
		{
			query = "DELETE";
			delete();
		}
		return "";
	}
	// -----------------------------------------------------------------------------------------------------------------------------
	
	
	
	
	// -------------------------------------------------------------------- UPDATE METHOD ------------------------------------------
	public static String update()
	{
		Connection conn = getConnection();
		Statement statement = null;
		String dbname = "";
		String tname = "";
		String attr = "";
		String wattr = "";
		String value = "";
		String response = "";
		int count = 0;
		int i = 0;
		System.out.println("What is the name of the database you would like to update information in?");
		dbname = scan.nextLine().toUpperCase();
		query = query + " " + dbname + ".";
		System.out.println("What is the name of table you are updating information to?");
		tname = scan.nextLine().toUpperCase();
		query = query + tname + " SET ";
		System.out.println("How many attributes would you like to update?");
		count = scan.nextInt();
		scan.nextLine();
		if (count > 1)
		{
			do
			{
				System.out.println("What attribute of the table would you like to change?");
				attr = scan.nextLine().toUpperCase();
				query = query + attr + " = ";
				System.out.println("What is the new value you would like to input?");
				value = scan.nextLine();
				query = query + "'" + value + "', ";
				i++;
			} while (i != count-1);
			i = 0;
			System.out.println("What attribute of the table would you like to change?");
			attr = scan.nextLine().toUpperCase();
			query = query + attr + " = ";
			System.out.println("What is the new value you would like to input?");
			value = scan.nextLine();
			query = query + "'" + value + "' WHERE ";
		}
		else
		{
			System.out.println("What attribute of the table would you like to change?");
			attr = scan.nextLine().toUpperCase();
			query = query + attr + " = ";
			System.out.println("What is the new value you would like to input?");
			value = scan.nextLine();
			query = query + "'" + value + "' WHERE ";
		}
		System.out.println("How many attributes would you like to use in the WHERE clause?");
		count = scan.nextInt();
		scan.nextLine();
		if (count > 1)
		{
			do
			{
				System.out.println("What attribute would you like to use in the WHERE clause?");
				wattr = scan.nextLine().toUpperCase();
				query = query + wattr + " = '";
				System.out.println("Enter the value for that attribute for the tuple you would like to update.");
				value = scan.nextLine();
				query = query + value + "' AND ";
				i++;
			} while (i != count-1);
			i = 0;
			System.out.println("What attribute would you like to use in the WHERE clause?");
			wattr = scan.nextLine().toUpperCase();
			query = query + wattr + " = '";
			System.out.println("Enter the value for that attribute for the tuple you would like to update.");
			value = scan.nextLine();
			query = query + value + "'";
		}
		else
		{
			System.out.println("What attribute would you like to use in the WHERE clause?");
			wattr = scan.nextLine().toUpperCase();
			query = query + wattr + " = '";
			System.out.println("Enter the value for that attribute for the tuple you would like to update.");
			value = scan.nextLine();
			query = query + value + "'";
		}
		System.out.println("Does this query or update look correct? (Y/N)");
		System.out.println();
		System.out.println(query);
		response = scan.nextLine().toUpperCase();
		if (response.equals("Y"))
		{
			try 
			{
				statement = conn.createStatement();
				statement.executeUpdate(query);
			} 
			catch (SQLException e) 
			{
				System.out.println("Attempting to update this may have violated a foreign key constraint, or a tuple with"
						+ "the same primary key already exists in the table.");
			}
		}
		else
		{
			query = "UPDATE";
			update();
		}
		return "";
	}
	// -----------------------------------------------------------------------------------------------------------------------------
	
	
	
	// -------------------------------------------------------------------- QUERY METHOD -------------------------------------------
	public static String query()
	{
		Connection conn = getConnection();
		Statement statement = null;
		int count = 0;
		int i = 0;
		int colCount = 0;
		String wconfirm = "";
		String response = "";
		String attr = "";
		String dbname = "";
		String tname = "";
		String choice = "";
		String value = "";
		System.out.println("How many attributes are you resquesting for this query?");
		response = scan.nextLine();
		colCount = Integer.parseInt(response);
		System.out.println("Are these all of the attributes for the tables you are querying? (Y/N)");
		choice = scan.nextLine().toUpperCase();
		if (choice.equals("Y"))
		{
			response = "*";
		}
		// -------------------------------- Star Query ----------------------------------------------------------------------------
		if (response.equals("*"))
		{
			query = query + response + " FROM ";
			System.out.println("What is the name of the database you are querying to?");
			dbname = scan.nextLine().toUpperCase();
			query = query + dbname + ".";
			System.out.println("How many tables from this database are you querying data from?");
			count = scan.nextInt();
			scan.nextLine();
			if (count > 1)
			{
				do
				{
					System.out.println("What is the name of one of the tables?");
					tname = scan.nextLine().toUpperCase();
					query = query + tname + ", " + dbname + ".";
					i++;
				} while (i != count-1);
				i = 0;
				System.out.println("What is the name of one of the tables?");
				tname = scan.nextLine().toUpperCase();
				query = query + tname + " ";
			}
			else
			{
				System.out.println("What is the name of one of the tables?");
				tname = scan.nextLine().toUpperCase();
				query = query + tname + " ";
			}
			System.out.println("Would you like to include a WHERE clause? (Y/N)");
			wconfirm = scan.nextLine().toUpperCase();
			// ----------------------------------------- Star Query with WHERE -----------------------------------------------------
			if (wconfirm.equals("Y"))
			{
				query = query + "WHERE ";
				System.out.println("How many attributes would you like to use in the WHERE clause?");
				count = scan.nextInt();
				scan.nextLine();
				// ------------------------------- Star Query with WHERE with multiple attributes -----------------------------------
				if (count > 1)
				{
					do
					{
						System.out.println("What is the name of the table this attribute comes from?");
						tname = scan.nextLine().toUpperCase();
						query = query + tname + ".";
						System.out.println("What is the name of the attribute?");
						attr = scan.nextLine().toUpperCase();
						query = query + attr + " = ";
						System.out.println("Is the other side of the equals sign a specific, "
								+ "known value (SPECIFIC), or another table (TABLE)?");
						choice = scan.nextLine().toUpperCase();
						if (choice.equals("SPECIFIC"))
						{
							System.out.println("What is the specific value?");
							value = scan.nextLine();
							query = query + "'" + value + "' AND ";
						}
						if (choice.equals("TABLE"))
						{
							System.out.println("What is the name of the table for the other side of the equals side?");
							tname = scan.nextLine().toUpperCase();
							query = query + tname + ".";
							System.out.println("What is the attribute from this second table?");
							attr = scan.nextLine().toUpperCase();
							query = query + attr + " AND ";
						}
						i++;
					} while (i != count-1);
					i = 0;
					System.out.println("What is the name of the table this attribute comes from?");
					tname = scan.nextLine().toUpperCase();
					query = query + tname + ".";
					System.out.println("What is the name of the attribute?");
					attr = scan.nextLine().toUpperCase();
					query = query + attr + " = ";
					System.out.println("Is the other side of the equals sign a specific, "
								+ "known value (SPECIFIC), or another table (TABLE)?");
					choice = scan.nextLine().toUpperCase();
					if (choice.equals("SPECIFIC"))
					{
						System.out.println("What is the specific value?");
						value = scan.nextLine();
						query = query + "'" + value + "'";
					}
					if (choice.equals("TABLE"))
					{
						System.out.println("What is the name of the table for the other side of the equals sign?");
						tname = scan.nextLine().toUpperCase();
						query = query + tname + ".";
						System.out.println("What is the attribute from this second table?");
						attr = scan.nextLine().toUpperCase();
						query = query + attr;
					}
				}
				// ----------------------------------END STAR with multiple attribute WHERE --------------------------------------
				else
				{
					System.out.println("What is the name of the table this attribute comes from?");
					tname = scan.nextLine().toUpperCase();
					query = query + tname + ".";
					System.out.println("What is the name of the attribute?");
					attr = scan.nextLine().toUpperCase();
					query = query + attr + " = ";
					System.out.println("Is the other side of the equals sign a specific, "
								+ "known value (SPECIFIC), or another table (TABLE)?");
					choice = scan.nextLine().toUpperCase();
					if (choice.equals("SPECIFIC"))
					{
						System.out.println("What is the specific value?");
						value = scan.nextLine();
						query = query + "'" + value + "'";
					}
					if (choice.equals("TABLE"))
					{
						System.out.println("What is the name of the table for the other side of the equals sign?");
						tname = scan.nextLine().toUpperCase();
						query = query + tname + ".";
						System.out.println("What is the attribute from this second table?");
						attr = scan.nextLine().toUpperCase();
						query = query + attr;
					}
				}
				i = 1;
				System.out.println("Does this query or update look correct? (Y/N)");
				System.out.println();
				System.out.println(query);
				response = scan.nextLine().toUpperCase();
				if (response.equals("Y"))
				{
					try 
					{
						statement = conn.createStatement();
						ResultSet rs = statement.executeQuery(query);
						while (rs.next())
						{
							while (i != colCount+1)
							{
								System.out.print(rs.getString(i) + "\t");
								i++;
							}
							System.out.println();
							i = 1;
						}
						System.out.println();
						i = 1;
					} 
					catch (SQLException e) 
					{
						System.out.println("There may be an error in your SQL syntax, or one or more item you "
								+ "listed may not exist in the database you specified.");
					}
				}
				else
				{
					query = "SELECT ";
					query();
				}
			}
			// ----------------------------------------END Star with WHERE --------------------------------------------------------
			else
			{
				i = 1;
				System.out.println("Does this query or update look correct? (Y/N)");
				System.out.println();
				System.out.println(query);
				response = scan.nextLine().toUpperCase();
				if (response.equals("Y"))
				{
					try 
					{
						statement = conn.createStatement();
						ResultSet rs = statement.executeQuery(query);
						while (rs.next())
						{
							while (i != colCount+1)
							{
								System.out.print(rs.getString(i) + "\t");
								i++;
							}
							System.out.println();
							i = 1;
						}
						System.out.println();
						i = 1;
					} 
					catch (SQLException e) 
					{
						System.out.println("There may be an error in your SQL syntax, or one or more item you "
								+ "listed may not exist in the database you specified.");
					}
				}
				else
				{
					query = "SELECT ";
					query();
				}
			}
		}
		// ---------------------------------------------END Star -------------------------------------------------------------------
		else
		{
			count = Integer.parseInt(response);
			if (count > 1)
			{
				do
				{
					System.out.println("What is the name of the table one of the attributes is from?");
					tname = scan.nextLine().toUpperCase();
					query = query + tname + ".";
					System.out.println("Enter the attribute from this table you would like to request in this query.");
					attr = scan.nextLine().toUpperCase();
					query = query + attr + ", ";
					i++;
				} while (i != count-1);
				i = 0;
				System.out.println("What is the name of the table one of the attributes is from?");
				tname = scan.nextLine().toUpperCase();
				query = query + tname + ".";
				System.out.println("Enter the attribute from this table you would like to request in this query.");
				attr = scan.nextLine().toUpperCase();
				query = query + attr + " FROM ";
			}
			else
			{
				System.out.println("What is the name of the table the attribute is from?");
				tname = scan.nextLine().toUpperCase();
				query = query + tname + ".";
				System.out.println("Enter the attribute you would like to request in this query.");
				attr = scan.nextLine().toUpperCase();
				query = query + attr + " FROM ";
			}
			System.out.println("What is the name of the database you are querying to?");
			dbname = scan.nextLine().toUpperCase();
			query = query + dbname + ".";
			System.out.println("How many tables from this database are you querying data from?");
			count = scan.nextInt();
			scan.nextLine();
			if (count > 1)
			{
				do
				{
					System.out.println("What is the name of one of the tables?");
					tname = scan.nextLine().toUpperCase();
					query = query + tname + ", " + dbname + ".";
					i++;
				} while (i != count-1);
				i = 0;
				System.out.println("What is the name of one of the tables?");
				tname = scan.nextLine().toUpperCase();
				query = query + tname + " ";
			}
			else
			{
				System.out.println("What is the name of one of the tables?");
				tname = scan.nextLine().toUpperCase();
				query = query + tname + " ";
			}
			System.out.println("Would you like to include a WHERE clause? (Y/N)");
			wconfirm = scan.nextLine().toUpperCase();
			if (wconfirm.equals("Y"))
			{
				query = query + "WHERE ";
				System.out.println("How many attributes would you like to use in the WHERE clause?");
				count = scan.nextInt();
				scan.nextLine();
				if (count > 1)
				{
					do
					{
						System.out.println("What is the name of the table this attribute comes from?");
						tname = scan.nextLine().toUpperCase();
						query = query + tname + ".";
						System.out.println("What is the name of the attribute?");
						attr = scan.nextLine().toUpperCase();
						query = query + attr + " = ";
						System.out.println("Is the other side of the equals sign a specific, "
								+ "known value (SPECIFIC), or another table (TABLE)?");
						choice = scan.nextLine().toUpperCase();
						if (choice.equals("SPECIFIC"))
						{
							System.out.println("What is the specific value?");
							value = scan.nextLine();
							query = query + "'" + value + "' AND ";
						}
						if (choice.equals("TABLE"))
						{
							System.out.println("What is the name of the table for the other side of the equals side?");
							tname = scan.nextLine().toUpperCase();
							query = query + tname + ".";
							System.out.println("What is the attribute from this second table?");
							attr = scan.nextLine().toUpperCase();
							query = query + attr + " AND ";
						}
						i++;
					} while (i != count-1);
					i = 0;
					System.out.println("What is the name of the table this attribute comes from?");
					tname = scan.nextLine().toUpperCase();
					query = query + tname + ".";
					System.out.println("What is the name of the attribute?");
					attr = scan.nextLine().toUpperCase();
					query = query + attr + " = ";
					System.out.println("Is the other side of the equals sign a specific, "
								+ "known value (SPECIFIC), or another table (TABLE)?");
					choice = scan.nextLine().toUpperCase();
					if (choice.equals("SPECIFIC"))
					{
						System.out.println("What is the specific value?");
						value = scan.nextLine();
						query = query + "'" + value + "'";
					}
					if (choice.equals("TABLE"))
					{
						System.out.println("What is the name of the table for the other side of the equals sign?");
						tname = scan.nextLine().toUpperCase();
						query = query + tname + ".";
						System.out.println("What is the attribute from this second table?");
						attr = scan.nextLine().toUpperCase();
						query = query + attr;
					}
				}
				else
				{
					System.out.println("What is the name of the table this attribute comes from?");
					tname = scan.nextLine().toUpperCase();
					query = query + tname + ".";
					System.out.println("What is the name of the attribute?");
					attr = scan.nextLine().toUpperCase();
					query = query + attr + " = ";
					System.out.println("Is the other side of the equals sign a specific, "
								+ "known value (SPECIFIC), or another table (TABLE)?");
					choice = scan.nextLine().toUpperCase();
					if (choice.equals("SPECIFIC"))
					{
						System.out.println("What is the specific value?");
						value = scan.nextLine();
						query = query + "'" + value + "'";
					}
					if (choice.equals("TABLE"))
					{
						System.out.println("What is the name of the table for the other side of the equals sign?");
						tname = scan.nextLine().toUpperCase();
						query = query + tname + ".";
						System.out.println("What is the attribute from this second table?");
						attr = scan.nextLine().toUpperCase();
						query = query + attr;
					}
				}
				i = 1;
				System.out.println("Does this query or update look correct? (Y/N)");
				System.out.println();
				System.out.println(query);
				response = scan.nextLine().toUpperCase();
				if (response.equals("Y"))
				{
					try 
					{
						statement = conn.createStatement();
						ResultSet rs = statement.executeQuery(query);
						while (rs.next())
						{
							while (i != colCount+1)
							{
								System.out.print(rs.getString(i) + "\t");
								i++;
							}
							System.out.println();
							i = 1;
						}
						System.out.println();
						i = 1;
					}  
					catch (SQLException e) 
					{
						System.out.println("There may be an error in your SQL syntax, or one or more item you "
								+ "listed may not exist in the database you specified.");
					}
				}
				else
				{
					query = "SELECT ";
					query();
				}
			}
			else
			{
				i = 1;
				System.out.println("Does this query or update look correct? (Y/N)");
				System.out.println();
				System.out.println(query);
				response = scan.nextLine().toUpperCase();
				if (response.equals("Y"))
				{
					try 
					{
						statement = conn.createStatement();
						ResultSet rs = statement.executeQuery(query);
						while (rs.next())
						{
							while (i != colCount+1)
							{
								System.out.print(rs.getString(i) + "\t");
								i++;
							}
							System.out.println();
							i = 1;
						}
						System.out.println();
						i = 1;
					} 
					catch (SQLException e) 
					{
						System.out.println("There may be an error in your SQL syntax, or one or more item you "
								+ "listed may not exist in the database you specified.");
					}
				}
				else
				{
					query = "SELECT ";
					query();
				}
			}
		}
		return "";
	}
	// -----------------------------------------------------------------------------------------------------------------------------
	
	
	
	
	// -------------------------------------------------------------------- VIEW METHOD --------------------------------------------
	public static String view()
	{
		Connection conn = getConnection();
		Statement statement = null;
		String response = "";
		String dbname = "";
		String tname = "";
		int i = 0;
		System.out.println("Would you like to view the databases (D), tables within a database (TD), "
				+ "or the columns of a table (CT)?");
		response = scan.nextLine().toUpperCase();
		if (response.equals("D"))
		{
			response = "";
			query = query + "DATABASES";
		}
		else
		{
			if (response.equals("TD"))
			{
				response = "";
				System.out.println("What is the name of the database?");
				dbname = scan.nextLine().toUpperCase();
				query = query + "TABLES FROM " + dbname;
			}
			else
			{
				if (response.equals("CT"));
				{
					response = "";
					System.out.println("What is the name of the database?");
					dbname = scan.nextLine().toUpperCase();
					query = query + "COLUMNS FROM " + dbname + ".";
					System.out.println("What is the name of the table?");
					tname = scan.nextLine().toUpperCase();
					query = query + tname;
				}
			}
		}
		i = 1;
		System.out.println("Does this query or update look correct? (Y/N)");
		System.out.println();
		System.out.println(query);
		response = scan.nextLine().toUpperCase();
		if (response.equals("Y"))
		{
			try 
			{
				statement = conn.createStatement();
				ResultSet rs = statement.executeQuery(query);
				while (rs.next())
				{
						System.out.print(rs.getString(i) + "\t");
						System.out.println();
				}
				System.out.println();
			}  
			catch (SQLException e) 
			{
				System.out.println("There may be an error in your SQL syntax, or one or more item you "
						+ "listed may not exist in the database you specified.");
			}
			
		}
		else
		{
			query = "SHOW ";
			view();
		}
		return "";
	}
	// -----------------------------------------------------------------------------------------------------------------------------
	

	
	
	public static Connection getConnection()
	{
		Connection conn = null;
		try
		{
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root","");
			System.out.println("Connected to database");
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	    return conn;
	}
}