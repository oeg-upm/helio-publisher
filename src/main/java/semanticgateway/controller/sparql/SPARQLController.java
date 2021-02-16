package semanticgateway.controller.sparql;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.parser.ParsedGraphQuery;
import org.eclipse.rdf4j.query.parser.ParsedOperation;
import org.eclipse.rdf4j.query.parser.ParsedTupleQuery;
import org.eclipse.rdf4j.query.parser.QueryParserUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import helio.framework.objects.SparqlResultsFormat;



/**
 * This class handles all kind of requests related to SPARQL queries.
 * 
 * The headers and response codes where extracted from <a href="http://vos.openlinksw.com/owiki/wiki/VOS/VOSSparqlProtocol">Virtuoso Open-Source Edition Documentation</a>
 * @param response
 */
@Controller
@RequestMapping("/sparql")
public class SPARQLController extends AbstractSPARQLController {

	// INFO: pass the format as variable as well ?
	
	// -- Attributes
	private Logger log = Logger.getLogger(SPARQLController.class.getName());
	private static Map<String,SparqlResultsFormat> sparqlResponseFormats;

	// -- Overrided methods
	
	@Override
	protected void prepareResponse(HttpServletResponse response) {
		response.setHeader("Server", "Helio Publisher");
		response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR ); // by default response code is BAD
	}	
	
	// Interface method
	
	// -- GET method

	@RequestMapping(method = RequestMethod.GET, produces = { "text/html", "application/xhtml+xml", "application/xml" })
	public String sparqlGUI(@RequestHeader Map<String, String> headers, HttpServletResponse response) {
		return "sparql.html";
	}
	
	// Query solving methods
	
	// -- GET method
	
	@SuppressWarnings("deprecation")
	@RequestMapping(method = RequestMethod.GET, produces = {"application/sparql-results+xml", "text/rdf+n3", "text/rdf+ttl", "text/rdf+turtle", "text/turtle", "text/n3", "application/turtle", "application/x-turtle", "application/x-nice-turtle", "text/rdf+nt", "text/plain", "text/ntriples", "application/x-trig", "application/rdf+xml", "application/soap+xml", "application/soap+xml;11",  "application/vnd.ms-excel", "text/csv", "text/tab-separated-values", "application/javascript", "application/json", "application/sparql-results+json", "application/odata+json", "application/microdata+json", "text/cxml", "text/cxml+qrcode", "application/atom+xml"})
	@ResponseBody
	public String sparqlEndpointGET(@RequestHeader Map<String, String> headers, @RequestParam (required = true) String query, HttpServletResponse response) {
		prepareResponse(response);
		try{
			
			if(query.startsWith("query="))
				query = query.substring(6);
			if(query.startsWith("update="))
				query = query.substring(7);
			if(Base64.isArrayByteBase64(query.getBytes())) 
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
	private String solveQuery(String query, Map<String,String> headers, HttpServletResponse response) {
		String result = "";
		try {
			/*SparqlResultsFormat specifiedFormat =  SparqlResultsFormat.JSON;
			ParsedOperation operation = QueryParserUtil.parseOperation(QueryLanguage.SPARQL, query, null); 
			if(operation instanceof ParsedTupleQuery) {
				specifiedFormat =  SparqlResultsFormat.XML;
			}else if(operation instanceof ParsedGraphQuery) {
				specifiedFormat = SparqlResultsFormat.RDF_TTL;
			}*/
			String format = extractSPARQLAnswerFormat(headers);
			SparqlResultsFormat specifiedFormat = sparqlResponseFormats.get(format);
			if(specifiedFormat == null)
				specifiedFormat = SparqlResultsFormat.JSON;
			
			result = this.rdfService.solveQuery(query, specifiedFormat);
			if(result == null) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				log.info("Query has syntax errors");
			}else {
				response.setStatus(200);
				response.setHeader("Content-Type", specifiedFormat.getFormat());
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
	
	static{
		sparqlResponseFormats = new HashMap<>();
		sparqlResponseFormats.put("application/sparql-results+xml", SparqlResultsFormat.XML );
		sparqlResponseFormats.put("text/rdf+n3", SparqlResultsFormat.RDF_N3 );
		sparqlResponseFormats.put("text/rdf+ttl", SparqlResultsFormat.RDF_TTL );
		sparqlResponseFormats.put("text/rdf+turtle", SparqlResultsFormat.RDF_TURTLE );
		sparqlResponseFormats.put("text/turtle", SparqlResultsFormat.RDF_TURTLE );
		sparqlResponseFormats.put("text/n3", SparqlResultsFormat.RDF_N3 );
		sparqlResponseFormats.put("application/turtle", SparqlResultsFormat.RDF_TURTLE );
		sparqlResponseFormats.put("application/x-turtle", SparqlResultsFormat.RDF_TURTLE );
		sparqlResponseFormats.put("application/x-nice-turtle", SparqlResultsFormat.RDF_TURTLE );
		sparqlResponseFormats.put("text/rdf+nt", SparqlResultsFormat.RDF_NT );
		sparqlResponseFormats.put("text/plain", SparqlResultsFormat.TEXT );
		sparqlResponseFormats.put("text/ntriples", SparqlResultsFormat.N_TRIPLES );
		sparqlResponseFormats.put("application/x-trig", SparqlResultsFormat.TRIG );
		sparqlResponseFormats.put("application/rdf+xml", SparqlResultsFormat.RDF_XML );
		// TODO:  sparqlResponseFormats.put("application/soap+xml", SparqlResultsFormat. );
		// TODO:  sparqlResponseFormats.put("application/soap+xml;11", SparqlResultsFormat. );
		sparqlResponseFormats.put("text/html", SparqlResultsFormat.HTML );
		sparqlResponseFormats.put("text/md+html", SparqlResultsFormat.HTML ); // TODO: 
		sparqlResponseFormats.put("text/microdata+html", SparqlResultsFormat.HTML ); // TODO: 
		sparqlResponseFormats.put("text/x-html+ul", SparqlResultsFormat.HTML ); // TODO: 
		sparqlResponseFormats.put("text/x-html+tr", SparqlResultsFormat.HTML ); // TODO: 
		// TODO:  sparqlResponseFormats.put("application/vnd.ms-excel", SparqlResultsFormat. ); 
		sparqlResponseFormats.put("text/csv", SparqlResultsFormat.CSV );
		sparqlResponseFormats.put("text/tab-separated-values", SparqlResultsFormat.TSV );
		// TODO: sparqlResponseFormats.put("application/javascript", SparqlResultsFormat. );
		sparqlResponseFormats.put("application/json", SparqlResultsFormat.JSON );
		sparqlResponseFormats.put("application/sparql-results+json", SparqlResultsFormat.JSON );
		// TODO:  sparqlResponseFormats.put("application/odata+json", SparqlResultsFormat. );
		// TODO:  sparqlResponseFormats.put("application/microdata+json", SparqlResultsFormat. );
		// TODO:  sparqlResponseFormats.put("text/cxml", SparqlResultsFormat. );
		// TODO:  sparqlResponseFormats.put("text/cxml+qrcode", SparqlResultsFormat. );
		// TODO:  sparqlResponseFormats.put("application/atom+xml", SparqlResultsFormat. );
		sparqlResponseFormats.put("application/xhtml+xml", SparqlResultsFormat.HTML ); // TODO: 
	}
	
	
}
