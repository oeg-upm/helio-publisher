package semanticgateway.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import helio.framework.exceptions.MalformedMappingException;
import helio.framework.materialiser.MappingTranslator;
import helio.framework.materialiser.mappings.HelioMaterialiserMapping;
import helio.materialiser.mappings.AutomaticTranslator;
import semanticgateway.SemanticGatewayApplication;
import semanticgateway.controller.views.AbstractRDFController;

@Controller
public class IndexController extends AbstractRDFController{

	// --
	private static Logger log = Logger.getLogger(IndexController.class.getName());

	// -- Controller methods

	@RequestMapping(method = RequestMethod.GET, value="/", produces = {"text/html", "application/xhtml+xml", "application/xml"})
	public String indexRoute(@RequestHeader Map<String, String> headers, HttpServletResponse response, Model model) {
		return "redirect:sparql";
	}


	// -- Pre and Post construct methods

		@PostConstruct
		public void firstVirtualization() {
			if(SemanticGatewayApplication.configDirectory!=null)
				rdfService.configureMaterialiser(SemanticGatewayApplication.configDirectory);
			HelioMaterialiserMapping mappings = new HelioMaterialiserMapping();
			if(SemanticGatewayApplication.mappingsDirectory!=null)
				mappings.merge(readMappingsDirectory(SemanticGatewayApplication.mappingsDirectory));
			log.info("Initializing helio materialiser with mappings");
			rdfService.initialiseMaterialiser(mappings);
			log.info("Helio materialiser ready");

			// TODO: initialise the static views

		}

		@PreDestroy
		public void closeEngine() {
			rdfService.finilizeMaterialiser();
		}

		private HelioMaterialiserMapping readMappingsDirectory(String mappingsDirectory) {
			HelioMaterialiserMapping mappings = new HelioMaterialiserMapping();
			// 1. Read mappings & load them into the engine
			String message = "Reading mappings directory ".concat( mappingsDirectory);
			log.info(message);
			File folder = new File(mappingsDirectory);
			File[] listOfFiles = folder.listFiles();
			if (listOfFiles != null) {
				MappingTranslator translator = new AutomaticTranslator();
				for (File file : listOfFiles) {
					if(file.isFile() && !file.getName().startsWith(".")) {
						log.info("\tParsing "+file.getAbsolutePath());
						mappings.merge(translateMapping(translator, file.getAbsolutePath()));
					}
				}
			} else {
				message = "No files found in directory ".concat(mappingsDirectory);
				log.severe(message);
				System.exit(1);
			}
			return mappings;
		}


		/**
		 * This method initializes a {@link Mapping} object using a file, then includes the mapping in the engine
		 * @param translator A mapping translator
		 * @param file A file containing the mapping
		 */
		private HelioMaterialiserMapping translateMapping(MappingTranslator translator, String file) {
			HelioMaterialiserMapping mapping = new HelioMaterialiserMapping();

			try {
				String mappingContent = new String(Files.readAllBytes(Paths.get(file)));
				if(translator.isCompatible(mappingContent)) {
					mapping.merge(translator.translate(mappingContent));
				}
			} catch (IOException | MalformedMappingException e) {
				log.severe(e.toString());
				String message = "[FAILURE] NOT Compatible Helio Materialiser Mapping: "+file;
				log.info(message);
				System.exit(1);
			}

			return mapping;
		}
}

