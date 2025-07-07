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

public class TelaDeCadastroFuncionarios extends javax.swing.JInternalFrame {

    Connection conn = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    public TelaDeCadastroFuncionarios() {
        initComponents();
        setarFonteDasTabelas();
    }
    
    public void setarFonteDasTabelas() {

        tblFuncionarios.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tblFuncionarios.setFont(new Font("Arial", Font.BOLD, 12));
        tblFuncionarios.setRowHeight(20);

        tblFuncionarios.setSelectionBackground(Color.BLACK);  // Cor de fundo da seleção (preto)
        tblFuncionarios.setSelectionForeground(Color.WHITE);  // Cor do texto da seleção (branco)

        
        tblVincularSetor.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tblVincularSetor.setFont(new Font("Arial", Font.BOLD, 12));
        tblVincularSetor.setRowHeight(20);

        tblVincularSetor.setSelectionBackground(Color.BLACK);  // Cor de fundo da seleção (preto)
        tblVincularSetor.setSelectionForeground(Color.WHITE);
    }

    public void adicionarFuncionario() {

        String sql = "insert into efi_funcionarios (funcionario_matricula, funcionario_nome, setor_id) values (?,?,?)";

        try {
            conn = ModuloConexao.conector();
            pst = conn.prepareStatement(sql);
            pst.setString(1, txtFuncionarioMatricula.getText());
            pst.setString(2, txtFuncionarioNome.getText().toUpperCase());
            pst.setString(3, lblIdSetor.getText());

            if (txtFuncionarioNome.getText().isEmpty() || lblIdSetor.getText().isEmpty() || txtFuncionarioMatricula.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios!");
            } else {
                int adicionado = pst.executeUpdate();
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Dados salvos!");
                    int confirma = JOptionPane.showConfirmDialog(null, "Continuar adicionando operários no mesmo setor?", "Atenção", JOptionPane.YES_NO_OPTION);
                    
                    if(confirma != JOptionPane.YES_OPTION){
                        lblIdSetor.setText(null);
                    }
                    txtFuncionarioNome.requestFocus();
                    txtFuncionarioNome.setText(null);
                    txtFuncionarioMatricula.setText(null);
                    conn.close();
                    pesquisarFuncionarios();
                }
            }

        } 
        
