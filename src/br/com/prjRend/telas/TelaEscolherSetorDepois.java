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

public class TelaEscolherSetorDepois extends javax.swing.JFrame {

    Connection conn = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    public TelaEscolherSetorDepois() {
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Selecionar Setor");
        setAlwaysOnTop(true);
        setFocusable(false);
        setUndecorated(true);
        setResizable(false);

        cboSetor.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cboSetor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " " }));
        cboSetor.setFocusable(false);
        cboSetor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboSetorActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Dubai", 1, 16)); // NOI18N
        jLabel1.setText("Selecione o setor");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(cboSetor, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboSetor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

            TelaDeEficienciaDepois cadastro = new TelaDeEficienciaDepois();
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

            //cadastro.setExtendedState(MAXIMIZED_BOTH);
            cadastro.setVisible(true);
            cadastro.lblBancada.setText(bancada);
            cadastro.txtTempoOperacional.setText(setorTempo);
            cadastro.lblSetorNome.setText(setor);
            
            if(setorTipo.equals("INDIVIDUAL")){
                cadastro.btnAddFunc.setVisible(false);
                cadastro.btnRemoverFunc.setVisible(false);
                cadastro.jScrollPane3.setVisible(false);
                cadastro.lblF.setVisible(false);
            }
            else{
                cadastro.cboFuncionario.removeAllItems();
                cadastro.buscarLinhas();
            }
                

            cadastro.nomePrimarioSetor = cadastro.lblSetorNome.getText();
            
            this.dispose();
        }
    }//GEN-LAST:event_cboSetorActionPerformed

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
            java.util.logging.Logger.getLogger(TelaEscolherSetorDepois.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaEscolherSetorDepois.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaEscolherSetorDepois.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaEscolherSetorDepois.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaEscolherSetorDepois().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cboSetor;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
