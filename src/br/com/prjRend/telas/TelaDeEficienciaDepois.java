package br.com.prjRend.telas;

import br.com.prjRend.dal.ModuloConexao;
import br.com.prjRend.model.Improducao;
import br.com.prjRend.model.PausaPlanejada;
import br.com.prjRend.model.Peca;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

public class TelaDeEficienciaDepois extends javax.swing.JFrame {

    Connection conn = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    public TelaDeEficienciaDepois() {
        initComponents();
        
        ////////////////////////////// importacao do icone das janelas
        ImageIcon icon = new ImageIcon(TelaEntrada.class.getResource("/br/com/prjRend/icones/icon.png"));
        this.setIconImage(icon.getImage());
        ////////////////////////////// importacao do icone das janelas
        
        carregarEventoDaTabela();
        carregarEventoDaTabelaImproducao();
        carregarEventoDaTabelaFuncionarios();
        // timer que conta o tempo de trabalho
        iniciarTimer();
        setarFonteDasTabelas();
        CheckboxEntradaAutomatica.setSelected(false);
        // timer que é disparado para dar tempo do leitor de codigo de barras capturar o codigo completo antes de pesquisar no combobox codigo
        configurarTimer();
        txtQuantidadeAutomatica.setVisible(false);
        lblqntd.setVisible(false);
        salvo = true;
        AutoCompleteDecorator.decorate(cboTrabalho);
        AutoCompleteDecorator.decorate(cboImproducaoTipo);
        AutoCompleteDecorator.decorate(cboFuncionario);
        cboCodigo.setEditable(true);
        AutoCompleteDecorator.decorate(cboCodigo);
        definirOspinnerComoNaoEditavel(spinQuantidadeFuncionarios);
        definirOspinnerComoNaoEditavel(spinRuins);
        txtCodigoLeitor.setVisible(false);
        lblBancada.setVisible(false);
        cboImproducaoTipo.setMaximumRowCount(30);
       
        ocultarComponentes();
    }

    //declaração de variáveis utilizadas
    boolean salvo;
    boolean logoclicada = false;
    boolean tempoClicado = false;
    boolean pausaClicada = false;
    boolean trabalhoEncerrado = false;
   

    String nomePrimarioSetor;
    String codigo;
    int quantidade;
    String trabalho;

    String tipo;
    double tempo;

    ArrayList<Peca> pecas = new ArrayList<>();
    ArrayList<Improducao> paradas = new ArrayList<>();
    long tempoTotal = 0;
    ArrayList<PausaPlanejada> pausas = new ArrayList<>();

    private Timer timer;
    
    public void ocultarComponentes(){
        lblBancada.setVisible(false);
        btnPausar.setVisible(false);
        CheckboxEntradaAutomatica.setVisible(false);
        btnEncerrarProducao.setVisible(false);
        txtCodigoLeitor.setVisible(false);
        lblqntd.setVisible(false);
        txtQuantidadeAutomatica.setVisible(false);
    }

    public void definirOspinnerComoNaoEditavel(JSpinner spinQuantidadeFuncionarios) {
        // Obtendo o editor do JSpinner
        JComponent editor = spinQuantidadeFuncionarios.getEditor();

        if (editor instanceof JSpinner.DefaultEditor) {
            JFormattedTextField textField = ((JSpinner.DefaultEditor) editor).getTextField();
            textField.setEditable(false);  // Desativa a edição direta
            textField.setFocusable(false); // Remove o foco para evitar o cursor piscando
        }
    }

    public void receberTempoDaPausa(String tempo) {
       txtImproducaoTempo.setText(tempo);

    }

    public void definirStatusProduzindo() {
    }

    public void definirStatusPausado() {
    }

    public void definirStatusParado() {
    }

