package br.com.prjRend.telas;

import br.com.prjRend.dal.ModuloConexao;
import java.awt.Color;
import java.awt.Font;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import net.proteanit.sql.DbUtils;

public class TelaDeCadastroTakts extends javax.swing.JInternalFrame {

    Connection conn = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    public TelaDeCadastroTakts() {
        initComponents();
        setarFonteDasTabelas();
    }

    public void setarFonteDasTabelas() {

        tblTakts.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tblTakts.setFont(new Font("Arial", Font.BOLD, 12));
        tblTakts.setRowHeight(20);

        tblTakts.setSelectionBackground(Color.BLACK);  // Cor de fundo da seleção (preto)
        tblTakts.setSelectionForeground(Color.WHITE);  // Cor do texto da seleção (branco)

        tblSetores.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tblSetores.setFont(new Font("Arial", Font.BOLD, 12));
        tblSetores.setRowHeight(20);

        tblSetores.setSelectionBackground(Color.BLACK);  // Cor de fundo da seleção (preto)
        tblSetores.setSelectionForeground(Color.WHITE);

        tblPecas.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tblPecas.setFont(new Font("Arial", Font.BOLD, 12));
        tblPecas.setRowHeight(20);

        tblPecas.setSelectionBackground(Color.BLACK);  // Cor de fundo da seleção (preto)
        tblPecas.setSelectionForeground(Color.WHITE);

        tblTrabalhos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tblTrabalhos.setFont(new Font("Arial", Font.BOLD, 12));
        tblTrabalhos.setRowHeight(20);

        tblTrabalhos.setSelectionBackground(Color.BLACK);  // Cor de fundo da seleção (preto)
        tblTrabalhos.setSelectionForeground(Color.WHITE);

    }

