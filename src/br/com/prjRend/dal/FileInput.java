package br.com.prjRend.dal;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class FileInput {

    public static String AcessarNumeroBancada() {
        Properties propriedades = new Properties();

        try {
            // Obter o caminho do diretório onde o JAR está sendo executado
            File Diretorio = new File(ModuloConexaoParaConstruirComJAR.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
            File ArquivoDeConfiguracao = new File(Diretorio, "config.properties");

            // Verificar se o arquivo de configuração existe
            if (!ArquivoDeConfiguracao.exists()) {
                System.out.println("Arquivo config.properties não encontrado no diretório: " + Diretorio.getAbsolutePath());
            }

            // Carregar as propriedades do arquivo config.properties
            try (FileInputStream fis = new FileInputStream(ArquivoDeConfiguracao)) {
                propriedades.load(fis);
            }

            // Obter as propriedades do banco de dados
            String numeroBancada = propriedades.getProperty("nom.bancada");

            return numeroBancada;

        } catch (Exception e) {

            System.out.println("Erro ao conectar ao banco de dados: " + e.getMessage());

        }
        return null;

    }

    public static String AcessarNomeSetor() {
        Properties propriedades = new Properties();

        try {
            // Obter o caminho do diretório onde o JAR está sendo executado
            File Diretorio = new File(ModuloConexaoParaConstruirComJAR.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
            File ArquivoDeConfiguracao = new File(Diretorio, "config.properties");

            // Verificar se o arquivo de configuração existe
            if (!ArquivoDeConfiguracao.exists()) {
                System.out.println("Arquivo config.properties não encontrado no diretório: " + Diretorio.getAbsolutePath());
            }

            // Carregar as propriedades do arquivo config.properties
            try (FileInputStream fis = new FileInputStream(ArquivoDeConfiguracao)) {
                propriedades.load(fis);
            }

            // Obter as propriedades do banco de dados
            
            String nomeSetor = propriedades.getProperty("nom.setor");
            System.out.println(nomeSetor);

            return nomeSetor;

        } catch (Exception e) {

            System.out.println("Erro ao conectar ao banco de dados: " + e.getMessage());

        }
        return null;

    }

}
