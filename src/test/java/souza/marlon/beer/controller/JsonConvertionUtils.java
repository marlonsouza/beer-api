package souza.marlon.beer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import souza.marlon.beer.dto.BaseDTO;
import souza.marlon.beer.dto.BeerDTO;

public class JsonConvertionUtils {

	public static String asJsonString(BaseDTO dto) {
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,false);
		objectMapper.registerModules(new JavaTimeModule());
		
		try {
			return objectMapper.writeValueAsString(dto);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		
	}

}
