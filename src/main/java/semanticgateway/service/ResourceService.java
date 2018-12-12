package semanticgateway.service;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import framework.components.engine.EngineImp;
import framework.components.exceptions.ResourceNotFoundException;
import framework.mapping.Mapping;
import framework.objects.RDF;

/**
 * This class handles the requests that aim to retrieve the RDF of a specific resource.
 * @author Andrea Cimmino
 *
 */
@Service
public class ResourceService {

	// -- Attributes
	
	private Logger log = Logger.getLogger(ResourceService.class.getName());
		
	// -- Constructor
		
	public ResourceService() {
		super();
	}
		
	// -- Methods
	
	/**
	 * This method looks for an IRI in the virtual RDF. To solve 500 requests in parallel during 4 loops it takes in average 16.143 (s) with a deviation of 5.264 (s)
	 * @param iri An IRI of a resource
	 * @param mapping The {@link Mapping} that will be used to virtualize the RDF
	 * @return A {@link CompletableFuture} of RDF containing the IRI data 
	 */
	@Async
    public CompletableFuture<RDF> findResourceAsync(String iri, Mapping mapping) {
        
        EngineImp engine = new EngineImp(mapping);
        RDF results = new RDF();
		try {
			results = engine.getResource(iri);
		} catch (ResourceNotFoundException e) {
			log.severe(e.getMessage());
			results = null;
		}
       
        return CompletableFuture.completedFuture(results);
    }

	/**
	 * This method looks for an IRI in the virtual RDF. To solve 500 requests in parallel during 4 loops it takes in average 11.728 (s) with a deviation of 21.994 (s)
	 * @param iri An IRI of a resource
	 * @param mapping The {@link Mapping} that will be used to virtualize the RDF
	 * @return A RDF containing the IRI data 
	 */
    public RDF findResource(String iri, Mapping mapping) {
        
        EngineImp engine = new EngineImp(mapping);
        RDF results = new RDF();
		try {
			results = engine.getResource(iri);
		} catch (ResourceNotFoundException e) {
			log.severe(e.getMessage());
			results = null;
		}
       
        return results;
    }
	
}
