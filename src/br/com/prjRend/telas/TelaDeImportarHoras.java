package br.com.prjRend.telas;

import br.com.prjRend.dal.ModuloConexao;
import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import net.proteanit.sql.DbUtils;

public class TelaDeImportarHoras extends javax.swing.JInternalFrame {

    Connection conn = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    public TelaDeImportarHoras() {
        initComponents();
        setarDesignTabela();
    }
    
    public void setarDesignTabela(){
    
        tbImportacao.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18));
        tbImportacao.setFont(new Font("Arial", Font.PLAIN, 16));
        tbImportacao.setRowHeight(20);
    
    }

    public void importarHoras() {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Escolha o arquivo de dados");
        int resultado = fileChooser.showOpenDialog(null);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            File arquivoSelecionado = fileChooser.getSelectedFile();
            System.out.println("Arquivo escolhido: " + arquivoSelecionado.getAbsolutePath());
            // Processar o arquivo selecionado
            processarArquivo(arquivoSelecionado);
        } else {
            System.out.println("Nenhum arquivo selecionado.");
        }
    }

    public boolean erro = false;

    public void processarArquivo(File arquivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) { // Lê cada linha do arquivo
                processarLinha(linha);
                if (erro == true) {
                    return;
                }
                btnCorrigirHoras.setEnabled(true);
                btnEscolherArquivo.setEnabled(false);
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
        }
    }

    public void processarLinha(String linha) {
        if (linha.length() < 20) { // Para evitar erros caso a linha seja inválida
            erro = true;
            JOptionPane.showMessageDialog(null, "O arquivo selecionado é inválido ou está danificado e não poderá ser processado!");
            DefaultTableModel mo = (DefaultTableModel) tbImportacao.getModel();
            mo.setRowCount(0);
            return;
        }

        String dia = linha.substring(6, 8);
        String mes = linha.substring(3, 5);
        String ano = "20" + linha.substring(0, 2);
        String codigoFuncionario = linha.substring(8, 14).trim();
        String horasTrabalhadas = linha.substring(15, 20).trim();
        String dataFormatada = (dia + "/" + mes + "/" + ano);

        try {
            int diaInt = Integer.parseInt(dia);
            int mesInt = Integer.parseInt(mes);
            int anoInt = Integer.parseInt(ano);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "O arquivo selecionado é inválido ou está danificado e não poderá ser processado!");
            DefaultTableModel m = (DefaultTableModel) tbImportacao.getModel();
            m.setRowCount(0);
            erro = true;
            return;
        }

        DefaultTableModel model = (DefaultTableModel) tbImportacao.getModel();
        model.addRow(new Object[]{dataFormatada, codigoFuncionario, horasTrabalhadas});

        // Exibindo os resultados
        System.out.println("Data: " + dia + "/" + mes + "/" + ano);
        System.out.println("Codigo do funcionario: " + codigoFuncionario);
        System.out.println("Horas trabalhadas: " + horasTrabalhadas);
        System.out.println("----------------------");
    }

    public void corrigirHoras() {
        String data = null;
        String matricula = null;
        String horas = null;

        int matriculaInt = 0;
        double horasDouble = 0.0;

        for (int i = 0; i < tbImportacao.getRowCount(); i++) {
            data = tbImportacao.getValueAt(i, 0).toString();
            matricula = tbImportacao.getValueAt(i, 1).toString();
            horas = tbImportacao.getValueAt(i, 2).toString();

            try {
                matriculaInt = Integer.parseInt(matricula);
                horasDouble = Double.parseDouble(horas);

                // Verifica se as horas são 00.00 e remove a linha se for o caso
                if (horasDouble == 0.00) {
                    DefaultTableModel model = (DefaultTableModel) tbImportacao.getModel();
                    model.removeRow(i);
                    i--;  // Decrementa o índice para evitar pular a linha seguinte após a remoção
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Os dados estão corrompidos", "Atenção", JOptionPane.WARNING_MESSAGE);
                DefaultTableModel model = (DefaultTableModel) tbImportacao.getModel();
                model.setRowCount(0);
                erro = true;
                return;
            }
        }

        JOptionPane.showMessageDialog(null, "Os dados estão corretos e prontos para serem importados!");
        btnCorrigirHoras.setEnabled(false);
        btnSalvarNovasHoras.setEnabled(true);
    }

    public boolean update = false;
    
    public void salvarNovasHoras() {

        String sqlObterParametros = "select data, funcionario_matricula, horas, performance, ft from efi_atividades JOIN efi_funcionarios ON efi_atividades.ft = funcionario_nome where funcionario_matricula = ? and data = ?";

        try {
            conn = ModuloConexao.conector();
            pst = conn.prepareStatement(sqlObterParametros);

            for (int i = 0; i < tbImportacao.getRowCount(); i++) {
                String data = tbImportacao.getValueAt(i, 0).toString();
                String dia = data.substring(0, 2);
                String mes = data.substring(3, 5);
                String ano = data.substring(6, 10);
                String dataENG = (ano + "-" + mes + "-" + dia);

                String matricula = tbImportacao.getValueAt(i, 1).toString();
                String horas = tbImportacao.getValueAt(i, 2).toString();

                String nomeDoBanco = null;
                String matriculaDoBanco = null;
                String horasDoBanco = null;
                String performanceDoBanco = null;
                String dataDoBanco = null;

                pst.setString(1, matricula); // aqui deve ser passado a variavel matricula
                pst.setString(2, dataENG); // aqui deve ser a variavel dataENG
                rs = pst.executeQuery();

                if (rs != null && rs.next()) {
                    //System.out.println(rs.getString(3));
                    nomeDoBanco = rs.getString(5);
                    matriculaDoBanco = rs.getString(2);
                    horasDoBanco = rs.getString(3);
                    performanceDoBanco = rs.getString(4);
                    dataDoBanco = rs.getString(1);

                    System.out.println("Dados da consulta: " + dataENG + " " + matricula);
                    System.out.println("Dados do banco: " + dataDoBanco + " " + matriculaDoBanco + " " + horasDoBanco + " " + performanceDoBanco + " " + nomeDoBanco);

                    try {
                        double ef = Double.parseDouble(performanceDoBanco);
                        double hr = Double.parseDouble(horasDoBanco);
                        double newhr = Double.parseDouble(horas);

                        // Cálculo correto da nova eficiência
                        double performance2 = (ef * hr) / newhr;

                        String sqlSubstituirHoras = "UPDATE efi_atividades SET horas = ?, performance = ? WHERE data = ? AND ft = ?";

                        try (PreparedStatement pst = conn.prepareStatement(sqlSubstituirHoras)) {
                            pst.setDouble(1, newhr);  // Atualiza as horas trabalhadas
                            pst.setDouble(2, performance2);    // Atualiza a performance corrigida
                            pst.setString(3, dataENG);
                            pst.setString(4, nomeDoBanco);

                            int linhasAfetadas = pst.executeUpdate();
                            if (linhasAfetadas > 0) {
                                            update = true;
                            } else {
                                System.out.println("Nenhum registro encontrado para atualizar.");
                            }
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, "Erro ao atualizar dados: " + e.getMessage());
                        e.printStackTrace();
                    }

                    System.out.println("PROXIMA LINHA");
                } else {
                    System.out.println("Nao tem dados com esses parametros no banco (Data e matricula)");
                    System.out.println("PROXIMA LINHA");
                }

            }
            JOptionPane.showMessageDialog(null, "Dados importados com sucesso!");
            JOptionPane.showMessageDialog(null, "Não existem registros com esses parâmetros. Nenhum registro afetado!");
            if(update == true){
                JOptionPane.showMessageDialog(null, "Dados de performance modificados com sucesso!");
            }
            
            btnSalvarNovasHoras.setEnabled(false);
           
            this.dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Ocorreu um erro inesperado");
        }

        try {
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(TelaDeImportarHoras.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tbImportacao = new javax.swing.JTable();
        btnEscolherArquivo = new javax.swing.JButton();
        btnCorrigirHoras = new javax.swing.JButton();
        btnSalvarNovasHoras = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Importação de Horas");
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

        tbImportacao.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Data", "Matrícula - Funcionário", "Horas Trabalhadas"
            }
        ));
        jScrollPane1.setViewportView(tbImportacao);

        btnEscolherArquivo.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnEscolherArquivo.setText("Escolher Arquivo");
        btnEscolherArquivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEscolherArquivoActionPerformed(evt);
            }
        });

        btnCorrigirHoras.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnCorrigirHoras.setText("Corrigir horas");
        btnCorrigirHoras.setEnabled(false);
        btnCorrigirHoras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCorrigirHorasActionPerformed(evt);
            }
        });

        btnSalvarNovasHoras.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnSalvarNovasHoras.setText("Importar");
        btnSalvarNovasHoras.setEnabled(false);
        btnSalvarNovasHoras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarNovasHorasActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 2, 24)); // NOI18N
        jLabel1.setText("Importação de Horas do RH");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(207, 207, 207)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnEscolherArquivo, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnCorrigirHoras, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnSalvarNovasHoras, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 662, Short.MAX_VALUE))
                        .addGap(195, 195, 195))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(96, 96, 96)
                .addComponent(jLabel1)
                .addGap(36, 36, 36)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEscolherArquivo, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCorrigirHoras, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSalvarNovasHoras, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                .addGap(141, 141, 141))
        );

        setBounds(0, 0, 1076, 663);
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked

    }//GEN-LAST:event_formMouseClicked

    private void btnEscolherArquivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEscolherArquivoActionPerformed
        erro = false;
        DefaultTableModel model = (DefaultTableModel) tbImportacao.getModel();
        model.setRowCount(0);
        importarHoras();
    }//GEN-LAST:event_btnEscolherArquivoActionPerformed

    private void btnCorrigirHorasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCorrigirHorasActionPerformed
        corrigirHoras();
    }//GEN-LAST:event_btnCorrigirHorasActionPerformed

    private void btnSalvarNovasHorasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarNovasHorasActionPerformed
        salvarNovasHoras();
    }//GEN-LAST:event_btnSalvarNovasHorasActionPerformed

    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaDeImportarHoras().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCorrigirHoras;
    private javax.swing.JButton btnEscolherArquivo;
    private javax.swing.JButton btnSalvarNovasHoras;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tbImportacao;
    // End of variables declaration//GEN-END:variables
}
