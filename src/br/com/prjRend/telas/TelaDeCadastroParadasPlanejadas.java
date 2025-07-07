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

public class TelaDeCadastroParadasPlanejadas extends javax.swing.JInternalFrame {

    Connection conn = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    public TelaDeCadastroParadasPlanejadas() {
        initComponents();
        setarFonteDasTabelas();
    }
    
    public void setarFonteDasTabelas() {

        tblPausas.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tblPausas.setFont(new Font("Arial", Font.BOLD, 12));
        tblPausas.setRowHeight(25);

        tblPausas.setSelectionBackground(Color.BLACK);  // Cor de fundo da seleção (preto)
        tblPausas.setSelectionForeground(Color.WHITE);  // Cor do texto da seleção (branco)
        
        tblVincularSetor.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tblVincularSetor.setFont(new Font("Arial", Font.BOLD, 12));
        tblVincularSetor.setRowHeight(20);

        tblVincularSetor.setSelectionBackground(Color.BLACK);  // Cor de fundo da seleção (preto)
        tblVincularSetor.setSelectionForeground(Color.WHITE);  // Cor do texto da seleção (branco)

    }

    public void adicionarPausa() {

        String sql = "insert into efi_pausas_planejadas (pausa_planejada_nome, setor_id, pausa_planejada_minutos) values (?,?,?)";

        try {
            conn = ModuloConexao.conector();
            pst = conn.prepareStatement(sql);
            pst.setString(1, txtPausaNome.getText().toUpperCase());
            pst.setString(2, lblIdSetor.getText());
            pst.setString(3, txtPausaMinutos.getText().replace(",", ".").replace("-", ""));

            if (txtPausaNome.getText().isEmpty() || lblIdSetor.getText().isEmpty() || txtPausaMinutos.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios!");
            } else {
                int adicionado = pst.executeUpdate();
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Pausa criada!");
                    
                        txtPausaNome.setText(null);
                        txtPausasPesquisar.setText(null);
                        txtPausaMinutos.setText(null);
                        lblIdPausa.setText(null);
                        lblIdSetor.setText(null);
                        conn.close();
                        pesquisarPausas();
                    

                }
            }

        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(null, "A pausa já existe!");
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
        
        tblVincularSetor.clearSelection();
    }

