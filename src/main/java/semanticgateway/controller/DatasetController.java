package semanticgateway.controller;

import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import framework.objects.RDF;
import semanticgateway.SemanticGatewayApplication;
import semanticgateway.service.RDFService;


@RestController
@RequestMapping("**/dataset")
public class DatasetController extends RDFController{
	
	// -- Attributes
	@Autowired
	private RDFService rdfService;
	private Logger log = Logger.getLogger(DatasetController.class.getName());
	
	// -- GET method
	
	 @RequestMapping(method = RequestMethod.GET, produces= {"text/rdf+n3", "text/n3", "text/ntriples", "text/rdf+ttl", "text/rdf+nt", "text/plain", "text/rdf+turtle", "text/turtle", "application/turtle", "application/x-turtle", "application/x-nice-turtle", "application/json", "application/odata+json", "application/ld+json", "application/x-trig", "application/rdf+xml"})
	 @ResponseBody
	 public String getDataset(@RequestHeader Map<String, String> headers, HttpServletResponse response){
		 prepareResponse(response);
		 String dump = "";
		 try {
			 // 1. Virtualize dump
			 RDF data = rdfService.getRDF(SemanticGatewayApplication.mapping);
			 // 2. Change format
			 String lang = this.extractResponseAnswerFormat(headers).getLabel();
			 dump = data.toString(lang);
			 response.setStatus(HttpServletResponse.SC_ACCEPTED);
		 }catch(Exception e) {
			 log.severe(e.toString());
		 }
	
		 return dump;
	 }
	
	
	 
	
	 
}
