
package br.com.prjRend.telas;

import br.com.prjRend.dal.ModuloConexao;
import java.awt.Color;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;

public class AddFuncionario extends javax.swing.JFrame {

    private TelaDeEficiencia telaDeEficiencia;
    private Teclado teclado;
    
    public AddFuncionario(TelaDeEficiencia telaDeEficiencia) {
        initComponents();
        this.telaDeEficiencia = telaDeEficiencia;
        setarFonteDaTabela();
    }
    
    public void setarFonteDaTabela(){
        tblFuncionarios.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tblFuncionarios.setFont(new Font("Arial", Font.BOLD, 12));
        tblFuncionarios.setRowHeight(40);
        
        tblFuncionarios.setSelectionBackground(Color.BLACK);  // Cor de fundo da seleção (preto)
        tblFuncionarios.setSelectionForeground(Color.WHITE);  // Cor do texto da seleção (branco)
    }

    Connection conn = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
 
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtNome = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblFuncionarios = new javax.swing.JTable();
        AdicionarFuncionario = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        AdicionarFuncionario1 = new javax.swing.JButton();
        AdicionarFuncionario2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Pesquisa de funcionários");
        setAlwaysOnTop(true);
        setResizable(false);

        txtNome.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        txtNome.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtNomeMouseClicked(evt);
            }
        });
        txtNome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNomeActionPerformed(evt);
            }
        });

        tblFuncionarios = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tblFuncionarios.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        tblFuncionarios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nome"
            }
        ));
        jScrollPane1.setViewportView(tblFuncionarios);

        AdicionarFuncionario.setBackground(new java.awt.Color(0, 0, 0));
        AdicionarFuncionario.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        AdicionarFuncionario.setForeground(new java.awt.Color(255, 255, 255));
        AdicionarFuncionario.setText("+ Adicionar");
        AdicionarFuncionario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AdicionarFuncionarioActionPerformed(evt);
            }
        });

        jLabel1.setText("Nome:");

        AdicionarFuncionario1.setBackground(new java.awt.Color(0, 0, 0));
        AdicionarFuncionario1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        AdicionarFuncionario1.setForeground(new java.awt.Color(255, 255, 255));
        AdicionarFuncionario1.setText("Teclado");
        AdicionarFuncionario1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AdicionarFuncionario1ActionPerformed(evt);
            }
        });

        AdicionarFuncionario2.setBackground(new java.awt.Color(204, 0, 0));
        AdicionarFuncionario2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        AdicionarFuncionario2.setForeground(new java.awt.Color(255, 255, 255));
        AdicionarFuncionario2.setText("Fechar Tela");
        AdicionarFuncionario2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        AdicionarFuncionario2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AdicionarFuncionario2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(txtNome)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(AdicionarFuncionario1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(37, 37, 37)
                        .addComponent(AdicionarFuncionario2, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE))
                    .addComponent(AdicionarFuncionario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(AdicionarFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 491, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AdicionarFuncionario1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(AdicionarFuncionario2, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtNomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNomeActionPerformed
    buscarFuncionarios();
    }//GEN-LAST:event_txtNomeActionPerformed

    private void AdicionarFuncionarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AdicionarFuncionarioActionPerformed
    int linhaSelecionada = tblFuncionarios.getSelectedRow();
    String texto = null;

    if (linhaSelecionada != -1) { 
        Object valor = tblFuncionarios.getValueAt(linhaSelecionada, 0);
        texto = valor.toString();
    } else {
        
        return; // Interrompe o código caso nenhum funcionário esteja selecionado
    }

    DefaultTableModel modelTEfi = (DefaultTableModel) this.telaDeEficiencia.tblFunc.getModel();

    // Verifica se o funcionário já está na tabela
    for (int i = 0; i < modelTEfi.getRowCount(); i++) {
        Object obj = modelTEfi.getValueAt(i, 0);
        if (obj != null && obj.toString().equals(texto)) {
            
            return;
        }
    }

    // Se não encontrou duplicata, adiciona
    modelTEfi.addRow(new Object[]{texto});

    txtNome.setText(null);
    buscarFuncionarios();
    
    }//GEN-LAST:event_AdicionarFuncionarioActionPerformed

    private void txtNomeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtNomeMouseClicked
    
    }//GEN-LAST:event_txtNomeMouseClicked

    private void AdicionarFuncionario1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AdicionarFuncionario1ActionPerformed
    teclado = new Teclado(this);
    teclado.setVisible(true);
    }//GEN-LAST:event_AdicionarFuncionario1ActionPerformed

    private void AdicionarFuncionario2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AdicionarFuncionario2ActionPerformed
    
    if(teclado != null){
        teclado.dispose();
    }
    
    this.dispose();
    }//GEN-LAST:event_AdicionarFuncionario2ActionPerformed

    public void buscarFuncionarios(){
        
        String nome = txtNome.getText().toString().trim().toUpperCase();
        
        try {
            conn = ModuloConexao.conector();

        String sql = "select funcionario_nome AS Nome from efi_funcionarios JOIN efi_setores ON efi_funcionarios.setor_id = efi_setores.setor_id where funcionario_nome like ? and setor_nome = ? order by funcionario_nome";

        try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, nome + "%");
            pst.setString(2, this.telaDeEficiencia.lblSetorNome.getText());
            rs = pst.executeQuery();
            tblFuncionarios.setModel(DbUtils.resultSetToTableModel(rs));
            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
        finally {
            try {
                if (pst != null) {
                    pst.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Erro ao fechar conexão: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
        
    }
    
    
    public static void main(String args[]) {
 
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AdicionarFuncionario;
    public javax.swing.JButton AdicionarFuncionario1;
    public javax.swing.JButton AdicionarFuncionario2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblFuncionarios;
    public javax.swing.JTextField txtNome;
    // End of variables declaration//GEN-END:variables
}
