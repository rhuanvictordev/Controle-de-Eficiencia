package br.com.prjRend.telas;

import br.com.prjRend.dal.FileInput;
import br.com.prjRend.dal.ModuloConexao;
import static java.awt.Frame.MAXIMIZED_BOTH;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;

public class TelaEscolherSetor extends javax.swing.JFrame {

    Connection conn = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    public TelaEscolherSetor() {
        initComponents();
        obterSetores();
    }

    LocalDateTime agora = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    String horarioFormatado = agora.format(formatter);

    public void obterSetores() {

        String sqlObterSetores = "select setor_nome from efi_setores";

        conn = ModuloConexao.conector();

        try {
            pst = conn.prepareStatement(sqlObterSetores);
            rs = pst.executeQuery();

            while (rs.next()) {
                cboSetor.addItem(rs.getString(1));
            }
            pst = null;
            rs = null;
            conn.close();
        } catch (Exception e) {
            return;
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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cboSetor = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Selecionar Setor");
        setFocusable(false);
        setUndecorated(true);
        setResizable(false);

        cboSetor.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        cboSetor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " " }));
        cboSetor.setFocusable(false);
        cboSetor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboSetorActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Dubai", 1, 24)); // NOI18N
        jLabel1.setText("Selecione o setor");

        jButton1.setText("Fechar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cboSetor, javax.swing.GroupLayout.PREFERRED_SIZE, 483, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboSetor, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void cboSetorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboSetorActionPerformed
        String setor = cboSetor.getSelectedItem().toString();
        String bancada = FileInput.AcessarNumeroBancada();
        String setorTipo = null;
        String setorTempo = null;

        if (setor.equals(" ")) {
            JOptionPane.showMessageDialog(null, "Selecione um setor na lista","Mensagem",JOptionPane.INFORMATION_MESSAGE);
        } else {

            TelaDeEficiencia cadastro = new TelaDeEficiencia();
            String sqlObterTipoSetor = "select setor_tipo from efi_setores where setor_nome = ?";
            String sqlObterSetores = "select pausa_nome from efi_pausas JOIN efi_setores ON efi_pausas.setor_id = efi_setores.setor_id where setor_nome = ?";
            String sqlObterTrabalhos = "select trabalho_nome from efi_trabalhos JOIN efi_setores ON efi_trabalhos.setor_id = efi_setores.setor_id where setor_nome = ?";
            String sqlObterFuncionarios = "select funcionario_nome from efi_funcionarios JOIN efi_setores ON efi_funcionarios.setor_id = efi_setores.setor_id where setor_nome = ?";
            String sqlObterTempoOperacional = "select setor_tempo_operacional from efi_setores where setor_nome = ?";
            
            conn = ModuloConexao.conector();

            try {

                pst = conn.prepareStatement(sqlObterTipoSetor);
                pst.setString(1, cboSetor.getSelectedItem().toString());
                rs = pst.executeQuery();
                if (rs.next()) {
                    setorTipo = rs.getString(1);
                }
                
                
                pst = conn.prepareStatement(sqlObterTempoOperacional);
                pst.setString(1, cboSetor.getSelectedItem().toString());
                rs = pst.executeQuery();
                if (rs.next()) {
                    setorTempo = rs.getString(1);
                }

                pst = conn.prepareStatement(sqlObterSetores);
                pst.setString(1, cboSetor.getSelectedItem().toString());
                rs = pst.executeQuery();
                cadastro.cboImproducaoTipo.addItem("");
                while (rs.next()) {
                    cadastro.cboImproducaoTipo.addItem(rs.getString(1));
                }
                pst = null;
                rs = null;

                pst = conn.prepareStatement(sqlObterTrabalhos);
                pst.setString(1, cboSetor.getSelectedItem().toString());
                rs = pst.executeQuery();
                cadastro.cboTrabalho.addItem("");
                while (rs.next()) {
                    cadastro.cboTrabalho.addItem(rs.getString(1));
                }
                pst = null;
                rs = null;

                pst = conn.prepareStatement(sqlObterFuncionarios);
                pst.setString(1, cboSetor.getSelectedItem().toString());
                rs = pst.executeQuery();
                cadastro.cboFuncionario.addItem("");
                while (rs.next()) {
                   cadastro.cboFuncionario.addItem(rs.getString(1));
                }
                pst = null;
                rs = null;
                conn.close();

            } catch (Exception e) {
                return;
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

            cadastro.setExtendedState(MAXIMIZED_BOTH);
            cadastro.setVisible(true);
            cadastro.lblBancada.setText(bancada);
            cadastro.txtTempoDesejadoParaProduzir.setText(setorTempo);
            cadastro.lblSetorNome.setText(setor);
                //cadastro.lblFuncionario.setVisible(false);
                cadastro.cboFuncionario.setVisible(false);
            if (setorTipo.equals("INDIVIDUAL")) {
                cadastro.spinQuantidadeFuncionarios.setVisible(false);
                cadastro.lblQuantidadeFuncionarios.setVisible(false);
                cadastro.lblBancada.setVisible(false);
                cadastro.CheckboxEntradaAutomatica.setVisible(false);
                //cadastro.lblFuncionario.setVisible(true);
               cadastro.cboFuncionario.setVisible(true);
               
               cadastro.btnAddFunc.setVisible(false);
               cadastro.btnRemoverFunc.setVisible(false);
               cadastro.tblFunc.setVisible(false);
               cadastro.jScrollPane3.setVisible(false);
            }

            cadastro.nomePrimarioSetor = cadastro.lblSetorNome.getText();
            
            this.dispose();
        }
    }//GEN-LAST:event_cboSetorActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    System.exit(0);
    }//GEN-LAST:event_jButton1ActionPerformed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TelaEscolherSetor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaEscolherSetor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaEscolherSetor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaEscolherSetor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaEscolherSetor().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cboSetor;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
