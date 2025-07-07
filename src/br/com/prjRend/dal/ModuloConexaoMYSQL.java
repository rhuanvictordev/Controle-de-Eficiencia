package br.com.prjRend.dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.File;
import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class ModuloConexaoMYSQL {
    private static HikariDataSource dataSource;

    static {
        try {
            Properties props = new Properties();

            // Obter o caminho do diretório onde o JAR está sendo executado
            File jarDir = new File(ModuloConexaoMYSQL.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
            File configFile = new File(jarDir, "config.properties");

            // Verificar se o arquivo de configuração existe
            if (!configFile.exists()) {
                System.out.println("Arquivo config.properties não encontrado no diretório: " + jarDir.getAbsolutePath());
                throw new RuntimeException("Arquivo de configuração não encontrado!");
            }

            // Carregar as propriedades do arquivo config.properties
            try (FileInputStream fis = new FileInputStream(configFile)) {
                props.load(fis);
            }

            // Obter as propriedades do banco de dados
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");

            // Configuração do HikariCP
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(url);
            config.setUsername(user);
            config.setPassword(password);
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");

            // Configurações do pool
            config.setMaximumPoolSize(80);  // Máximo de conexões simultâneas
            config.setMinimumIdle(2);       // Conexões mínimas mantidas abertas
            config.setIdleTimeout(100);   // Tempo antes de fechar conexões ociosas (30s)
            config.setMaxLifetime(10000); // Tempo máximo de vida da conexão (30min)
            config.setConnectionTimeout(20000); // Tempo máximo de espera por conexão (10s)
            config.setValidationTimeout(10000); // tempo para validar a conexao

            dataSource = new HikariDataSource(config);

        } catch (Exception e) {
            System.out.println("Erro ao configurar o HikariCP: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Falha ao inicializar a conexão com o banco.");
        }
    }

    // Método para obter conexão
    public static Connection conector() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            System.out.println("Erro ao obter conexão do pool: " + e.getMessage());
            return null;
        }
    }

    // Método para fechar o pool quando a aplicação for encerrada
    public static void fecharPool() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}

