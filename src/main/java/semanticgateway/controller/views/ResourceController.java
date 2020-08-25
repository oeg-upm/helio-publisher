package semanticgateway.controller.views;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.datatypes.xsd.impl.RDFLangString;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFLanguages;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import helio.framework.objects.RDF;
import helio.framework.objects.SparqlResultsFormat;
import semanticgateway.SemanticGatewayApplication;
import semanticgateway.model.DynamicView;
import semanticgateway.service.DynamicViewService;
import semanticgateway.service.RDFService;
import semanticgateway.service.SPARQLService;


@Controller
@RequestMapping(value = {"/{path:[^\\.]*}", "/**/{path:^(?!css).*}/{path:[^\\.]*}", "/**/{path:^(?!js).*}/{path:[^\\.]*}", "/**/{path:^(?!img).*}/{path:[^\\.]*}", "/**/{path:^(?!helio_static).*}/{path:[^\\.]*}", "/**/{path:^(?!helio-api).*}/{path:[^\\.]*}"})// Anything that does not start with static or templates
public class ResourceController extends AbstractRDFController {

	// -- Attributes

	@Autowired
	public RDFService virtualizationService;
	private Logger log = Logger.getLogger(ResourceController.class.getName());
	@Autowired
	public DynamicViewService dynamicViewService;
	
	@RequestMapping(method = RequestMethod.GET, produces = { "text/rdf+n3",
			"text/n3", "text/ntriples", "text/rdf+ttl", "text/rdf+nt", "text/plain", "text/rdf+turtle", "text/turtle",
			"application/turtle", "application/x-turtle", "application/x-nice-turtle", "application/json",
			"application/odata+json", "application/ld+json", "application/x-trig", "application/rdf+xml" })
	@ResponseBody
	public String getResourceRaw(@RequestHeader Map<String, String> headers,  final HttpServletRequest request, HttpServletResponse response, Model model) {
		String resourceData = "";
		prepareResponse(response);
		//System.out.println(">+"+acceptType);
		System.out.println("****"+headers);
		try {
			// 1. Adapt request IRI in case request was forwarded
			String cleanIri = buildIRIToRetrieve(request).toString();
			if(cleanIri.startsWith("https"))
				cleanIri = cleanIri.replaceFirst("https", "http");
			// 2. Virtualize resource
			// Async
			// CompletableFuture<RDF> futureRdfData = virtualizationService.findResourceAsync(iri.toString(), SemanticGatewayApplication.mapping);
			// resourceData = futureRdfData.get(); // by default JSON-LD
			RDF resource = virtualizationService.findResource(cleanIri, SemanticGatewayApplication.mapping);
			if (resource != null) {
				// 3. Adapt format to request
				response.setStatus(HttpServletResponse.SC_ACCEPTED);
				String lang = this.extractResponseAnswerFormat(headers).getLabel();
				resourceData = resource.toString(lang);
			} else {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			
			}
		} catch (Exception e) {
			log.severe(e.toString());
			// TODO: add themplate for not found
		}
		return resourceData;
	}

	
	
	// -- GET Resources
	

