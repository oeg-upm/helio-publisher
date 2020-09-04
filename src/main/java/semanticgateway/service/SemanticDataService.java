package semanticgateway.service;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.parser.ParsedBooleanQuery;
import org.eclipse.rdf4j.query.parser.ParsedGraphQuery;
import org.eclipse.rdf4j.query.parser.ParsedOperation;
import org.eclipse.rdf4j.query.parser.ParsedTupleQuery;
import org.eclipse.rdf4j.query.parser.ParsedUpdate;
import org.eclipse.rdf4j.query.parser.QueryParserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import helio.framework.exceptions.ResourceNotFoundException;
import helio.framework.materialiser.mappings.HelioMaterialiserMapping;
import helio.framework.objects.SparqlResultsFormat;


/**
 * This class handles the requests that aim to retrieve the virtual RDF.
 * @author Andrea Cimmino
 *
 */
@Service
public class SemanticDataService {

	// -- Attributes
	
	private Logger log = Logger.getLogger(SemanticDataService.class.getName());
	@Autowired
	private MaterialiserService materialiserService;
	// TODO: include here the devirtualiser engine as service
	
	// -- Constructor
		
	public SemanticDataService() {
		super();
	}
		
	// -- Management Methods
	
	public void initialiseMaterialiser(HelioMaterialiserMapping mappings) {
		materialiserService.addMappings(mappings);
	}
	
	public void configureMaterialiser(String configurationFile) {
		materialiserService.configureMaterialiser(configurationFile);
	}
	
	public void finilizeMaterialiser() {
		materialiserService.close();
	}
	
	// -- RDF Methods
	
	/**
	 * This method looks for an IRI in the virtual RDF. To solve 500 requests in parallel during 4 loops it takes in average 16.143 (s) with a deviation of 5.264 (s)
	 * @param iri An IRI of a resource
	 * @param mapping The {@link Mapping} that will be used to virtualize the RDF
	 * @return A {@link CompletableFuture} of RDF containing the IRI data 
	 */
	@Async
    public CompletableFuture<Model> findResourceAsync(String iri) {
        
        Model results = ModelFactory.createDefaultModel();
		try {
			results = materialiserService.getResource(iri);
		} catch (ResourceNotFoundException e) {
			log.severe(e.getMessage());
		}
       
        return CompletableFuture.completedFuture(results);
    }

	/**
	 * This method looks for an IRI in the virtual RDF. To solve 500 requests in parallel during 4 loops it takes in average 11.728 (s) with a deviation of 21.994 (s)
	 * @param iri An IRI of a resource
	 * @param mapping The {@link Mapping} that will be used to virtualize the RDF
	 * @return A RDF containing the IRI data 
	 */
    public Model findResource(String iri) {
    		Model results = ModelFactory.createDefaultModel();
 		try {
 			results = materialiserService.getResource(iri);
 		} catch (ResourceNotFoundException e) {
 			log.severe(e.getMessage());
 		}
       
        return results;
    }
	
    /**
     * This method generates RDF from heterogeneous data providers
     * @param mapping A mapping containing how to generate the RDF
     * @return The virtual RDF
     */
    public Model getRDF() {
		return materialiserService.getRDF();
    }

    
    // -- SPARQL Methods
    
    /**
	 * This method solves a SPARQL query relying on the Semantic-Engine framework
	 * @param query A SPARQL query
	 * @param answerFormat A {@link SparqlResultsFormat} object specifying the output format
	 * @return The query results
	 */
	public String solveQuery(String query, SparqlResultsFormat answerFormat){
		String response = null;
		
		ParsedOperation operation = QueryParserUtil.parseOperation(QueryLanguage.SPARQL, query, null); 
		if (operation instanceof ParsedTupleQuery || operation instanceof ParsedBooleanQuery || operation instanceof ParsedGraphQuery) {
			response = this.materialiserService.solveQuery(query, answerFormat);
		} else if(operation instanceof ParsedUpdate) {
			// TODO: devirtualise the query here
		}
		
		return response ;
	}
	
	 /**
		 * This method solves a SPARQL query relying on the Semantic-Engine framework
		 * @param query A SPARQL query
		 * @param answerFormat A {@link SparqlResultsFormat} object specifying the output format
		 * @return The query results
		 */
		public String solveQueryStream(String query, SparqlResultsFormat answerFormat){
			String response = null;
			
			ParsedOperation operation = QueryParserUtil.parseOperation(QueryLanguage.SPARQL, query, null); 
			if (operation instanceof ParsedTupleQuery || operation instanceof ParsedGraphQuery) {
				response = this.materialiserService.solveQueryStream(query, answerFormat);
			} else if(operation instanceof ParsedUpdate) {
				// TODO: devirtualise the query here
			}
			
			return response ;
		}
	
}
