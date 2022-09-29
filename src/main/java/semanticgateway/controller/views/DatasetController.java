package semanticgateway.controller.views;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;



@Controller
@RequestMapping("/dataset")
public class DatasetController extends AbstractRDFController{

	// -- Attributes
	private Logger log = Logger.getLogger(DatasetController.class.getName());

	// -- GET Resources

	@RequestMapping(method = RequestMethod.GET, produces = {"text/html", "application/xhtml+xml", "application/xml"})
	public String datasetGUI(@RequestHeader Map<String, String> headers, HttpServletResponse response, Model model) {
		return "dataset.html";
	}

	/* check for more information https://medium.com/swlh/streaming-data-with-spring-boot-restful-web-service-87522511c071
	 @RequestMapping(method = RequestMethod.GET, produces= {"text/rdf+n3", "text/n3", "text/ntriples", "text/rdf+ttl", "text/rdf+nt", "text/plain", "text/rdf+turtle", "text/turtle", "application/turtle", "application/x-turtle", "application/x-nice-turtle", "application/json", "application/odata+json", "application/ld+json", "application/x-trig", "application/rdf+xml"})
	 @ResponseBody
	 public ResponseEntity<StreamingResponseBody> getDataset(@RequestHeader Map<String, String> headers, HttpServletResponse response){
		 prepareResponse(response);
		 RDFFormat format = extractRDFResponseAnswerFormat(headers) ;
		 StreamingResponseBody stream = out -> {
		 try {
			Rio.write(rdfService.getRDF(), writer, format);
			response.setStatus(HttpServletResponse.SC_ACCEPTED);
		 }catch(Exception e) {
			 log.severe(e.toString());
		 }

		 };

		 return ResponseEntity.ok()
		            .header(HttpHeaders.CONTENT_TYPE, format.getDefaultMIMEType())
		            .contentType(MediaType.APPLICATION_OCTET_STREAM)
		            .body(stream);
	 }*/

	 @RequestMapping(method = RequestMethod.GET, produces = {"text/rdf+n3", "text/n3", "text/ntriples", "text/rdf+ttl", "text/rdf+nt", "text/plain", "text/rdf+turtle", "text/turtle", "application/turtle", "application/x-turtle", "application/x-nice-turtle", "application/json", "application/odata+json", "application/ld+json", "application/x-trig", "application/rdf+xml"})
	 @ResponseBody
	 public String getDataset(@RequestHeader Map<String, String> headers, HttpServletResponse response) {
		 prepareResponse(response);
		 String format = extractRDFResponseAnswerFormat(headers).getLabel() ;
		 Writer writer = new StringWriter();
		 try {
			 rdfService.getRDF().write(writer, format);
			 response.setStatus(HttpServletResponse.SC_ACCEPTED);
		 }catch(Exception e) {
			 log.severe(e.toString());
		 }

		 return writer.toString();
	 }




}
