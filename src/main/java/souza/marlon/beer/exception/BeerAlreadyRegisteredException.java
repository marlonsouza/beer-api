package souza.marlon.beer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BeerAlreadyRegisteredException extends Exception {

	public BeerAlreadyRegisteredException(String nameBeer) {
		super(String.format("Beer with name %s already registered in the system.", nameBeer));
	}
	
	
}
