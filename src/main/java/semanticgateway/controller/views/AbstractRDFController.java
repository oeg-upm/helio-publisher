package semanticgateway.controller.views;

import java.util.HashMap;
import java.util.Map;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import semanticgateway.controller.AbstractController;
import semanticgateway.service.SemanticDataService;


/**
 * This class is meant to be extended by controllers that need to expose RDF. This class contains methods to ease the change of formats based on HTTP 'Accept' headers
 * @author Andrea Cimmino
 *
 */
@Controller
public abstract class AbstractRDFController extends AbstractController {

	// INFO: Commented formats are not included in the produces header of the
	// ResourceController nor in the DatasetController

	protected static Map<String, Lang> rdfResponseFormats;

	@Autowired
	protected SemanticDataService rdfService;

	// -- Methods to identify format of output resources

	/**
	 * This method extracts from the request headers the right
	 * {@link RDFFormat} to format the rdf data results
	 *
	 * @param headers A set of headers
	 * @return A {@link RDFFormat} object
	 */
	protected Lang extractRDFResponseAnswerFormat(Map<String, String> headers) {
		Lang finalFormat = Lang.TTL;
		String format = "text/turtle";
		if (headers != null && !headers.isEmpty()) {
			if (headers.containsKey("accept"))
				format = headers.get("accept");
			if (headers.containsKey("Accept"))
				format = headers.get("Accept");
		}
		if(format.contains(",")) {
			String [] formats = format.split(",");
			for (String format2 : formats) {
				if(rdfResponseFormats.containsKey(format2)) {
					finalFormat = rdfResponseFormats.get(format2);
					break;
				}
			}
		}else {
			if(rdfResponseFormats.containsKey(format)) {
				finalFormat = rdfResponseFormats.get(format);
			}
		}
		return finalFormat;
	}



	static {
		rdfResponseFormats = new HashMap<>();
		rdfResponseFormats.put("text/rdf+n3", Lang.N3);
		rdfResponseFormats.put("text/n3",Lang.N3);
		rdfResponseFormats.put("text/ntriples", Lang.NTRIPLES);
		rdfResponseFormats.put("text/rdf+nt", Lang.NTRIPLES);

		rdfResponseFormats.put("text/rdf+ttl", Lang.TURTLE);
		rdfResponseFormats.put("text/plain",  Lang.TURTLE);
		rdfResponseFormats.put("text/rdf+turtle",  Lang.TURTLE);
		rdfResponseFormats.put("text/turtle",  Lang.TURTLE);
		rdfResponseFormats.put("application/turtle",  Lang.TURTLE);
		rdfResponseFormats.put("application/x-turtle",  Lang.TURTLE);
		rdfResponseFormats.put("application/x-nice-turtle",  Lang.TURTLE);
		rdfResponseFormats.put("application/json",  Lang.RDFJSON);
		rdfResponseFormats.put("application/ld+json", Lang.JSONLD);
		rdfResponseFormats.put("application/rdf+xml", Lang.RDFXML);



		//rdfResponseFormats.put("text/html", RDFFormat.RDFXML);// TODO:
		// TODO: rdfResponseFormats.put("text/md+html", RDFFormat.HTML ); // TODO:
		// TODO: rdfResponseFormats.put("text/microdata+html", RDFFormat.HTML ); // TODO:
		// TODO: rdfResponseFormats.put("text/x-html+ul", RDFFormat.HTML ); // TODO:
		// TODO: rdfResponseFormats.put("text/x-html+tr", RDFFormat.HTML ); // TODO:
		//rdfResponseFormats.put("application/xhtml+xml", RDFFormat.RDFXML); // TODO:
		// TODO: rdfResponseFormats.put("application/microdata+json", RDFFormat. );
		// TODO: rdfResponseFormats.put("text/cxml", RDFFormat. );
		// TODO: rdfResponseFormats.put("text/cxml+qrcode", RDFFormat. );
		// TODO: rdfResponseFormats.put("application/atom+xml", RDFFormat. );

	}


}
