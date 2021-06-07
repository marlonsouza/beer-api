package souza.marlon.beer.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import souza.marlon.beer.dto.BeerDTO;
import souza.marlon.beer.dto.QuantityDTO;
import souza.marlon.beer.exception.BeerAlreadyRegisteredException;
import souza.marlon.beer.exception.BeerNotFoundException;
import souza.marlon.beer.exception.BeerStockExceededException;
import souza.marlon.beer.service.BeerService;

@RestController
@RequestMapping("/api/v1/beers")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class BeerController implements BeerControllerDocs{

	private final BeerService beerService;
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public BeerDTO createBeer(@RequestBody @Valid BeerDTO beerDTO) throws BeerAlreadyRegisteredException {
		return beerService.createBeer(beerDTO);
	}
	
	@GetMapping("/{name}")
	public BeerDTO findByName(@PathVariable String name) throws BeerNotFoundException {
		return beerService.findByName(name);
	}
	
	@GetMapping
	public List<BeerDTO> listBeers(){
		return beerService.listAll();
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteById(@PathVariable Long id) throws BeerNotFoundException {
		beerService.deleteById(id);
	}
	
	@PatchMapping("/{id}/increment")
	public BeerDTO increment(@PathVariable Long id,@RequestBody @Valid QuantityDTO quantityDTO) throws BeerNotFoundException, BeerStockExceededException{
		return beerService.increment(id, quantityDTO.getQuantity());
	}
	
	@PatchMapping("/{id}/decrement")
	public BeerDTO decrement(@PathVariable Long id,@RequestBody @Valid QuantityDTO quantityDTO) throws BeerNotFoundException, BeerStockExceededException{
		return beerService.decrement(id, quantityDTO.getQuantity());
	}
}
