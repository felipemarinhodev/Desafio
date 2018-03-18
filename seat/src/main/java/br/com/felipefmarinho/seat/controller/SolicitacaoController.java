package br.com.felipefmarinho.seat.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.felipefmarinho.seat.service.SolicitacaoService;

@Controller
@RequestMapping("/senhas")
public class SolicitacaoController {
	
	@Autowired
	SolicitacaoService solicitacaoService;
	
	@GetMapping
	public String novo() {
		return "Solicitacao";
	}
	
	@GetMapping("/1")
	public String millestone1(){
		solicitacaoService.recuperarSenhasOrdenarEEncaminhar();
		return "redirect:/senhas";
	}
	
	@GetMapping("/2")
	public String millestone2() throws IOException{
		solicitacaoService.recuperarSenhasOrdenarClassificarEEncaminhar();
		return "redirect:/senhas";
	}

	@GetMapping("/3")
	public String millestone3() throws IOException{
		solicitacaoService.recuperarSenhaOrdenarClassificarDefinirTempoPrevisto();
		return "redirect:/senhas";
	}

	@GetMapping("/4")
	public String millestone4() throws IOException{
		solicitacaoService.classificarTiquetesSenhaPorTempoImprimir();
		return "redirect:/senhas";
	}
	
	
}
