package souza.marlon.beer;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import souza.marlon.beer.dto.BeerDTO;
import souza.marlon.beer.entity.Beer;

@Mapper
public interface BeerMapper {

	BeerMapper INSTANCE = Mappers.getMapper(BeerMapper.class);
	
	Beer from(BeerDTO dto);
	
	BeerDTO to(Beer beer);
}
