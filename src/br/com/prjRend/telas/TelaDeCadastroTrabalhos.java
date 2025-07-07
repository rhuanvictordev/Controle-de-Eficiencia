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

public class TelaDeCadastroTrabalhos extends javax.swing.JInternalFrame {

    Connection conn = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    public TelaDeCadastroTrabalhos() {
        initComponents();
        setarFonteDasTabelas();
    }
    
    public void setarFonteDasTabelas() {

        tblTrabalhos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tblTrabalhos.setFont(new Font("Arial", Font.BOLD, 12));
        tblTrabalhos.setRowHeight(20);

        tblTrabalhos.setSelectionBackground(Color.BLACK);  // Cor de fundo da seleção (preto)
        tblTrabalhos.setSelectionForeground(Color.WHITE);  // Cor do texto da seleção (branco)
        
        
        tblVincularSetor.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tblVincularSetor.setFont(new Font("Arial", Font.BOLD, 12));
        tblVincularSetor.setRowHeight(20);

        tblVincularSetor.setSelectionBackground(Color.BLACK);  // Cor de fundo da seleção (preto)
        tblVincularSetor.setSelectionForeground(Color.WHITE);  // Cor do texto da seleção (branco)
        

    }
    
    public void adicionarTrabalho() {

        String sql = "insert into efi_trabalhos (setor_id, trabalho_nome) values (?,?)";

        try {
            conn = ModuloConexao.conector();
            pst = conn.prepareStatement(sql);
            pst.setString(1, lblIdSetor.getText());
            pst.setString(2, txtTrabalhoNome.getText().toUpperCase());
            
            
            if (txtTrabalhoNome.getText().isEmpty() || lblIdSetor.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios!");
            } else {
                int adicionado = pst.executeUpdate();
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Trabalho criado!");
                    int confirma = JOptionPane.showConfirmDialog(null, "Continuar adicionando trabalhos no mesmo setor?", "Atenção", JOptionPane.YES_NO_OPTION);
                    
                    if(confirma != JOptionPane.YES_OPTION){
                        lblIdSetor.setText(null);
                    }
                    
                    txtTrabalhoNome.setText(null);
                    
                    conn.close();
                    txtTrabalhoNome.requestFocus();
                    pesquisarTrabalhos();
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
    
    
    public void pesquisarTrabalhos(){
        
        conn = ModuloConexao.conector();
        
        String sql = "SELECT setor_nome AS Setor, trabalho_nome AS Trabalho FROM efi_trabalhos JOIN efi_setores ON efi_trabalhos.setor_id = efi_setores.setor_id WHERE trabalho_nome like ? and setor_nome like ?";
        
        try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, txtTrabalhosPesquisar.getText().toUpperCase() + "%");
            pst.setString(2, txtSetoresPesquisar.getText().toUpperCase() + "%");
            rs = pst.executeQuery();
            tblTrabalhos.setModel(DbUtils.resultSetToTableModel(rs));
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
    
    public void pesquisarSetores(){
        
        conn = ModuloConexao.conector();
        
        String sql = "select setor_id AS Id, setor_nome AS Nome from efi_setores";
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            tblVincularSetor.setModel(DbUtils.resultSetToTableModel(rs));
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
    
    public void setar_campos(){
        conn = ModuloConexao.conector();
        int setar = tblTrabalhos.getSelectedRow();
        txtTrabalhoNome.setText(tblTrabalhos.getModel().getValueAt(setar, 1).toString());
        String sqlIdTrabalho = "select trabalho_id from efi_trabalhos where trabalho_nome = ?";
        String sqlIdSetor = "select setor_id from efi_trabalhos where trabalho_nome = ?";
        
        
        try {
            pst = conn.prepareStatement(sqlIdTrabalho);
            pst.setString(1, tblTrabalhos.getModel().getValueAt(setar, 1).toString());
            rs = pst.executeQuery();

            if (rs.next()) {
                lblIdTrabalho.setText(rs.getString(1));
            }

            pst = null;

            pst = conn.prepareStatement(sqlIdSetor);
            pst.setString(1, tblTrabalhos.getModel().getValueAt(setar, 1).toString());
            rs = pst.executeQuery();

            if (rs.next()) {
                lblIdSetor.setText(rs.getString(1));
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
        
        btnCadastrarTrabalho.setEnabled(false);
    }
    
    public void editarTrabalho(){
        conn = ModuloConexao.conector();
        
        
        String sql = "update efi_trabalhos set setor_id = ?, trabalho_nome = ? where trabalho_id = ?";
        try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, lblIdSetor.getText());
            pst.setString(2, txtTrabalhoNome.getText().toUpperCase());
            pst.setString(3, lblIdTrabalho.getText());
            
            if (txtTrabalhoNome.getText().isEmpty() || lblIdSetor.getText().isEmpty() || lblIdTrabalho.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Selecione um trabalho na tabela!");
            } else {
                int alterado = pst.executeUpdate();
                conn.close();
                if(alterado > 0){
                    JOptionPane.showMessageDialog(null, "Alterado dados do trabalho!");
                    txtTrabalhoNome.setText(null);
              
                    txtTrabalhosPesquisar.setText(null);
                    lblIdTrabalho.setText(null);
                    lblIdSetor.setText(null);
                    btnCadastrarTrabalho.setEnabled(true);
                    pesquisarTrabalhos();
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
    
    public void removerTrabalho(){
        int confirma = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja remover este trabalho?", "Atenção", JOptionPane.YES_NO_OPTION);
        if(confirma == JOptionPane.YES_OPTION){
            if (lblIdSetor.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Selecione um trabalho na tabela para remover");
            } else {
                String sql = "delete from efi_trabalhos where trabalho_id = ?";
            try {
                conn = ModuloConexao.conector();
                pst = conn.prepareStatement(sql);
                pst.setString(1, lblIdTrabalho.getText());
                int apagado = pst.executeUpdate();
                if(apagado > 0){
                JOptionPane.showMessageDialog(null, "trabalho removido!");
                    txtTrabalhoNome.setText(null);
                    
                    txtTrabalhosPesquisar.setText(null);
                    lblIdTrabalho.setText(null);
                    lblIdSetor.setText(null);
                    btnCadastrarTrabalho.setEnabled(true);
                    pesquisarTrabalhos();
                }
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Você não pode remover este trabalho porque ele mantém a integridade do banco!");
                lblIdSetor.setText(null);
                txtTrabalhoNome.setText(null);
                btnCadastrarTrabalho.setEnabled(true);
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

        jLabel8 = new javax.swing.JLabel();
        lblIdSetor = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblVincularSetor = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblTrabalhos = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblIdTrabalho = new javax.swing.JTextField();
        txtTrabalhoNome = new javax.swing.JTextField();
        btnCadastrarTrabalho = new javax.swing.JButton();
        btnAlterarTrabalho = new javax.swing.JButton();
        btnRemoverTrabalho = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        txtSetoresPesquisar = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtTrabalhosPesquisar = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Banco de Trabalhos");
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

        jLabel8.setText("* ID Setor");

        lblIdSetor.setEditable(false);
        lblIdSetor.setBackground(new java.awt.Color(204, 204, 204));

        tblVincularSetor = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tblVincularSetor.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "NOME"
            }
        ));
        tblVincularSetor.setFocusable(false);
        tblVincularSetor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblVincularSetorMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblVincularSetor);

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/prjRend/icones/search_icon.png"))); // NOI18N
        jLabel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel5MouseClicked(evt);
            }
        });

        jLabel7.setText("Tabela de Setores");

        tblTrabalhos = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tblTrabalhos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SETOR", "TRABALHO"
            }
        ));
        tblTrabalhos.setFocusable(false);
        tblTrabalhos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblTrabalhosMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tblTrabalhos);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel10.setText("* ID Trabalho");

        jLabel6.setText("* Nome");

        lblIdTrabalho.setEditable(false);
        lblIdTrabalho.setBackground(new java.awt.Color(204, 204, 204));

        btnCadastrarTrabalho.setText("Adicionar");
        btnCadastrarTrabalho.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCadastrarTrabalho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCadastrarTrabalhoActionPerformed(evt);
            }
        });

        btnAlterarTrabalho.setText("Editar");
        btnAlterarTrabalho.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAlterarTrabalho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAlterarTrabalhoActionPerformed(evt);
            }
        });

        btnRemoverTrabalho.setText("Remover");
        btnRemoverTrabalho.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRemoverTrabalho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverTrabalhoActionPerformed(evt);
            }
        });

        jLabel4.setText("* Campos obrigatórios");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnCadastrarTrabalho)
                        .addGap(18, 18, 18)
                        .addComponent(btnAlterarTrabalho, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnRemoverTrabalho)
                        .addGap(0, 73, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(lblIdTrabalho, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel4))
                            .addComponent(txtTrabalhoNome))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(lblIdTrabalho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtTrabalhoNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCadastrarTrabalho, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAlterarTrabalho, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRemoverTrabalho, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jLabel1.setFont(new java.awt.Font("Segoe UI", 2, 24)); // NOI18N
        jLabel1.setText("Cadastro de Trabalhos");

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtSetoresPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSetoresPesquisarKeyReleased(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setText("Setor");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setText("Trabalho");

        txtTrabalhosPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTrabalhosPesquisarKeyReleased(evt);
            }
        });

        jButton1.setText("Buscar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSetoresPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTrabalhosPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap(15, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(txtTrabalhosPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton1))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(txtSetoresPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel9.setText("Consultar dados");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 575, Short.MAX_VALUE)
                        .addGap(26, 26, 26))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblIdSetor, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(48, 48, 48))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addComponent(jLabel1)
                        .addGap(1, 1, 1)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(6, 6, 6)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel7)
                                        .addComponent(jLabel8)))
                                .addComponent(lblIdSetor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel5))
                        .addGap(12, 12, 12)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(114, 114, 114)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addGap(85, 85, 85))
        );

        setBounds(0, 0, 1076, 663);
    }// </editor-fold>//GEN-END:initComponents

    private void txtTrabalhosPesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTrabalhosPesquisarKeyReleased
    pesquisarTrabalhos();
    }//GEN-LAST:event_txtTrabalhosPesquisarKeyReleased

    private void btnCadastrarTrabalhoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCadastrarTrabalhoActionPerformed
    adicionarTrabalho();
    }//GEN-LAST:event_btnCadastrarTrabalhoActionPerformed

    private void tblVincularSetorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVincularSetorMouseClicked
        int setar = tblVincularSetor.getSelectedRow();
        lblIdSetor.setText(tblVincularSetor.getModel().getValueAt(setar, 0).toString());
    }//GEN-LAST:event_tblVincularSetorMouseClicked

    private void jLabel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5MouseClicked
    pesquisarSetores();
    }//GEN-LAST:event_jLabel5MouseClicked

    private void tblTrabalhosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblTrabalhosMouseClicked
    setar_campos();
    }//GEN-LAST:event_tblTrabalhosMouseClicked

    private void btnAlterarTrabalhoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlterarTrabalhoActionPerformed
    editarTrabalho();
    }//GEN-LAST:event_btnAlterarTrabalhoActionPerformed

    private void btnRemoverTrabalhoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverTrabalhoActionPerformed
    removerTrabalho();    // TODO add your handling code here:
    }//GEN-LAST:event_btnRemoverTrabalhoActionPerformed

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
    
    txtTrabalhoNome.setText(null);
    txtTrabalhosPesquisar.setText(null);
    txtSetoresPesquisar.setText(null);
    lblIdSetor.setText(null);
    lblIdTrabalho.setText(null);
    tblTrabalhos.clearSelection();
    tblVincularSetor.clearSelection();
    btnCadastrarTrabalho.setEnabled(true);
    }//GEN-LAST:event_formMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    pesquisarTrabalhos();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txtSetoresPesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSetoresPesquisarKeyReleased
     pesquisarTrabalhos();
    }//GEN-LAST:event_txtSetoresPesquisarKeyReleased

    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaDeCadastroTrabalhos().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAlterarTrabalho;
    private javax.swing.JButton btnCadastrarTrabalho;
    private javax.swing.JButton btnRemoverTrabalho;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField lblIdSetor;
    private javax.swing.JTextField lblIdTrabalho;
    private javax.swing.JTable tblTrabalhos;
    private javax.swing.JTable tblVincularSetor;
    private javax.swing.JTextField txtSetoresPesquisar;
    private javax.swing.JTextField txtTrabalhoNome;
    private javax.swing.JTextField txtTrabalhosPesquisar;
    // End of variables declaration//GEN-END:variables
}
