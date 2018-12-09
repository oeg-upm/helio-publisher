package semanticgateway.controller;

import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import framework.objects.RDF;
import semanticgateway.SemanticGatewayApplication;


@RestController
@RequestMapping("**/dataset")
public class DatasetController extends AbstractController{

	public static Logger log = Logger.getLogger(DatasetController.class.getName());
	
	 @RequestMapping(method = RequestMethod.GET, headers = {"Accept=text/turtle","Accept=application/rdf+xml","Accept=application/n-triples","Accept=application/ld+json","Accept=application/owl+xml","Accept=text/trig","Accept=application/n-quads","Accept=application/trix+xml","Accept=application/rdf+thrift"})
	 @ResponseBody
	 public String getDataset(@RequestHeader Map<String, String> headers, HttpServletResponse response){
		 prepareResponse(response);
		 String dump = "";
		
		 try {
			 // 1. Virtualize dump
			 if(SemanticGatewayApplication.engine.getMapping() !=null) {
				 RDF data = SemanticGatewayApplication.engine.publishRDF();
				 // TODO: make user select the format of this rdf, use the headers for this
				 dump = data.toString();
				 response.setStatus(HttpServletResponse.SC_ACCEPTED);
			 }else {
				response.setStatus(HttpServletResponse.SC_CONFLICT);
				log.warning("No mapping was found for the semantic engine to be used");
			}
		 }catch(Exception e) {
			 log.severe(e.toString());
		 }
		 
		 System.out.println(headers);
		 return dump;
	 }
	
	 private String changeFormat(RDF data, String format) {
		 return "";
	 }
	 
	 /* TODO: get the dump zipped
	 @RequestMapping(value="/dump",method = RequestMethod.GET)
	 @ResponseBody
	 public String getQuery(@RequestParam(value = "zipped") Boolean zipped){
		 return "RDF dump, zipped: "+zipped;
	 }*/
	 
}
