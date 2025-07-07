package br.com.prjRend.telas;

import br.com.prjRend.dal.ModuloConexao;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import net.proteanit.sql.DbUtils;
import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import static sun.jvm.hotspot.HelloWorld.e;

public class TelaRelatorioDeProducao extends javax.swing.JInternalFrame {

    Connection conn = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    public TelaRelatorioDeProducao() {
        initComponents();
        datePickerInicio.setDate(new Date());
        datePickerFim.setDate(new Date());
    }

    
public void exportarTabela() {
      
        
    if (tblAtividades.getRowCount() == 0) {
        JOptionPane.showMessageDialog(null, "A tabela está vazia!", "Aviso", JOptionPane.WARNING_MESSAGE);
        return;
    }

    String desktopPath = System.getProperty("user.home") + "\\Desktop";
    JFileChooser excelFileChooser = new JFileChooser(new File(desktopPath));
    excelFileChooser.setDialogTitle("Salvar como");
    FileNameExtensionFilter fnef = new FileNameExtensionFilter("Arquivos do Excel (.xlsx)", "xlsx");
    excelFileChooser.setFileFilter(fnef);

    int excelChooser = excelFileChooser.showSaveDialog(null);
    
    
    
    
    if (excelChooser == JFileChooser.APPROVE_OPTION) {
        File selectedFile = excelFileChooser.getSelectedFile();
        String filePath = selectedFile.getAbsolutePath();

        if (!filePath.toLowerCase().endsWith(".xlsx")) {
            filePath += ".xlsx";
        }

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             FileOutputStream fileOut = new FileOutputStream(filePath);
             BufferedOutputStream bufferedOut = new BufferedOutputStream(fileOut)) {

            XSSFSheet sheet = workbook.createSheet("Dados");

            // Criando cabeçalhos
            XSSFRow headerRow = sheet.createRow(0);
            for (int col = 0; col < tblAtividades.getColumnCount(); col++) {
                XSSFCell cell = headerRow.createCell(col);
                cell.setCellValue(tblAtividades.getColumnName(col));
            }

            // Preenchendo os dados
            for (int row = 0; row < tblAtividades.getRowCount(); row++) {
                XSSFRow excelRow = sheet.createRow(row + 1);
                for (int col = 0; col < tblAtividades.getColumnCount(); col++) {
                    XSSFCell cell = excelRow.createCell(col);
                    Object value = tblAtividades.getValueAt(row, col);
                    cell.setCellValue(value != null ? value.toString() : "");
                }
            }

            workbook.write(bufferedOut);
            JOptionPane.showMessageDialog(null, "Exportação concluída com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao exportar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
     
}


    DateTimeFormatter formatador = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void pesquisarAtividades() {
    Connection conn = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    conn = ModuloConexao.conector();
    
    // Inicia a construção da consulta SQL com a parte fixa
    StringBuilder sqlBuilder = new StringBuilder("SELECT ");
    
    // Verifica as checkboxes e monta a lista de colunas a serem selecionadas
    if (checkboxData.isSelected()) {
        sqlBuilder.append("TO_CHAR(data, 'DD/MM/YYYY') AS data, ");
    }
    if (checkboxFT.isSelected()) {
        sqlBuilder.append("ft, ");
    }
    if (checkboxSetor.isSelected()) {
        sqlBuilder.append("setor, ");
    }
    if (checkboxTrabalho.isSelected()) {
        sqlBuilder.append("trabalho, ");
    }
    if (checkboxOperacao.isSelected()) {
        sqlBuilder.append("operacao, ");
    }
    if (checkboxQuantidade.isSelected()) {
        sqlBuilder.append("quantidade, ");
    }
    if (checkboxTipo.isSelected()) {
        sqlBuilder.append("tipo, ");
    }
    if (checkboxHoras.isSelected()) {
        sqlBuilder.append("horas, ");
    }
    if (checkboxDisponibilidade.isSelected()) {
        sqlBuilder.append("disponibilidade, ");
    }
    
    if (checkboxPerformance.isSelected()) {
        sqlBuilder.append("performance, ");
    }
    
    if (checkboxQualidade.isSelected()) {
        sqlBuilder.append("qualidade, ");
    }
    
    if (checkboxOle.isSelected()) {
        sqlBuilder.append("ole, ");
    }
    
    if (checkboxFuncionarios.isSelected()) {
        sqlBuilder.append("funcionarios, ");
    }
    
    if (checkboxObs.isSelected()) {
        sqlBuilder.append("obs, ");
    }
    
    if (checkboxTempoImprodutivo.isSelected()) {
        sqlBuilder.append("tempo_imp AS Tempo_Improdutivo, ");
    }

    // Remover a vírgula extra no final da consulta
    sqlBuilder.deleteCharAt(sqlBuilder.length() - 2);
    
    // Adiciona a parte fixa da consulta (de onde os dados serão recuperados)
    sqlBuilder.append(" FROM efi_atividades WHERE data BETWEEN ? AND ? AND setor LIKE ? AND tipo LIKE ? and ft LIKE ?");
    
    // Agora a string 'sql' contém a consulta dinâmica
    String sql = sqlBuilder.toString();
        

    try {
        // Pegando as datas do JDateChooser
        java.util.Date dataInicioUtil = datePickerInicio.getDate();
        java.util.Date dataFimUtil = datePickerFim.getDate();

        if (dataInicioUtil == null || dataFimUtil == null) {
            JOptionPane.showMessageDialog(null, "Por favor, selecione as datas corretamente.");
            return;
        }

        // Convertendo para java.sql.Date
        java.sql.Date dataInicioSQL = new java.sql.Date(dataInicioUtil.getTime());
        java.sql.Date dataFimSQL = new java.sql.Date(dataFimUtil.getTime());

        // Tratando o campo 'tipo'
        String tipo = cboTipo.getSelectedItem().toString();
        String ft = txtFT.getText();
        if ("AMBOS".equals(tipo)) {
            tipo = "";
        }

        // Criando o PreparedStatement
        pst = conn.prepareStatement(sql);
        pst.setDate(1, dataInicioSQL);
        pst.setDate(2, dataFimSQL);
        pst.setString(3, txtSetor.getText().toUpperCase() + "%");
        pst.setString(4, tipo + "%");
        pst.setString(5, ft.toUpperCase() + "%");

        // Executando a query
        rs = pst.executeQuery();
        tblAtividades.setModel(DbUtils.resultSetToTableModel(rs));
        
        if(tblAtividades.getRowCount() < 1){
            JOptionPane.showMessageDialog(null, "Não há dados para este intervalo", "Mensagem", JOptionPane.INFORMATION_MESSAGE);
        }

    } catch (Exception e) {
    JOptionPane.showMessageDialog(null, "Selecione os parâmetros desejados", "Erro", JOptionPane.WARNING_MESSAGE);
        
        DefaultTableModel model = (DefaultTableModel) tblAtividades.getModel();
        model.setRowCount(0);
        
}

        
    finally {
        // Fechando conexão
        try {
            if (rs != null) {
                rs.close();
            }
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


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblAtividades = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cboTipo = new javax.swing.JComboBox<>();
        txtSetor = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        checkboxData = new javax.swing.JCheckBox();
        checkboxFT = new javax.swing.JCheckBox();
        checkboxSetor = new javax.swing.JCheckBox();
        checkboxTrabalho = new javax.swing.JCheckBox();
        checkboxOperacao = new javax.swing.JCheckBox();
        checkboxQuantidade = new javax.swing.JCheckBox();
        checkboxTipo = new javax.swing.JCheckBox();
        checkboxHoras = new javax.swing.JCheckBox();
        datePickerInicio = new org.jdesktop.swingx.JXDatePicker();
        datePickerFim = new org.jdesktop.swingx.JXDatePicker();
        jLabel2 = new javax.swing.JLabel();
        btnBuscar = new javax.swing.JButton();
        checkboxDisponibilidade = new javax.swing.JCheckBox();
        checkboxPerformance = new javax.swing.JCheckBox();
        checkboxQualidade = new javax.swing.JCheckBox();
        checkboxOle = new javax.swing.JCheckBox();
        txtFT = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        checkboxFuncionarios = new javax.swing.JCheckBox();
        checkboxObs = new javax.swing.JCheckBox();
        checkboxTempoImprodutivo = new javax.swing.JCheckBox();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Histórico de Produção");
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

        tblAtividades = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tblAtividades.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tblAtividades);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/prjRend/icones/excel-removebg-preview.png"))); // NOI18N
        jButton1.setText("Excel");
        jButton1.setFocusable(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel1MouseClicked(evt);
            }
        });

        jLabel1.setText("Intervalo:");

        cboTipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "AMBOS", "PRODUTIVO", "IMPRODUTIVO" }));
        cboTipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTipoActionPerformed(evt);
            }
        });

        txtSetor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSetorKeyReleased(evt);
            }
        });

        jLabel3.setText("Setor");

        jLabel4.setText("Tipo");

        jLabel5.setText("FT");

        checkboxData.setSelected(true);
        checkboxData.setText("Data");

        checkboxFT.setSelected(true);
        checkboxFT.setText("FT");

        checkboxSetor.setSelected(true);
        checkboxSetor.setText("Setor");

        checkboxTrabalho.setSelected(true);
        checkboxTrabalho.setText("Trabalho");

        checkboxOperacao.setSelected(true);
        checkboxOperacao.setText("Operação");

        checkboxQuantidade.setSelected(true);
        checkboxQuantidade.setText("Quantidade");

        checkboxTipo.setSelected(true);
        checkboxTipo.setText("Tipo");

        checkboxHoras.setSelected(true);
        checkboxHoras.setText("Horas");

        jLabel2.setText("à");

        btnBuscar.setText("Buscar");
        btnBuscar.setFocusable(false);
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        checkboxDisponibilidade.setSelected(true);
        checkboxDisponibilidade.setText("Disponibilidade");

        checkboxPerformance.setSelected(true);
        checkboxPerformance.setText("Performance");

        checkboxQualidade.setSelected(true);
        checkboxQualidade.setText("Qualidade");

        checkboxOle.setSelected(true);
        checkboxOle.setText("OLE");

        txtFT.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFTKeyReleased(evt);
            }
        });

        jButton2.setText("Limpar Filtros");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        checkboxFuncionarios.setSelected(true);
        checkboxFuncionarios.setText("Funcionários");

        checkboxObs.setSelected(true);
        checkboxObs.setText("Obs.");

        checkboxTempoImprodutivo.setSelected(true);
        checkboxTempoImprodutivo.setText("Tempo Improdutivo");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(checkboxData)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(checkboxFT)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(checkboxSetor)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(checkboxTrabalho)
                                .addGap(5, 5, 5))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(checkboxPerformance)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(checkboxQualidade)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(checkboxOle)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(checkboxOperacao)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(checkboxQuantidade)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(checkboxTipo)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(checkboxHoras)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(checkboxDisponibilidade))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(checkboxFuncionarios)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(checkboxObs)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(checkboxTempoImprodutivo)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtFT, javax.swing.GroupLayout.PREFERRED_SIZE, 51, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel3))
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cboTipo, 0, 220, Short.MAX_VALUE)
                            .addComponent(txtSetor)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(datePickerInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(datePickerFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBuscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton2)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtSetor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkboxData, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkboxFT, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkboxSetor, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkboxTrabalho, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkboxOperacao, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkboxQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkboxTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkboxHoras, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkboxDisponibilidade, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(txtFT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(checkboxPerformance, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkboxQualidade, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkboxOle, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkboxFuncionarios, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkboxObs, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkboxTempoImprodutivo, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(datePickerInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(datePickerFim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(btnBuscar)
                    .addComponent(jButton2))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 984, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1))
                .addGap(39, 39, 39))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addGap(35, 35, 35))
        );

        setBounds(0, 0, 1076, 663);
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        txtFT.setText(null);
        txtSetor.setText(null);
        DefaultTableModel model = (DefaultTableModel) tblAtividades.getModel();
        model.setRowCount(0);
    }//GEN-LAST:event_formMouseClicked

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        pesquisarAtividades();
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void txtSetorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSetorKeyReleased
    
    }//GEN-LAST:event_txtSetorKeyReleased

    private void cboTipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTipoActionPerformed
        pesquisarAtividades();
    }//GEN-LAST:event_cboTipoActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        exportarTabela();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txtFTKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFTKeyReleased
    
    }//GEN-LAST:event_txtFTKeyReleased

    private void jPanel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseClicked
    txtFT.setText(null);
    txtSetor.setText(null);
    }//GEN-LAST:event_jPanel1MouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
    checkboxData.setSelected(false);
    checkboxDisponibilidade.setSelected(false);
    checkboxFT.setSelected(false);
    checkboxHoras.setSelected(false);
    checkboxOle.setSelected(false);
    checkboxOperacao.setSelected(false);
    checkboxPerformance.setSelected(false);
    checkboxQualidade.setSelected(false);
    checkboxQuantidade.setSelected(false);
    checkboxSetor.setSelected(false);
    checkboxTipo.setSelected(false);
    checkboxTrabalho.setSelected(false);
    checkboxObs.setSelected(false);
    checkboxTempoImprodutivo.setSelected(false);
    checkboxFuncionarios.setSelected(false);
    txtFT.setText(null);
    txtSetor.setText(null);
    cboTipo.setSelectedItem("AMBOS");
    }//GEN-LAST:event_jButton2ActionPerformed

    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaRelatorioDeProducao().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private javax.swing.JComboBox<String> cboTipo;
    private javax.swing.JCheckBox checkboxData;
    private javax.swing.JCheckBox checkboxDisponibilidade;
    private javax.swing.JCheckBox checkboxFT;
    private javax.swing.JCheckBox checkboxFuncionarios;
    private javax.swing.JCheckBox checkboxHoras;
    private javax.swing.JCheckBox checkboxObs;
    private javax.swing.JCheckBox checkboxOle;
    private javax.swing.JCheckBox checkboxOperacao;
    private javax.swing.JCheckBox checkboxPerformance;
    private javax.swing.JCheckBox checkboxQualidade;
    private javax.swing.JCheckBox checkboxQuantidade;
    private javax.swing.JCheckBox checkboxSetor;
    private javax.swing.JCheckBox checkboxTempoImprodutivo;
    private javax.swing.JCheckBox checkboxTipo;
    private javax.swing.JCheckBox checkboxTrabalho;
    private org.jdesktop.swingx.JXDatePicker datePickerFim;
    private org.jdesktop.swingx.JXDatePicker datePickerInicio;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JTable tblAtividades;
    private javax.swing.JTextField txtFT;
    private javax.swing.JTextField txtSetor;
    // End of variables declaration//GEN-END:variables
}
