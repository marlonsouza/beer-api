package souza.marlon.beer.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import souza.marlon.beer.dto.BeerDTO;
import souza.marlon.beer.exception.BeerAlreadyRegisteredException;
import souza.marlon.beer.exception.BeerNotFoundException;

@Api("Manages beer stock")
public interface BeerControllerDocs {

	@ApiOperation(value = "Beer creatioon operation")
	@ApiResponses(value = {
			@ApiResponse(code =201,message = "Success beer creation"),
			@ApiResponse(code = 400,message = "Missing required fields or wrong field range value")
	})
	BeerDTO createBeer(BeerDTO beerDTO) throws BeerAlreadyRegisteredException;

	@ApiOperation(value = "Returns beer found by given name")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Success beer found"),
			@ApiResponse(code = 404, message = "Beer with given name not found")
	})
	BeerDTO findByName(@PathVariable String name) throws BeerNotFoundException;
	
	@ApiOperation(value = "Returns list of all beer")
	@ApiResponses({
		@ApiResponse(code = 200, message = "List of all beers")
	})
	List<BeerDTO> listBeers();
	
	@ApiOperation(value = "Delete a beer found by a given valid Id")
	@ApiResponses({
		@ApiResponse(code = 204,message = "Success beer deleted"),
		@ApiResponse(code = 404,message = "Beer with given id not found")
	})
	void deleteById(@PathVariable Long id) throws BeerNotFoundException;
	
}
