package semanticgateway.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.jena.riot.Lang;
import org.springframework.stereotype.Controller;

import framework.components.engine.sparql.SparqlResultsFormat;

/**
 * This class is meant to be extended by controllers that need to expose RDF. This class contains methods to ease the change of formats based on HTTP 'Accept' headers
 * @author Andrea Cimmino
 *
 */
@Controller
public class ResponseRDFController extends AbstractController {

	// INFO: Commented formats are not included in the produces header of the
	// ResourceController nor in the DatasetController

	protected static Map<String, Lang> rdfResponseFormats;

	// -- Methods to identify format of output resources

	/**
	 * This method extracts from the request headers the right
	 * {@link SparqlResultsFormat} to format the query results
	 * 
	 * @param headers
	 *            A set of headers
	 * @return A {@link SparqlResultsFormat} object
	 */
	protected Lang extractResponseAnswerFormat(Map<String, String> headers) {
		String format = "text/turtle";
		if (headers != null && !headers.isEmpty()) {
			if (headers.containsKey("accept"))
				format = headers.get("accept");
			if (headers.containsKey("Accept"))
				format = headers.get("Accept");
		}
		Lang formatSpecified = null;
		if(format.contains(",")) {
			String [] formats = format.split(",");
			for(int index=0; index < formats.length; index++) {
				formatSpecified = rdfResponseFormats.get(formats[index]);
				if(formatSpecified!=null) {
					break;
				}
			}
		}else {
			formatSpecified = rdfResponseFormats.get(format);
		}
		if (formatSpecified == null)
			formatSpecified = Lang.N3;
		
		return formatSpecified;
	}

	static {
		rdfResponseFormats = new HashMap<>();
		rdfResponseFormats.put("text/rdf+n3", Lang.N3);
		rdfResponseFormats.put("text/n3", Lang.N3);
		rdfResponseFormats.put("text/ntriples", Lang.NTRIPLES);
		rdfResponseFormats.put("text/rdf+ttl", Lang.TTL);
		rdfResponseFormats.put("text/rdf+nt", Lang.NT);
		rdfResponseFormats.put("text/plain", Lang.TURTLE);
		rdfResponseFormats.put("text/rdf+turtle", Lang.TURTLE);
		rdfResponseFormats.put("text/turtle", Lang.TURTLE);
		rdfResponseFormats.put("application/turtle", Lang.TURTLE);
		rdfResponseFormats.put("application/x-turtle", Lang.TURTLE);
		rdfResponseFormats.put("application/x-nice-turtle", Lang.TURTLE);
		rdfResponseFormats.put("application/json", Lang.JSONLD);
		rdfResponseFormats.put("application/odata+json", Lang.JSONLD);
		rdfResponseFormats.put("application/ld+json", Lang.JSONLD);
		rdfResponseFormats.put("application/x-trig", Lang.TRIG);
		rdfResponseFormats.put("application/rdf+xml", Lang.RDFXML);

	
		//rdfResponseFormats.put("text/html", Lang.RDFXML);// TODO:
		// TODO: rdfResponseFormats.put("text/md+html", Lang.HTML ); // TODO:
		// TODO: rdfResponseFormats.put("text/microdata+html", Lang.HTML ); // TODO:
		// TODO: rdfResponseFormats.put("text/x-html+ul", Lang.HTML ); // TODO:
		// TODO: rdfResponseFormats.put("text/x-html+tr", Lang.HTML ); // TODO:
		//rdfResponseFormats.put("application/xhtml+xml", Lang.RDFXML); // TODO:
		// TODO: rdfResponseFormats.put("application/microdata+json", Lang. );
		// TODO: rdfResponseFormats.put("text/cxml", Lang. );
		// TODO: rdfResponseFormats.put("text/cxml+qrcode", Lang. );
		// TODO: rdfResponseFormats.put("application/atom+xml", Lang. );

	}

}
