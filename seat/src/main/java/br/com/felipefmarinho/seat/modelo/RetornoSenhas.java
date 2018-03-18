package br.com.felipefmarinho.seat.modelo;

import java.util.List;

public class RetornoSenhas {

	private String chave;
	private String nome;
	private List<Senha> input;
	private PostTo postTo;
	private String mensagem;

	public String getChave() {
		return chave;
	}

	public void setChave(String chave) {
		this.chave = chave;
	}

	public List<Senha> getInput() {
		return input;
	}

	public void setInput(List<Senha> input) {
		this.input = input;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public PostTo getPostTo() {
		return postTo;
	}

	public void setPostTo(PostTo postTo) {
		this.postTo = postTo;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
}
