package semanticgateway.controller;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.jena.riot.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import framework.components.engine.sparql.SparqlResultsFormat;
import framework.objects.RDF;
import semanticgateway.SemanticGatewayApplication;
import semanticgateway.service.ResourceService;


@RestController
@RequestMapping("/**")
public class ResourceController extends ResponseRDFController{

	// -- Attributes
	@Autowired
	public ResourceService virtualizationService;
	private Logger log = Logger.getLogger(ResourceController.class.getName());
	
	 // -- GET Resources
	 
		@RequestMapping(method = RequestMethod.GET, produces= {"text/rdf+n3", "text/n3", "text/ntriples", "text/rdf+ttl", "text/rdf+nt", "text/plain", "text/rdf+turtle", "text/turtle", "application/turtle", "application/x-turtle", "application/x-nice-turtle", "application/json", "application/odata+json", "application/ld+json", "application/x-trig", "application/rdf+xml"})
		@ResponseBody
		public String getResource(@RequestHeader Map<String, String> headers, final HttpServletRequest request, HttpServletResponse response) {
			String resourceData = null;
			prepareResponse(response);
			try {
				// 1. Adapt request IRI in case request was forwarded
				StringBuilder iri = buildIRIToRetrieve(request);
				// 2. Virtualize resource 
				// Async
				// CompletableFuture<RDF> futureRdfData = virtualizationService.findResourceAsync(iri.toString(), SemanticGatewayApplication.mapping);
				//resourceData = futureRdfData.get(); // by default JSON-LD
				RDF resource = virtualizationService.findResource(iri.toString(), SemanticGatewayApplication.mapping);
				if(resource!=null) {
					// 3. Adapt format to request
					response.setStatus(HttpServletResponse.SC_ACCEPTED);	
					String lang = this.extractResponseAnswerFormat(headers).getLabel();
					resourceData = resource.toString(lang);
				}else {
					response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				}
			}catch(Exception e) {
				log.severe(e.toString());
			}
		
			return resourceData;
		}
		
		// -- Methods to handle forwarded requests
		
		/**
		 * This method handles forwarded requests to solve domain names
		 * @param request A request
		 * @return The IRI built taking into account forwarded requests
		 */
		private StringBuilder buildIRIToRetrieve(HttpServletRequest request) {
			StringBuilder iri = new StringBuilder();
			String protocol = extractProtocol(request);
			String forwardedHost = request.getHeader("x-forwarded-host");
			String forwardedServer = request.getHeader("x-forwarded-host");
			if(forwardedHost!=null) {
				iri = iri.append(protocol).append("://").append(forwardedHost).append(request.getServletPath());
			}else if(forwardedServer!=null) {
				iri = iri.append(protocol).append("://").append(forwardedServer).append(request.getServletPath());
			}else {
				// No forwarding is being produced
				iri = iri.append(request.getRequestURL().toString());
			}
			return iri;
		}
		
		private String extractProtocol(HttpServletRequest request) {
			String protocol = request.getProtocol();
			if(protocol!=null) {
				protocol = protocol.substring(protocol.indexOf(':')+1, protocol.indexOf('/')).toLowerCase();
			}else {
				protocol = "http:";
			}
			return protocol;
		}
		
		
		
		
}
