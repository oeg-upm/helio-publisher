package semanticgateway.controller;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import framework.objects.RDF;
import semanticgateway.SemanticGatewayApplication;


@RestController
@RequestMapping("/**")
public class ResourceController extends AbstractController{

	public static Logger log = Logger.getLogger(ResourceController.class.getName());
	
	 // -- Resources
	 
		@RequestMapping(method = RequestMethod.GET, produces = "application/ld+json")
		@ResponseBody
		public String getResource(final HttpServletRequest request, HttpServletResponse response) {
			String resourceData = null;
			prepareResponse(response);
			// 1. Transform request into IRI
			StringBuilder iri = new StringBuilder(request.getRequestURL().toString());
			if (request.getQueryString() != null)
				iri.append("?").append(request.getQueryString());
			
			try {
				// 2. Virtualize resource data
				if(SemanticGatewayApplication.engine.getMapping()!=null) {
					 RDF rdfData = SemanticGatewayApplication.engine.getResource(iri.toString());
					 // TODO: maybe IRI was not in the dataset, thus return that resource was not found
					 // TODO: make user select the format of this rdf, use the headers for this
					 // TODO: this is actually returning all the instances instead of the one requested, due to cache of engine, check that
					 resourceData = rdfData.toString(); // by default JSON-LD
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
}
