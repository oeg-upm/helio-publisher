package semanticgateway.controller;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import semanticgateway.SemanticGatewayApplication;

@RestController
@RequestMapping("**/sparql")
public class QueryController extends AbstractController {

	public static Logger log = Logger.getLogger(QueryController.class.getName());

	// http://localhost:8080/sparql?query=select+distinct+*+where+%7B%3Fs+%3Fp+%3Fo+.%7D+LIMIT+100 include interface
	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public String sparqlEndpointGET(@RequestParam (required = false) String query, HttpServletResponse response) {
		String result = null;
		prepareResponse(response);
		
		try {
			// Check that the engine has a mapping
			if (SemanticGatewayApplication.engine.getMapping() != null ) {
				if(query!=null) {
					log.info("Solving SPARQL query (GET)");
					JSONObject queryResultsJson = SemanticGatewayApplication.engine.query(query);
					result = queryResultsJson.toString();
				}else {
					log.info("Returning SPARQL GUI");
					
				}
				response.setStatus(HttpServletResponse.SC_ACCEPTED);
			} else {
				response.setStatus(HttpServletResponse.SC_CONFLICT);
				log.warning("No mapping was found for the semantic engine to be used");
			}
		} catch (Exception e) {
			log.severe(e.getMessage());
		}
		log.info("Query solved");
		return result;
	}
	
	
	
	@RequestMapping(method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public String sparqlEndpointPOST(@RequestBody(required = true) String query, HttpServletResponse response) {
		String result = null;
		prepareResponse(response);
		
		try {
			// Check that the engine has a mapping
			if (SemanticGatewayApplication.engine.getMapping() != null ) {
				if(query!=null) {
					log.info("Solving SPARQL query (POST)");
					JSONObject queryResultsJson = SemanticGatewayApplication.engine.query(query);
					result = queryResultsJson.toString();
				}else {
					log.info("Returning SPARQL GUI");
				}
				response.setStatus(HttpServletResponse.SC_ACCEPTED);
			} else {
				response.setStatus(HttpServletResponse.SC_CONFLICT);
				log.warning("No mapping was found for the semantic engine to be used");
			}
		} catch (Exception e) {
			log.severe(e.getMessage());
		}
		log.info("Query solved");
		return result;
	}

}
