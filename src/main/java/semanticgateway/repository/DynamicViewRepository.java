package semanticgateway.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import semanticgateway.model.DynamicView;

@Repository
public interface DynamicViewRepository extends CrudRepository<DynamicView, String> {
	

}
