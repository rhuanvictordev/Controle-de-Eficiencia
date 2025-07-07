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

public class TelaDeCadastroLinhas extends javax.swing.JInternalFrame {

    Connection conn = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    public TelaDeCadastroLinhas() {
        initComponents();
        setarFonteDasTabelas();
    }
    
    public void setarFonteDasTabelas() {

        tblLinhas.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tblLinhas.setFont(new Font("Arial", Font.BOLD, 12));
        tblLinhas.setRowHeight(20);

        tblLinhas.setSelectionBackground(Color.BLACK);  // Cor de fundo da seleção (preto)
        tblLinhas.setSelectionForeground(Color.WHITE);  // Cor do texto da seleção (branco)

    }

    
    public void adicionarLinha() {

        String sql = "insert into efi_linhas (linha_nome) values (?)";

        try {
            conn = ModuloConexao.conector();
            pst = conn.prepareStatement(sql);
            pst.setString(1, txtLinhaNome.getText().toUpperCase().trim());
            if (txtLinhaNome.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha os campos obrigatórios!");
            } else {
                int adicionado = pst.executeUpdate();
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Linha adicionada!");
                    txtLinhaNome.setText(null);
                    conn.close();
                    pesquisarLinhas();
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
        txtLinhaNome.requestFocus();
    }
    
    
    public void pesquisarLinhas(){
        
        conn = ModuloConexao.conector();
        String sql = "select linha_nome as Nome from efi_linhas where linha_nome like ?";
        
        try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, txtLinhasPesquisar.getText().toUpperCase() + "%");
            rs = pst.executeQuery();
            tblLinhas.setModel(DbUtils.resultSetToTableModel(rs));
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
    int setar = tblLinhas.getSelectedRow();
    String sql = "select linha_id from efi_linhas where linha_nome = ?";
    txtLinhaNome.setText(tblLinhas.getModel().getValueAt(setar, 0).toString());
    
    try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, tblLinhas.getModel().getValueAt(setar, 0).toString());
            rs = pst.executeQuery();

            if (rs.next()) {
                lblLinhaId.setText(rs.getString(1));
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
        
        conn = ModuloConexao.conector();
        
        String sql = "update efi_linhas set linha_nome = ? where linha_id=?";
        
        try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, txtLinhaNome.getText().toUpperCase().trim());
            pst.setString(2, lblLinhaId.getText());
            
            if (txtLinhaNome.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Selecione uma linha na tabela!");
            } else {
                int alterado = pst.executeUpdate();
                if(alterado > 0){
                    JOptionPane.showMessageDialog(null, "Alterado dados da linha");
                    
                    conn.close();
                    lblLinhaId.setText(null);
                    txtLinhaNome.setText(null);
                    btnCadastrarLinha.setEnabled(true);
                    pesquisarLinhas();
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
        int confirma = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja remover esta linha?", "Atenção", JOptionPane.YES_NO_OPTION);
        if(confirma == JOptionPane.YES_OPTION){
            if (lblLinhaId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Selecione uma linha na tabela para remover");
            } else {
                String sql = "delete from efi_linhas where linha_id = ?";
            try {
                conn = ModuloConexao.conector();
                pst = conn.prepareStatement(sql);
                pst.setString(1, lblLinhaId.getText());
                int apagado = pst.executeUpdate();
                if(apagado > 0){
                JOptionPane.showMessageDialog(null, "Linha removida!");
                    lblLinhaId.setText(null);
                    txtLinhaNome.setText(null);
                    pesquisarLinhas();
                }
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Ocorreu um erro ao tentar remover a linha. " + e.getMessage());
                lblLinhaId.setText(null);
                txtLinhaNome.setText(null);
                btnCadastrarLinha.setEnabled(true);
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
        tblLinhas = new javax.swing.JTable();
        btnCadastrarLinha = new javax.swing.JButton();
        btnAlterarLinha = new javax.swing.JButton();
        btnRemoverLinha = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtLinhasPesquisar = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtLinhaNome = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        lblLinhaId = new javax.swing.JTextField();
        btnPesquisarLinhas = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Banco de Linhas de Produção");
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

        tblLinhas = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tblLinhas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblLinhas.setFocusable(false);
        tblLinhas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblLinhasMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblLinhas);

        btnCadastrarLinha.setText("Adicionar");
        btnCadastrarLinha.setToolTipText("Adicionar Peça");
        btnCadastrarLinha.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCadastrarLinha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCadastrarLinhaActionPerformed(evt);
            }
        });

        btnAlterarLinha.setText("Editar");
        btnAlterarLinha.setToolTipText("Alterar dados da Peça");
        btnAlterarLinha.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAlterarLinha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAlterarLinhaActionPerformed(evt);
            }
        });

        btnRemoverLinha.setText("Remover");
        btnRemoverLinha.setToolTipText("Remover Peça");
        btnRemoverLinha.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRemoverLinha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverLinhaActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setText("Consulta por Nome");

        txtLinhasPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtLinhasPesquisarKeyReleased(evt);
            }
        });

        jLabel4.setText("* Campos obrigatórios");

        jLabel6.setText("* Nome");

        jLabel8.setText("* ID");

        lblLinhaId.setEditable(false);
        lblLinhaId.setBackground(new java.awt.Color(204, 204, 204));

        btnPesquisarLinhas.setText("Buscar");
        btnPesquisarLinhas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesquisarLinhasActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 2, 24)); // NOI18N
        jLabel3.setText("Cadastro de Linhas de Produção");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 984, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtLinhasPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnPesquisarLinhas)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel4)))
                        .addGap(40, 40, 40))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel6))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(lblLinhaId, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(txtLinhaNome)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnCadastrarLinha)
                                .addGap(18, 18, 18)
                                .addComponent(btnAlterarLinha)
                                .addGap(18, 18, 18)
                                .addComponent(btnRemoverLinha))
                            .addComponent(jLabel3))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(jLabel3)
                .addGap(61, 61, 61)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtLinhasPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPesquisarLinhas)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                .addGap(60, 60, 60)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(lblLinhaId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtLinhaNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(btnCadastrarLinha, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAlterarLinha, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRemoverLinha, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(99, 99, 99))
        );

        setBounds(0, 0, 1076, 663);
    }// </editor-fold>//GEN-END:initComponents

    private void btnCadastrarLinhaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCadastrarLinhaActionPerformed
    adicionarLinha();
    }//GEN-LAST:event_btnCadastrarLinhaActionPerformed

    private void txtLinhasPesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtLinhasPesquisarKeyReleased
    pesquisarLinhas();
    }//GEN-LAST:event_txtLinhasPesquisarKeyReleased

    private void tblLinhasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblLinhasMouseClicked
    setar_campos();
    }//GEN-LAST:event_tblLinhasMouseClicked

    private void btnAlterarLinhaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlterarLinhaActionPerformed
    alterar();
    }//GEN-LAST:event_btnAlterarLinhaActionPerformed

    private void btnRemoverLinhaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverLinhaActionPerformed
    remover();
    }//GEN-LAST:event_btnRemoverLinhaActionPerformed

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
    txtLinhasPesquisar.setText(null);
    txtLinhaNome.setText(null);
    lblLinhaId.setText(null);
    tblLinhas.clearSelection();
    btnCadastrarLinha.setEnabled(true);
    }//GEN-LAST:event_formMouseClicked

    private void btnPesquisarLinhasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesquisarLinhasActionPerformed
    pesquisarLinhas();
    }//GEN-LAST:event_btnPesquisarLinhasActionPerformed

    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaDeCadastroLinhas().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAlterarLinha;
    private javax.swing.JButton btnCadastrarLinha;
    private javax.swing.JButton btnPesquisarLinhas;
    private javax.swing.JButton btnRemoverLinha;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField lblLinhaId;
    private javax.swing.JTable tblLinhas;
    private javax.swing.JTextField txtLinhaNome;
    private javax.swing.JTextField txtLinhasPesquisar;
    // End of variables declaration//GEN-END:variables
}
