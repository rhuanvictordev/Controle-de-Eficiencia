package br.com.prjRend.telas;

import br.com.prjRend.dal.ModuloConexao;
import java.awt.Color;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import net.proteanit.sql.DbUtils;

public class TelaDeCadastroSetores extends javax.swing.JInternalFrame {

    Connection conn = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    public TelaDeCadastroSetores() {
        initComponents();
        setarFonteDasTabelas();
    }
    
    public void setarFonteDasTabelas() {

        tblSetores.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tblSetores.setFont(new Font("Arial", Font.BOLD, 12));
        tblSetores.setRowHeight(20);

        tblSetores.setSelectionBackground(Color.BLACK);  // Cor de fundo da seleção (preto)
        tblSetores.setSelectionForeground(Color.WHITE);  // Cor do texto da seleção (branco)

    }

    
    public void adicionarSetor() {
        
        try {
            double tempoOp = Double.valueOf(txtSetorTempoOperacional.getText());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Informe o tempo operacional correto!");
            txtSetorTempoOperacional.setText(null);
            return;
        }

        String sql = "insert into efi_setores (setor_nome, setor_tipo, setor_tempo_operacional) values (?,?,?)";

        try {
            conn = ModuloConexao.conector();
            pst = conn.prepareStatement(sql);
            pst.setString(1, txtSetorNome.getText().toUpperCase());
            if(CheckBoxComposto.isSelected()){
            pst.setString(2, "COMPOSTO");
            } else {
            pst.setString(2, "INDIVIDUAL");
            }
            pst.setDouble(3, Double.parseDouble(txtSetorTempoOperacional.getText().toUpperCase().toString()));
            if (txtSetorNome.getText().isEmpty() || txtSetorTempoOperacional.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha os campos obrigatórios!");
            } else {
                int adicionado = pst.executeUpdate();
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Setor adicionado!");
                    txtSetorNome.setText(null);
                    txtSetorTempoOperacional.setText(null);
                    conn.close();
                    pesquisarSetores();
                }
            }
            
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
    }
    
    
    public void pesquisarSetores(){
        
        conn = ModuloConexao.conector();
        String sql = "select setor_nome as Nome, setor_tipo AS Tipo, setor_tempo_operacional AS Tempo from efi_setores where setor_nome like ?";
        
        try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, txtSetorPesquisar.getText().toUpperCase() + "%");
            rs = pst.executeQuery();
            tblSetores.setModel(DbUtils.resultSetToTableModel(rs));
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
//        definirLarguraDasColunas();
    }
    
    public void setar_campos(){
    conn = ModuloConexao.conector();
    int setar = tblSetores.getSelectedRow();
    String sql = "select setor_id from efi_setores where setor_nome = ?";
    String tipo = tblSetores.getModel().getValueAt(setar, 1).toString();
    
    if(tipo.equals("COMPOSTO")){
            CheckBoxComposto.setSelected(true);
        } else {
            CheckBoxComposto.setSelected(false);
        }
    
    txtSetorNome.setText(tblSetores.getModel().getValueAt(setar, 0).toString());
    txtSetorTempoOperacional.setText(tblSetores.getModel().getValueAt(setar, 2).toString());
    
    try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, tblSetores.getModel().getValueAt(setar, 0).toString());
            rs = pst.executeQuery();

            if (rs.next()) {
                lblSetorId.setText(rs.getString(1));
            }
            
            pst = null;
            rs = null;
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
    
    }
    
    
    public void alterar(){
        
        
        try {
            double tempoOp = Double.valueOf(txtSetorTempoOperacional.getText());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Informe o tempo operacional correto!");
            txtSetorTempoOperacional.setText(null);
            return;
        }
        
        
        
        conn = ModuloConexao.conector();
        
        
        String sql = "update efi_setores set setor_nome = ?, setor_tipo = ?, setor_tempo_operacional = ? where setor_id=?";
        try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, txtSetorNome.getText().toUpperCase());
            if(CheckBoxComposto.isSelected()){
            pst.setString(2, "COMPOSTO");
            } else {
            pst.setString(2, "INDIVIDUAL");
            }
            pst.setString(3, txtSetorTempoOperacional.getText());
            pst.setString(4, lblSetorId.getText());
            
            if (txtSetorNome.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Selecione um setor na tabela!");
            } else {
                int alterado = pst.executeUpdate();
                
                if(alterado > 0){
                    JOptionPane.showMessageDialog(null, "Alterado dados do setor!");
                    CheckBoxComposto.setSelected(false);
                    lblSetorId.setText(null);
                    txtSetorTempoOperacional.setText(null);
                    txtSetorNome.setText(null);
                    conn.close();
                    btnCadastrarSetor.setEnabled(true);
                    pesquisarSetores();
                }
            }
            
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
    }
    
    public void remover(){
        int confirma = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja remover este setor?", "Atenção", JOptionPane.YES_NO_OPTION);
        if(confirma == JOptionPane.YES_OPTION){
            if (lblSetorId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Selecione um setor na tabela para remover");
            } else {
                String sql = "delete from efi_setores where setor_id = ?";
            try {
                conn = ModuloConexao.conector();
                pst = conn.prepareStatement(sql);
                pst.setString(1, lblSetorId.getText());
                int apagado = pst.executeUpdate();
                if(apagado > 0){
                JOptionPane.showMessageDialog(null, "Setor removido!");
                    lblSetorId.setText(null);
                    txtSetorNome.setText(null);
                    pesquisarSetores();
                }
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Você não pode remover este setor porque ele é o responsável por manter a integridade do banco!");
                lblSetorId.setText(null);
                txtSetorNome.setText(null);
                btnCadastrarSetor.setEnabled(true);
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
                
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblSetores = new javax.swing.JTable();
        btnCadastrarSetor = new javax.swing.JButton();
        btnAlterarSetor = new javax.swing.JButton();
        btnRemoverSetor = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtSetorPesquisar = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtSetorNome = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        lblSetorId = new javax.swing.JTextField();
        CheckBoxComposto = new javax.swing.JCheckBox();
        jButton1 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        txtSetorTempoOperacional = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Banco de Setores");
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

        tblSetores = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tblSetores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "NOME", "TIPO", "TEMPO"
            }
        ));
        tblSetores.setFocusable(false);
        tblSetores.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblSetoresMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblSetores);

        btnCadastrarSetor.setText("Adicionar");
        btnCadastrarSetor.setToolTipText("Adicionar Peça");
        btnCadastrarSetor.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCadastrarSetor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCadastrarSetorActionPerformed(evt);
            }
        });

        btnAlterarSetor.setText("Editar");
        btnAlterarSetor.setToolTipText("Alterar dados da Peça");
        btnAlterarSetor.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAlterarSetor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAlterarSetorActionPerformed(evt);
            }
        });

        btnRemoverSetor.setText("Remover");
        btnRemoverSetor.setToolTipText("Remover Peça");
        btnRemoverSetor.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRemoverSetor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverSetorActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setText("Consulta por Nome");

        txtSetorPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSetorPesquisarKeyReleased(evt);
            }
        });

        jLabel4.setText("* Campos obrigatórios");

        jLabel6.setText("* Nome");

        jLabel8.setText("* ID");

        lblSetorId.setEditable(false);
        lblSetorId.setBackground(new java.awt.Color(204, 204, 204));

        CheckBoxComposto.setText("Composto");

        jButton1.setText("Buscar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel7.setText("* Tempo Operacional");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 2, 24)); // NOI18N
        jLabel3.setText("Cadastro de Setores");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnCadastrarSetor)
                                .addGap(18, 18, 18)
                                .addComponent(btnAlterarSetor)
                                .addGap(18, 18, 18)
                                .addComponent(btnRemoverSetor))
                            .addComponent(jLabel3)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel6))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(lblSetorId, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(CheckBoxComposto))
                                    .addComponent(txtSetorNome, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtSetorTempoOperacional, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 728, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 980, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtSetorPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton1)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel4)))
                        .addGap(40, 40, 40))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(jLabel3)
                .addGap(61, 61, 61)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtSetorPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(lblSetorId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CheckBoxComposto))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtSetorNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtSetorTempoOperacional, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(btnCadastrarSetor, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAlterarSetor, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRemoverSetor, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(99, 99, 99))
        );

        setBounds(0, 0, 1076, 663);
    }// </editor-fold>//GEN-END:initComponents

    private void btnCadastrarSetorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCadastrarSetorActionPerformed
        adicionarSetor();
    }//GEN-LAST:event_btnCadastrarSetorActionPerformed

    private void txtSetorPesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSetorPesquisarKeyReleased
    pesquisarSetores();
    }//GEN-LAST:event_txtSetorPesquisarKeyReleased

    private void tblSetoresMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSetoresMouseClicked
    setar_campos();
    }//GEN-LAST:event_tblSetoresMouseClicked

    private void btnAlterarSetorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlterarSetorActionPerformed
    alterar();
    }//GEN-LAST:event_btnAlterarSetorActionPerformed

    private void btnRemoverSetorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverSetorActionPerformed
    remover();
    }//GEN-LAST:event_btnRemoverSetorActionPerformed

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
    txtSetorTempoOperacional.setText(null);
    txtSetorPesquisar.setText(null);
    txtSetorNome.setText(null);
    lblSetorId.setText(null);
    tblSetores.clearSelection();
    CheckBoxComposto.setSelected(false);
    btnCadastrarSetor.setEnabled(true);
    }//GEN-LAST:event_formMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    pesquisarSetores();
    }//GEN-LAST:event_jButton1ActionPerformed

    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaDeCadastroSetores().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox CheckBoxComposto;
    private javax.swing.JButton btnAlterarSetor;
    private javax.swing.JButton btnCadastrarSetor;
    private javax.swing.JButton btnRemoverSetor;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField lblSetorId;
    private javax.swing.JTable tblSetores;
    private javax.swing.JTextField txtSetorNome;
    private javax.swing.JTextField txtSetorPesquisar;
    private javax.swing.JTextField txtSetorTempoOperacional;
    // End of variables declaration//GEN-END:variables
}
