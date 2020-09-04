package semanticgateway.service;

import org.apache.jena.rdf.model.Model;
import org.springframework.stereotype.Service;
import helio.framework.exceptions.ResourceNotFoundException;
import helio.framework.materialiser.mappings.HelioMaterialiserMapping;
import helio.framework.objects.SparqlResultsFormat;
import helio.materialiser.HelioMaterialiser;
import helio.materialiser.configuration.HelioConfiguration;

@Service
public class MaterialiserService {


	private HelioMaterialiser materialiser;
	
	public MaterialiserService() {
		super();
	}
	
	public void addMappings(HelioMaterialiserMapping mappings) {
		if(materialiser==null) {
			materialiser = new HelioMaterialiser(mappings);
		}else {
			// TODO: ADD MORE MAPINGS ON THE FLY
		}
	}
	
	public void configureMaterialiser(String configurationFile) {
		HelioConfiguration.readConfigurationFile(configurationFile);
	}
	
	public void close() {
		this.materialiser.close();
	}
	
	public Model getRDF() {
		materialiser.updateSynchronousSources();
		return materialiser.getRDF();
	}
	
	public Model getResource(String iri) throws ResourceNotFoundException {
		materialiser.updateSynchronousSources();
		return materialiser.getResource(iri);
	}
	
	public String solveQueryStream(String sparqlQuery, SparqlResultsFormat format) {
		materialiser.updateSynchronousSources();
		return materialiser.query(sparqlQuery, format);
	}
	
	public String solveQuery(String sparqlQuery, SparqlResultsFormat format) {
		materialiser.updateSynchronousSources();
		return materialiser.query(sparqlQuery, format);
	}
	
	
}
