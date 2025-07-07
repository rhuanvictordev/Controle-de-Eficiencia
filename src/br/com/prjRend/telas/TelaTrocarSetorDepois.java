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
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class TelaTrocarSetorDepois extends javax.swing.JFrame {
    
    private static TelaDeEficienciaDepois telaDeEficienciaDepois;

    Connection conn = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    
    public TelaTrocarSetorDepois(TelaDeEficienciaDepois telaDeEficienciaDepois) {
        this.telaDeEficienciaDepois = telaDeEficienciaDepois;
        initComponents();
    }

    

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
    
    public String obterTipo(String nome){
        
        conn = ModuloConexao.conector();
        String sql = "select setor_tipo from efi_setores where setor_nome = ?";
        
        try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, nome);
            rs = pst.executeQuery();
                if(rs.next()){
                    return rs.getString(1);
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
//        
        return "INDIVIDUAL";
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
        jLabel1.setText("Selecione outro setor");

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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
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
        
        if(cboSetor.getSelectedItem() == null || cboSetor.getSelectedItem().toString().isEmpty() || cboSetor.getSelectedItem().toString() == " "){
            JOptionPane.showMessageDialog(null, "Selecione um setor na lista");
            return;
        }
        
        
        String setor = cboSetor.getSelectedItem().toString();
        telaDeEficienciaDepois.lblSetorNome.setText(setor);
        telaDeEficienciaDepois.cboTrabalho.removeAllItems();
        telaDeEficienciaDepois.buscarTrabalhos();
        telaDeEficienciaDepois.buscarFuncionarios();
        String tipo = obterTipo(setor);
        
        if(tipo.equals("COMPOSTO")){
            telaDeEficienciaDepois.cboFuncionario.removeAllItems();
            telaDeEficienciaDepois.buscarLinhas();
            
            telaDeEficienciaDepois.btnAddFunc.setVisible(true);
            telaDeEficienciaDepois.btnRemoverFunc.setVisible(true);
            telaDeEficienciaDepois.jScrollPane3.setVisible(true);
            telaDeEficienciaDepois.lblF.setVisible(true);
        }else{
            telaDeEficienciaDepois.btnAddFunc.setVisible(false);
            telaDeEficienciaDepois.btnRemoverFunc.setVisible(false);
            telaDeEficienciaDepois.jScrollPane3.setVisible(false);
            telaDeEficienciaDepois.lblF.setVisible(false);
        }
        
        telaDeEficienciaDepois.buscarPausas();
        telaDeEficienciaDepois.buscarTo();
        telaDeEficienciaDepois.limparArrayPausasPlanejadas();
        telaDeEficienciaDepois.setVisible(true);
        this.dispose();
        
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
            java.util.logging.Logger.getLogger(TelaTrocarSetorDepois.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaTrocarSetorDepois.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaTrocarSetorDepois.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaTrocarSetorDepois.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaTrocarSetorDepois(telaDeEficienciaDepois).setVisible(true);
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cboSetor;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
