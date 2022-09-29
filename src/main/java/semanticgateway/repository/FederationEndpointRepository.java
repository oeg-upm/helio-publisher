package semanticgateway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import semanticgateway.model.FederationEndpoint;

@Repository
public interface FederationEndpointRepository extends CrudRepository<FederationEndpoint, String> {

	@Query("SELECT DISTINCT endpoint FROM FederationEndpoint")
	public List<String> getAllEndpoints();
}
