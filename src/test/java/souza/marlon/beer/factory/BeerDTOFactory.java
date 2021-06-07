package souza.marlon.beer.factory;

import souza.marlon.beer.dto.BeerDTO;
import souza.marlon.beer.dto.BeerDTO.BeerDTOBuilder;
import souza.marlon.beer.enums.BeerType;

public class BeerDTOFactory {

	public static BeerDTOBuilder ofFakeBuilder() {
		return BeerDTO.builder()
				.id(1L)
				.brand("Brahma")
				.name("Ambev")
				.max(50)
				.quantity(10)
				.type(BeerType.LAGER);
	}
	
	public static BeerDTO ofFakeValid() {
		return ofFakeBuilder().build();
	}
	
}
