package semanticgateway.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import framework.components.engine.EngineImp;
import framework.components.exceptions.ResourceNotFoundException;
import framework.objects.RDF;
import semanticgateway.SemanticGatewayApplication;

@Service
public class VirtualizationService {


	
	@Async
    public CompletableFuture<RDF> findResource(String iri) throws InterruptedException {
        
        EngineImp engine = new EngineImp(SemanticGatewayApplication.mapping);
        RDF results = new RDF();
		try {
			results = engine.getResource(iri);
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		}
       
        return CompletableFuture.completedFuture(results);
    }
	
}
