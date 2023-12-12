
package com.phonebook;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class DB {
    
    private final String URL = "jdbc:derby:ContactDB;create=true";
    private final String USERNAME = "";
    private final String PASSWORD = "";
    private Connection conn;
    private Statement statement;
    private DatabaseMetaData dbmd;
    
    public DB() {
        try {
            conn = DriverManager.getConnection(URL);
            statement = conn.createStatement();
            dbmd = conn.getMetaData();
            ResultSet res = dbmd.getTables(null, "APP", "CONTACTS", null);
            if (!res.next()) {
                statement.execute("create table contacts (id INT not null primary key GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1),"
                        +"lastname varchar(45), firstname varchar(45), email varchar(90))");
            }
            System.out.println("Connected");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }      
    }
    
    public List<Person> getAllContacts() {
        String sql = "select * from contacts";
        List<Person> list = new ArrayList<>();
        try {
            ResultSet res = statement.executeQuery(sql);
            while (res.next()) {
                list.add(new Person(res.getInt("id"), res.getString("lastname"), res.getString("firstname"), res.getString("email")));
                
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }
    
    public void addContact(Person person) {
        String sql = "insert into contacts (lastname, firstname, email) values (?,?,?)";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, person.getLastName());
            preparedStatement.setString(2, person.getFirstName());
            preparedStatement.setString(3, person.getEmail());
            preparedStatement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public void updateContact(Person person) {
        String sql = "update contacts set lastname = ?, firstname = ?, email = ? where id = ?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, person.getLastName());
            preparedStatement.setString(2, person.getFirstName());
            preparedStatement.setString(3, person.getEmail());
            preparedStatement.setInt(4, Integer.parseInt(person.getId()));
            preparedStatement.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    
}
