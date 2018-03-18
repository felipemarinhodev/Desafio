package br.com.felipefmarinho.seat.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.felipefmarinho.seat.modelo.RespostaTiquetesSenha;
import br.com.felipefmarinho.seat.modelo.RetornoSenhas;
import br.com.felipefmarinho.seat.modelo.Senha;

@Service
public class SolicitacaoService {

	@Autowired
	RestService restService;

	private Long tempoMedioAtendimento;
	private final boolean JA_ATENDIDOS = false;
	private final boolean NAO_ATENDIDOS = true;
	private final String PARAMETRO_NOME = "?nome=felipefilgueiramarinho";

	public List<Senha> ordenarSenhasPelaEmissao(List<Senha> tiquetesSenhas) {
		tiquetesSenhas = removerTiquetesSenhasDuplicadas(tiquetesSenhas);
		tiquetesSenhas = this.recuperarTiquetesSenhaNaoAtendidoOuJaAtendido(tiquetesSenhas, NAO_ATENDIDOS);
		ordenarTiquetesPelaEmissao(tiquetesSenhas);
		return tiquetesSenhas;
	}

	public String converterObjetoEmJsonString(Object o) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(o);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void recuperarSenhasOrdenarClassificarEEncaminhar() {
		RetornoSenhas senhasRecebidas = solicitarTiquetesSenha();
		List<Senha> tiquetesSenhas = senhasRecebidas.getInput();

		tiquetesSenhas = classificarQualSenhasEstaNaFrente(tiquetesSenhas);
		encaminharSenhasVerificadas(senhasRecebidas, tiquetesSenhas);
	}

	public void recuperarSenhasOrdenarEEncaminhar() {
		RetornoSenhas senhasRecebidas = solicitarTiquetesSenha();
		List<Senha> tiquetesSenhas = senhasRecebidas.getInput();

		tiquetesSenhas = ordenarTiquetes(tiquetesSenhas);

		encaminharSenhasVerificadas(senhasRecebidas, tiquetesSenhas);
	}

	public void recuperarSenhaOrdenarClassificarDefinirTempoPrevisto() {
		RetornoSenhas senhasRecebidas = solicitarTiquetesSenha();
		List<Senha> tiquetesSenhas = senhasRecebidas.getInput();

		tiquetesSenhas = classificarDefinirTempoPrevistoDeAtendimento(tiquetesSenhas);
		encaminharSenhasVerificadas(senhasRecebidas, tiquetesSenhas);
	}

	public void classificarTiquetesSenhaPorTempoImprimir() {
		RetornoSenhas senhasRecebidas = solicitarTiquetesSenha();
		List<Senha> tiquetesSenhas = senhasRecebidas.getInput();

		tiquetesSenhas = classificarDefinirTempoPrevistoDeAtendimento(tiquetesSenhas);

		this.definirHistogramaDe5Em5Minutos(tiquetesSenhas);
	}

	private List<Senha> classificarQualSenhasEstaNaFrente(List<Senha> tiquetesSenhas) {
		tiquetesSenhas = ordenarTiquetes(tiquetesSenhas);
		this.definirQuemEstaNaFrente(tiquetesSenhas);
		return tiquetesSenhas;
	}
	
	private List<Senha> ordenarTiquetes(List<Senha> tiquetesSenhas) {

		this.removerTiquetesSenhasDuplicadas(tiquetesSenhas);
		tiquetesSenhas = this.recuperarTiquetesSenhaNaoAtendidoOuJaAtendido(tiquetesSenhas, NAO_ATENDIDOS);
		ordenarTiquetesPelaEmissao(tiquetesSenhas);
		this.ordenarTiquetesPorPrioridade(tiquetesSenhas);

		return tiquetesSenhas;
	}
	
	private List<Senha> classificarDefinirTempoPrevistoDeAtendimento(List<Senha> tiquetesSenhas) {
		
		calcularTempoMedioDosAtendimentosJaRealizados(tiquetesSenhas);
		tiquetesSenhas = this.classificarQualSenhasEstaNaFrente(tiquetesSenhas);
		this.definirTempoPrevistoDasSenhas(tiquetesSenhas);
		return tiquetesSenhas;
	}
	
	private void definirHistogramaDe5Em5Minutos(List<Senha> tiquetesSenhas) {
		int intervaloInicial = 300000; // 5 minutos
		int tiquetesAte5Min = 0;
		int tiquetesAte10Min = 0;
		int tiquetesAte15Min = 0;
		int tiquetesMaisQue15Min = 0;
		for (Senha senha : tiquetesSenhas) {
			int faixa = senha.getEspera().intValue() / intervaloInicial;
			switch (faixa) {
			case 1:
				tiquetesAte5Min++;
				break;
			case 2:
				tiquetesAte10Min++;
				break;
			case 3:
				tiquetesAte15Min++;
				break;
			default:
				tiquetesMaisQue15Min++;
				break;
			}
		}
		
		System.out.println("< 05 Minutos: " + montarInformacaoHistograma(tiquetesAte5Min));
		System.out.println("< 10 Minutos: " + montarInformacaoHistograma(tiquetesAte10Min));
		System.out.println("< 15 Minutos: " + montarInformacaoHistograma(tiquetesAte15Min));
		System.out.println("> 15 Minutos: " + montarInformacaoHistograma(tiquetesMaisQue15Min));
	}

