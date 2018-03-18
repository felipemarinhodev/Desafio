package br.com.felipefmarinho.seat.modelo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RespostaTiquetesSenha {
	
	private String nome;
    private String chave;
    private List<Senha> resultado;
	
    public RespostaTiquetesSenha(
    		@JsonProperty("nome")String nome, 
    		@JsonProperty("chave")String chave, 
    		@JsonProperty("resultado")List<Senha> resultado) {
		super();
		this.nome = nome;
		this.chave = chave;
		this.resultado = resultado;
	}
    
    public String getNome() {
		return nome;
	}
    
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getChave() {
		return chave;
	}
	
	public void setChave(String chave) {
		this.chave = chave;
	}
	
	public List<Senha> getResultado() {
		return resultado;
	}
	
	public void setResultado(List<Senha> resultado) {
		this.resultado = resultado;
	}

	@JsonIgnore
	public String imprmirResultado() {
		StringBuilder resultadoJson = new StringBuilder();
		for (Senha senha : this.resultado) {
			StringBuilder impressaoSenha = new StringBuilder();
			impressaoSenha.append("Senha: " + senha.getSenha());
			impressaoSenha.append(" | emissão: " + senha.getEmissao().getTime());
			impressaoSenha.append(" | Prioridade: " + senha.getPrioridade());
			impressaoSenha.append( senha.getNaFrente() != null ? " | na frente: " + senha.getNaFrente() : "");
			impressaoSenha.append(senha.getEspera() != null ? " | Espera prevista: " + senha.getEspera() : "");
			impressaoSenha.append(System.lineSeparator());
			resultadoJson.append(impressaoSenha.toString());
		}
		return resultadoJson.toString();
	}
	
}
