/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package br.com.prjRend.telas;

import br.com.prjRend.dal.ModuloConexao;
import br.com.prjRend.model.PausaPlanejada;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import br.com.prjRend.telas.TelaDeEficiencia;
import java.awt.Color;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;

public class TelaPausaPlanejadaDepois extends javax.swing.JFrame {

    
    Connection conn = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    
    private TelaDeEficienciaDepois telaDeEficienciaDepois;
     boolean pausaExiste = false;
    
    
    public TelaPausaPlanejadaDepois(TelaDeEficienciaDepois te) {
        this.telaDeEficienciaDepois = te;
        initComponents();
        
        ////////////////////////////// importacao do icone das janelas
        ImageIcon icon = new ImageIcon(TelaEntrada.class.getResource("/br/com/prjRend/icones/icon.png"));
        this.setIconImage(icon.getImage());
        ////////////////////////////// importacao do icone das janelas
        
        lblAviso.setVisible(false);
    }
    
    
    
    String tempoPausa =  null;
    
    public void buscarPausas() {
        
        
        String nomeDoSetor = telaDeEficienciaDepois.lblSetorNome.getText();
        String sqlObterPausas = "select pausa_planejada_nome, pausa_planejada_minutos from efi_pausas_planejadas JOIN efi_setores ON efi_pausas_planejadas.setor_id = efi_setores.setor_id where setor_nome = ?";

        conn = ModuloConexao.conector();

        try {
            pst = conn.prepareStatement(sqlObterPausas);
            pst.setString(1, nomeDoSetor);
            rs = pst.executeQuery();

           cboPausaPlanejada.removeAllItems();
           cboPausaPlanejada.addItem("");
            
           while (rs.next()) {
                cboPausaPlanejada.addItem(rs.getString(1));
            }
            
            System.out.println(rs.getString(2));
            
            
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
    
    
    String novaPausaString = null;
    public void obterTempo(){
        
        Object item = cboPausaPlanejada.getSelectedItem();
        novaPausaString = item.toString();
        
        if(item == null || item.toString().equals(null) || item.toString().equals("") ){
            lblAviso.setVisible(true);
            return;
        }else{
        
        
        String pausa = cboPausaPlanejada.getSelectedItem().toString();
        String sql = "select pausa_planejada_minutos from efi_pausas_planejadas JOIN efi_setores ON efi_pausas_planejadas.setor_id = efi_setores.setor_id where setor_nome = ? and pausa_planejada_nome = ?";
        
        conn = ModuloConexao.conector();
        
        try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, telaDeEficienciaDepois.lblSetorNome.getText());
            pst.setString(2, item.toString());
            rs = pst.executeQuery();

            
           if (rs.next()) {
               tempoPausa = rs.getString(1);
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
        
        enviarDados();
    }
    
   public void enviarDados(){
       
       Double tempoOperacional = Double.valueOf(telaDeEficienciaDepois.txtTempoOperacional.getText());
       String NomeNovaPausa = novaPausaString;
       Double TempoNovaPausa = Double.valueOf(tempoPausa);
       
       for (PausaPlanejada p : telaDeEficienciaDepois.pausas){
           if(p.nome.equals(NomeNovaPausa)){
               JOptionPane.showMessageDialog(null, "Esta pausa já foi adicionada");
               pausaExiste = true;
               return;
           }
       }
       
       if(pausaExiste == false){
           String novoTempoOperacional = String.valueOf(tempoOperacional -  TempoNovaPausa);
           /////////////////////////////////////////////////////////////////////// validar se tem tempo suficiente
           
           
           double tempoDecorrido = telaDeEficienciaDepois.tempoTotal;
           
           if((tempoDecorrido + TempoNovaPausa) > tempoOperacional){
               JOptionPane.showMessageDialog(null, "Tempo restante insuficiente");
               return;
           }else{
           telaDeEficienciaDepois.txtTempoOperacional.setText(novoTempoOperacional);
           }
           
           /////////////////////////////////////////////////////////////////////// validar se tem tempo suficiente
           PausaPlanejada pa = new PausaPlanejada(NomeNovaPausa);
           telaDeEficienciaDepois.pausas.add(pa);
           this.dispose();
       }
       
       telaDeEficienciaDepois.calcularEficiencia();
       pausaExiste = false;
}

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        cboPausaPlanejada = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        lblAviso = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Nova pausa Planejada");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setText("Causa:");

        cboPausaPlanejada.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        cboPausaPlanejada.setFocusable(false);
        cboPausaPlanejada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboPausaPlanejadaActionPerformed(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(0, 0, 0));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Confirmar");
        jButton1.setBorderPainted(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        lblAviso.setBackground(new java.awt.Color(0, 0, 0));
        lblAviso.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lblAviso.setText("Nova pausa planejada");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 112, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblAviso)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 196, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addComponent(jButton1)
                                    .addGap(370, 370, 370))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addComponent(cboPausaPlanejada, javax.swing.GroupLayout.PREFERRED_SIZE, 584, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(206, 206, 206)))))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(61, 61, 61)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboPausaPlanejada, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(63, 63, 63)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(lblAviso)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(169, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        buscarPausas();
    }//GEN-LAST:event_formWindowActivated

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
       if(cboPausaPlanejada.getSelectedItem() == ""){
           lblAviso.setText("SELECIONE UMA PAUSA PARA ADICIONAR");
           lblAviso.setVisible(true);
       }
        obterTempo();
        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void cboPausaPlanejadaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboPausaPlanejadaActionPerformed
      
      lblAviso.setVisible(false);
    }//GEN-LAST:event_cboPausaPlanejadaActionPerformed

    /**
     * @param args the command line arguments
     */
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
            java.util.logging.Logger.getLogger(TelaPausaPlanejadaDepois.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaPausaPlanejadaDepois.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaPausaPlanejadaDepois.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaPausaPlanejadaDepois.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
              
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cboPausaPlanejada;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblAviso;
    // End of variables declaration//GEN-END:variables
}
