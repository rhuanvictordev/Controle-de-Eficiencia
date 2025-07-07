package br.com.prjRend.dal;

import java.sql.*;

public class ModuloConexaoLocalhost {

    public static Connection conector() {
        
        java.sql.Connection conexao = null;
        
        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/banco2?useTimezone=true&serverTimezone=UTC";
        String user = "root";
        String password = "root";

        try {
            Class.forName(driver);
            conexao = DriverManager.getConnection(url, user, password);
            return conexao;
        } catch (Exception e) {
            return null;
        }

    }
}