    public void obterOperacoes() {
        conn = ModuloConexao.conector();
        cboCodigo.removeAllItems();

        String sqlObterPecas = "select peca_descricao from efi_tempos JOIN efi_trabalhos ON efi_tempos.trabalho_id = efi_trabalhos.trabalho_id JOIN efi_pecas ON efi_tempos.peca_id = efi_pecas.peca_id WHERE trabalho_nome = ?";
        String sqlObterPecasCodigo = "select peca_codigo from efi_tempos JOIN efi_trabalhos ON efi_tempos.trabalho_id = efi_trabalhos.trabalho_id JOIN efi_pecas ON efi_tempos.peca_id = efi_pecas.peca_id WHERE trabalho_nome = ?";

        if (CheckboxEntradaAutomatica.isSelected()) {
            try {
                pst = conn.prepareStatement(sqlObterPecasCodigo);
                pst.setString(1, cboTrabalho.getSelectedItem().toString());
                rs = pst.executeQuery();
                cboCodigo.addItem("");
                while (rs.next()) {
                    cboCodigo.addItem(rs.getString(1));
                }
                pst = null;
                rs = null;
            } catch (Exception e) {
                return;
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

        } else {

            try {
                pst = conn.prepareStatement(sqlObterPecas);
                pst.setString(1, cboTrabalho.getSelectedItem().toString());
                rs = pst.executeQuery();
                cboCodigo.addItem("");
                while (rs.next()) {
                    cboCodigo.addItem(rs.getString(1));
                }
                pst = null;
                rs = null;
            } catch (Exception e) {
                return;
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

    }

    public void setarFonteDasTabelas() {

        tblProducao.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tblProducao.setFont(new Font("Arial", Font.BOLD, 12));
        tblProducao.setRowHeight(14);

        tblImproducao.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tblImproducao.setFont(new Font("Arial", Font.BOLD, 12));
        tblImproducao.setRowHeight(14);
        
        tblFunc.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tblFunc.setFont(new Font("Arial", Font.BOLD, 12));
        tblFunc.setRowHeight(14);

        tblFunc.setSelectionBackground(Color.BLACK);  // Cor de fundo da seleção (preto)
        tblFunc.setSelectionForeground(Color.WHITE);  // Cor do texto da seleção (branco)
        
        tblImproducao.setSelectionBackground(Color.BLACK);  // Cor de fundo da seleção (preto)
        tblImproducao.setSelectionForeground(Color.WHITE);  // Cor do texto da seleção (branco)

        tblProducao.setSelectionBackground(Color.BLACK);  // Cor de fundo da seleção (preto)
        tblProducao.setSelectionForeground(Color.WHITE);  // Cor do texto da seleção (branco)

    }

    public void limparSelecaodasTabelas() {
        tblProducao.clearSelection();
        tblImproducao.clearSelection();
    }

    public void carregarEventoDaTabela() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                // Cria a instância de TelaPrincipal

                // Obtém o modelo da JTable tblPecas
                tblProducao.getModel().addTableModelListener(new TableModelListener() {
                    public void tableChanged(TableModelEvent e) {
                        // Verifica se a alteração é de uma célula (e não inserção/remoção de linha)
                        if (e.getType() == TableModelEvent.UPDATE || e.getType() == TableModelEvent.INSERT) {

                            calcularEficiencia();
                            pecas.removeAll(pecas);
                            paradas.removeAll(paradas);
                        }
                    }
                }
                );
            }
        });
    }

    public void carregarEventoDaTabelaImproducao() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {

                // Obtém o modelo da JTable tblPecas
                tblImproducao.getModel().addTableModelListener(new TableModelListener() {
                    public void tableChanged(TableModelEvent e) {
                        // Verifica se a alteração é de uma célula (e não inserção/remoção de linha)
                        if (e.getType() == TableModelEvent.UPDATE || e.getType() == TableModelEvent.INSERT) {
                            calcularEficiencia();
                            paradas.removeAll(paradas);
                        }
                    }
                }
                );
            }
        });
    }
    
    
    public void carregarEventoDaTabelaFuncionarios() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {

                // Obtém o modelo da JTable tblPecas
                tblFunc.getModel().addTableModelListener(new TableModelListener() {
                    public void tableChanged(TableModelEvent e) {
                        // Verifica se a alteração é de uma célula (e não inserção/remoção de linha)
                        if (e.getType() == TableModelEvent.UPDATE || e.getType() == TableModelEvent.INSERT || e.getType() == TableModelEvent.DELETE) {
                            int c = tblFunc.getRowCount();
                            spinQuantidadeFuncionarios.setValue(c);
                            calcularEficiencia();
                        }
                    }
                }
                );
            }
        });
    }
    
    

    public void adicionar_producao() {

        Object codigoSelecionado = cboCodigo.getSelectedItem();

        if (codigoSelecionado == null & codigoSelecionado.toString().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Selecione uma operação na lista");
            return;
        } else {

            if (CheckboxEntradaAutomatica.isSelected()) {

                codigo = codigoSelecionado.toString();

            } else {

                String textoDigitado = cboCodigo.getEditor().getItem().toString().trim();
                boolean encontrado = false;

                for (int i = 0; i < cboCodigo.getItemCount(); i++) {
                    if (cboCodigo.getItemAt(i).toString().equals(textoDigitado)) {
                        encontrado = true;
                        break;
                    }
                }

                if (encontrado == true) {
                    codigo = codigoSelecionado.toString();
                } else {
                    JOptionPane.showMessageDialog(null, "A operação " + textoDigitado + " não existe!");
                    return;
                }

            }

        }

        String quantidade = txtQuantidade.getText().trim();

        if (CheckboxEntradaAutomatica.isSelected()) {
            quantidade = txtQuantidadeAutomatica.getSelectedItem().toString();
        }

        String trabalho = null;
        try {
            trabalho = cboTrabalho.getSelectedItem().toString();
        } catch (Exception e) {
        }

        try {
            // Valida se os campos obrigatórios foram preenchidos
            if (codigo == null || codigo.equals("") || quantidade == null || quantidade.equals("") || quantidade.equals(" ")) {
                return;
            }

            // Verifica se a quantidade é um número inteiro
            int quantidadeInt = Integer.parseInt(quantidade);

            // Obtém o modelo da tabela
            DefaultTableModel model = (DefaultTableModel) tblProducao.getModel();
            boolean trabalhoCodigoExiste = false;

            // Percorre todas as linhas da tabela
            for (int i = 0; i < model.getRowCount(); i++) {
                String trabalhoNaTabela = model.getValueAt(i, 0).toString();
                String codigoNaTabela = model.getValueAt(i, 1).toString();

                // Verifica se o trabalho e o código já existem
                if (trabalhoNaTabela.equals(trabalho) && codigoNaTabela.equals(codigo)) {
                    // Se já existir, soma a quantidade na linha existente
                    int quantidadeExistente = Integer.parseInt(model.getValueAt(i, 2).toString());
                    int novaQuantidade = quantidadeExistente + quantidadeInt;
                    model.setValueAt(novaQuantidade, i, 2);  // Atualiza a quantidade na linha
                    trabalhoCodigoExiste = true;
                    break;  // Sai do loop, pois a peça foi encontrada    
                }
            }

            // Se o trabalho e código não existirem, adiciona uma nova linha
            if (!trabalhoCodigoExiste) {
                model.addRow(new Object[]{trabalho, codigo, quantidadeInt});
            }

            // Limpa os campos de entrada
            cboCodigo.removeAllItems();
            txtQuantidade.setText(null);

        } catch (NumberFormatException e) {
            // Exibe mensagem de erro se a quantidade não for um número inteiro
            JOptionPane.showMessageDialog(null, "Informe uma quantidade válida");
            return;
        }

        cboCodigo.removeAllItems();
        String trabalhoSelecionado = cboTrabalho.getSelectedItem().toString();
        cboTrabalho.setSelectedItem("");
        cboTrabalho.setSelectedItem(trabalhoSelecionado);

        if (CheckboxEntradaAutomatica.isSelected()) {
            txtCodigoLeitor.requestFocus();
        }

    }

    public void adicionar_improducao() {

        double somaDosTemposDaTabelaMaisTxtTempo = 0.0;

        double tempoDetrabalhoEmMinutos = ((double) seconds) / 60.0;
        double txtDaPausa = 0.0;

        try {
            // Converte o valor digitado e arredonda para 2 casas decimais
            txtDaPausa = Double.parseDouble(txtImproducaoTempo.getText().replace(",", "."));
            txtDaPausa = Math.round(txtDaPausa * 100.0) / 100.0;

            somaDosTemposDaTabelaMaisTxtTempo = 0.0; // Inicializa a variável corretamente

            for (int i = 0; i < tblImproducao.getRowCount(); i++) {
                try {
                    double tempo = Double.parseDouble(tblImproducao.getValueAt(i, 1).toString());
                    somaDosTemposDaTabelaMaisTxtTempo += tempo;
                } catch (NumberFormatException e) {
                    System.out.println("Erro ao converter tempo na linha " + i);
                }
            }

            somaDosTemposDaTabelaMaisTxtTempo += txtDaPausa;

            // System.out.println("Soma dos tempos da tabela mais txtTempo: " + somaDosTemposDaTabelaMaisTxtTempo);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Insira um valor válido!");
            return;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

        //System.out.println("Trabalho: " + tempoDetrabalhoEmMinutos);
        //System.out.println("pausa: " + somaDosTemposDaTabelaMaisTxtTempo);
        if (tempoDetrabalhoEmMinutos <= somaDosTemposDaTabelaMaisTxtTempo) {
            JOptionPane.showMessageDialog(null, "A soma dos tempos de pausa não pode ser maior ou igual ao tempo de trabalho!", "Erro", JOptionPane.INFORMATION_MESSAGE);

            return;
        } else {

            String qntdPessoas = "1";
            String tipo = null;

            Object TipoSelecionado = cboImproducaoTipo.getSelectedItem();
            if (TipoSelecionado == null || TipoSelecionado.toString().trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Selecione uma justificativa na lista");
                return;
            } else {
                tipo = TipoSelecionado.toString();
            }

            if (tipo.equals("") || tipo.equals(null)) {
                JOptionPane.showMessageDialog(null, "Selecione uma justificativa na lista");
                return;
            }

            String tempo = txtImproducaoTempo.getText().replace(",", ".");
            double tempo2 = 0.0;

            try {
                tempo2 = (Double.parseDouble(tempo) * Integer.parseInt(qntdPessoas));
                tempo2 = Math.round(tempo2 * 1000.0) / 1000.0;

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Informe um valor válido no campo minutos!");
                txtImproducaoTempo.setText(null);
                return;
            }

            if (tipo.equals(" ") || tempo.equals("0")) {
                JOptionPane.showMessageDialog(null, "insira um valor maior que zero no campo minutos!");
                return;
            } else {

                DefaultTableModel model = (DefaultTableModel) tblImproducao.getModel();

                boolean peçaExiste = false;

                for (int i = 0; i < model.getRowCount(); i++) {
                    // Verifica se o código da peça já existe na tabela
                    if (model.getValueAt(i, 0).equals(tipo)) {
                        // Se já existir, soma a quantidade na linha existente
                        double quantidadeExistente = Double.parseDouble(model.getValueAt(i, 1).toString());
                        double novaQuantidade = quantidadeExistente + tempo2;
                        novaQuantidade = Math.round(novaQuantidade * 1000.0) / 1000.0;
                        model.setValueAt(novaQuantidade, i, 1);  // Atualiza a quantidade na linha
                        peçaExiste = true;
                        break;  // Sai do loop, já que a peça foi encontrada    
                    }
                }

                if (!peçaExiste) {
                    model.addRow(new Object[]{tipo, tempo2});
                    paradas.add(new Improducao(tipo, tempo2));
                }
            }
            txtImproducaoTempo.setText(null);
            paradas.removeAll(paradas);
            cboImproducaoTipo.setSelectedItem("");

            if (CheckboxEntradaAutomatica.isSelected()) {
                txtCodigoLeitor.requestFocus();
            }

        }
        setarTempoTotalPausa();
    }

    public void setarTempoTotalPausa() {
        double sum = 0.0;
        for (int i = 0; i < tblImproducao.getRowCount(); i++) {
            double s = Double.parseDouble(tblImproducao.getValueAt(i, 1).toString());
            sum += s;
        }

        // Calcular as horas
        int horas = (int) (sum / 60);

        // Calcular os minutos restantes
        int minutos = (int) (sum % 60);

        // Calcular os segundos a partir da parte decimal dos minutos
        int segundos = (int) Math.round((sum % 1) * 60);

        // Exibir no formato "h:mm:ss"
        lblTempoTotalPausa.setText(horas + ":" + String.format("%02d", minutos) + ":" + String.format("%02d", segundos));
    }

        double quantidadeBoas = 0.0;
        double quantidadeRefugo = 0.0;
        double quantidadeDePecasProduzidas = 0.0;
    
        
        
    public void calcularEficiencia() {
        
        tempoTotal = seconds / 60;
        double somaDosTemposDasPecas = 0.0;
        double somaDosTemposDasParadas = 0.0;

        pecas.clear(); // Limpa a lista em vez de remover item por item
        paradas.clear();

        String sql = "SELECT tempo FROM efi_tempos "
                + "JOIN efi_trabalhos ON efi_tempos.trabalho_id = efi_trabalhos.trabalho_id "
                + "JOIN efi_pecas ON efi_tempos.peca_id = efi_pecas.peca_id "
                + "WHERE trabalho_nome = ? AND peca_descricao = ?";

        // Abrir conexão apenas uma vez
        try (Connection conn = ModuloConexao.conector(); PreparedStatement pst = conn.prepareStatement(sql)) {

            for (int i = 0; i < tblProducao.getRowCount(); i++) {
                String codigo = tblProducao.getValueAt(i, 1).toString();
                String trabalho = tblProducao.getValueAt(i, 0).toString();
                int quantidade = 0;

                try {
                    quantidade = Integer.parseInt(tblProducao.getValueAt(i, 2).toString());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Informe uma quantidade válida!");
                    continue; // Pula essa iteração caso a quantidade seja inválida
                }

                double minutos = 0.0;
                pst.setString(1, trabalho);
                pst.setString(2, codigo);

                try (ResultSet rs = pst.executeQuery()) { // Fecha automaticamente
                    if (rs.next()) {
                        minutos = rs.getDouble("tempo");
                    }
                }

                pecas.add(new Peca(codigo, minutos, quantidade, trabalho));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao buscar tempos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }

        // Processamento das paradas
        for (int i = 0; i < tblImproducao.getRowCount(); i++) {
            try {
                String tipo = tblImproducao.getValueAt(i, 0).toString();
                double tempo = Double.parseDouble(tblImproducao.getValueAt(i, 1).toString());
                paradas.add(new Improducao(tipo, tempo));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Tempo de improdução inválido!");
            }
        }

        // Cálculo do tempo produtivo
        for (Peca peca : pecas) {
            somaDosTemposDasPecas += peca.quantidade * peca.tempoNecessario;
        }

        for (Improducao item : paradas) {
            somaDosTemposDasParadas += item.tempo;
        }

        double tempoDesejadoParaProduzir = Double.parseDouble(txtTempoOperacional.getText());
        double disponibilidadeInversa = 0.0;
        double disponibilidadeReal = 0.0;
        double performance = 0.0;
        double qualidade = 0.0;
        double ole = 0.0;
        quantidadeDePecasProduzidas = 0.0;
        double divisor = tempoTotal - somaDosTemposDasParadas;
        
        double quantidadeBoasDouble = 0;
        
        

        try {
            quantidadeRefugo = Double.valueOf(spinRuins.getValue().toString());
        } catch (Exception e) {
            quantidadeRefugo = 0;
        }

        
        if (tempoDesejadoParaProduzir > 0) {
            disponibilidadeInversa = (1.0 - (tempoTotal / tempoDesejadoParaProduzir)) * 100.0;
        } else {
            disponibilidadeInversa = 0;
        }
        
        
        if (tempoDesejadoParaProduzir > 0) {
            disponibilidadeReal = ((tempoTotal - somaDosTemposDasParadas) / tempoDesejadoParaProduzir) * 100;
        } else {
            disponibilidadeReal = 0;
        }
        
        
        
        if (tempoTotal > 0) {
            performance = (somaDosTemposDasPecas / (tempoTotal - somaDosTemposDasParadas)) * 100;
        } else {
            performance = 0;
        }

        
        for (int i = 0; i < tblProducao.getRowCount(); i++) {
        quantidadeBoas = Integer.parseInt(tblProducao.getValueAt(i, 2).toString());
        quantidadeBoasDouble += quantidadeBoas;
        }
        
        quantidadeDePecasProduzidas = quantidadeBoasDouble + quantidadeRefugo;
        
        performance = (performance / Double.valueOf(spinQuantidadeFuncionarios.getValue().toString()));

        
        if(quantidadeDePecasProduzidas > 0){
        qualidade = ((quantidadeBoasDouble / quantidadeDePecasProduzidas) * 100.0);
        }
        
        ole = disponibilidadeReal * performance * qualidade;
        ole = ole / 10000.0;
        
        //System.out.println("Quantidade de Pecas Boas: " + quantidadeBoasDouble);
        //System.out.println("Quantidade de Pecas Ruins: " + quantidadeRefugo);
        //System.out.println("Quantidade Total Produzida: " + quantidadeDePecasProduzidas);
        //System.out.println("Qualidade: " + qualidade);
        
        lblDisponibilidade.setText(String.format("%.2f%%", disponibilidadeReal));
        lblDisponibilidade.setForeground(Color.CYAN);
        
        lblDisponibilidadeInversa.setText(String.format("%.2f%%", disponibilidadeInversa));
        lblDisponibilidadeInversa.setForeground(Color.CYAN);
        
        lblPerformance.setText(String.format("%.2f%%", performance));
        
        lblQualidade.setText(String.format("%.2f%%", qualidade));
        lblQualidade.setForeground(Color.pink);
        
        lblOLE.setText(String.format("%.2f%%", ole));
        
        
        
        if (performance < 70.0) {
            lblPerformance.setForeground(Color.RED);
        } else if (performance >= 70.0 && performance <= 85.0) {
            lblPerformance.setForeground(Color.YELLOW);
        } else if (lblPerformance.getText().equals("NaN%")) {
            lblPerformance.setText("0,00%");
            lblPerformance.setForeground(Color.CYAN);
        } else {
            lblPerformance.setForeground(Color.GREEN);
        }
        
        
        if (qualidade > 0.0 && qualidade < 90.0){
            lblQualidade.setForeground(Color.YELLOW);
        }else if (qualidade > 90.0){
            lblQualidade.setForeground(Color.GREEN);
        }else{
            lblQualidade.setForeground(Color.CYAN);
        }
        
        //System.out.println("OLE: " + ole);
        if (ole < 70.0) {
            lblOLE.setForeground(Color.RED);
        } else if (ole >= 70.0 && ole <= 85.0) {
            lblOLE.setForeground(Color.YELLOW);
        } else if (lblOLE.getText().equals("NaN%")) {
            lblOLE.setText("0,00%");
            lblOLE.setForeground(Color.CYAN);
        } else {
            lblOLE.setForeground(Color.GREEN);
        }
        
        if(disponibilidadeInversa < 30.0){
            lblDisponibilidadeInversa.setForeground(Color.CYAN);
        }else{
            lblDisponibilidadeInversa.setForeground(Color.CYAN);
        }

    }
    
    public void buscarTrabalhos(){
      
    String sqlObterTrabalhos = "select trabalho_nome from efi_trabalhos JOIN efi_setores ON efi_trabalhos.setor_id = efi_setores.setor_id where setor_nome = ?";
    
    
    try (Connection conn = ModuloConexao.conector(); PreparedStatement pst = conn.prepareStatement(sqlObterTrabalhos)) {

            pst.setString(1, lblSetorNome.getText());
        
            try (ResultSet rs = pst.executeQuery()) { // Fecha automaticamente
                    cboTrabalho.addItem("");
                while (rs.next()) {
                        cboTrabalho.addItem(rs.getString(1));
                    }
                }
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
    
    public void buscarFuncionarios(){
        
        cboFuncionario.removeAllItems();
      
    String sqlObterFuncionarios = "select funcionario_nome from efi_funcionarios JOIN efi_setores ON efi_funcionarios.setor_id = efi_setores.setor_id where setor_nome = ?";
    
    
    try (Connection conn = ModuloConexao.conector(); PreparedStatement pst = conn.prepareStatement(sqlObterFuncionarios)) {

            pst.setString(1, lblSetorNome.getText());
        
            try (ResultSet rs = pst.executeQuery()) { // Fecha automaticamente
                    cboFuncionario.addItem("");
                while (rs.next()) {
                        cboFuncionario.addItem(rs.getString(1));
                    }
                }
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
    
    
    public void buscarLinhas(){
        
        cboFuncionario.removeAllItems();
      
    String sqlObterFuncionarios = "select linha_nome from efi_linhas";
    
    
    try (Connection conn = ModuloConexao.conector(); PreparedStatement pst = conn.prepareStatement(sqlObterFuncionarios)) {

        
            try (ResultSet rs = pst.executeQuery()) { // Fecha automaticamente
                    cboFuncionario.addItem("");
                while (rs.next()) {
                        cboFuncionario.addItem(rs.getString(1));
                    }
                }
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
    
    
    
    
    public void buscarPausas(){
        
        cboImproducaoTipo.removeAllItems();
      
    String sqlObterFuncionarios = "select pausa_nome from efi_pausas JOIN efi_setores ON efi_pausas.setor_id = efi_setores.setor_id where setor_nome = ?";
    
    
    try (Connection conn = ModuloConexao.conector(); PreparedStatement pst = conn.prepareStatement(sqlObterFuncionarios)) {

            pst.setString(1, lblSetorNome.getText());
        
            try (ResultSet rs = pst.executeQuery()) { // Fecha automaticamente
                    cboImproducaoTipo.addItem("");
                while (rs.next()) {
                        cboImproducaoTipo.addItem(rs.getString(1));
                    }
                }
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
    
    public void buscarTo(){
        
        cboImproducaoTipo.removeAllItems();
      
    String sqlObterTo = "select setor_tempo_operacional from efi_setores where setor_nome = ?";
    
    
    try (Connection conn = ModuloConexao.conector(); PreparedStatement pst = conn.prepareStatement(sqlObterTo)) {

            pst.setString(1, lblSetorNome.getText());
        
            try (ResultSet rs = pst.executeQuery()) { // Fecha automaticamente
                    cboImproducaoTipo.addItem("");
                if (rs.next()) {
                        txtTempoOperacional.setText(rs.getString(1));
                    }
                }
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
    
        buscarPausas();
    }
    
    public void limparArrayPausasPlanejadas(){
        pausas.clear();
    }
    
    

    long seconds = 0;

    private ScheduledExecutorService scheduler; // Armazena o scheduler

    public void iniciarTimer() {
        scheduler = Executors.newScheduledThreadPool(1); // Armazena o scheduler

        Runnable countTask = new Runnable() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        long horas = seconds / 3600;
                        long minutos = (seconds % 3600) / 60;
                        long segundos = seconds % 60;

                        String tempoFormatado = String.format("%02d:%02d:%02d", horas, minutos, segundos);
                        lblDuracao.setText(tempoFormatado);
                        calcularEficiencia();
                    }
                });
                
            }
        };

        scheduler.scheduleAtFixedRate(countTask, 0, 1, TimeUnit.DAYS);
    }

    // para o tempo de trabalho
    public void pararTimerTrabalho() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

// Método para parar o timer
    public void pararTimer() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown(); // Para o timer
            scheduler = null; // Libera a referência
        }
    }

    // timer do texfield que recebe o input do leitor de codigo de barras no modo de insercao automatico
    private void configurarTimer() {
        timer = new Timer(200, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Ação a ser executada após 1 segundo de inatividade
                String codigo = txtCodigoLeitor.getText();
                txtCodigoLeitor.setText(null);
                pesquisarNoComboBox(codigo);
            }
        });

        timer.setRepeats(false); // O Timer executa apenas uma vez
    }

    private void pesquisarNoComboBox(String codigo) {
    Connection conn = null;
    PreparedStatement pstTrabalho = null;
    PreparedStatement pstPeca = null;
    ResultSet rsTrabalho = null;
    ResultSet rsPeca = null;

    try {
        conn = ModuloConexao.conector();

        // Consulta 1: Nome do trabalho
        String sqlTrabalho = "SELECT TRABALHO_NOME FROM efi_tempos JOIN efi_trabalhos ON efi_tempos.trabalho_id = efi_trabalhos.trabalho_id WHERE efi_tempos.codigobarras = ?";
        pstTrabalho = conn.prepareStatement(sqlTrabalho);
        pstTrabalho.setString(1, codigo);
        rsTrabalho = pstTrabalho.executeQuery();

        if (rsTrabalho.next()) {
            cboTrabalho.setSelectedItem(rsTrabalho.getString(1));
        }

        // Consulta 2: Nome da peça
        String sqlPeca = "SELECT PECA_DESCRICAO FROM efi_tempos JOIN efi_pecas ON efi_tempos.peca_id = efi_pecas.peca_id WHERE efi_tempos.codigobarras = ?";
        pstPeca = conn.prepareStatement(sqlPeca);
        pstPeca.setString(1, codigo);
        rsPeca = pstPeca.executeQuery();

        if (rsPeca.next()) {
            cboCodigo.setSelectedItem(rsPeca.getString(1));
            txtQuantidade.setText("1");
            adicionar_producao();
            txtCodigoLeitor.requestFocus();
        }else{
            JOptionPane.showMessageDialog(null, "Operação não cadastrada");
            return;
        }

    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        try {
            if (rsTrabalho != null) rsTrabalho.close();
            if (rsPeca != null) rsPeca.close();
            if (pstTrabalho != null) pstTrabalho.close();
            if (pstPeca != null) pstPeca.close();
            if (conn != null) conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

    
    public void verificarRegistrosExistentes(){
        String dataFormatada;
        String ft;
        Date dataSelecionada = txtData.getDate();
        Object selectedFuncionario = cboFuncionario.getSelectedItem();
        String sql = "select * from efi_atividades where DATA = ? and FT = ?";
        
        if (dataSelecionada != null) {
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yy"); // Ex: 12/06/25
            dataFormatada = formato.format(dataSelecionada);
            System.out.println("Data formatada: " + dataFormatada);  
        } else {
            JOptionPane.showMessageDialog(null, "Nenhuma data selecionada!");
                return;
            }
        
        if (selectedFuncionario == null || selectedFuncionario.toString().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Selecione um funcionário ou bancada na lista");
                return;
         }else{
            ft = selectedFuncionario.toString();
            
            try {
            conn = ModuloConexao.conector();
            pst = conn.prepareStatement(sql);
            pst.setString(1, dataFormatada);
            pst.setString(2, ft);
            rs = pst.executeQuery();
            
            if (rs.next()) {
                int resposta = JOptionPane.showConfirmDialog(null,"Já existem apontamentos desse(a) funcionário/bancada na data informada.\nDeseja substituir?","Confirmação",JOptionPane.YES_NO_OPTION);

                if (resposta == JOptionPane.YES_OPTION) {
                    deletarRegistros(ft, dataFormatada);
                    if(seconds == 0){
                        JOptionPane.showMessageDialog(null, "O tempo trabalhado não pode ser zero!");
                        return;
                    }
                    salvarAtividade(dataFormatada);
                } else {
                    JOptionPane.showMessageDialog(null, "Apontamento cancelado!");
                    return;
                }

                conn.close();
            }else{
                conn.close();
                if(seconds == 0){
                        JOptionPane.showMessageDialog(null, "O tempo trabalhado não pode ser zero!");
                        return;
                    }
                salvarAtividade(dataFormatada);
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

        
    
    }

    
    public void deletarRegistros(String ft, String dataFormatada){
        
        String sql = "delete from efi_atividades where DATA = ? and FT = ?";
            try {
                conn = ModuloConexao.conector();
                pst = conn.prepareStatement(sql);
                pst.setString(1, dataFormatada);
                pst.setString(2, ft);
                int apagado = pst.executeUpdate();
                if(apagado > 0){
                    JOptionPane.showMessageDialog(null, "Dados removidos!");
                    return;
                }else{
                    JOptionPane.showMessageDialog(null, "Erro ao remover os dados!");
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
    
    

    public void salvarAtividade(String data) {
        String sql = "INSERT INTO efi_atividades (data, ft, setor, trabalho, operacao, tipo, quantidade, horas, disponibilidade, performance, qualidade, ole, funcionarios, obs, tempo_imp) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String bancada = "";
        
        String funcionarios = "individual";
        String obs = txtObs.getText().toString().trim();
        

        if (obs.isEmpty()) {
            obs = "n";
        }
        
       
        Object selectedItem = cboFuncionario.getSelectedItem();
        if (selectedItem == null || selectedItem.toString().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Selecione um funcionário ou uma bancada na lista");
            return;
        }
        
        
        else{
        bancada = selectedItem.toString();
        }
        
        
        // preenchendo a string com os nomes dos funcionarios //
        int q = tblFunc.getRowCount();
        
        if (q > 0) {
            funcionarios = "";
            
            for(int i= 0; i < tblFunc.getRowCount(); i++){
                funcionarios = funcionarios + tblFunc.getValueAt(i, 0);
                funcionarios += ", ";
            }
        }
        else {
            if (lblF.isVisible()){
                JOptionPane.showMessageDialog(null, "Adicione os funcionários antes de salvar!");
                 return;
            }
        }
        
        
        if (funcionarios != null && funcionarios.length() >= 2 && tblFunc.getRowCount() > 0) {
                funcionarios = funcionarios.substring(0, funcionarios.length() - 2);
            }
        
        // preenchendo a string com os nomes dos funcionarios //

        String setor = lblSetorNome.getText();

        // Verifica se as tabelas possuem dados antes de conectar ao banco
        if (tblProducao.getRowCount() == 0 && tblImproducao.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Nenhuma atividade para salvar!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        conn = ModuloConexao.conector();
        if (conn == null) {
            JOptionPane.showMessageDialog(null, "Erro ao conectar ao banco de dados!");
            return;
        }

        try {
            conn.setAutoCommit(false); // Iniciar transação
            pst = conn.prepareStatement(sql);

            // Contadores para verificar se algo foi salvo
            int registrosSalvos = 0;

            // Inserir dados da produção
            registrosSalvos += inserirAtividades(pst, tblProducao, bancada, setor, "PRODUTIVO", true, data, funcionarios, obs);

            // Inserir dados da improdutividade
            registrosSalvos += inserirAtividades(pst, tblImproducao, bancada, setor, "IMPRODUTIVO", false, data, funcionarios, obs);

            if (registrosSalvos > 0) {
                conn.commit(); // Confirmar transação
                JOptionPane.showMessageDialog(null, "Registro salvo com sucesso!");
                seconds = 0;
                lblDuracao.setText("00:00:00");
                salvo = true;
                lblDisponibilidade.setText("");
                lblDisponibilidadeInversa.setText("");
                lblPerformance.setText("");
                lblQualidade.setText("");
                lblOLE.setText("");
                txtObs.setText("");
                

                String funcSalvo = cboFuncionario.getSelectedItem().toString();

                DefaultTableModel model = (DefaultTableModel) tblProducao.getModel();
                model.setRowCount(0); // Remove todas as linhas

                DefaultTableModel model2 = (DefaultTableModel) tblImproducao.getModel();
                model2.setRowCount(0); // Remove todas as linhas
                
                DefaultTableModel model3 = (DefaultTableModel) tblFunc.getModel();
                model3.setRowCount(0); // Remove todas as linhas

                cboFuncionario.setSelectedItem("");
                cboTrabalho.setSelectedItem("");
                cboImproducaoTipo.setSelectedItem("");
                lblTempoTotalPausa.setText("0");

                int o = JOptionPane.showConfirmDialog(null, "Continuar salvando atividades?", "Atenção", JOptionPane.YES_NO_OPTION);
                if (o == JOptionPane.YES_OPTION) {
                    cboFuncionario.removeItem(funcSalvo);
                    seconds = 0;
                    lblDuracao.setText("00:00:00");
                } else {
                    this.dispose();
                }

            } else {
                conn.rollback(); // Reverter transação caso nada tenha sido salvo
                JOptionPane.showMessageDialog(null, "Nenhum registro foi salvo.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }

        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback(); // Reverter transação em caso de erro
                }
            } catch (SQLException rollbackEx) {
                JOptionPane.showMessageDialog(null, "Erro ao reverter operação: " + rollbackEx.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
            JOptionPane.showMessageDialog(null, "Erro ao salvar atividade: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
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

// Método para inserir atividades no banco e contar registros inseridos
    private int inserirAtividades(PreparedStatement pst, JTable tabela, String bancada, String setor, String tipo, boolean isProducao, String data, String funcionarios, String obs) throws SQLException {
        int registrosInseridos = 0;

        for (int i = 0; i < tabela.getRowCount(); i++) {
            String trb = isProducao ? tabela.getValueAt(i, 0).toString() : "GERAIS";
            String op = tabela.getValueAt(i, isProducao ? 1 : 0).toString();
            String quantidade = isProducao ? tabela.getValueAt(i, 2).toString() : "0";
            String tempoImp = isProducao ? "0" : formatarTempo(tabela.getValueAt(i, 1).toString());

            double horas = (double) seconds / 3600.0; // Converter segundos para horas

            // Validar e converter valores
            double disponibilidade = 0.0;
            try {
                disponibilidade = Double.parseDouble(lblDisponibilidade.getText().replace("%", "").replace(",", "."));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Erro ao converter disponibildiade!", "Erro", JOptionPane.ERROR_MESSAGE);
                return 0;
            }
            
            double performance = 0.0;
            try {
                performance = Double.parseDouble(lblPerformance.getText().replace("%", "").replace(",", "."));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Erro ao converter performance!", "Erro", JOptionPane.ERROR_MESSAGE);
                return 0;
            }
            
            double qualidade = 0.0;
            try {
                qualidade = Double.parseDouble(lblQualidade.getText().replace("%", "").replace(",", "."));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Erro ao converter qualidade!", "Erro", JOptionPane.ERROR_MESSAGE);
                return 0;
            }
            
            double ole = 0.0;
            try {
                ole = Double.parseDouble(lblOLE.getText().replace("%", "").replace(",", "."));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Erro ao converter ole!", "Erro", JOptionPane.ERROR_MESSAGE);
                return 0;
            }
            
            pst.setString(1, data);
            pst.setString(2, bancada);
            pst.setString(3, setor);
            pst.setString(4, trb);
            pst.setString(5, op);
            pst.setString(6, tipo);
            pst.setString(7, quantidade);
            pst.setDouble(8, horas);
            pst.setDouble(9, disponibilidade);
            pst.setDouble(10, performance);
            pst.setDouble(11, qualidade);
            pst.setDouble(12, ole);
            pst.setString(13, funcionarios);
            pst.setString(14, obs);
            pst.setString(15, tempoImp);
            pst.executeUpdate();

            registrosInseridos++;
        }

        return registrosInseridos;
    }

// Método para converter tempo em minutos para horas formatadas corretamente
    private String formatarTempo(String tempoPausa) {
        try {
            double tempoMinutos = Double.parseDouble(tempoPausa);
            return String.format("%.2f", tempoMinutos / 60.0).replace(",", ".");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Erro ao converter tempo!", "Erro", JOptionPane.ERROR_MESSAGE);
            return "0.00";
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jProgressBar1 = new javax.swing.JProgressBar();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProducao = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        btnRemoverParada = new javax.swing.JButton();
        btnRemoverPeca = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblImproducao = new javax.swing.JTable();
        btnPausar = new javax.swing.JButton();
        CheckboxEntradaAutomatica = new javax.swing.JCheckBox();
        txtCodigoLeitor = new javax.swing.JTextField();
        lblqntd = new javax.swing.JLabel();
        txtQuantidadeAutomatica = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        btnAddPausaPlanejada = new javax.swing.JButton();
        lblF = new javax.swing.JLabel();
        btnAddFunc = new javax.swing.JButton();
        btnRemoverFunc = new javax.swing.JButton();
        txtObs = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblFunc = new javax.swing.JTable();
        lblBancada = new javax.swing.JLabel();
        btnEncerrarProducao = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        txtData = new org.jdesktop.swingx.JXDatePicker();
        jLabel5 = new javax.swing.JLabel();
        spinQuantidadeFuncionarios = new javax.swing.JSpinner();
        jLabel18 = new javax.swing.JLabel();
        lblDuracao = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        lblTempoTotalPausa = new javax.swing.JLabel();
        lblQuantidadeFuncionarios = new javax.swing.JLabel();
        txtTempoOperacional = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        lblFuncionario = new javax.swing.JLabel();
        cboFuncionario = new javax.swing.JComboBox<>();
        lblqntd1 = new javax.swing.JLabel();
        spinRuins = new javax.swing.JSpinner();
        logo = new javax.swing.JLabel();
        lblSetorNome = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        lblDisponibilidadeInversa = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        lblDisponibilidade = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        lblPerformance = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        lblQualidade = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        lblOLE = new javax.swing.JLabel();
        btnSalvarAtividade = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        cboTrabalho = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        cboCodigo = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        txtQuantidade = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        cboImproducaoTipo = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        txtImproducaoTempo = new javax.swing.JTextField();
        btnAdicionarProducao = new javax.swing.JButton();
        btnAdicionarImproducao = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Apontamento de Produção");
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });

        tblProducao = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tblProducao.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Trabalho", "Operação", "Quantidade"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblProducao.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblProducao);

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("Produção");

        btnRemoverParada.setBackground(new java.awt.Color(0, 0, 0));
        btnRemoverParada.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnRemoverParada.setForeground(new java.awt.Color(255, 255, 255));
        btnRemoverParada.setText("Remover Linha Selecionada");
        btnRemoverParada.setBorder(null);
        btnRemoverParada.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRemoverParada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverParadaActionPerformed(evt);
            }
        });

        btnRemoverPeca.setBackground(new java.awt.Color(0, 0, 0));
        btnRemoverPeca.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnRemoverPeca.setForeground(new java.awt.Color(255, 255, 255));
        btnRemoverPeca.setText("Remover Linha Selecionada");
        btnRemoverPeca.setBorder(null);
        btnRemoverPeca.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRemoverPeca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverPecaActionPerformed(evt);
            }
        });

        tblImproducao = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tblImproducao.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Motivo", "Tempo"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblImproducao.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(tblImproducao);

        btnPausar.setBackground(new java.awt.Color(0, 0, 0));
        btnPausar.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnPausar.setForeground(new java.awt.Color(153, 153, 153));
        btnPausar.setText("Pausar");
        btnPausar.setBorder(null);
        btnPausar.setEnabled(false);
        btnPausar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPausarActionPerformed(evt);
            }
        });

        CheckboxEntradaAutomatica.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        CheckboxEntradaAutomatica.setForeground(new java.awt.Color(153, 153, 153));
        CheckboxEntradaAutomatica.setText("Entrada Automática");
        CheckboxEntradaAutomatica.setEnabled(false);
        CheckboxEntradaAutomatica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CheckboxEntradaAutomaticaActionPerformed(evt);
            }
        });

        txtCodigoLeitor.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        txtCodigoLeitor.setForeground(new java.awt.Color(153, 153, 153));
        txtCodigoLeitor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodigoLeitorActionPerformed(evt);
            }
        });
        txtCodigoLeitor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCodigoLeitorKeyReleased(evt);
            }
        });

        lblqntd.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        lblqntd.setForeground(new java.awt.Color(153, 153, 153));
        lblqntd.setText("Qt Entrada");

        txtQuantidadeAutomatica.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        txtQuantidadeAutomatica.setForeground(new java.awt.Color(153, 153, 153));
        txtQuantidadeAutomatica.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "100", "101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "112", "113", "114", "115", "116", "117", "118", "119", "120", "121", "122", "123", "124", "125", "126", "127", "128", "129", "130", "131", "132", "133", "134", "135", "136", "137", "138", "139", "140", "141", "142", "143", "144", "145", "146", "147", "148", "149", "150", "151", "152", "153", "154", "155", "156", "157", "158", "159", "160", "161", "162", "163", "164", "165", "166", "167", "168", "169", "170", "171", "172", "173", "174", "175", "176", "177", "178", "179", "180", "181", "182", "183", "184", "185", "186", "187", "188", "189", "190", "191", "192", "193", "194", "195", "196", "197", "198", "199", "200", "201", "202", "203", "204", "205", "206", "207", "208", "209", "210", "211", "212", "213", "214", "215", "216", "217", "218", "219", "220", "221", "222", "223", "224", "225", "226", "227", "228", "229", "230", "231", "232", "233", "234", "235", "236", "237", "238", "239", "240", "241", "242", "243", "244", "245", "246", "247", "248", "249", "250", "251", "252", "253", "254", "255", "256", "257", "258", "259", "260", "261", "262", "263", "264", "265", "266", "267", "268", "269", "270", "271", "272", "273", "274", "275", "276", "277", "278", "279", "280", "281", "282", "283", "284", "285", "286", "287", "288", "289", "290", "291", "292", "293", "294", "295", "296", "297", "298", "299", "300", "301", "302", "303", "304", "305", "306", "307", "308", "309", "310", "311", "312", "313", "314", "315", "316", "317", "318", "319", "320", "321", "322", "323", "324", "325", "326", "327", "328", "329", "330", "331", "332", "333", "334", "335", "336", "337", "338", "339", "340", "341", "342", "343", "344", "345", "346", "347", "348", "349", "350", "351", "352", "353", "354", "355", "356", "357", "358", "359", "360", "361", "362", "363", "364", "365", "366", "367", "368", "369", "370", "371", "372", "373", "374", "375", "376", "377", "378", "379", "380", "381", "382", "383", "384", "385", "386", "387", "388", "389", "390", "391", "392", "393", "394", "395", "396", "397", "398", "399", "400", "401", "402", "403", "404", "405", "406", "407", "408", "409", "410", "411", "412", "413", "414", "415", "416", "417", "418", "419", "420", "421", "422", "423", "424", "425", "426", "427", "428", "429", "430", "431", "432", "433", "434", "435", "436", "437", "438", "439", "440", "441", "442", "443", "444", "445", "446", "447", "448", "449", "450", "451", "452", "453", "454", "455", "456", "457", "458", "459", "460", "461", "462", "463", "464", "465", "466", "467", "468", "469", "470", "471", "472", "473", "474", "475", "476", "477", "478", "479", "480", "481", "482", "483", "484", "485", "486", "487", "488", "489", "490", "491", "492", "493", "494", "495", "496", "497", "498", "499", "500" }));
        txtQuantidadeAutomatica.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        txtQuantidadeAutomatica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtQuantidadeAutomaticaActionPerformed(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel14.setText("Pausas");
        jLabel14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel14MouseClicked(evt);
            }
        });

        btnAddPausaPlanejada.setBackground(new java.awt.Color(0, 0, 0));
        btnAddPausaPlanejada.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAddPausaPlanejada.setForeground(new java.awt.Color(255, 255, 255));
        btnAddPausaPlanejada.setText("Adicionar pausa Planejada");
        btnAddPausaPlanejada.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddPausaPlanejada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddPausaPlanejadaActionPerformed(evt);
            }
        });

        lblF.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblF.setText("Funcionários");

        btnAddFunc.setBackground(new java.awt.Color(0, 0, 0));
        btnAddFunc.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAddFunc.setForeground(new java.awt.Color(255, 255, 255));
        btnAddFunc.setText("+ Adicionar");
        btnAddFunc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddFuncActionPerformed(evt);
            }
        });

        btnRemoverFunc.setBackground(new java.awt.Color(0, 0, 0));
        btnRemoverFunc.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnRemoverFunc.setForeground(new java.awt.Color(255, 255, 255));
        btnRemoverFunc.setText("Remover Funcionário Selecionado");
        btnRemoverFunc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverFuncActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("OBS:");

        tblFunc.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        tblFunc.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nome"
            }
        ));
        jScrollPane3.setViewportView(tblFunc);

        lblBancada.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        lblBancada.setForeground(new java.awt.Color(153, 153, 153));
        lblBancada.setText("lblBancada");

        btnEncerrarProducao.setBackground(new java.awt.Color(0, 0, 0));
        btnEncerrarProducao.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnEncerrarProducao.setForeground(new java.awt.Color(153, 153, 153));
        btnEncerrarProducao.setText("Encerrar");
        btnEncerrarProducao.setBorder(null);
        btnEncerrarProducao.setEnabled(false);
        btnEncerrarProducao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEncerrarProducaoActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel5.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(102, 102, 102));
        jLabel5.setText("Data:");

        spinQuantidadeFuncionarios.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        spinQuantidadeFuncionarios.setModel(new javax.swing.SpinnerNumberModel(1, 1, 30, 1));
        spinQuantidadeFuncionarios.setEnabled(false);
        spinQuantidadeFuncionarios.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinQuantidadeFuncionariosStateChanged(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(102, 102, 102));
        jLabel18.setText("Tempo Decorrido (TD):");

        lblDuracao.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        lblDuracao.setText("00:00:00");
        lblDuracao.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblDuracao.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblDuracaoMouseClicked(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(102, 102, 102));
        jLabel1.setText("Tempo total de Pausas:");

        lblTempoTotalPausa.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        lblTempoTotalPausa.setForeground(new java.awt.Color(102, 102, 102));
        lblTempoTotalPausa.setText("00:00:00");

        lblQuantidadeFuncionarios.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        lblQuantidadeFuncionarios.setForeground(new java.awt.Color(102, 102, 102));
        lblQuantidadeFuncionarios.setText("Funcionários");

        txtTempoOperacional.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        txtTempoOperacional.setText("520");
        txtTempoOperacional.setBorder(null);
        txtTempoOperacional.setEnabled(false);
        txtTempoOperacional.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTempoOperacionalActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(102, 102, 102));
        jLabel2.setText("Tempo Operacional (TO):");

        lblFuncionario.setBackground(new java.awt.Color(51, 51, 51));
        lblFuncionario.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblFuncionario.setText("Funcionário / Bancada");

        cboFuncionario.setBorder(null);
        cboFuncionario.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cboFuncionario.setFocusable(false);
        cboFuncionario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboFuncionarioActionPerformed(evt);
            }
        });

        lblqntd1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblqntd1.setForeground(new java.awt.Color(51, 51, 51));
        lblqntd1.setText("Peças Reprovadas:");

        spinRuins.setModel(new javax.swing.SpinnerNumberModel(0, 0, 1000, 1));
        spinRuins.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        spinRuins.setFocusable(false);
        spinRuins.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinRuinsStateChanged(evt);
            }
        });

        logo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/prjRend/icones/roca_128.png"))); // NOI18N
        logo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logoMouseClicked(evt);
            }
        });

        lblSetorNome.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        lblSetorNome.setText("lblSetorNome");
        lblSetorNome.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblSetorNome.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblSetorNomeMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtData, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblQuantidadeFuncionarios)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinQuantidadeFuncionarios, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTempoOperacional, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblDuracao))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTempoTotalPausa))
                    .addComponent(lblFuncionario)
                    .addComponent(cboFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblqntd1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinRuins, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(logo)
                    .addComponent(lblSetorNome))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblSetorNome)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtTempoOperacional, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(lblDuracao))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(lblTempoTotalPausa))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblQuantidadeFuncionarios)
                    .addComponent(spinQuantidadeFuncionarios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblFuncionario)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblqntd1)
                    .addComponent(spinRuins, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(logo, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel17.setText("Diferença TO e TD:");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Disponibilidade:");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setText("Performance:");

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel15.setText("Qualidade:");

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel16.setText("OLE:");

        jPanel4.setBackground(new java.awt.Color(0, 0, 0));

        lblDisponibilidadeInversa.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblDisponibilidadeInversa.setForeground(new java.awt.Color(153, 153, 153));
        lblDisponibilidadeInversa.setText("0%");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblDisponibilidadeInversa)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblDisponibilidadeInversa)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(0, 0, 0));

        lblDisponibilidade.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblDisponibilidade.setForeground(new java.awt.Color(153, 153, 153));
        lblDisponibilidade.setText("0%");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblDisponibilidade)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblDisponibilidade)
                .addContainerGap())
        );

        jPanel6.setBackground(new java.awt.Color(0, 0, 0));

        lblPerformance.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblPerformance.setForeground(new java.awt.Color(153, 153, 153));
        lblPerformance.setText("0%");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblPerformance)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblPerformance)
                .addContainerGap())
        );

        jPanel7.setBackground(new java.awt.Color(0, 0, 0));

        lblQualidade.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblQualidade.setForeground(new java.awt.Color(153, 153, 153));
        lblQualidade.setText("0%");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblQualidade)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblQualidade)
                .addContainerGap())
        );

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));

        lblOLE.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblOLE.setForeground(new java.awt.Color(153, 153, 153));
        lblOLE.setText("0%");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblOLE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblOLE)
                .addContainerGap())
        );

        btnSalvarAtividade.setBackground(new java.awt.Color(0, 0, 0));
        btnSalvarAtividade.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnSalvarAtividade.setForeground(new java.awt.Color(255, 255, 255));
        btnSalvarAtividade.setText("Salvar");
        btnSalvarAtividade.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSalvarAtividade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarAtividadeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 379, Short.MAX_VALUE)
                .addComponent(btnSalvarAtividade, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSalvarAtividade, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setText("Trabalho");

        cboTrabalho.setToolTipText("");
        cboTrabalho.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 0, 0, new java.awt.Color(0, 0, 0)));
        cboTrabalho.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        cboTrabalho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTrabalhoActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(51, 51, 51));
        jLabel3.setText("Código");

        cboCodigo.setToolTipText("");
        cboCodigo.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 0, 0, new java.awt.Color(0, 0, 0)));
        cboCodigo.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        cboCodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboCodigoActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(51, 51, 51));
        jLabel4.setText("Quantidade");

        txtQuantidade.setToolTipText("");
        txtQuantidade.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txtQuantidade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtQuantidadeActionPerformed(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel19.setText("Motivo");

        cboImproducaoTipo.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 0, 0, new java.awt.Color(0, 0, 0)));
        cboImproducaoTipo.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        cboImproducaoTipo.setPreferredSize(new java.awt.Dimension(280, 31));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setText("Tempo");

        txtImproducaoTempo.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txtImproducaoTempo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtImproducaoTempoActionPerformed(evt);
            }
        });

        btnAdicionarProducao.setBackground(new java.awt.Color(0, 0, 0));
        btnAdicionarProducao.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAdicionarProducao.setForeground(new java.awt.Color(255, 255, 255));
        btnAdicionarProducao.setText("Adicionar");
        btnAdicionarProducao.setBorder(null);
        btnAdicionarProducao.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAdicionarProducao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarProducaoActionPerformed(evt);
            }
        });

        btnAdicionarImproducao.setBackground(new java.awt.Color(0, 0, 0));
        btnAdicionarImproducao.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAdicionarImproducao.setForeground(new java.awt.Color(255, 255, 255));
        btnAdicionarImproducao.setText("Adicionar");
        btnAdicionarImproducao.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAdicionarImproducao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarImproducaoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboImproducaoTipo, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(cboTrabalho, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(cboCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addGap(460, 460, 460)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4)
                    .addComponent(jLabel11)
                    .addComponent(txtQuantidade, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(txtImproducaoTempo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAdicionarProducao, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAdicionarImproducao, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboTrabalho, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel8Layout.createSequentialGroup()
                            .addComponent(jLabel4)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnAdicionarProducao, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel8Layout.createSequentialGroup()
                            .addComponent(jLabel3)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cboCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboImproducaoTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtImproducaoTempo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAdicionarImproducao, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(35, 35, 35)
                                .addComponent(CheckboxEntradaAutomatica)
                                .addGap(6, 6, 6)
                                .addComponent(btnEncerrarProducao)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnPausar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblBancada)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblqntd)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtQuantidadeAutomatica, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCodigoLeitor, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane1)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel9)
                                        .addGap(214, 214, 214)
                                        .addComponent(jLabel6)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtObs))
                                    .addComponent(jScrollPane2)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel14)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnAddPausaPlanejada, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnRemoverPeca, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jScrollPane3)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(lblF)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnAddFunc, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnRemoverFunc)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 286, Short.MAX_VALUE)
                                        .addComponent(btnRemoverParada, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(jLabel6)
                            .addComponent(txtObs, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel14)
                                    .addComponent(btnAddPausaPlanejada, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnRemoverPeca, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblF)
                                    .addComponent(btnAddFunc, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnRemoverFunc)))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnRemoverParada, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 59, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(CheckboxEntradaAutomatica)
                            .addComponent(lblBancada)
                            .addComponent(btnPausar)
                            .addComponent(btnEncerrarProducao)
                            .addComponent(lblqntd)
                            .addComponent(txtQuantidadeAutomatica, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCodigoLeitor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        setSize(new java.awt.Dimension(1217, 726));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnAdicionarProducaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarProducaoActionPerformed
        adicionar_producao();
        definirStatusProduzindo();
        salvo = false;
        logoclicada = false;
        pausaClicada = false;
        tempoClicado = false;
        cboTrabalho.requestFocus();
    }//GEN-LAST:event_btnAdicionarProducaoActionPerformed

    private void btnRemoverParadaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverParadaActionPerformed

        int linhaSelecionada = tblImproducao.getSelectedRow();

        if (linhaSelecionada != -1) {
            DefaultTableModel model = (DefaultTableModel) tblImproducao.getModel();
            model.removeRow(linhaSelecionada);
        } else {
            JOptionPane.showMessageDialog(null, "Selecione uma pausa para remover");
        }

        calcularEficiencia();
        limparSelecaodasTabelas();

        if (CheckboxEntradaAutomatica.isSelected()) {
            txtCodigoLeitor.requestFocus();
        }
        setarTempoTotalPausa();
        definirStatusProduzindo();
        logoclicada = false;
        pausaClicada = false;
        tempoClicado = false;
    }//GEN-LAST:event_btnRemoverParadaActionPerformed

    private void btnRemoverPecaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverPecaActionPerformed

        int linhaSelecionada = tblProducao.getSelectedRow();

        if (linhaSelecionada != -1) {
            DefaultTableModel model = (DefaultTableModel) tblProducao.getModel();
            model.removeRow(linhaSelecionada);
        } else {
            JOptionPane.showMessageDialog(null, "Selecione uma peça para remover");
        }

        calcularEficiencia();
        limparSelecaodasTabelas();

        if (CheckboxEntradaAutomatica.isSelected()) {
            txtCodigoLeitor.requestFocus();
        }
        definirStatusProduzindo();
        logoclicada = false;
        pausaClicada = false;
        tempoClicado = false;
    }//GEN-LAST:event_btnRemoverPecaActionPerformed

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        limparSelecaodasTabelas();
        logoclicada = false;
        pausaClicada = false;
        tempoClicado = false;
        if (CheckboxEntradaAutomatica.isSelected()) {
            txtCodigoLeitor.requestFocus();
        }
    }//GEN-LAST:event_formMouseClicked

    private void btnAdicionarImproducaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarImproducaoActionPerformed
        adicionar_improducao();
        setarTempoTotalPausa();
        txtImproducaoTempo.setEditable(true);
        salvo = false;
        logoclicada = false;
        pausaClicada = false;
        tempoClicado = false;
        if(CheckboxEntradaAutomatica.isSelected()){
            txtCodigoLeitor.requestFocus();
        }
    }//GEN-LAST:event_btnAdicionarImproducaoActionPerformed

    private void cboTrabalhoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTrabalhoActionPerformed
        obterOperacoes();
        if (CheckboxEntradaAutomatica.isSelected()) {
            txtCodigoLeitor.requestFocus();
        }
        logoclicada = false;
        pausaClicada = false;
        tempoClicado = false;
        cboCodigo.requestFocus();
    }//GEN-LAST:event_cboTrabalhoActionPerformed

    private void btnPausarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPausarActionPerformed
        
    }//GEN-LAST:event_btnPausarActionPerformed

    private void cboCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCodigoActionPerformed
        logoclicada = false;
        pausaClicada = false;
        tempoClicado = false;
        txtQuantidade.requestFocus();
    }//GEN-LAST:event_cboCodigoActionPerformed

    private void CheckboxEntradaAutomaticaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CheckboxEntradaAutomaticaActionPerformed
        if (cboCodigo.isVisible()) {
                cboCodigo.setVisible(false);  // Desabilita o JComboBox
                jLabel3.setVisible(false);
                jLabel12.setVisible(false);
                cboTrabalho.setVisible(false);
                jLabel4.setVisible(false);
                btnAdicionarProducao.setVisible(false);
                txtQuantidade.setVisible(false);
                cboTrabalho.setEnabled(false);
                txtQuantidade.setEnabled(false);
                txtQuantidadeAutomatica.setVisible(true);
                lblqntd.setVisible(true);
                btnAdicionarProducao.setEnabled(false);
                txtCodigoLeitor.setVisible(true);
                txtCodigoLeitor.requestFocus();
            } else {
                jLabel12.setVisible(true);
                cboTrabalho.setVisible(true);
                btnAdicionarProducao.setVisible(true);
                jLabel3.setVisible(true);
                jLabel4.setVisible(true);
                txtQuantidade.setVisible(true);
                cboTrabalho.setEnabled(true);
                cboCodigo.setVisible(true);  // Habilita o JComboBox
                txtQuantidade.setEnabled(true);
                txtQuantidadeAutomatica.setVisible(false);
                lblqntd.setVisible(false);
                btnAdicionarProducao.setEnabled(true);
                cboTrabalho.setSelectedItem("");
                txtCodigoLeitor.setVisible(false);
            }
    }//GEN-LAST:event_CheckboxEntradaAutomaticaActionPerformed

    private void txtCodigoLeitorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoLeitorActionPerformed
        String codigoLeitor = txtCodigoLeitor.getText();
        System.out.println(codigoLeitor);
        pesquisarNoComboBox(codigoLeitor);

    }//GEN-LAST:event_txtCodigoLeitorActionPerformed

    private void txtCodigoLeitorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodigoLeitorKeyReleased
        if (timer.isRunning()) {
            timer.restart(); // Se o Timer estiver rodando, reinicia
        } else {
            timer.start(); // Se não estiver rodando, inicia
        }
    }//GEN-LAST:event_txtCodigoLeitorKeyReleased

    private void btnSalvarAtividadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarAtividadeActionPerformed
        //cboTrabalho.removeAllItems();
        //buscarTrabalhos();
        verificarRegistrosExistentes();
    }//GEN-LAST:event_btnSalvarAtividadeActionPerformed

    private void txtQuantidadeAutomaticaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtQuantidadeAutomaticaActionPerformed
        if (CheckboxEntradaAutomatica.isSelected()) {
            txtCodigoLeitor.requestFocus();
        }
    }//GEN-LAST:event_txtQuantidadeAutomaticaActionPerformed

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        definirStatusProduzindo();
        if(CheckboxEntradaAutomatica.isSelected()){
            txtCodigoLeitor.requestFocus();
        }
    }//GEN-LAST:event_formWindowActivated

    private void btnEncerrarProducaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEncerrarProducaoActionPerformed
        
    }//GEN-LAST:event_btnEncerrarProducaoActionPerformed

    private void logoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoMouseClicked
        
    }//GEN-LAST:event_logoMouseClicked

    private void lblDuracaoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblDuracaoMouseClicked
        
        
        try {
        String input = JOptionPane.showInputDialog("Informe o Tempo Decorrido (TD), Em minutos:",txtTempoOperacional.getText().toString().replace(".", "").replace(",", ""));

        if (input != null && !input.trim().isEmpty()) {
        input = input.trim();

        // Pressupõe que o usuário digitou corretamente um número
        double minutosDigitados = Double.parseDouble(input);
        long totalSegundos = (long) (minutosDigitados * 60);
        seconds = totalSegundos;

        long horas = totalSegundos / 3600;
        long minutos = (totalSegundos % 3600) / 60;
        long segundos = totalSegundos % 60;

        String tempoFormatado = String.format("%02d:%02d:%02d", horas, minutos, segundos);
        lblDuracao.setText(tempoFormatado);
        calcularEficiencia();
}
        
        
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Informe um valor válido!");
        }
        
        
        
    }//GEN-LAST:event_lblDuracaoMouseClicked

    private void jLabel14MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel14MouseClicked
        
    }//GEN-LAST:event_jLabel14MouseClicked

    private void cboFuncionarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboFuncionarioActionPerformed
        logoclicada = false;
        pausaClicada = false;
        tempoClicado = false;
    }//GEN-LAST:event_cboFuncionarioActionPerformed





    private void btnAddPausaPlanejadaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddPausaPlanejadaActionPerformed
    TelaPausaPlanejadaDepois tppd = new TelaPausaPlanejadaDepois(this);
    tppd.setVisible(true);
    }//GEN-LAST:event_btnAddPausaPlanejadaActionPerformed

    private void txtTempoOperacionalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTempoOperacionalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTempoOperacionalActionPerformed

    private void txtImproducaoTempoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtImproducaoTempoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtImproducaoTempoActionPerformed

    private void spinRuinsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinRuinsStateChanged
      calcularEficiencia();
      if(CheckboxEntradaAutomatica.isSelected()){
            txtCodigoLeitor.requestFocus();
        }
    }//GEN-LAST:event_spinRuinsStateChanged

    private void lblSetorNomeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSetorNomeMouseClicked
        this.setVisible(false);
        TelaTrocarSetorDepois telTroca = new TelaTrocarSetorDepois(this);
        telTroca.setVisible(true);
        telTroca.obterSetores();        
    }//GEN-LAST:event_lblSetorNomeMouseClicked

    private void txtQuantidadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtQuantidadeActionPerformed
    btnAdicionarProducao.requestFocus();
    }//GEN-LAST:event_txtQuantidadeActionPerformed

    private void btnAddFuncActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddFuncActionPerformed
        AddFuncionarioDepois telaAdd = new AddFuncionarioDepois(this);
        telaAdd.setVisible(true);
        telaAdd.buscarFuncionarios();
    }//GEN-LAST:event_btnAddFuncActionPerformed

    private void btnRemoverFuncActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverFuncActionPerformed

        DefaultTableModel dtm = (DefaultTableModel) tblFunc.getModel();

        int linhaSelecionada = tblFunc.getSelectedRow();
        String texto = null;

        if (linhaSelecionada != -1) {
            dtm.removeRow(linhaSelecionada);
        } else {
            JOptionPane.showMessageDialog(null, "Selecione um funcionário na tabela.");
            return; // Interrompe o código caso nenhum funcionário esteja selecionado
        }

    }//GEN-LAST:event_btnRemoverFuncActionPerformed

    private void spinQuantidadeFuncionariosStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinQuantidadeFuncionariosStateChanged
        calcularEficiencia();
        if(CheckboxEntradaAutomatica.isSelected()){
            txtCodigoLeitor.requestFocus();
        }
    }//GEN-LAST:event_spinQuantidadeFuncionariosStateChanged

    public static void main(String args[]) {
        TelaDeEficienciaDepois tela = new TelaDeEficienciaDepois();
        tela.setVisible(true);
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JCheckBox CheckboxEntradaAutomatica;
    public javax.swing.JButton btnAddFunc;
    public javax.swing.JButton btnAddPausaPlanejada;
    private javax.swing.JButton btnAdicionarImproducao;
    private javax.swing.JButton btnAdicionarProducao;
    private javax.swing.JButton btnEncerrarProducao;
    private javax.swing.JButton btnPausar;
    public javax.swing.JButton btnRemoverFunc;
    private javax.swing.JButton btnRemoverParada;
    private javax.swing.JButton btnRemoverPeca;
    private javax.swing.JButton btnSalvarAtividade;
    private javax.swing.JComboBox<String> cboCodigo;
    public javax.swing.JComboBox<String> cboFuncionario;
    public javax.swing.JComboBox<String> cboImproducaoTipo;
    public javax.swing.JComboBox<String> cboTrabalho;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    public javax.swing.JScrollPane jScrollPane3;
    public javax.swing.JLabel lblBancada;
    private javax.swing.JLabel lblDisponibilidade;
    private javax.swing.JLabel lblDisponibilidadeInversa;
    public javax.swing.JLabel lblDuracao;
    public javax.swing.JLabel lblF;
    public javax.swing.JLabel lblFuncionario;
    private javax.swing.JLabel lblOLE;
    private javax.swing.JLabel lblPerformance;
    private javax.swing.JLabel lblQualidade;
    public javax.swing.JLabel lblQuantidadeFuncionarios;
    public javax.swing.JLabel lblSetorNome;
    private javax.swing.JLabel lblTempoTotalPausa;
    private javax.swing.JLabel lblqntd;
    private javax.swing.JLabel lblqntd1;
    private javax.swing.JLabel logo;
    public javax.swing.JSpinner spinQuantidadeFuncionarios;
    public javax.swing.JSpinner spinRuins;
    public javax.swing.JTable tblFunc;
    public javax.swing.JTable tblImproducao;
    public javax.swing.JTable tblProducao;
    private javax.swing.JTextField txtCodigoLeitor;
    private org.jdesktop.swingx.JXDatePicker txtData;
    public javax.swing.JTextField txtImproducaoTempo;
    private javax.swing.JTextField txtObs;
    public javax.swing.JTextField txtQuantidade;
    private javax.swing.JComboBox<String> txtQuantidadeAutomatica;
    public javax.swing.JTextField txtTempoOperacional;
    // End of variables declaration//GEN-END:variables
}
