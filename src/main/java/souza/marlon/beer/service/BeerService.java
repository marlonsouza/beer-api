package souza.marlon.beer.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import souza.marlon.beer.BeerMapper;
import souza.marlon.beer.dto.BeerDTO;
import souza.marlon.beer.entity.Beer;
import souza.marlon.beer.exception.BeerAlreadyRegisteredException;
import souza.marlon.beer.exception.BeerNotFoundException;
import souza.marlon.beer.exception.BeerStockExceededException;
import souza.marlon.beer.repository.BeerReporitory;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class BeerService {

	private final BeerReporitory beerReporitory;
	private final BeerMapper beerMapper = BeerMapper.INSTANCE;

	public BeerDTO createBeer(BeerDTO beerDTO) throws BeerAlreadyRegisteredException {
		verifyIfIsAlreadyRegistered(beerDTO.getName());

		Beer beer = beerMapper.from(beerDTO);
		Beer saved = beerReporitory.save(beer);

		return beerMapper.to(saved);
	}

	public List<BeerDTO> listAll() {
		return beerReporitory.findAll().stream().map(b -> beerMapper.to(b)).collect(Collectors.toList());
	}
	
	public BeerDTO findByName(String name) throws BeerNotFoundException{
		Beer foundBeer = beerReporitory.findByName(name)
				.orElseThrow(() -> new BeerNotFoundException(name));
		return beerMapper.to(foundBeer);
	}
	
	public void deleteById(Long id) throws BeerNotFoundException{
		verifyIfExists(id);
		beerReporitory.deleteById(id);
	}

	private Beer verifyIfExists(Long id) throws BeerNotFoundException {
		return beerReporitory.findById(id).orElseThrow(() -> new BeerNotFoundException(id));
		
	}

	private void verifyIfIsAlreadyRegistered(String name) throws BeerAlreadyRegisteredException {
		Optional<Beer> optBeer = beerReporitory.findByName(name);
		
		if(optBeer.isPresent()) {
			throw new BeerAlreadyRegisteredException(name);
		}
	}

	public BeerDTO increment(Long id, int quantityToIncrement) throws BeerNotFoundException, BeerStockExceededException{
		Beer foundBeerToIncrement = verifyIfExists(id);
		
		if(quantityToIncrement + foundBeerToIncrement.getQuantity()  <= foundBeerToIncrement.getMax()) {
			foundBeerToIncrement.setQuantity(foundBeerToIncrement.getQuantity() + quantityToIncrement);
			
			Beer savedBeer = beerReporitory.save(foundBeerToIncrement);
			
			return beerMapper.to(savedBeer);
		}
		
		throw new BeerStockExceededException(id, quantityToIncrement);
	}

	public BeerDTO decrement(Long id, int quantityToDecrement) throws BeerNotFoundException, BeerStockExceededException {
		Beer foundBeerToDecrement = verifyIfExists(id);
		
		if(foundBeerToDecrement.getQuantity() - quantityToDecrement > -1) {
			foundBeerToDecrement.setQuantity(foundBeerToDecrement.getQuantity() - quantityToDecrement);
			
			Beer savedBeer = beerReporitory.save(foundBeerToDecrement);
			
			return beerMapper.to(savedBeer);
		}
		
		throw new BeerStockExceededException(id, quantityToDecrement);
	}

}
