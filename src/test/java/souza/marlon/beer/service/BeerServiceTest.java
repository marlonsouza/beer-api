package souza.marlon.beer.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.description;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import souza.marlon.beer.BeerMapper;
import souza.marlon.beer.dto.BeerDTO;
import souza.marlon.beer.entity.Beer;
import souza.marlon.beer.exception.BeerAlreadyRegisteredException;
import souza.marlon.beer.exception.BeerNotFoundException;
import souza.marlon.beer.exception.BeerStockExceededException;
import souza.marlon.beer.factory.BeerDTOFactory;
import souza.marlon.beer.repository.BeerReporitory;

@ExtendWith(MockitoExtension.class)
public class BeerServiceTest {

	@Mock
	private BeerReporitory beerReporitory;

	@InjectMocks
	private BeerService beerService;
	
	private final BeerMapper beerMapper = BeerMapper.INSTANCE;
	
	@Test
	void whenBeerInformedThenItShouldBeCreated() throws BeerAlreadyRegisteredException{
		BeerDTO expectBeerDTO = BeerDTOFactory.ofFakeValid();
		Beer expectedSavedBeer = beerMapper.from(expectBeerDTO);
		
		when(beerReporitory.findByName(expectBeerDTO.getName())).thenReturn(Optional.empty());
		when(beerReporitory.save(expectedSavedBeer)).thenReturn(expectedSavedBeer);
		
		BeerDTO createBeer = beerService.createBeer(expectBeerDTO);
		
		assertThat(createBeer.getId(), is(equalTo(expectBeerDTO.getId())));
		assertThat(createBeer.getName(), is(equalTo(expectBeerDTO.getName())));
		assertThat(createBeer.getQuantity(), is(equalTo(expectBeerDTO.getQuantity())));
		
	}
	
	@Test
	void whenAlreadyRegisterBeerInformedThenAnExceptionShouldBeThrow() throws BeerAlreadyRegisteredException {
		BeerDTO expectBeerDTO = BeerDTOFactory.ofFakeValid();
		Beer duplicatedBeer = beerMapper.from(expectBeerDTO);
		
		when(beerReporitory.findByName(expectBeerDTO.getName())).thenReturn(Optional.of(duplicatedBeer));
		
		assertThrows(BeerAlreadyRegisteredException.class, () -> beerService.createBeer(expectBeerDTO));
	}
	
	@Test
	void whenValidBeerNameIsGivenThenReturnABeer() throws BeerNotFoundException {
		BeerDTO expectBeerFoundDTO = BeerDTOFactory.ofFakeValid();
		Beer expectFoundBeer = beerMapper.from(expectBeerFoundDTO);
		
		when(beerReporitory.findByName(expectBeerFoundDTO.getName())).thenReturn(Optional.of(expectFoundBeer));
		
		BeerDTO foundedBeer = beerService.findByName(expectBeerFoundDTO.getName());
		
		assertThat(foundedBeer.getId(), is(equalTo(expectBeerFoundDTO.getId())));
		assertThat(foundedBeer.getName(), is(equalTo(expectBeerFoundDTO.getName())));
		assertThat(foundedBeer.getType(), is(equalTo(expectBeerFoundDTO.getType())));
	}
	
	@Test
	void whenNoRegisteredBeerNameisGivenThenBeerNotFoundException() throws BeerNotFoundException {
		BeerDTO expectBeerFoundDTO = BeerDTOFactory.ofFakeValid();
		
		when(beerReporitory.findByName(expectBeerFoundDTO.getName())).thenReturn(Optional.empty());

		assertThrows(BeerNotFoundException.class, () -> beerService.findByName(expectBeerFoundDTO.getName()));
		
	}
	
	@Test
	void whenListBeerIsCalledThenReturnAListOfBeers() {
		BeerDTO expectBeerFoundDTO = BeerDTOFactory.ofFakeValid();
		Beer expectFoundBeer = beerMapper.from(expectBeerFoundDTO);
		
		when(beerReporitory.findAll()).thenReturn(Collections.singletonList(expectFoundBeer));
		
		List<BeerDTO> foundListBeerDTOs = beerService.listAll();
		
		assertThat(foundListBeerDTOs, is(not(empty())));
		assertThat(foundListBeerDTOs.get(0), is(equalTo(expectBeerFoundDTO)));
		
	}
	
	@Test
	void whenListBeerIsCalledThenReturnAEmptyListOfBeers() {
		
		when(beerReporitory.findAll()).thenReturn(Collections.emptyList());
		
		List<BeerDTO> foundListBeerDTOs = beerService.listAll();
		
		assertThat(foundListBeerDTOs, is(empty()));
		
	}
	
