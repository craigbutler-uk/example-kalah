package com.cbuk.kalah.rest.model;

import java.net.URI;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GameDto {

	private long id;
	private URI uri;
	
	@JsonInclude(Include.NON_NULL)
	private Map<Integer,Integer> pits;
}
