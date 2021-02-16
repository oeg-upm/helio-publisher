package semanticgateway.controller.sparql;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import helio.framework.objects.SparqlResultsFormat;
import semanticgateway.SemanticGatewayApplication;
import semanticgateway.model.FederationEndpoint;
import semanticgateway.service.FederationService;



/**
 * This class handles all kind of requests related to SPARQL queries.
 * 
 * The headers and response codes where extracted from <a href="http://vos.openlinksw.com/owiki/wiki/VOS/VOSSparqlProtocol">Virtuoso Open-Source Edition Documentation</a>
 * @param response
 */
//@Controller
//@RequestMapping("/sparql")
public class SPARQLFederatedController extends AbstractSPARQLController {

	// INFO: pass the format as variable as well ?
	
	// -- Attributes
	private Logger log = Logger.getLogger(SPARQLFederatedController.class.getName());

	@Autowired
	private FederationService federationService;
	
	// -- Overrided methods
	
	@Override
	protected void prepareResponse(HttpServletResponse response) {
		response.setHeader("Server", "Helio Publisher");
		response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR ); // by default response code is BAD
	}	
	
	@PostConstruct
	public void registerHelio() {
		federationService.addEndpoint(new FederationEndpoint("http://localhost:"+SemanticGatewayApplication.httpPort+"/sparql-393cb7f5c1a61611f07a16c4e5865d51"));
	}
	
	
	
	// Query solving methods
	
	// -- GET method
	
	@RequestMapping(method = RequestMethod.GET, produces = {"application/sparql-results+xml", "text/rdf+n3", "text/rdf+ttl", "text/rdf+turtle", "text/turtle", "text/n3", "application/turtle", "application/x-turtle", "application/x-nice-turtle", "text/rdf+nt", "text/plain", "text/ntriples", "application/x-trig", "application/rdf+xml", "application/soap+xml", "application/soap+xml;11",  "application/vnd.ms-excel", "text/csv", "text/tab-separated-values", "application/javascript", "application/json", "application/sparql-results+json", "application/odata+json", "application/microdata+json", "text/cxml", "text/cxml+qrcode", "application/atom+xml"})
	@ResponseBody
	public String sparqlEndpointGET(@RequestHeader Map<String, String> headers, @RequestParam (required = true) String query, HttpServletResponse response) {
		prepareResponse(response);
		try{
			if(query.startsWith("query="))
				query = query.substring(6);
			if(query.startsWith("update="))
				query = query.substring(7);
			query = java.net.URLDecoder.decode(query, StandardCharsets.UTF_8.toString());
		}catch(Exception e) {
			log.severe(e.toString());
		}
		return solveQuery(query, headers, response);
	}
	
	// -- POST method
	
	@RequestMapping(method = RequestMethod.POST, produces = {"application/sparql-results+xml", "text/rdf+n3", "text/rdf+ttl", "text/rdf+turtle", "text/turtle", "text/n3", "application/turtle", "application/x-turtle", "application/x-nice-turtle", "text/rdf+nt", "text/plain", "text/ntriples", "application/x-trig", "application/rdf+xml", "application/soap+xml", "application/soap+xml;11", "application/vnd.ms-excel", "text/csv", "text/tab-separated-values", "application/javascript", "application/json", "application/sparql-results+json", "application/odata+json", "application/microdata+json", "text/cxml", "text/cxml+qrcode", "application/atom+xml"}) 
	@ResponseBody
	public String sparqlEndpointPOST(@RequestHeader Map<String, String> headers, @RequestBody(required = true) String query, HttpServletResponse response) {
		prepareResponse(response);
		return solveQuery(query, headers, response);
	}
	


	/**
	 * This method solves a SPARQL query using the semantic engine. By default answer is in JSON format
	 * @param query A SPARQL query to solve
	 * @param headers A set of headers that should contain the format to display the query answer (it can be null or not contain 'Accept' header).
	 * @param response A {@link HttpServletResponse} containing the possible responses, i.e., OK (200), syntax error (400), error processing query or fetching data or missing mappings (500)
	 * @return The query answer in the specified format
	 */
	private String solveQuery(String query, Map<String, String> headers, HttpServletResponse response) {
		String result = "";
		try {
			String format = extractSPARQLAnswerFormat(headers);
			SparqlResultsFormat specifiedFormat = sparqlResponseFormats.get(format);
			if(specifiedFormat == null)
				specifiedFormat = SparqlResultsFormat.JSON;
			result = federationService.solveQuery(query, specifiedFormat);
			if(result == null) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				log.info("Query has syntax errors");
			}else {
				response.setStatus(HttpServletResponse.SC_ACCEPTED);
				response.setHeader("Content-Type", "format");
				log.info("Query solved");
			}
		} catch (Exception e) {
			log.severe(e.getMessage());
		}
		return result;
	}
	



	/**
	 * This method extracts from the request headers the right {@link SparqlResultsFormat} to format the query results
	 * @param headers A set of headers
	 * @return A {@link SparqlResultsFormat} object
	 */
	private String extractSPARQLAnswerFormat(Map<String, String> headers) {
		String format = "application/json";
		if(headers!=null && !headers.isEmpty()) {
			if(headers.containsKey("accept"))
				format = headers.get("accept");
			if(headers.containsKey("Accept"))
				format = headers.get("Accept");
		}
		if(format.contains(",")) {
			String[] formatsAux = format.split(",");
			for(int index=0; index < formatsAux.length; index++) {
				String formatAux = formatsAux[index];
				if(sparqlResponseFormats.containsKey(formatAux)) {
					format = formatAux;
					break;
				}
			}
		}
		
		return format;
	}

	
	
	
	
}
