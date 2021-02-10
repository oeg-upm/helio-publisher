package semanticgateway.controller.sparql;

import java.util.HashMap;
import java.util.Map;

import helio.framework.objects.SparqlResultsFormat;
import semanticgateway.controller.views.AbstractRDFController;

public class AbstractSPARQLController extends AbstractRDFController {

	
	protected static Map<String,SparqlResultsFormat> sparqlResponseFormats;

	
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
