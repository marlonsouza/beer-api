package souza.marlon.beer.factory;

import souza.marlon.beer.dto.QuantityDTO;

public class QuantityDTOFactory {

	public static QuantityDTO of(int quantity) {
		return new QuantityDTO(quantity);
	}

}
