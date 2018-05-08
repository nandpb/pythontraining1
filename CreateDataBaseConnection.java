package com.ge.treasury.pfi.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 
 * @author Pankaj1.Tiwari
 *
 */

public class CreateDataBaseConnection {
	
	Connection connection = null;
	
	public Connection getDataBaseConnection(){
		try {
			Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");
			String connectionUrl = prepareConnectionURL();
			connection = DriverManager.getConnection(connectionUrl);
		} catch (ClassNotFoundException e) {
			//logger.error("MySQL JDBC Driver is missing");
			e.printStackTrace();
			throw new RuntimeException();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connection;
	}
	
	/**
	 * 
	 */
	public void closeDataBaseConnection(){
		try{
			if(connection != null){
				connection.close();
			}
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param preparedStmt
	 */
	public void closePreparedStatement(PreparedStatement preparedStmt){
		try{
			if(preparedStmt != null){
				preparedStmt.close();
			}
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Method used for preparing the Connection URL
	 * @return
	 */
	private String prepareConnectionURL(){
		
		Properties properties = getPropertiesFromFile("E:\\IXPROCESS\\CONVERT\\jdbc.properties");
		
		String url = properties.getProperty("url");
		String username = properties.getProperty("username");
		String password = properties.getProperty("password");
		String dbName = properties.getProperty("dbName");
		
		return "jdbc:sqlserver://"+url+";user="+username+";password="+password+";database="+dbName;
		
	}
	
	/**
	 * Method used for getting the data from properties file
	 * @param fileName
	 * @return
	 */
	protected Properties getPropertiesFromFile(String fileName){
		Properties properties = null;
		try {		
			File file = new File(fileName);
			InputStream is = new FileInputStream(file.getAbsolutePath());
			//InputStream is = new FileInputStream("E:\\IXPROCESS\\jdbc.properties");
			properties = new Properties();
			properties.load(is);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		 return properties;
	}
	
}