	private String montarInformacaoHistograma(int quantidadeTiquetes) {
		StringBuilder informacaoHistograma = new StringBuilder();
		for (int i = 0; i < quantidadeTiquetes; i++) {
			informacaoHistograma.append("#");
		}
		informacaoHistograma.append(" ");
		informacaoHistograma.append(quantidadeTiquetes);
		return informacaoHistograma.toString();
	}

	private void definirTempoPrevistoDasSenhas(List<Senha> tiquetesSenhas) {
		tiquetesSenhas.forEach(ts -> ts.setEspera(tempoMedioAtendimento * (ts.getNaFrente() + 1)));
	}

	private void encaminharSenhasVerificadas(RetornoSenhas senhasRecebidas, List<Senha> tiquetesSenhasOrdenados) {
		RespostaTiquetesSenha respostaTiquetesSenha = new RespostaTiquetesSenha(senhasRecebidas.getNome(),
																				senhasRecebidas.getChave(), 
																				tiquetesSenhasOrdenados);
		String json = this.converterObjetoEmJsonString(respostaTiquetesSenha);
		String url = new StringBuilder().append(senhasRecebidas.getPostTo().getUrl())
//										.append(PARAMETRO_NOME)
										.toString();
		restService.post(url, json);
	}

	private void calcularTempoMedioDosAtendimentosJaRealizados(List<Senha> tiquetesSenhas) {
		this.removerTiquetesSenhasDuplicadas(tiquetesSenhas);
		tiquetesSenhas = this.recuperarTiquetesSenhaNaoAtendidoOuJaAtendido(tiquetesSenhas, JA_ATENDIDOS);
		Long totalTempoAtendimento = Long.valueOf(0);
		int cont = 0;
		for (Senha senha : tiquetesSenhas) {
			if (senha.emAtendimento()) {
				continue;
			}
			cont++;
			Long finalAtendimento = senha.getFim().getTimeInMillis();
			Long inicioAtendimento = senha.getChamada().getTimeInMillis();

			totalTempoAtendimento = Long.sum(totalTempoAtendimento, Long.sum(finalAtendimento, -inicioAtendimento));
		}
		tempoMedioAtendimento = totalTempoAtendimento / cont;
	}

	private void definirQuemEstaNaFrente(List<Senha> senhas) {
		int naFrente = 0;
		for (Senha senha : senhas) {
			senha.setNaFrente(naFrente++);
		}
	}

	private List<Senha> recuperarTiquetesSenhaNaoAtendidoOuJaAtendido(List<Senha> itens, boolean naoAtendidos) {
		List<Senha> tiquetesJaAtendidos = new ArrayList<>();
		List<Senha> tiquetesNaoAtendidos = new ArrayList<>();
		for (Senha senha : itens) {
			if (senha.atendimentoJaRealizado() || senha.emAtendimento()) {
				tiquetesJaAtendidos.add(senha);
			} else {
				tiquetesNaoAtendidos.add(senha);
			}
		}
		return naoAtendidos == true ? tiquetesNaoAtendidos : tiquetesJaAtendidos;
	}

	private void ordenarTiquetesPorPrioridade(List<Senha> tiquetesSenhas) {
		List<Senha> senhasPrioritarias = new ArrayList<>();
		for (Senha senha : tiquetesSenhas) {
			if (senha.getPrioridade().equals("preferencial")) {
				senhasPrioritarias.add(senha);
			}
		}
		tiquetesSenhas.removeAll(senhasPrioritarias);
		tiquetesSenhas.addAll(0, senhasPrioritarias);
	}
	
	private void ordenarTiquetesPelaEmissao(List<Senha> itens) {
		Collections.sort(itens);
	}

	private RetornoSenhas solicitarTiquetesSenha() {
		return restService.get("http://seat.ind.br/processo-seletivo/2018/02/desafio.php"+PARAMETRO_NOME);
	}

	private List<Senha> removerTiquetesSenhasDuplicadas(List<Senha> itens) {
		if (itens == null || itens.isEmpty()) {
			return null;
		}
		Map<String, Senha> senhas = new HashMap<>();
		itens.forEach(i -> senhas.put(i.getSenha(), i));
		return new ArrayList<Senha>(senhas.values());
	}
}
