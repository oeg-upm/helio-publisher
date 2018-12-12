package semanticgateway.service;

import java.util.logging.Logger;
import org.apache.jena.query.QueryFactory;
import org.springframework.stereotype.Service;

import framework.components.engine.EngineImp;
import framework.components.engine.sparql.SparqlResultsFormat;
import framework.mapping.Mapping;
import semanticgateway.controller.SPARQLController;

/**
 * This class handles SPARQL queries received in the controller by passing them to the Semantic-Engine framework.
 * @author Andrea Cimmino
 *
 */
@Service
public class SPARQLService {

	// -- Attributes
	
	private Logger log = Logger.getLogger(SPARQLController.class.getName());
	
	// -- Constructor
	
	public SPARQLService() {
		super();
	}
	
	// -- Methods
	
	/**
	 * This method solves a SPARQL query relying on the Semantic-Engine framework
	 * @param query A SPARQL query
	 * @param answerFormat A {@link SparqlResultsFormat} object specifying the output format
	 * @return The query results
	 */
	public String solveQuery(String query, SparqlResultsFormat answerFormat, Mapping mapping){
		String response = null;
		if(isQueryCorrect(query)) {
			EngineImp engine = new EngineImp(mapping);
			response = engine.query(query, answerFormat);
		}
		return response ;
	}
	
	/**
	 * This method checks syntax errors for a given SPARQL query
	 * @param query A SPARQL query
	 * @return A {@link Boolean} value specifying if the input query was correct (true), or had some errors (false)
	 */
	private Boolean isQueryCorrect(String query) {
		Boolean isCorrect = false;
		try {
			QueryFactory.create(query);
			isCorrect = true;
		}catch (Exception e) {
			log.severe(e.getMessage());
		}
		
		return isCorrect;
	}
	

	
}