    public void adicionarTakt() {

        try {
            Double.parseDouble(txtMinutos.getText().replace(",", "."));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Insira um valor válido para os minutos", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "insert into efi_tempos (setor_id, trabalho_id, peca_id, tempo, codigobarras) values (?,?,?,?,?)";

        // Usando try-with-resources para garantir o fechamento automático da conexão e do PreparedStatement
        try (
                Connection conn = ModuloConexao.conector(); PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, lblIdSetor.getText());
            pst.setString(2, lblIdTrabalho.getText());
            pst.setString(3, lblIdPeca.getText());
            pst.setString(4, txtMinutos.getText().replace(",", "."));
            if(!txtCodigoDeBarras.getText().equals("")){
                try {
                String codigoTexto = txtCodigoDeBarras.getText().trim();
                BigInteger codigoBarras = new BigInteger(codigoTexto); // aceita até centenas de dígitos
                pst.setString(5, codigoBarras.toString());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Código de barras inválido");
                return;
            }
            }else{
                pst.setString(5, "");
            }

            // Validação de campos obrigatórios
            if (lblIdSetor.getText().isEmpty() || lblIdTrabalho.getText().isEmpty() || lblIdPeca.getText().isEmpty() || txtMinutos.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios!");
            } else {
                try {
                    int adicionado = pst.executeUpdate();
                    if (adicionado > 0) {
                        JOptionPane.showMessageDialog(null, "Takt salvo!");
                        int opt = JOptionPane.showConfirmDialog(null, "Deseja continuar adicionando takts no mesmo Trabalho e Setor?", "Atenção", JOptionPane.YES_NO_OPTION);
                        if (opt == JOptionPane.YES_OPTION) {
                            int row = tblTrabalhos.getSelectedRow();

                            lblIdPeca.setText(null);
                            btnCadastrarTakt.setEnabled(true);
                            txtMinutos.setText(null);
                            txtCodigoDeBarras.setText(null);
                            tblPecas.clearSelection();
                            pesquisarTrabalhos();
                            tblTrabalhos.setRowSelectionInterval(row, row);
                        } else {
                            txtPesquisarPeca.setText(null);
                            txtPesquisarSetor.setText(null);
                            txtPesquisarTakt.setText(null);
                            txtPesquisarTrabalho.setText(null);
                            lblIdSetor.setText(null);
                            lblIdTrabalho.setText(null);
                            lblIdPeca.setText(null);
                            limparSelecoes();
                            pesquisarTrabalhos();
                        }
                    }
                    txtMinutos.setText(null);
                    spinMinutosInteiro.setValue(0);
                    spinMinutosFracao.setValue(0);
                } catch (com.mysql.cj.jdbc.exceptions.MysqlDataTruncation e) {
                    JOptionPane.showMessageDialog(null, "Digite o campo minutos correto, seguindo o padrão apresentado!");
                } catch (java.sql.SQLIntegrityConstraintViolationException e) {
                    JOptionPane.showMessageDialog(null, "Já existe um registro com os mesmos dados!");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

        pesquisarTakts();
    }

    public void limparSelecoes() {
        tblPecas.clearSelection();
        tblSetores.clearSelection();
        tblTrabalhos.clearSelection();
        tblTakts.clearSelection();
    }

    public void pesquisarTakts() {

        conn = ModuloConexao.conector();

        String sql = "select tempo_id AS Id, setor_nome AS Setor, trabalho_nome AS Trabalho, peca_descricao AS Operacao, tempo AS \"TEMPO (MINUTOS)\", codigobarras AS \"CODIGO DE BARRAS\"  from efi_tempos\n"
                + "JOIN efi_setores\n"
                + "ON efi_tempos.setor_id = efi_setores.setor_id\n"
                + "JOIN efi_trabalhos\n"
                + "ON efi_tempos.trabalho_id = efi_trabalhos.trabalho_id\n"
                + "JOIN efi_pecas\n"
                + "ON efi_tempos.peca_id = efi_pecas.peca_id where peca_descricao like ? and efi_setores.setor_nome like ? and efi_trabalhos.trabalho_nome like ?";

        try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, txtPesquisarTakt.getText().toUpperCase() + "%");
            pst.setString(2, txtPesquisarTaktSetor.getText().toUpperCase() + "%");
            pst.setString(3, txtPesquisarTaktTrabalho.getText().toUpperCase() + "%");
            rs = pst.executeQuery();
            tblTakts.setModel(DbUtils.resultSetToTableModel(rs));
            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
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

    public void pesquisarTrabalhos() {

        conn = ModuloConexao.conector();

        String sql = "select trabalho_id AS Id, trabalho_nome AS Nome from efi_trabalhos where trabalho_nome like ? and setor_id = ?";

        try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, txtPesquisarTrabalho.getText() + "%");
            pst.setString(2, lblIdSetor.getText());
            rs = pst.executeQuery();
            tblTrabalhos.setModel(DbUtils.resultSetToTableModel(rs));
            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
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

        String sql = "select setor_id AS Id, setor_nome AS Nome from efi_setores where setor_nome like ?";

        try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, txtPesquisarSetor.getText() + "%");
            rs = pst.executeQuery();
            tblSetores.setModel(DbUtils.resultSetToTableModel(rs));
            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
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

    public void pesquisarPeca() {

        conn = ModuloConexao.conector();

        String sql = "select peca_id AS Id, peca_descricao AS Codigo, peca_codigo AS Descricao from efi_pecas where peca_descricao like ? and peca_codigo like ?";

        try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, txtPesquisarPeca.getText().toUpperCase() + "%");
            pst.setString(2, txtPesquisarPecaD.getText().toUpperCase() + "%");
            rs = pst.executeQuery();
            tblPecas.setModel(DbUtils.resultSetToTableModel(rs));
            conn.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
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

    public void setarTakt() {

        int setar = tblTakts.getSelectedRow();
        lblIdTakt.setText(tblTakts.getModel().getValueAt(setar, 0).toString());
        conn = ModuloConexao.conector();
        String sqlColetarIdSetor = "select setor_id from efi_setores where setor_nome = ?";
        String sqlColetarIdTrabalho = "select trabalho_id from efi_trabalhos where trabalho_nome = ?";
        String sqlColetarIdPeca = "select peca_id from efi_pecas where peca_descricao = ?";

        try {
            pst = conn.prepareStatement(sqlColetarIdSetor);
            pst.setString(1, tblTakts.getModel().getValueAt(setar, 1).toString());
            rs = pst.executeQuery();

            if (rs.next()) {
                lblIdSetor.setText(rs.getString(1));
            }

            pst = null;

            pst = conn.prepareStatement(sqlColetarIdTrabalho);
            pst.setString(1, tblTakts.getModel().getValueAt(setar, 2).toString());
            rs = pst.executeQuery();
            if (rs.next()) {
                lblIdTrabalho.setText(rs.getString(1));
            }

            pst = null;

            pst = conn.prepareStatement(sqlColetarIdPeca);
            pst.setString(1, tblTakts.getModel().getValueAt(setar, 3).toString());
            rs = pst.executeQuery();
            if (rs.next()) {
                lblIdPeca.setText(rs.getString(1));
            }

            txtMinutos.setText(tblTakts.getModel().getValueAt(setar, 4).toString());
            
            Object leitorcodigo = tblTakts.getModel().getValueAt(setar, 5);
            
            if(leitorcodigo == null || leitorcodigo.toString().equals("")){
                txtCodigoDeBarras.setText("");
            }else{
                txtCodigoDeBarras.setText(tblTakts.getModel().getValueAt(setar, 5).toString());
            }
            
            pst = null;
            conn.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
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

        btnCadastrarTakt.setEnabled(false);

    }

    public void setarSetor() {
        try {
            int setarS = tblSetores.getSelectedRow();
            String v = tblSetores.getModel().getValueAt(setarS, 0).toString();
            System.out.println(v);
            lblIdSetor.setText(v);
        } catch (Exception e) {
            return;
        }

    }

    public void setarTrabalho() {
        try {
            int setarS = tblTrabalhos.getSelectedRow();
            String v = tblTrabalhos.getModel().getValueAt(setarS, 0).toString();
            System.out.println(v);
            lblIdTrabalho.setText(v);
        } catch (Exception e) {
            return;
        }

    }

    public void setarPeca() {
        try {
            int setarS = tblPecas.getSelectedRow();
            String v = tblPecas.getModel().getValueAt(setarS, 0).toString();
            System.out.println(v);
            lblIdPeca.setText(v);
        } catch (Exception e) {
            return;
        }

    }

    public void editarTakt() {

        try {
            Double.parseDouble(txtMinutos.getText().replace(",", "."));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Insira um valor válido para os minutos", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "update efi_tempos set setor_id = ?, trabalho_id = ?, peca_id = ?, tempo = ?, codigobarras = ? where tempo_id = ?";

        // Usando try-with-resources para garantir o fechamento automático da conexão e do PreparedStatement
        try (
                Connection conn = ModuloConexao.conector(); PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, lblIdSetor.getText());
            pst.setString(2, lblIdTrabalho.getText());
            pst.setString(3, lblIdPeca.getText());
            pst.setString(4, txtMinutos.getText().replace(",", "."));
            if(!txtCodigoDeBarras.getText().equals("")){
                try {
                String codigoTexto = txtCodigoDeBarras.getText().trim();
                BigInteger codigoBarras = new BigInteger(codigoTexto); // aceita até centenas de dígitos
                pst.setString(5, codigoBarras.toString());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Código de barras inválido");
                return;
            }
            }else{
                pst.setString(5, "");
            }

            pst.setString(6, lblIdTakt.getText());

            // Validação de campos obrigatórios
            if (lblIdSetor.getText().isEmpty() || lblIdTrabalho.getText().isEmpty() || lblIdPeca.getText().isEmpty() || txtMinutos.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Selecione um Takt na tabela!");
            } else {
                try {
                    int alterado = pst.executeUpdate();
                    if (alterado > 0) {
                        JOptionPane.showMessageDialog(null, "Takt alterado com sucesso!");
                        // Limpeza dos campos após a alteração
                        limparCampos();
                    }
                } catch (com.mysql.cj.jdbc.exceptions.MysqlDataTruncation e) {
                    JOptionPane.showMessageDialog(null, "Digite o campo minutos correto, seguindo o padrão apresentado!");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

        pesquisarTakts();
    }

    private void limparCampos() {
        txtCodigoDeBarras.setText(null);
        txtPesquisarPecaD.setText(null);
        spinMinutosInteiro.setValue(0);
        spinMinutosFracao.setValue(0);
        txtMinutos.setText(null);
        txtPesquisarPeca.setText(null);
        txtPesquisarSetor.setText(null);
        txtPesquisarTrabalho.setText(null);
        txtPesquisarTakt.setText(null);
        lblIdPeca.setText(null);
        lblIdSetor.setText(null);
        lblIdTakt.setText(null);
        lblIdTrabalho.setText(null);
        btnCadastrarTakt.setEnabled(true);
    }

    public void removerTakt() {
        int confirma = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja remover este Takt?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            if (lblIdSetor.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Selecione um Takt na tabela para remover");
            } else {
                String sql = "delete from efi_tempos where tempo_id = ?";
                // Usando try-with-resources
                try (Connection conn = ModuloConexao.conector(); PreparedStatement pst = conn.prepareStatement(sql)) {

                    pst.setString(1, lblIdTakt.getText());
                    int apagado = pst.executeUpdate();

                    if (apagado > 0) {
                        JOptionPane.showMessageDialog(null, "Takt removido!");
                        // Limpar os campos
                        txtCodigoDeBarras.setText(null);
                        txtMinutos.setText(null);
                        txtPesquisarPeca.setText(null);
                        txtPesquisarSetor.setText(null);
                        txtPesquisarTakt.setText(null);
                        txtPesquisarTrabalho.setText(null);
                        lblIdPeca.setText(null);
                        lblIdSetor.setText(null);
                        lblIdTakt.setText(null);
                        lblIdTrabalho.setText(null);
                        btnCadastrarTakt.setEnabled(true);
                        pesquisarTakts();
                    }

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Você não pode remover este Takt porque ele mantém a integridade do banco!");
                }
            }
        }
        pesquisarTakts();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtMinutos = new javax.swing.JTextField();
        btnCadastrarTakt = new javax.swing.JButton();
        btnAlterarTakt = new javax.swing.JButton();
        btnRemoverTakt = new javax.swing.JButton();
        lblIdTakt = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        iconeBuscarSetores = new javax.swing.JLabel();
        lblIdSetor = new javax.swing.JTextField();
        txtPesquisarSetor = new javax.swing.JTextField();
        jScrollPane6 = new javax.swing.JScrollPane();
        tblSetores = new javax.swing.JTable();
        jLabel19 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        iconeBuscarPecas = new javax.swing.JLabel();
        lblIdPeca = new javax.swing.JTextField();
        txtPesquisarPeca = new javax.swing.JTextField();
        jScrollPane7 = new javax.swing.JScrollPane();
        tblPecas = new javax.swing.JTable();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        txtPesquisarPecaD = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        iconeBuscarTrabalhos = new javax.swing.JLabel();
        lblIdTrabalho = new javax.swing.JTextField();
        txtPesquisarTrabalho = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        tblTrabalhos = new javax.swing.JTable();
        jScrollPane8 = new javax.swing.JScrollPane();
        tblTakts = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        txtPesquisarTaktSetor = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtPesquisarTaktTrabalho = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtPesquisarTakt = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtCodigoDeBarras = new javax.swing.JTextField();
        spinMinutosInteiro = new javax.swing.JSpinner();
        spinMinutosFracao = new javax.swing.JSpinner();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Banco de Valores Takt");
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

        jLabel4.setText("* Campos obrigatórios");

        jLabel1.setText("* Tempo:");

        txtMinutos.setEditable(false);
        txtMinutos.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtMinutos.setForeground(new java.awt.Color(102, 102, 102));

        btnCadastrarTakt.setText("Adicionar");
        btnCadastrarTakt.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCadastrarTakt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCadastrarTaktActionPerformed(evt);
            }
        });

        btnAlterarTakt.setText("Editar");
        btnAlterarTakt.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAlterarTakt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAlterarTaktActionPerformed(evt);
            }
        });

