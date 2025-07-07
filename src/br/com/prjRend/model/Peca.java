
package br.com.prjRend.model;

public class Peca {
    
    public String codigo;
    public double tempoNecessario;
    public int quantidade;
    public String trabalho;
	
	public Peca() {
            
	}

	public Peca(String codigo, double tempoNecessario, int quantidade, String trabalho) {
		super();
                this.codigo = codigo;
		this.tempoNecessario = tempoNecessario;
		this.quantidade = quantidade;
                this.trabalho = trabalho;
	}
}
