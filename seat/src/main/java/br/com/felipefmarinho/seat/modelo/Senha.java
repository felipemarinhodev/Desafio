package br.com.felipefmarinho.seat.modelo;

import java.util.Calendar;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Senha implements Comparable<Senha>{

	private String prioridade;
	private Calendar emissao;
//	private Prioridade prioridade;
	private Calendar chamada;
	private Calendar fim;
	private String senha;
	private String guiche;
	private String atendente;
	private Integer naFrente;
	private Long espera;

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}
	
	public String getGuiche() {
		return guiche;
	}

	public void setGuiche(String guiche) {
		this.guiche = guiche;
	}

	public String getAtendente() {
		return atendente;
	}

	public void setAtendente(String atendente) {
		this.atendente = atendente;
	}

	public Calendar getEmissao() {
		return emissao;
	}

	public void setEmissao(Calendar emissao) {
		this.emissao = emissao;
	}

	public String getPrioridade() {
		return prioridade;
	}

	public void setPrioridade(String prioridade) {
		this.prioridade = prioridade;
	}

	public Calendar getChamada() {
		return chamada;
	}

	public void setChamada(Calendar chamada) {
		this.chamada = chamada;
	}

	public Calendar getFim() {
		return fim;
	}

	public void setFim(Calendar fim) {
		this.fim = fim;
	}
	
	public Integer getNaFrente() {
		return naFrente;
	}

	public void setNaFrente(Integer naFrente) {
		this.naFrente = naFrente;
	}
	
	public Long getEspera() {
		return espera;
	}

	public void setEspera(Long espera) {
		this.espera = espera;
	}

	@Override
	public int compareTo(Senha senha) {
		if(this.emissao.before(senha.getEmissao())) {
			return -1;
		}
		if(this.emissao.after(senha.getEmissao())) {
			return 1;
		}
		return 0;
	}
	@JsonIgnore
	public boolean emAtendimento() {
		return this.getChamada() != null && this.getFim() == null;
	}

	@JsonIgnore
	public boolean atendimentoJaRealizado() {
		return this.getFim() != null;
	}

}
