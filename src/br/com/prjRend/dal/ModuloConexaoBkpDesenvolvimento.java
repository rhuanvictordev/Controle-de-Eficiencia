package br.com.prjRend.dal;

import java.sql.*;

public class ModuloConexaoBkpDesenvolvimento {

    public static Connection conector() {
        
        java.sql.Connection conexao = null;
        
        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://192.168.10.3:3306/banco?useTimezone=true&serverTimezone=UTC";
        String user = "dba";
        String password = "Dba@123456";

        try {
            Class.forName(driver);
            conexao = DriverManager.getConnection(url, user, password);
            return conexao;
        } catch (Exception e) {
            return null;
        }

    }
}