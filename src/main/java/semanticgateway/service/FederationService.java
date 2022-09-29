package semanticgateway.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.rdf.model.AnonId;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.eclipse.rdf4j.federated.FedXConfig;
import org.eclipse.rdf4j.federated.FedXFactory;
import org.eclipse.rdf4j.federated.repository.FedXRepository;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.query.parser.ParsedBooleanQuery;
import org.eclipse.rdf4j.query.parser.ParsedGraphQuery;
import org.eclipse.rdf4j.query.parser.ParsedOperation;
import org.eclipse.rdf4j.query.parser.ParsedTupleQuery;
import org.eclipse.rdf4j.query.parser.QueryParserUtil;
import org.eclipse.rdf4j.query.resultio.sparqljson.SPARQLResultsJSONWriter;
import org.eclipse.rdf4j.query.resultio.sparqlxml.SPARQLResultsXMLWriter;
import org.eclipse.rdf4j.query.resultio.text.csv.SPARQLResultsCSVWriter;
import org.eclipse.rdf4j.query.resultio.text.tsv.SPARQLResultsTSVWriter;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.util.Repositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import helio.framework.objects.SparqlResultsFormat;
import helio.materialiser.configuration.HelioConfiguration;
import semanticgateway.model.FederationEndpoint;
import semanticgateway.repository.FederationEndpointRepository;

@Service
public class FederationService {

	@Autowired
	public FederationEndpointRepository federationEndpointrepository;

	private Logger logger = Logger.getLogger(FederationService.class.getName());



	public FederationService() {
		super();
	}


	public String solveQuery(String sparqlQuery, SparqlResultsFormat format) {
		List<String> endpoints =  getEndpoints();
		FedXConfig config = new FedXConfig().withIncludeInferredDefault(true);
		FedXRepository repository = FedXFactory.newFederation().withSparqlEndpoints(endpoints).withConfig(config).create();



		String queryResults = null;
		ParsedOperation operation = QueryParserUtil.parseOperation(QueryLanguage.SPARQL, sparqlQuery, null);

		if (operation instanceof ParsedTupleQuery || operation instanceof ParsedBooleanQuery) {
			queryResults =  solveTupleQuery(repository, sparqlQuery, format);
		} else if (operation instanceof ParsedGraphQuery) {
			Model model =  solveGraphQuery(repository, sparqlQuery);
			Writer writer = new StringWriter();
			try {
				model.write(writer,format.getFormat(), HelioConfiguration.DEFAULT_BASE_URI);
				queryResults = writer.toString();
			}finally {
				try {
					writer.close();
				}catch(Exception e) {
					logger.severe(e.toString());
				}
			}

		} else {
			logger.warning("Query is not valid or is unsupported, currently supported queries: Select, Ask, Construct, and Describe");
		}
		return queryResults;
	}


	private String formatASKResult(Boolean partialQueryResult, SparqlResultsFormat format) {
		StringBuilder builder = new StringBuilder();
		if(format.equals(SparqlResultsFormat.JSON)) {
			builder.append("{ \"head\" : { \"link\": [] } ,  \"boolean\" : ").append(partialQueryResult).append(" }");
		}else if(format.equals(SparqlResultsFormat.CSV) || format.equals(SparqlResultsFormat.TSV)) {
			builder.append("\"bool\"\n").append(partialQueryResult);
		}else if(format.equals(SparqlResultsFormat.XML)) {
			builder.append("<?xml version=\"1.0\"?>\n<sparql xmlns=\"http://www.w3.org/2005/sparql-results#\">\n\t<head></head>\n\t<boolean>").append(partialQueryResult).append("</boolean>\n</sparql>");
		}

		return builder.toString();
	}



