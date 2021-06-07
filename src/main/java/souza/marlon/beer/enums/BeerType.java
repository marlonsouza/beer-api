package souza.marlon.beer.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BeerType {

	
	LAGER("Lager"),
	MALZIBIER("Malzbier"),
	WITBIER("Witbier"),
	WEISS("Weiss"),
	ALE("Ale"),
	IPA("Ipa"),
	STOUT("Stout");
	
	private final String description;
}