        catch (java.sql.SQLIntegrityConstraintViolationException e){
            JOptionPane.showMessageDialog(null, "Já existe um funcionário com essa mesma matrícula!");
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

    public void pesquisarFuncionarios() {

        conn = ModuloConexao.conector();

        String sql = "select funcionario_matricula AS Matricula, funcionario_nome AS Nome, setor_nome AS Setor from efi_funcionarios JOIN efi_setores ON efi_funcionarios.setor_id = efi_setores.setor_id where funcionario_nome like ? and setor_nome like ? and funcionario_matricula like ?";

        try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, txtFuncionariosPesquisar.getText().toUpperCase() + "%");
            pst.setString(2, txtSetorPesquisar.getText().toUpperCase() + "%");
            pst.setString(3, txtMatriculaPesquisar.getText().toUpperCase() + "%");
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
        int setar = tblFuncionarios.getSelectedRow();
        String sqlIdFunc = "select funcionario_id from efi_funcionarios where funcionario_nome = ?";
        String sqlIdSetor = "select setor_id from efi_setores where setor_nome = ?";

        txtFuncionarioNome.setText(tblFuncionarios.getModel().getValueAt(setar, 1).toString());
        txtFuncionarioMatricula.setText(tblFuncionarios.getModel().getValueAt(setar, 0).toString());
        
        try {
            pst = conn.prepareStatement(sqlIdFunc);
            pst.setString(1, tblFuncionarios.getModel().getValueAt(setar, 1).toString());
            rs = pst.executeQuery();

            if (rs.next()) {
                lblIdFuncionario.setText(rs.getString(1));
            }

            pst = null;

            pst = conn.prepareStatement(sqlIdSetor);
            pst.setString(1, tblFuncionarios.getModel().getValueAt(setar, 2).toString());
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

        btnCadastrarFuncionario.setEnabled(false);
    }

    public void editarFuncionario() {
        conn = ModuloConexao.conector();

        String sql = "update efi_funcionarios set funcionario_matricula = ?, funcionario_nome = ?, setor_id = ? where funcionario_id = ?";
        try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, txtFuncionarioMatricula.getText());
            pst.setString(2, txtFuncionarioNome.getText().toUpperCase());
            pst.setString(3, lblIdSetor.getText());
            pst.setString(4, lblIdFuncionario.getText());

            if (txtFuncionarioNome.getText().isEmpty() || lblIdSetor.getText().isEmpty() || lblIdFuncionario.getText().isEmpty() || txtFuncionarioMatricula.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Selecione um funcionário na tabela!");
            } else {
                int alterado = pst.executeUpdate();
                conn.close();
                if (alterado > 0) {
                    JOptionPane.showMessageDialog(null, "Alterado dados do funcionário!");
                    txtFuncionarioNome.setText(null);
                    txtFuncionarioMatricula.setText(null);
                    txtFuncionariosPesquisar.setText(null);
                    lblIdFuncionario.setText(null);
                    lblIdSetor.setText(null);
                    btnCadastrarFuncionario.setEnabled(true);
                    pesquisarFuncionarios();
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

    public void removerFuncionario() {
        int confirma = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja remover este funcionário?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            if (lblIdFuncionario.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Selecione um funcionário na tabela para remover");
            } else {
                String sql = "delete from efi_funcionarios where funcionario_id = ?";
                try {
                    conn = ModuloConexao.conector();
                    pst = conn.prepareStatement(sql);
                    pst.setString(1, lblIdFuncionario.getText());
                    int apagado = pst.executeUpdate();
                    if (apagado > 0) {
                        JOptionPane.showMessageDialog(null, "funcionário removido!");
                        txtFuncionarioNome.setText(null);
                        txtFuncionarioMatricula.setText(null);
                        txtFuncionariosPesquisar.setText(null);
                        lblIdFuncionario.setText(null);
                        lblIdSetor.setText(null);
                        btnCadastrarFuncionario.setEnabled(true);
                        pesquisarFuncionarios();
                    }

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Você não pode remover este trabalho porque ele mantém a integridade do banco!");
                    lblIdSetor.setText(null);
                    txtFuncionarioNome.setText(null);
                    btnCadastrarFuncionario.setEnabled(true);
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
        tblFuncionarios = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        lblIdFuncionario = new javax.swing.JTextField();
        txtFuncionarioNome = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtFuncionarioMatricula = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        btnAlterarFuncionario = new javax.swing.JButton();
        btnCadastrarFuncionario = new javax.swing.JButton();
        btnRemoverFuncionario = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtFuncionariosPesquisar = new javax.swing.JTextField();
        txtSetorPesquisar = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        txtMatriculaPesquisar = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Banco de Funcionários");
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameIconified(evt);
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });
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

        tblFuncionarios = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tblFuncionarios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "MATRICULA", "NOME", "SETOR"
            }
        ));
        tblFuncionarios.setFocusable(false);
        tblFuncionarios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblFuncionariosMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tblFuncionarios);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel10.setText("* ID Func");

        lblIdFuncionario.setEditable(false);
        lblIdFuncionario.setBackground(new java.awt.Color(204, 204, 204));

        jLabel6.setText("* Nome");

        jLabel1.setText("* Matrícula");