        btnRemoverTakt.setText("Remover");
        btnRemoverTakt.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRemoverTakt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverTaktActionPerformed(evt);
            }
        });

        lblIdTakt.setEditable(false);
        lblIdTakt.setBackground(new java.awt.Color(204, 204, 204));

        jLabel10.setText("* Id Registro");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setText("Takt Time");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel20.setText("* ID");

        iconeBuscarSetores.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/prjRend/icones/search-small.png"))); // NOI18N
        iconeBuscarSetores.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        iconeBuscarSetores.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        iconeBuscarSetores.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                iconeBuscarSetoresMouseClicked(evt);
            }
        });

        lblIdSetor.setEditable(false);
        lblIdSetor.setBackground(new java.awt.Color(204, 204, 204));

        txtPesquisarSetor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPesquisarSetorKeyReleased(evt);
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
                "ID", "NOME"
            }
        ));
        tblSetores.setFocusable(false);
        tblSetores.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblSetoresMouseClicked(evt);
            }
        });
        jScrollPane6.setViewportView(tblSetores);

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(51, 51, 51));
        jLabel19.setText("Associar Setor");

        jLabel22.setText("Nome:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblIdSetor, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(iconeBuscarSetores, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(64, Short.MAX_VALUE)
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPesquisarSetor, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel20)
                        .addComponent(lblIdSetor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel19))
                    .addComponent(iconeBuscarSetores))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPesquisarSetor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel23.setText("* ID");

        iconeBuscarPecas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/prjRend/icones/search-small.png"))); // NOI18N
        iconeBuscarPecas.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        iconeBuscarPecas.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        iconeBuscarPecas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                iconeBuscarPecasMouseClicked(evt);
            }
        });

        lblIdPeca.setEditable(false);
        lblIdPeca.setBackground(new java.awt.Color(204, 204, 204));

        txtPesquisarPeca.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPesquisarPecaKeyReleased(evt);
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
                "ID", "CODIGO", "DESCRICAO"
            }
        ));
        tblPecas.setFocusable(false);
        tblPecas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPecasMouseClicked(evt);
            }
        });
        jScrollPane7.setViewportView(tblPecas);

        jLabel25.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(51, 51, 51));
        jLabel25.setText("Associar Peça");

        jLabel26.setText("Operação:");

        txtPesquisarPecaD.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPesquisarPecaDKeyReleased(evt);
            }
        });

        jLabel28.setText("Descrição:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 65, Short.MAX_VALUE)
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblIdPeca, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(iconeBuscarPecas, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel28)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtPesquisarPecaD))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel26)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtPesquisarPeca)))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel23)
                        .addComponent(lblIdPeca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel25))
                    .addComponent(iconeBuscarPecas))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPesquisarPeca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPesquisarPecaD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel27.setText("* ID");

        iconeBuscarTrabalhos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/prjRend/icones/search-small.png"))); // NOI18N
        iconeBuscarTrabalhos.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        iconeBuscarTrabalhos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        iconeBuscarTrabalhos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                iconeBuscarTrabalhosMouseClicked(evt);
            }
        });

        lblIdTrabalho.setEditable(false);
        lblIdTrabalho.setBackground(new java.awt.Color(204, 204, 204));

        txtPesquisarTrabalho.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPesquisarTrabalhoKeyReleased(evt);
            }
        });

        jLabel29.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(51, 51, 51));
        jLabel29.setText("Associar Trabalho");

        jLabel30.setText("Nome:");

        tblTrabalhos = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tblTrabalhos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "NOME"
            }
        ));
        tblTrabalhos.setFocusable(false);
        tblTrabalhos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblTrabalhosMouseClicked(evt);
            }
        });
        jScrollPane9.setViewportView(tblTrabalhos);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblIdTrabalho, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(iconeBuscarTrabalhos, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(0, 58, Short.MAX_VALUE)
                        .addComponent(jLabel30)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPesquisarTrabalho, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel27)
                        .addComponent(lblIdTrabalho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel29))
                    .addComponent(iconeBuscarTrabalhos))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPesquisarTrabalho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        tblTakts = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tblTakts.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "SETOR", "TRABALHO", "OPERACAO", "TEMPO (MINUTOS)", "CODIGO DE BARRAS"
            }
        ));
        tblTakts.setFocusable(false);
        tblTakts.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblTaktsMouseClicked(evt);
            }
        });
        jScrollPane8.setViewportView(tblTakts);

        jLabel9.setFont(new java.awt.Font("Segoe UI", 2, 24)); // NOI18N
        jLabel9.setText("Cadastro de Takts");

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel7.setText("Setor");

        txtPesquisarTaktSetor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPesquisarTaktSetorKeyReleased(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel8.setText("Trabalho");

        txtPesquisarTaktTrabalho.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPesquisarTaktTrabalhoKeyReleased(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setText("Operação");

        txtPesquisarTakt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPesquisarTaktKeyReleased(evt);
            }
        });

        jButton1.setText("Buscar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPesquisarTaktSetor)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPesquisarTaktTrabalho)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPesquisarTakt)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addGap(232, 232, 232))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtPesquisarTaktSetor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(txtPesquisarTaktTrabalho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtPesquisarTakt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addContainerGap())
        );

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel11.setText("Consultar dados");

        jLabel3.setText("Código de Barras:");

        spinMinutosInteiro.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        spinMinutosInteiro.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));
        spinMinutosInteiro.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinMinutosInteiroStateChanged(evt);
            }
        });

        spinMinutosFracao.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        spinMinutosFracao.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));
        spinMinutosFracao.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinMinutosFracaoStateChanged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinMinutosInteiro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinMinutosFracao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtMinutos, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAlterarTakt, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCadastrarTakt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemoverTakt)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane8)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel11)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblIdTakt, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCodigoDeBarras, javax.swing.GroupLayout.PREFERRED_SIZE, 497, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4)))
                .addGap(62, 62, 62))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblIdTakt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3)
                    .addComponent(txtCodigoDeBarras, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(spinMinutosInteiro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(spinMinutosFracao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtMinutos, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnAlterarTakt, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnCadastrarTakt, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnRemoverTakt, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(20, 20, 20))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(21, 21, 21))))
        );

        setBounds(0, 0, 1076, 663);
    }// </editor-fold>//GEN-END:initComponents

    private void txtPesquisarTaktKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisarTaktKeyReleased
        pesquisarTakts();
    }//GEN-LAST:event_txtPesquisarTaktKeyReleased

    private void btnCadastrarTaktActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCadastrarTaktActionPerformed
        int mi = (int) spinMinutosInteiro.getValue();
        int mf = (int) spinMinutosFracao.getValue();
        
        if(mi + mf != 0){
                adicionarTakt();
        }else{
            JOptionPane.showMessageDialog(null, "O tempo não foi definido!");
        }
    }//GEN-LAST:event_btnCadastrarTaktActionPerformed

    private void btnAlterarTaktActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlterarTaktActionPerformed
        if(lblIdTakt.getText().equals(null) || lblIdTakt.getText().equals("")){
            JOptionPane.showMessageDialog(null, "Selecione um Tatk na tabela para editar!");
            return;
        } else {
            try {
                double min = Double.valueOf(txtMinutos.getText().replace(",", "."));
            if (min != 0.0){
                editarTakt();
            } else {
                JOptionPane.showMessageDialog(null, "O campo minutos não pode ser zero!");
                return;
            }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Verifique os dados");
            }
        }
    }//GEN-LAST:event_btnAlterarTaktActionPerformed

    private void btnRemoverTaktActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverTaktActionPerformed
        removerTakt();
    }//GEN-LAST:event_btnRemoverTaktActionPerformed

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        txtCodigoDeBarras.setText(null);
        txtPesquisarPecaD.setText(null);
        spinMinutosInteiro.setValue(0);
        spinMinutosFracao.setValue(0);
        btnAlterarTakt.setEnabled(true);
        txtCodigoDeBarras.setText(null);
        txtPesquisarTaktTrabalho.setText(null);
        txtPesquisarTaktSetor.setText(null);
        txtMinutos.setText(null);
        txtPesquisarPeca.setText(null);
        txtPesquisarPecaD.setText(null);
        txtPesquisarSetor.setText(null);
        txtPesquisarTakt.setText(null);
        txtPesquisarTrabalho.setText(null);
        lblIdPeca.setText(null);
        lblIdSetor.setText(null);
        lblIdTakt.setText(null);
        lblIdTrabalho.setText(null);
        btnCadastrarTakt.setEnabled(true);
        tblTakts.clearSelection();
        tblPecas.clearSelection();
        tblSetores.clearSelection();
        tblTrabalhos.clearSelection();
    }//GEN-LAST:event_formMouseClicked

    private void tblSetoresMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSetoresMouseClicked
        setarSetor();
        pesquisarTrabalhos();
    }//GEN-LAST:event_tblSetoresMouseClicked

    private void iconeBuscarSetoresMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_iconeBuscarSetoresMouseClicked
        pesquisarSetores();
    }//GEN-LAST:event_iconeBuscarSetoresMouseClicked

    private void iconeBuscarPecasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_iconeBuscarPecasMouseClicked
        pesquisarPeca();
    }//GEN-LAST:event_iconeBuscarPecasMouseClicked

    private void tblPecasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPecasMouseClicked
        setarPeca();
    }//GEN-LAST:event_tblPecasMouseClicked

    private void iconeBuscarTrabalhosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_iconeBuscarTrabalhosMouseClicked
        pesquisarTrabalhos();
    }//GEN-LAST:event_iconeBuscarTrabalhosMouseClicked

    private void tblTaktsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblTaktsMouseClicked
        setarTakt();
    }//GEN-LAST:event_tblTaktsMouseClicked

    private void txtPesquisarTrabalhoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisarTrabalhoKeyReleased
        pesquisarTrabalhos();
    }//GEN-LAST:event_txtPesquisarTrabalhoKeyReleased

    private void txtPesquisarSetorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisarSetorKeyReleased
        pesquisarSetores();
    }//GEN-LAST:event_txtPesquisarSetorKeyReleased

    private void txtPesquisarPecaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisarPecaKeyReleased
        pesquisarPeca();
    }//GEN-LAST:event_txtPesquisarPecaKeyReleased

    private void tblTrabalhosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblTrabalhosMouseClicked
        setarTrabalho();
    }//GEN-LAST:event_tblTrabalhosMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        pesquisarTakts();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txtPesquisarTaktSetorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisarTaktSetorKeyReleased
        pesquisarTakts();
    }//GEN-LAST:event_txtPesquisarTaktSetorKeyReleased

    private void txtPesquisarTaktTrabalhoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisarTaktTrabalhoKeyReleased
        pesquisarTakts();
    }//GEN-LAST:event_txtPesquisarTaktTrabalhoKeyReleased

    private void spinMinutosInteiroStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinMinutosInteiroStateChanged
    txtMinutos.setText(null);

    int parteInteira = (int) spinMinutosInteiro.getValue();   // minutos
    int parteFracionada = (int) spinMinutosFracao.getValue(); // segundos

    double pFracionada = parteFracionada / 60.0;
    double totalMinutos = parteInteira + pFracionada;

    txtMinutos.setText(String.format("%.4f", totalMinutos));
    
    btnAlterarTakt.setEnabled(true);
    }//GEN-LAST:event_spinMinutosInteiroStateChanged

    private void spinMinutosFracaoStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinMinutosFracaoStateChanged
    txtMinutos.setText(null);

    int parteInteira = (int) spinMinutosInteiro.getValue();   // minutos
    int parteFracionada = (int) spinMinutosFracao.getValue(); // segundos

    double pFracionada = parteFracionada / 60.0;
    double totalMinutos = parteInteira + pFracionada;

    txtMinutos.setText(String.format("%.4f", totalMinutos));
    
    btnAlterarTakt.setEnabled(true);
    }//GEN-LAST:event_spinMinutosFracaoStateChanged

    private void txtPesquisarPecaDKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisarPecaDKeyReleased
    pesquisarPeca();
    }//GEN-LAST:event_txtPesquisarPecaDKeyReleased

    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaDeCadastroTakts().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAlterarTakt;
    private javax.swing.JButton btnCadastrarTakt;
    private javax.swing.JButton btnRemoverTakt;
    private javax.swing.JLabel iconeBuscarPecas;
    private javax.swing.JLabel iconeBuscarSetores;
    private javax.swing.JLabel iconeBuscarTrabalhos;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTextField lblIdPeca;
    private javax.swing.JTextField lblIdSetor;
    private javax.swing.JTextField lblIdTakt;
    private javax.swing.JTextField lblIdTrabalho;
    private javax.swing.JSpinner spinMinutosFracao;
    private javax.swing.JSpinner spinMinutosInteiro;
    private javax.swing.JTable tblPecas;
    private javax.swing.JTable tblSetores;
    private javax.swing.JTable tblTakts;
    private javax.swing.JTable tblTrabalhos;
    private javax.swing.JTextField txtCodigoDeBarras;
    private javax.swing.JTextField txtMinutos;
    private javax.swing.JTextField txtPesquisarPeca;
    private javax.swing.JTextField txtPesquisarPecaD;
    private javax.swing.JTextField txtPesquisarSetor;
    private javax.swing.JTextField txtPesquisarTakt;
    private javax.swing.JTextField txtPesquisarTaktSetor;
    private javax.swing.JTextField txtPesquisarTaktTrabalho;
    private javax.swing.JTextField txtPesquisarTrabalho;
    // End of variables declaration//GEN-END:variables
}
