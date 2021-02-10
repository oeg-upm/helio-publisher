package semanticgateway.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import semanticgateway.model.HelioUser;

@Repository
public interface HelioUserRepository extends CrudRepository<HelioUser, String> {
	
}
