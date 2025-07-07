package br.com.prjRend.telas;

import br.com.prjRend.dal.ModuloConexao;
import java.awt.Color;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;
import net.proteanit.sql.DbUtils;

public class TelaDeStatus extends javax.swing.JInternalFrame {

    Connection conn = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    public TelaDeStatus() {
        initComponents();
        setarFonteDasTabelas();
        iniciarTimer();
        atualizarStatus();
       tblStatus.getParent().setBackground(Color.BLACK);
        
    }
    
    
    
    
    public void setarFonteDasTabelas() {
        

        tblStatus.getTableHeader().setFont(new Font("Arial", Font.BOLD, 20));
        tblStatus.getTableHeader().setBackground(Color.WHITE);
        
        
        tblStatus.setFont(new Font("Arial", Font.BOLD, 20));
        tblStatus.setRowHeight(50);
        
        
        tblStatus.setBackground(Color.BLACK);
        tblStatus.setForeground(Color.WHITE);

          
    }
    
    public void atualizarStatus() {
    String sql = "SELECT bancada AS LINHA, status AS STATUS, disponibilidade AS DISPONIBILIDADE, performance AS PERFORMANCE, qualidade AS QUALIDADE, quantidade_funcionarios AS FUNCIONARIOS, TO_CHAR(momento, 'DD/MM') || ' --- ' || TO_CHAR(momento, 'HH24:MI') AS LAST_UPDATED FROM efi_status_bancadas";
    
    conn = ModuloConexao.conector();
    
    try {
        pst = conn.prepareStatement(sql);
        rs = pst.executeQuery();
        
        // Define o modelo diretamente, sem o loop
        tblStatus.setModel(DbUtils.resultSetToTableModel(rs));

    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Erro ao atualizar status: " + e.getMessage());
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
    
    
    
    public void zerarStatus() {
    String sql = "TRUNCATE TABLE efi_status_bancadas";
    Connection conn = null;
    Statement stmt = null;

    try {
        conn = ModuloConexao.conector();
        if (conn == null) {
            JOptionPane.showMessageDialog(null, "Erro: Falha na conexão com o banco de dados.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        stmt = conn.createStatement();
        stmt.executeUpdate(sql);
        JOptionPane.showMessageDialog(null, "Dados limpos. Aguarde alguns segundos para perceber as mudanças.");

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Erro ao limpar os dados: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    } finally {
        try {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao fechar conexão: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}


    
    
    
    
    public void iniciarTimer() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable countTask = new Runnable() {
            

            @Override
            public void run() {
                // Usando SwingUtilities.invokeLater para garantir que a atualização
                // do componente seja feita na UI thread
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        atualizarStatus();
                    }
                });
                
            }
        };

        // A tarefa será executada a cada 1 segundo, começando imediatamente
        scheduler.scheduleAtFixedRate(countTask, 0, 10, TimeUnit.SECONDS);
    }

   

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblStatus = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle(" ");
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

        tblStatus = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tblStatus.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        tblStatus.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblStatus.setFocusable(false);
        jScrollPane1.setViewportView(tblStatus);

        jButton1.setText("Zerar Status");
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
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1052, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 586, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );

        setBounds(0, 0, 1076, 663);
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        tblStatus.clearSelection();
    }//GEN-LAST:event_formMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    zerarStatus();
    }//GEN-LAST:event_jButton1ActionPerformed

    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaDeStatus().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblStatus;
    // End of variables declaration//GEN-END:variables
}
