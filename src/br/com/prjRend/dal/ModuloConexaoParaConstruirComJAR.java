package br.com.prjRend.dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;

public class ModuloConexaoParaConstruirComJAR {

    public static Connection conector() {
        Properties props = new Properties();
        Connection conexao = null;

        try {
            // Obter o caminho do diretório onde o JAR está sendo executado
            File jarDir = new File(ModuloConexaoParaConstruirComJAR.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
            File configFile = new File(jarDir, "config.properties");

            // Verificar se o arquivo de configuração existe
            if (!configFile.exists()) {
                System.out.println("Arquivo config.properties não encontrado no diretório: " + jarDir.getAbsolutePath());
                return null;
            }

            // Carregar as propriedades do arquivo config.properties
            try (FileInputStream fis = new FileInputStream(configFile)) {
                props.load(fis);
            }

            // Obter as propriedades do banco de dados
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");
            String driver = "com.mysql.cj.jdbc.Driver";

            // Carregar o driver e estabelecer a conexão
            Class.forName(driver);
            conexao = DriverManager.getConnection(url, user, password);
            return conexao;

        } catch (Exception e) {
            System.out.println("Erro ao conectar ao banco de dados: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