    public void pesquisarPausas() {

        conn = ModuloConexao.conector();

        String sql = "select pausa_planejada_nome AS Nome, setor_nome AS Setor, pausa_planejada_minutos AS Minutos from efi_pausas_planejadas JOIN efi_setores ON efi_pausas_planejadas.setor_id = efi_setores.setor_id where pausa_planejada_nome like ? and efi_setores.setor_nome like ?";

        try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, txtPausasPesquisar.getText().toUpperCase() + "%");
            pst.setString(2, txtPausasSetorPesquisar.getText().toUpperCase() + "%");
            rs = pst.executeQuery();
            tblPausas.setModel(DbUtils.resultSetToTableModel(rs));
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

   
    public void pesquisarSetores() {

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

    public void setar_campos() {
        conn = ModuloConexao.conector();
        int setar = tblPausas.getSelectedRow();
        txtPausaNome.setText(tblPausas.getModel().getValueAt(setar, 0).toString());
        txtPausaMinutos.setText(tblPausas.getModel().getValueAt(setar, 2).toString());
        String sqlIdPausa = "SELECT pausa_planejada_id FROM efi_pausas_planejadas JOIN efi_setores ON efi_pausas_planejadas.setor_id = efi_setores.setor_id where pausa_planejada_nome = ? and setor_nome = ?";
        String sqlIdSetor = "SELECT efi_setores.setor_id FROM efi_pausas_planejadas JOIN efi_setores ON efi_pausas_planejadas.setor_id = efi_setores.setor_id where pausa_planejada_nome = ? and setor_nome = ?";
        
        try {
            pst = conn.prepareStatement(sqlIdPausa);
            pst.setString(1, tblPausas.getModel().getValueAt(setar, 0).toString());
            pst.setString(2, tblPausas.getModel().getValueAt(setar, 1).toString());
            rs = pst.executeQuery();

            if (rs.next()) {
                lblIdPausa.setText(rs.getString(1));
            }

            pst = null;

            pst = conn.prepareStatement(sqlIdSetor);
            pst.setString(1, tblPausas.getModel().getValueAt(setar, 0).toString());
            pst.setString(2, tblPausas.getModel().getValueAt(setar, 1).toString());
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
        
        
        
        btnCadastrarPausa.setEnabled(false);
        tblVincularSetor.clearSelection();
    }

    public void editarPausa() {
        conn = ModuloConexao.conector();

        String sql = "update efi_pausas_planejadas set pausa_planejada_nome = ?, setor_id = ?, pausa_planejada_minutos = ? where pausa_planejada_id = ?";
        try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, txtPausaNome.getText().toUpperCase());
            pst.setString(2, lblIdSetor.getText());
            pst.setString(3, txtPausaMinutos.getText().replace(",", ".").replace("-", ""));
            pst.setString(4, lblIdPausa.getText());

            if (txtPausaNome.getText().isEmpty() || lblIdSetor.getText().isEmpty() || lblIdPausa.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Selecione uma pausa na tabela!");
            } else {
                int alterado = pst.executeUpdate();
                conn.close();
                if (alterado > 0) {
                    JOptionPane.showMessageDialog(null, "Alterado dados da pausa!");
                    txtPausaNome.setText(null);
                    txtPausasPesquisar.setText(null);
                    lblIdPausa.setText(null);
                    lblIdSetor.setText(null);
                    btnCadastrarPausa.setEnabled(true);
                    pesquisarPausas();
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
        tblVincularSetor.clearSelection();
    }

    public void removerPausa() {
        int confirma = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja remover esta pausa?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            if (lblIdSetor.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Selecione uma pausa na tabela para remover");
            } else {
                String sql = "delete from efi_pausas_planejadas where pausa_planejada_id = ?";
                try {
                    conn = ModuloConexao.conector();
                    pst = conn.prepareStatement(sql);
                    pst.setString(1, lblIdPausa.getText());
                    int apagado = pst.executeUpdate();
                    if (apagado > 0) {
                        JOptionPane.showMessageDialog(null, "pausa removida!");
                        txtPausaNome.setText(null);
                        txtPausasPesquisar.setText(null);
                        lblIdPausa.setText(null);
                        lblIdSetor.setText(null);
                        txtPausaMinutos.setText(null);
                        btnCadastrarPausa.setEnabled(true);
                        pesquisarPausas();
                    }

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Você não pode remover esta pausa porque ela mantém a integridade do banco!");
                    lblIdSetor.setText(null);
                    txtPausaNome.setText(null);
                    btnCadastrarPausa.setEnabled(true);
                    txtPausaMinutos.setText(null);
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
        tblVincularSetor.clearSelection();
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
        tblPausas = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        lblIdPausa = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtPausaNome = new javax.swing.JTextField();
        btnRemoverPausa = new javax.swing.JButton();
        btnAlterarPausa = new javax.swing.JButton();
        btnCadastrarPausa = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        txtPausaMinutos = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        txtPausasSetorPesquisar = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtPausasPesquisar = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Banco de Pausas Planejadas");
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

        tblPausas = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tblPausas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "NOME", "SETOR", "MINUTOS"
            }
        ));
        tblPausas.setFocusable(false);
        tblPausas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPausasMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tblPausas);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblIdPausa.setEditable(false);
        lblIdPausa.setBackground(new java.awt.Color(204, 204, 204));

        jLabel10.setText("* ID Pausa");

        jLabel4.setText("* Campos obrigatórios");

        jLabel6.setText("* Nome");

        btnRemoverPausa.setText("Remover");
        btnRemoverPausa.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRemoverPausa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverPausaActionPerformed(evt);
            }
        });

        btnAlterarPausa.setText("Editar");
        btnAlterarPausa.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAlterarPausa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAlterarPausaActionPerformed(evt);
            }
        });

        btnCadastrarPausa.setText("Adicionar");
        btnCadastrarPausa.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCadastrarPausa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCadastrarPausaActionPerformed(evt);
            }
        });

        jLabel1.setText("* Minutos");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnCadastrarPausa)
                        .addGap(18, 18, 18)
                        .addComponent(btnAlterarPausa)
                        .addGap(18, 18, 18)
                        .addComponent(btnRemoverPausa)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel10)
                            .addComponent(jLabel6)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(lblIdPausa, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel4))
                            .addComponent(txtPausaNome)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtPausaMinutos, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                                .addGap(214, 214, 214)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblIdPausa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel10))
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtPausaNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtPausaMinutos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCadastrarPausa, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAlterarPausa, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRemoverPausa, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16))
        );

        jLabel3.setFont(new java.awt.Font("Segoe UI", 2, 24)); // NOI18N
        jLabel3.setText("Cadastro de Pausas Planejadas");

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel9.setText("Setor");

        txtPausasSetorPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPausasSetorPesquisarKeyReleased(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setText("Nome");

        txtPausasPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPausasPesquisarKeyReleased(evt);
            }
        });

        jButton1.setText("Buscar");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPausasPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPausasSetorPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtPausasSetorPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtPausasPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addContainerGap())
        );

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel11.setText("Consultar dados");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane3))
                        .addGap(18, 18, 18)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblIdSetor, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(50, 50, 50))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jLabel3)
                        .addGap(26, 26, 26)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel8)
                                .addComponent(lblIdSetor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel7))
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)))
                .addGap(56, 56, 56))
        );

        setBounds(0, 0, 1076, 663);
    }// </editor-fold>//GEN-END:initComponents

    private void txtPausasPesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPausasPesquisarKeyReleased
        pesquisarPausas();
    }//GEN-LAST:event_txtPausasPesquisarKeyReleased

    private void btnCadastrarPausaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCadastrarPausaActionPerformed
        adicionarPausa();
    }//GEN-LAST:event_btnCadastrarPausaActionPerformed

    private void tblVincularSetorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVincularSetorMouseClicked
        int setar = tblVincularSetor.getSelectedRow();
        lblIdSetor.setText(tblVincularSetor.getModel().getValueAt(setar, 0).toString());
    }//GEN-LAST:event_tblVincularSetorMouseClicked

    private void jLabel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5MouseClicked
        pesquisarSetores();
    }//GEN-LAST:event_jLabel5MouseClicked

    private void tblPausasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPausasMouseClicked
        setar_campos();
    }//GEN-LAST:event_tblPausasMouseClicked

    private void btnAlterarPausaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlterarPausaActionPerformed
        editarPausa();
    }//GEN-LAST:event_btnAlterarPausaActionPerformed

    private void btnRemoverPausaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverPausaActionPerformed
        removerPausa();
    }//GEN-LAST:event_btnRemoverPausaActionPerformed

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        txtPausaNome.setText(null);
        txtPausasPesquisar.setText(null);
        txtPausasSetorPesquisar.setText(null);
        lblIdPausa.setText(null);
        lblIdSetor.setText(null);
        txtPausaMinutos.setText(null);
        tblPausas.clearSelection();
        btnCadastrarPausa.setEnabled(true);
    }//GEN-LAST:event_formMouseClicked

    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
    pesquisarPausas();
    }//GEN-LAST:event_jButton1MouseClicked

    private void txtPausasSetorPesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPausasSetorPesquisarKeyReleased
    pesquisarPausas();
    }//GEN-LAST:event_txtPausasSetorPesquisarKeyReleased

    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaDeCadastroParadasPlanejadas().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAlterarPausa;
    private javax.swing.JButton btnCadastrarPausa;
    private javax.swing.JButton btnRemoverPausa;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
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
    private javax.swing.JTextField lblIdPausa;
    private javax.swing.JTextField lblIdSetor;
    private javax.swing.JTable tblPausas;
    private javax.swing.JTable tblVincularSetor;
    private javax.swing.JTextField txtPausaMinutos;
    private javax.swing.JTextField txtPausaNome;
    private javax.swing.JTextField txtPausasPesquisar;
    private javax.swing.JTextField txtPausasSetorPesquisar;
    // End of variables declaration//GEN-END:variables
}