	@RequestMapping( method = RequestMethod.GET, produces = { "text/html", "application/xhtml+xml", "application/xml" }, headers= {"accept=text/html,application/xhtml+xml,application/xml"})
	public String getResource(@RequestHeader Map<String, String> headers, final HttpServletRequest request, HttpServletResponse response, Model model) {
		
		String resourceData = "error.html";
		prepareResponse(response);
		try {
			// 1. Adapt request IRI in case request was forwarded
			StringBuilder iri = buildIRIToRetrieve(request);
			System.out.println("IRI: "+iri);
			// 2. Virtualize resource
			RDF resource = virtualizationService.findResource(iri.toString(), SemanticGatewayApplication.mapping);
			DynamicView dynamicView = dynamicViewService.matchResource(iri.toString());
			System.out.println("Template: "+dynamicView);
			if (resource != null || dynamicView!=null) {
				response.setStatus(HttpServletResponse.SC_ACCEPTED);
				if(dynamicView!=null) {
					resourceData = dynamicView.getTemplate();
					String sparqlQuery = dynamicView.getSparqlQuery();
					if(sparqlQuery!=null && !sparqlQuery.isEmpty()) {
						plugSparqlInModel(sparqlQuery, model);
					}else  if(resource!=null) {
							plugRDFIntoModel(model, resource, iri.toString());
					}	
				}else {
					// 3.B Inject RDF into default html
					plugRDFIntoModel(model, resource, iri.toString());
					resourceData = "resource.html";
				}
			} else {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
			
		} catch (Exception e) {
			log.severe(e.toString());
		}
		return resourceData;
	}
	
	
	private void plugRDFIntoModel(Model model, RDF rdf, String subject) {
		List<List<String>> results = new ArrayList<>();
		int dp=0;
		int op=0;
		int ty=0;
		int sa=0;
		StmtIterator statements = rdf.getRDF().listStatements(null, null, (RDFNode) null);
		while(statements.hasNext()) {
			Statement statement = statements.next();
			String predicate = statement.getPredicate().toString();
			RDFNode node = statement.getObject();
			List<String> triple = transformStatement(statement);
			if(node.isLiteral()) {
				triple.add("dp");
				dp++;
			}else if(node.isResource()) {
				if(predicate.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")) {
					ty++;
					triple.add("ty");
				}else if(predicate.equals("http://www.w3.org/2002/07/owl#sameAs")) {
					sa++;
					triple.add("sa");
				}else {
					op++;
					triple.add("op");
				}
			}
			results.add(triple);
		}

		model.addAttribute("data", results);
		model.addAttribute("subject", subject);
		model.addAttribute("dp", dp);
		model.addAttribute("op", op);
		model.addAttribute("ty", ty);
		model.addAttribute("sa", sa);
	}
	
	@Autowired
	public SPARQLService sparqlService;
	
	private void plugSparqlInModel(String sparqlQuery, Model model) throws JSONException {
		String results = sparqlService.solveQuery(sparqlQuery, SparqlResultsFormat.JSON, SemanticGatewayApplication.engine, SemanticGatewayApplication.writtingEngine);
		JSONObject object = new JSONObject(results);
		JSONArray array = object.getJSONObject("results").getJSONArray("bindings");
		List<Map<String,Map<String,String>>> modelToPlug = new ArrayList<>();
		for(int i=0; i< array.length(); i++) {
			JSONObject partialDocument = array.getJSONObject(i);
			Map<String,Map<String,String>> partialResults = toMap(partialDocument);
			modelToPlug.add(partialResults);
		}
		model.addAttribute("data", modelToPlug);
	}
	
	public static Map<String, Map<String,String>> toMap(JSONObject object) throws JSONException {
	    Map<String, Map<String,String>> map = new HashMap<>();
	 
	    @SuppressWarnings("unchecked")
		Iterator<String> keysItr = object.keys();
	    while(keysItr.hasNext()) {
	        String key = keysItr.next();
	        JSONObject nested = object.getJSONObject(key);
	        Map<String,String> value = new HashMap<>();
	        value.put("value", nested.getString("value"));
	        value.put("type", nested.getString("type"));
	        map.put(key, value);
	    }
	    return map;
	}

	// -- Method to transform a statement into a CSV

	/**
	 * Transform the Jena {@link Statement} into a list of three elements, i.e., the
	 * predicate, the object, and the type of object: dp (data property), op (object
	 * property), ty (rdf:type), and sa (owl:sameAs)
	 * 
	 * @param statement
	 *            A Jena {@link Statement} to transform
	 * @return a list of elements containing the predicate, object and type of
	 *         object
	 */
	private List<String> transformStatement(Statement statement) {
		List<String> tuple = new ArrayList<>();
		String predicate = statement.getPredicate().toString();
		String object = statement.getObject().toString();
		tuple.add(predicate);
		tuple.add(object);
		
		return tuple;
	}

	// -- Methods to handle forwarded requests

	/**
	 * This method handles forwarded requests to solve domain names
	 * 
	 * @param request
	 *            A request
	 * @return The IRI built taking into account forwarded requests
	 */
	private StringBuilder buildIRIToRetrieve(HttpServletRequest request) {
		StringBuilder iri = new StringBuilder();
		String protocol = extractProtocol(request);
		String forwardedHost = request.getHeader("x-forwarded-host");
		String forwardedServer = request.getHeader("x-forwarded-host");
		if (forwardedHost != null) {
			iri = iri.append(protocol).append("://").append(forwardedHost).append(request.getServletPath());
		} else if (forwardedServer != null) {
			iri = iri.append(protocol).append("://").append(forwardedServer).append(request.getServletPath());
		} else {
			// No forwarding is being produced
			iri = iri.append(request.getRequestURL().toString());
		}
		return iri;
	}

	private String extractProtocol(HttpServletRequest request) {
		String protocol = request.getProtocol();
		if (protocol != null) {
			protocol = protocol.substring(protocol.indexOf(':') + 1, protocol.indexOf('/')).toLowerCase();
		} else {
			protocol = "http:";
		}
		return protocol;
	}

}
