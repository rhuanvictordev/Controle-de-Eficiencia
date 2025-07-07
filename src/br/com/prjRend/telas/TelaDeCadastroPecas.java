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

public class TelaDeCadastroPecas extends javax.swing.JInternalFrame {

    Connection conn = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    public TelaDeCadastroPecas() {
        initComponents();
        setarFonteDasTabelas();
    }

    public void setarFonteDasTabelas() {

        tblPecas.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tblPecas.setFont(new Font("Arial", Font.BOLD, 12));
        tblPecas.setRowHeight(20);

        tblPecas.setSelectionBackground(Color.BLACK);  // Cor de fundo da seleção (preto)
        tblPecas.setSelectionForeground(Color.WHITE);  // Cor do texto da seleção (branco)

    }
    
    
    public void adicionar() {

        String sql = "insert into efi_pecas(peca_descricao, peca_codigo) values (?,?)";

        try {
            conn = ModuloConexao.conector();

            pst = conn.prepareStatement(sql);
            pst.setString(1, txtDescricao.getText().toUpperCase());
            pst.setString(2, txtDescricao2.getText().toUpperCase());

            if (txtDescricao.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha os campos obrigatórios!");
            } else {
                int adicionado = pst.executeUpdate();
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Peça adicionada!");
                    
                    int confirma = JOptionPane.showConfirmDialog(null, "Continuar adicionando?", "Atenção", JOptionPane.YES_NO_OPTION);
                    
                    if(confirma == JOptionPane.YES_OPTION){
                        txtDescricao.requestFocus();
                    }
                    txtDescricao2.setText(null);
                    txtDescricao.setText(null);
                    txtOperacaoPesquisar.setText(null);
                    lblId.setText(null);
                    
                    conn.close();
                    pesquisarPecas();
                }
            }
            
        } 
        catch (java.sql.SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(null, "Já existe uma operação com esse código");
        }
        
        catch (Exception e) {
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
    
    
    public void pesquisarPecas(){
        
        conn = ModuloConexao.conector();
        String sql = "select peca_id as Id, peca_descricao AS Operacao, peca_codigo AS Descricao from efi_pecas where peca_descricao like ? and peca_codigo like ?";
        
        try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, txtOperacaoPesquisar.getText().toUpperCase() + "%");
            pst.setString(2, txtDescricaoPesquisar.getText().toUpperCase() + "%");
            rs = pst.executeQuery();
            
            tblPecas.setModel(DbUtils.resultSetToTableModel(rs));
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
    
        int setar = tblPecas.getSelectedRow();
        lblId.setText(tblPecas.getModel().getValueAt(setar, 0).toString());
        txtDescricao.setText(tblPecas.getModel().getValueAt(setar, 1).toString());
        txtDescricao2.setText(tblPecas.getModel().getValueAt(setar, 2).toString());
        btnCadastrarPeca.setEnabled(false);
    }
    
    public void alterar(){
        conn = ModuloConexao.conector();
        
        
        String sql = "update efi_pecas set peca_descricao = ?, peca_codigo = ? where peca_id = ?";
        try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, txtDescricao.getText().toUpperCase());
            pst.setString(2, txtDescricao2.getText().toUpperCase());
            pst.setString(3, lblId.getText());
            