	@Test
	void whenExclusionIsCalledWIthValidIdThenBeerShouldBeDeleted() throws BeerNotFoundException {
		
		BeerDTO expectDeletedBeerDTO = BeerDTOFactory.ofFakeValid();
		Beer expectDeletedBeer = beerMapper.from(expectDeletedBeerDTO);
		
		when(beerReporitory.findById(expectDeletedBeer.getId())).thenReturn(Optional.of(expectDeletedBeer));
		doNothing().when(beerReporitory).deleteById(expectDeletedBeer.getId());
		
		beerService.deleteById(expectDeletedBeerDTO.getId());
		
		verify(beerReporitory,Mockito.times(1)).findById(expectDeletedBeerDTO.getId());
		verify(beerReporitory,Mockito.times(1)).deleteById(expectDeletedBeerDTO.getId());
		
	}
	
	@Test
	void whenExclusionNoRegisteredBeerIdisGivenThenBeerNotFoundException() throws BeerNotFoundException {
		BeerDTO expectBeerFoundDTO = BeerDTOFactory.ofFakeValid();
		
		when(beerReporitory.findById(expectBeerFoundDTO.getId())).thenReturn(Optional.empty());

		assertThrows(BeerNotFoundException.class, () -> beerService.deleteById(expectBeerFoundDTO.getId()));
		
	}
	
	@Test
	void whenIncrementIsCalledThenIncrementBeerStock() throws Exception {
		
		BeerDTO expectBeerDTO = BeerDTOFactory.ofFakeValid();
		Beer expectBeer = beerMapper.from(expectBeerDTO);
		
		when(beerReporitory.findById(expectBeerDTO.getId())).thenReturn(Optional.of(expectBeer));
		when(beerReporitory.save(expectBeer)).thenReturn(expectBeer);
		
		int quantityToIncrement = 10;
		int expectedQuantityAfterIncrement = expectBeerDTO.getQuantity() + quantityToIncrement;
		
		BeerDTO incrementedBeerDTO = beerService.increment(expectBeerDTO.getId(), quantityToIncrement);
		
		assertThat(expectedQuantityAfterIncrement, equalTo(incrementedBeerDTO.getQuantity()));
		assertThat(incrementedBeerDTO.getQuantity(), lessThan(expectBeerDTO.getMax()));
		
	}
	
	@Test
	void whenIncrementIsGreaterThenMaxThenThrowsExeption() {
		
		BeerDTO expectBeerDTO = BeerDTOFactory.ofFakeValid();
		Beer expectBeer = beerMapper.from(expectBeerDTO);
		
		when(beerReporitory.findById(expectBeerDTO.getId())).thenReturn(Optional.of(expectBeer));
		
		int quantityToIncrement = expectBeerDTO.getMax() + 1;
		
		assertThrows(BeerStockExceededException.class, () -> beerService.increment(expectBeer.getId(), quantityToIncrement));
		
		
	}
	
	@Test
	void whenIncrementAfterSumIsGreaterThenMaxThenThrowsExeption() {
		
		BeerDTO expectBeerDTO = BeerDTOFactory.ofFakeValid();
		Beer expectBeer = beerMapper.from(expectBeerDTO);
		
		when(beerReporitory.findById(expectBeerDTO.getId())).thenReturn(Optional.of(expectBeer));
		
		int quantityToIncrement = 45;
		
		assertThrows(BeerStockExceededException.class, () -> beerService.increment(expectBeer.getId(), quantityToIncrement));
		
	}
	
	@Test
	void whenDecrementIsCalledThenReturnADecrementedBeerStock() throws BeerNotFoundException, BeerStockExceededException {
		
		BeerDTO expectBeerDTO = BeerDTOFactory.ofFakeValid();
		Beer expectBeer = beerMapper.from(expectBeerDTO);
		
		when(beerReporitory.findById(expectBeerDTO.getId())).thenReturn(Optional.of(expectBeer));
		when(beerReporitory.save(expectBeer)).thenReturn(expectBeer);
		
		int quantityToDecrement = 5;
		int expectedQuantityAfterDecrement = expectBeerDTO.getQuantity() - quantityToDecrement;
		
		BeerDTO decrementedBeerDTO = beerService.decrement(expectBeer.getId(),quantityToDecrement);
		
		assertThat(expectedQuantityAfterDecrement,equalTo(decrementedBeerDTO.getQuantity()));
		assertThat(decrementedBeerDTO.getQuantity(), greaterThan(0));
		
	}
	
	@Test
	void whenIncrementAfterSumIsLessThenZeroThenThrowsExeption() {
		
		BeerDTO expectBeerDTO = BeerDTOFactory.ofFakeValid();
		Beer expectBeer = beerMapper.from(expectBeerDTO);
		
		when(beerReporitory.findById(expectBeerDTO.getId())).thenReturn(Optional.of(expectBeer));
		
		int quantityToIncrement = 145;
		
		assertThrows(BeerStockExceededException.class, () -> beerService.decrement(expectBeer.getId(), quantityToIncrement));
		
	}
}