        btnAlterarFuncionario.setText("Editar");
        btnAlterarFuncionario.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAlterarFuncionario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAlterarFuncionarioActionPerformed(evt);
            }
        });

        btnCadastrarFuncionario.setText("Adicionar");
        btnCadastrarFuncionario.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCadastrarFuncionario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCadastrarFuncionarioActionPerformed(evt);
            }
        });

        btnRemoverFuncionario.setText("Remover");
        btnRemoverFuncionario.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRemoverFuncionario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverFuncionarioActionPerformed(evt);
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
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel10)
                            .addComponent(jLabel6)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtFuncionarioMatricula)
                            .addComponent(txtFuncionarioNome)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(lblIdFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel4))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnCadastrarFuncionario)
                        .addGap(18, 18, 18)
                        .addComponent(btnAlterarFuncionario)
                        .addGap(18, 18, 18)
                        .addComponent(btnRemoverFuncionario)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblIdFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10)))
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFuncionarioNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFuncionarioMatricula, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCadastrarFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAlterarFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRemoverFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel11.setFont(new java.awt.Font("Segoe UI", 2, 24)); // NOI18N
        jLabel11.setText("Cadastro de Funcionários");

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setText("Nome");

        txtFuncionariosPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFuncionariosPesquisarKeyReleased(evt);
            }
        });

        txtSetorPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSetorPesquisarKeyReleased(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel9.setText("Setor");

        jLabel3.setText("Buscar");
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel3MouseClicked(evt);
            }
        });

        jLabel12.setText("MATRICULA");

        txtMatriculaPesquisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMatriculaPesquisarActionPerformed(evt);
            }
        });
        txtMatriculaPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtMatriculaPesquisarKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtMatriculaPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFuncionariosPesquisar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSetorPesquisar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFuncionariosPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(txtSetorPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(jLabel12)
                    .addComponent(txtMatriculaPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel13.setText("Consultar dados");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE))
                        .addGap(37, 37, 37))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblIdSetor, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(83, 83, 83)
                        .addComponent(jLabel5))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(43, 43, 43))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(jLabel11)
                        .addGap(21, 21, 21)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(69, 69, 69)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel7)
                                .addComponent(jLabel8)
                                .addComponent(lblIdSetor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE)))
                .addGap(38, 38, 38))
        );

        setBounds(0, 0, 1076, 663);
    }// </editor-fold>//GEN-END:initComponents

    private void txtFuncionariosPesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFuncionariosPesquisarKeyReleased
        pesquisarFuncionarios();
    }//GEN-LAST:event_txtFuncionariosPesquisarKeyReleased

    private void btnCadastrarFuncionarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCadastrarFuncionarioActionPerformed
        adicionarFuncionario();
    }//GEN-LAST:event_btnCadastrarFuncionarioActionPerformed

    private void tblVincularSetorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVincularSetorMouseClicked
        int setar = tblVincularSetor.getSelectedRow();
        lblIdSetor.setText(tblVincularSetor.getModel().getValueAt(setar, 0).toString());
    }//GEN-LAST:event_tblVincularSetorMouseClicked

    private void tblFuncionariosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblFuncionariosMouseClicked
        setar_campos();
    }//GEN-LAST:event_tblFuncionariosMouseClicked

    private void btnAlterarFuncionarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlterarFuncionarioActionPerformed
        editarFuncionario();
    }//GEN-LAST:event_btnAlterarFuncionarioActionPerformed

    private void btnRemoverFuncionarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverFuncionarioActionPerformed
        removerFuncionario();    // TODO add your handling code here:
    }//GEN-LAST:event_btnRemoverFuncionarioActionPerformed

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        txtMatriculaPesquisar.setText(null);
        txtFuncionarioMatricula.setText(null);
        txtFuncionarioNome.setText(null);
        txtFuncionariosPesquisar.setText(null);
        txtSetorPesquisar.setText(null);
        lblIdSetor.setText(null);
        lblIdFuncionario.setText(null);
        tblFuncionarios.clearSelection();
        tblVincularSetor.clearSelection();
        btnCadastrarFuncionario.setEnabled(true);
    }//GEN-LAST:event_formMouseClicked

    private void jLabel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel3MouseClicked
    pesquisarFuncionarios();
    }//GEN-LAST:event_jLabel3MouseClicked

    private void jLabel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5MouseClicked
        pesquisarSetores();
    }//GEN-LAST:event_jLabel5MouseClicked

    private void txtSetorPesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSetorPesquisarKeyReleased
     pesquisarFuncionarios();
    }//GEN-LAST:event_txtSetorPesquisarKeyReleased

    private void txtMatriculaPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMatriculaPesquisarActionPerformed
    
    }//GEN-LAST:event_txtMatriculaPesquisarActionPerformed

    private void txtMatriculaPesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtMatriculaPesquisarKeyReleased
    pesquisarFuncionarios();
    }//GEN-LAST:event_txtMatriculaPesquisarKeyReleased

    private void formInternalFrameIconified(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameIconified
    
    }//GEN-LAST:event_formInternalFrameIconified

    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaDeCadastroFuncionarios().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAlterarFuncionario;
    private javax.swing.JButton btnCadastrarFuncionario;
    private javax.swing.JButton btnRemoverFuncionario;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JButton jLabel3;
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
    private javax.swing.JTextField lblIdFuncionario;
    private javax.swing.JTextField lblIdSetor;
    private javax.swing.JTable tblFuncionarios;
    private javax.swing.JTable tblVincularSetor;
    private javax.swing.JTextField txtFuncionarioMatricula;
    private javax.swing.JTextField txtFuncionarioNome;
    private javax.swing.JTextField txtFuncionariosPesquisar;
    private javax.swing.JTextField txtMatriculaPesquisar;
    private javax.swing.JTextField txtSetorPesquisar;
    // End of variables declaration//GEN-END:variables
}