            if (txtDescricao.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha os campos obrigatórios!");
            } else {
                int alterado = pst.executeUpdate();
                
                if(alterado > 0){
                    JOptionPane.showMessageDialog(null, "Dados da peça alterados!");
                    
                    txtDescricao.setText(null);
                    txtDescricao2.setText(null);
                    txtOperacaoPesquisar.setText(null);
                    lblId.setText(null);
                    btnCadastrarPeca.setEnabled(true);
                    conn.close();
                    pesquisarPecas();
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
        int confirma = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja remover esta peça?", "Atenção", JOptionPane.YES_NO_OPTION);
        if(confirma == JOptionPane.YES_OPTION){
            if (lblId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Selecione uma peça da tabela para remover");
            } else {
                String sql = "delete from efi_pecas where peca_id = ?";
            try {
                conn = ModuloConexao.conector();
                pst = conn.prepareStatement(sql);
                pst.setString(1, lblId.getText());
                int apagado = pst.executeUpdate();
                if(apagado > 0){
                JOptionPane.showMessageDialog(null, "Peça removida!");
                    
                    txtDescricao.setText(null);
                    txtDescricao2.setText(null);
                    txtOperacaoPesquisar.setText(null);
                    lblId.setText(null);
                    btnCadastrarPeca.setEnabled(true);
                    conn.close();
                    pesquisarPecas();
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
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblPecas = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtDescricao = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        lblId = new javax.swing.JTextField();
        btnCadastrarPeca = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        txtOperacaoPesquisar = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtDescricaoPesquisar = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtDescricao2 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Banco de Operações");
        setToolTipText("");
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

        tblPecas = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tblPecas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "OPERAÇÃO", "DESCRIÇÃO"
            }
        ));
        tblPecas.setFocusable(false);
        tblPecas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPecasMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblPecas);

        jLabel4.setText("* Campos obrigatórios");

        jLabel6.setText("* Operação");

        jLabel8.setText("* ID");

        lblId.setEditable(false);
        lblId.setBackground(new java.awt.Color(204, 204, 204));

        btnCadastrarPeca.setText("Adicionar");
        btnCadastrarPeca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCadastrarPecaActionPerformed(evt);
            }
        });

        jButton3.setText("Alterar");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Remover");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Segoe UI", 2, 24)); // NOI18N
        jLabel9.setText("Cadastro de Operações");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButton1.setText("Buscar");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });

        txtOperacaoPesquisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtOperacaoPesquisarActionPerformed(evt);
            }
        });
        txtOperacaoPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtOperacaoPesquisarKeyReleased(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setText("Operação");

        jLabel1.setText("Descrição");

        txtDescricaoPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDescricaoPesquisarKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtOperacaoPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDescricaoPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(txtOperacaoPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1)
                    .addComponent(txtDescricaoPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel10.setText("Consultar dados");

        jLabel7.setText("Descrição");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 975, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(jLabel8)
                                                .addGap(6, 6, 6))
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addComponent(jLabel7)
                                                    .addComponent(jLabel6))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(btnCadastrarPeca)
                                                .addGap(18, 18, 18)
                                                .addComponent(jButton3)
                                                .addGap(18, 18, 18)
                                                .addComponent(jButton4))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(lblId, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel4))
                                            .addComponent(txtDescricao2, javax.swing.GroupLayout.PREFERRED_SIZE, 528, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(36, 36, 36))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel9)
                .addGap(34, 34, 34)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(lblId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDescricao2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCadastrarPeca, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        setBounds(0, 0, 1076, 663);
    }// </editor-fold>//GEN-END:initComponents

    private void txtOperacaoPesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtOperacaoPesquisarKeyReleased
    pesquisarPecas();
    }//GEN-LAST:event_txtOperacaoPesquisarKeyReleased

    private void tblPecasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPecasMouseClicked
    setar_campos();
    }//GEN-LAST:event_tblPecasMouseClicked

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
    
    txtDescricao.setText(null);
    txtDescricao2.setText(null);
    txtOperacaoPesquisar.setText(null);
    txtDescricaoPesquisar.setText(null);
    lblId.setText(null);
    tblPecas.clearSelection();
    btnCadastrarPeca.setEnabled(true);
    }//GEN-LAST:event_formMouseClicked

    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
    pesquisarPecas();
    }//GEN-LAST:event_jButton1MouseClicked

    private void btnCadastrarPecaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCadastrarPecaActionPerformed
    adicionar();
    }//GEN-LAST:event_btnCadastrarPecaActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
    alterar();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
    remover();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void txtOperacaoPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtOperacaoPesquisarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtOperacaoPesquisarActionPerformed

    private void txtDescricaoPesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDescricaoPesquisarKeyReleased
    pesquisarPecas();
    }//GEN-LAST:event_txtDescricaoPesquisarKeyReleased

    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaDeCadastroPecas().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCadastrarPeca;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField lblId;
    private javax.swing.JTable tblPecas;
    private javax.swing.JTextField txtDescricao;
    private javax.swing.JTextField txtDescricao2;
    private javax.swing.JTextField txtDescricaoPesquisar;
    private javax.swing.JTextField txtOperacaoPesquisar;
    // End of variables declaration//GEN-END:variables
}