	private Model solveGraphQuery(Repository repository, String query) {
		Model modelJena = ModelFactory.createDefaultModel();
		try {
			org.eclipse.rdf4j.model.Model modelRDF4J = Repositories.graphQueryNoTransaction(repository, query, r ->  QueryResults.asModel(r));
			modelRDF4J.stream().forEach( st -> transformStatement(st, modelJena));
		} catch (Exception e) {
			logger.severe(e.toString());
		}
		return modelJena;
	}

	private void transformStatement(Statement st, Model model) {
		Resource subject = st.getSubject();
		Resource predicate = st.getPredicate();
		Value object = st.getObject();
		org.apache.jena.rdf.model.Resource subjectJena = ResourceFactory.createResource(subject.stringValue());
		if(subject instanceof BNode)
			subjectJena = model.createResource(AnonId.create(subject.stringValue()));
		if(object instanceof Literal) {
			Literal objectLiteral = (Literal) object;
			RDFNode node = ResourceFactory.createPlainLiteral(objectLiteral.stringValue());
			Optional<String> language = objectLiteral.getLanguage();
			if(language.isPresent()) {
				node = ResourceFactory.createLangLiteral(objectLiteral.stringValue(), language.get());
			}else if(objectLiteral.getDatatype()!=null) {
				TypeMapper mapper = new TypeMapper();
				String dataTypeString = objectLiteral.getDatatype().stringValue();
				RDFDatatype rdfDataTypeJena = mapper.getSafeTypeByName(dataTypeString);
				node = ResourceFactory.createTypedLiteral(objectLiteral.stringValue(), rdfDataTypeJena);

			}
			model.add(subjectJena, ResourceFactory.createProperty(predicate.stringValue()), node);

		}else if(object instanceof IRI) {
			IRI objectIRI = (IRI) object;
			model.add(subjectJena,ResourceFactory.createProperty(predicate.stringValue()), ResourceFactory.createResource(objectIRI.stringValue()));
		}else if(object instanceof BNode) {
			String blankObject = ((BNode) object).getID();
			org.apache.jena.rdf.model.Resource createdObject = model.createResource(AnonId.create(blankObject));

			model.add(subjectJena, ResourceFactory.createProperty(predicate.stringValue()),createdObject);


		}
	}

	private String solveTupleQuery(Repository repository, String query, SparqlResultsFormat format) {
		String queryResult = null;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			ParsedOperation operation = QueryParserUtil.parseOperation(QueryLanguage.SPARQL, query, null);
			if(operation instanceof ParsedBooleanQuery){
				Boolean partialQueryResult = repository.getConnection().prepareBooleanQuery(query).evaluate();
				queryResult = formatASKResult(partialQueryResult, format);
			}else {
				if(format.equals(SparqlResultsFormat.CSV)) {
					Repositories.tupleQuery(repository, query, new SPARQLResultsCSVWriter(outputStream));
				}else if (format.equals(SparqlResultsFormat.JSON)) {
					Repositories.tupleQuery(repository, query, new SPARQLResultsJSONWriter(outputStream));
				}else if (format.equals(SparqlResultsFormat.XML)) {
					Repositories.tupleQuery(repository, query, new SPARQLResultsXMLWriter(outputStream));
				}else if (format.equals(SparqlResultsFormat.TSV)) {
					Repositories.tupleQuery(repository, query, new SPARQLResultsTSVWriter(outputStream));
				} else {
					// throw exception
				}
				queryResult = outputStream.toString( "UTF-8" );
			}
		} catch (IOException e) {
			logger.severe(e.toString());
		}finally {
			try {
				outputStream.close();
			} catch (IOException e) {
				logger.severe(e.toString());
			}
		}

		return queryResult;
	}


	public void addEndpoint(FederationEndpoint endpoint) {
		federationEndpointrepository.save(endpoint);
	}

	public void revemoEndpoint(FederationEndpoint endpoint) {
		federationEndpointrepository.delete(endpoint);
	}

	public List<String> getEndpoints() {
		return federationEndpointrepository.getAllEndpoints().stream().map(elem -> elem.toString()).collect(Collectors.toList());
	}
}
