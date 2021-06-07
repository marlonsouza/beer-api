package souza.marlon.beer.controller;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static souza.marlon.beer.controller.JsonConvertionUtils.asJsonString;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import souza.marlon.beer.dto.BeerDTO;
import souza.marlon.beer.dto.QuantityDTO;
import souza.marlon.beer.exception.BeerNotFoundException;
import souza.marlon.beer.exception.BeerStockExceededException;
import souza.marlon.beer.factory.BeerDTOFactory;
import souza.marlon.beer.factory.QuantityDTOFactory;
import souza.marlon.beer.service.BeerService;

@ExtendWith(MockitoExtension.class)
public class BeerControllerTest {

	private static final String BEER_API_URL_PATH = "/api/v1/beers";
	private static final Long VALID_BEER_ID = 1L;
	private static final Long INVALID_BEER_ID = 2L;
	private static final String BEER_API_SUBPATH_INCREMENT_URL = "/increment";
	private static final String BEER_API_SUBPATH_DECREMENT_URL = "/decrement";
	
	private MockMvc mockMvc;
	
	@Mock
	private BeerService beerService;
	
	@InjectMocks
	private BeerController beerController;
	
	
	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(beerController)
					.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
					.setViewResolvers((s, locale) -> new MappingJackson2JsonView())
					.build();
	}
	
	@Test
	void whenPOSTIsCalledThenABeerIsCreated() throws Exception {
		BeerDTO beerDTO = BeerDTOFactory.ofFakeValid();
		
		when(beerService.createBeer(beerDTO)).thenReturn(beerDTO);
		
		mockMvc.perform(post(BEER_API_URL_PATH)
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(beerDTO)))
		.andExpect(status().isCreated())
		.andExpect(jsonPath("$.name", is(beerDTO.getName())))
		.andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
		.andExpect(jsonPath("$.type", is(beerDTO.getType().toString())));
	}
	
	@Test
	void whenPOSTIsCalledWithouRequiredFieldThenErrorIsReturned() throws Exception {
		BeerDTO beerDTO = BeerDTOFactory.ofFakeBuilder().brand(null).build();
		
		mockMvc.perform(post(BEER_API_URL_PATH)
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(beerDTO)))
		.andExpect(status().isBadRequest());
	}
	
	@Test
	void whenGETTIsCalledWithValidNameThenStatusOKIsReturned() throws Exception {
		BeerDTO beerDTO = BeerDTOFactory.ofFakeValid();
		
		when(beerService.findByName(beerDTO.getName())).thenReturn(beerDTO);
		
		mockMvc.perform(get(BEER_API_URL_PATH + "/" +beerDTO.getName())
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(beerDTO)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.name", is(beerDTO.getName())))
		.andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
		.andExpect(jsonPath("$.type", is(beerDTO.getType().toString())));
	}
	
	@Test
	void whenGETTIsCalledWithoutRegistedNameThenNotFoundStatusIsReturned() throws Exception {
		BeerDTO beerDTO = BeerDTOFactory.ofFakeValid();
		
		when(beerService.findByName(beerDTO.getName())).thenThrow(BeerNotFoundException.class);
		
		mockMvc.perform(get(BEER_API_URL_PATH + "/" +beerDTO.getName())
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound());
	}
	
	@Test
	void whenGETListCalledThenStatusOKIsReturned() throws Exception {
		BeerDTO beerDTO = BeerDTOFactory.ofFakeValid();
		
		when(beerService.listAll()).thenReturn(Collections.singletonList(beerDTO));
		
		mockMvc.perform(get(BEER_API_URL_PATH)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$[0].name", is(beerDTO.getName())))
		.andExpect(jsonPath("$[0].brand", is(beerDTO.getBrand())))
		.andExpect(jsonPath("$[0].type", is(beerDTO.getType().toString())));
	}
	
	@Test
	void whenDELETEsCalledWithValidIdThenStatusNoContentIsReturned() throws Exception {
		BeerDTO beerDTO = BeerDTOFactory.ofFakeValid();
		
		doNothing().when(beerService).deleteById(beerDTO.getId());
		
		mockMvc.perform(delete(BEER_API_URL_PATH + "/" +beerDTO.getId())
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isNoContent());
	}
	
	@Test
	void whenDELETEsCalledWithInvalidIdThenStatusNotFoundIsReturned() throws Exception {
		doThrow(BeerNotFoundException.class).when(beerService).deleteById(INVALID_BEER_ID);
		
		mockMvc.perform(delete(BEER_API_URL_PATH + "/" +INVALID_BEER_ID)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound());
	}
	
	@Test
	void whenPATCHIsCalledToIncrementThenOkStatusIsReturned() throws Exception {
		
		QuantityDTO quantityDTO = QuantityDTOFactory.of(10);
		BeerDTO beerDTO = BeerDTOFactory.ofFakeValid();
		
		beerDTO.setQuantity(beerDTO.getQuantity() + quantityDTO.getQuantity());
		
		when(beerService.increment(VALID_BEER_ID, quantityDTO.getQuantity())).thenReturn(beerDTO);
		
		mockMvc.perform(patch(BEER_API_URL_PATH + "/" + VALID_BEER_ID + BEER_API_SUBPATH_INCREMENT_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(quantityDTO)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name", is(beerDTO.getName())))
			.andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
			.andExpect(jsonPath("$.type", is(beerDTO.getType().toString())))
			.andExpect(jsonPath("$.quantity", is(beerDTO.getQuantity())));
	}
	
	@Test
	void whenPATCHIsCalledToIncrementGreaterThenMaxThenBadRequestStatusIsReturned() throws Exception {
		
		BeerDTO beerDTO = BeerDTOFactory.ofFakeValid();
		QuantityDTO quantityDTO = QuantityDTOFactory.of(beerDTO.getMax());
		
		when(beerService.increment(VALID_BEER_ID, quantityDTO.getQuantity())).thenThrow(BeerStockExceededException.class);
		
		mockMvc.perform(patch(BEER_API_URL_PATH + "/" + VALID_BEER_ID + BEER_API_SUBPATH_INCREMENT_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(quantityDTO)))
			.andExpect(status().isBadRequest());
	}
	
	@Test
	void whenPATCHIsCalledToDecrementThenOkStatusIsReturned() throws Exception {
		
		QuantityDTO quantityDTO = QuantityDTOFactory.of(5);
		BeerDTO beerDTO = BeerDTOFactory.ofFakeValid();
		
		beerDTO.setQuantity(beerDTO.getQuantity() - quantityDTO.getQuantity());
		
		when(beerService.decrement(VALID_BEER_ID, quantityDTO.getQuantity())).thenReturn(beerDTO);
		
		mockMvc.perform(patch(BEER_API_URL_PATH + "/" + VALID_BEER_ID + BEER_API_SUBPATH_DECREMENT_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(quantityDTO)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name", is(beerDTO.getName())))
			.andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
			.andExpect(jsonPath("$.type", is(beerDTO.getType().toString())))
			.andExpect(jsonPath("$.quantity", is(beerDTO.getQuantity())));
	}
	
	@Test
	void whenPATCHIsCalledToDecrementSmallerThenZeroThenBadRequestStatusIsReturned() throws Exception {
		
		BeerDTO beerDTO = BeerDTOFactory.ofFakeValid();
		QuantityDTO quantityDTO = QuantityDTOFactory.of(beerDTO.getMax());
		
		when(beerService.decrement(VALID_BEER_ID, quantityDTO.getQuantity())).thenThrow(BeerStockExceededException.class);
		
		mockMvc.perform(patch(BEER_API_URL_PATH + "/" + VALID_BEER_ID + BEER_API_SUBPATH_DECREMENT_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(quantityDTO)))
			.andExpect(status().isBadRequest());
	}
	
}
