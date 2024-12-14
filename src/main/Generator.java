/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

/**
 *
 * @author Almar Dave
 */
public class Generator {

    Connection con = new Connection();

    public int getId(String table, String column) {
        int temp = (int) (Math.random() * 999999999) + 100000000;
        while (con.rowCount("select * from "+table+" where "+column+" = " + temp) > 0) {
            temp = (int) (Math.random() * 999999999) + 100000000;
        }
        return temp;
    }
}
