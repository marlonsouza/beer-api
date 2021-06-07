package souza.marlon.beer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import souza.marlon.beer.entity.Beer;

@Repository
public interface BeerReporitory extends JpaRepository<Beer, Long>{

	Optional<Beer> findByName(String name);
	
}
