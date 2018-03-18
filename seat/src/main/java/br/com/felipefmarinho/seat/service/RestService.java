package br.com.felipefmarinho.seat.service;

import java.io.IOException;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.felipefmarinho.seat.modelo.RetornoSenhas;

@Service
public class RestService {

	private RestTemplate rest;
	private HttpHeaders headers;
	private HttpHeaders requestHeaders;
	private HttpStatus status;

	public RestService() {
		this.rest = new RestTemplate();
		this.headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "*/*");
		
		requestHeaders = new HttpHeaders();
		requestHeaders.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
	}

	public void post(String uri, String json) {
		HttpEntity<String> requestEntity = new HttpEntity<String>(json, requestHeaders);
		ResponseEntity<String> responseEntity = rest.exchange(uri, HttpMethod.POST, requestEntity, String.class);
		this.setStatus(responseEntity.getStatusCode());
		System.out.println("Json enviado:" + json);
		System.out.println("Resposta:");
		System.out.println(responseEntity.getBody());
	}
	
	public RetornoSenhas get(String uri) {
		RetornoSenhas retornoSenhas = new RetornoSenhas();
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
	    HttpEntity<String> requestEntity = new HttpEntity<String>("", requestHeaders);
	    ResponseEntity<String> responseEntity;
	    try {
	    	responseEntity = rest.exchange(uri, HttpMethod.GET, requestEntity, String.class);
			retornoSenhas = objectMapper.readValue(responseEntity.getBody(), RetornoSenhas.class);
			this.setStatus(responseEntity.getStatusCode());
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return retornoSenhas;
	  }

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}
}
