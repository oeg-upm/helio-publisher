package semanticgateway.controller;

import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import framework.components.engine.EngineImp;
import framework.objects.RDF;
import semanticgateway.SemanticGatewayApplication;
import semanticgateway.service.VirtualizationService;


@RestController
@RequestMapping("/**")
public class ResourceController extends AbstractController{

	public static Logger log = Logger.getLogger(ResourceController.class.getName());
	@Autowired
	public VirtualizationService virtualizationService;
	 // -- Resources
	 
		@RequestMapping(method = RequestMethod.GET, headers = {"Accept=text/turtle","Accept=application/rdf+xml","Accept=application/n-triples","Accept=application/ld+json","Accept=application/owl+xml","Accept=text/trig","Accept=application/n-quads","Accept=application/trix+xml","Accept=application/rdf+thrift"}) // , headers = {"Accept=application/*", "Accept=text/*"}
		@ResponseBody
		public String getResource(@RequestHeader Map<String, String> headers, final HttpServletRequest request, HttpServletResponse response) {
			String resourceData = null;
			prepareResponse(response);
			// 1. Adapt request IRI
			StringBuilder iri = buildIRIToRetrieve(request);
			
			try {
				// 2. Virtualize resource data
				if(SemanticGatewayApplication.mapping!=null) {
					 CompletableFuture<RDF> futureRdfData = virtualizationService.findResource(iri.toString());
					 resourceData = futureRdfData.get().toString(); // by default JSON-LD
					 response.setStatus(HttpServletResponse.SC_ACCEPTED);
				}else {
					response.setStatus(HttpServletResponse.SC_CONFLICT);
					log.warning("No mapping was found for the semantic engine to be used");
				}
			}catch(Exception e) {
				e.printStackTrace();
				log.severe(e.toString());
			}
			
			return resourceData;
		}
		
		
		
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
