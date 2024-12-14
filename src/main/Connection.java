/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sampanalims.LoginPage;

/**
 *
 * @author Almar Dave
 */
public class Connection {
    
     private java.sql.Connection con = null;
    private ResultSet rs;
    private java.sql.Connection initConnection()
    {
        try {
            // this is the code for log in and aithentication
            con = DriverManager.getConnection("jdbc:sqlserver://127.0.0.1:1433;databaseName=SampanaLIMS_DB;encrypt=true;trustServerCertificate=true;", "sa", "SampanaLims2023");
            return con;
        } catch (SQLException ex) {
            Logger.getLogger(LoginPage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return con;
    }
    public ResultSet getData(String sql)
    {
        con = initConnection();
        try {
            PreparedStatement p = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = p.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rs;
    }
    
    public boolean update(String SQL)
    {
        
        con = initConnection();
        try {
            PreparedStatement p = con.prepareStatement(SQL);
            p.execute();
            return true;
        } catch (SQLException ex) {
            System.out.println(ex);
            return false;
        }
    }
    
    
    public int rowCount(String sql)
    {
        int x = 0;
        con = initConnection();
        try {
            PreparedStatement p = con.prepareStatement(sql);
            rs = p.executeQuery();
            while(rs.next())
            {   x++;    }
        } catch (SQLException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return x;
    }
    
}
