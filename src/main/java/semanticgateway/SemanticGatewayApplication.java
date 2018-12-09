package semanticgateway;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import framework.components.engine.EngineImp;
import framework.components.exceptions.MalformedMappingException;
import framework.components.plugins.PluginDiscovery;
import framework.mapping.Mapping;
import framework.mappings.translators.JsonTranslator;



@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class ) // This prevents Spring to create default error-handling route /error 
@EnableAsync
public class SemanticGatewayApplication {


	private static Logger log = Logger.getLogger(SemanticGatewayApplication.class.getName());
	
	public static EngineImp engine;
	public static Mapping mapping = new Mapping();
	private static final String MAPPING_DIRECTORY_ARGUMENT = "--server.mappings=";
	private static final String PLUGGINS_DIRECTORY_ARGUMENT = "--server.plugins=";
	
	public static void main(String[] args) {
		// main
		String mappingsDirectory = null;
		for(String arg:args) {
			if(arg.startsWith(MAPPING_DIRECTORY_ARGUMENT))
				mappingsDirectory = arg.replace(MAPPING_DIRECTORY_ARGUMENT, "").trim();
			if(arg.startsWith(PLUGGINS_DIRECTORY_ARGUMENT)) {
				String pluginsDirectory = arg.replace(PLUGGINS_DIRECTORY_ARGUMENT, "").trim();
				PluginDiscovery.setPluginsDirectory(pluginsDirectory);
			}
		}
		if(mappingsDirectory==null) {
			log.severe("No mappings directory was specifyed");
			System.exit(1);
		}else {
			initializeEngine(mappingsDirectory); 
		}
		SpringApplication.run(SemanticGatewayApplication.class, args);
		//System.out.println("arguments");
	}
	
	 @Bean
	    public Executor asyncExecutor() {
	        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	        
	        //executor.setCorePoolSize(10);
	        executor.setMaxPoolSize(150);
	        executor.setQueueCapacity(500);
	        executor.setThreadNamePrefix("GithubLookup-");
	        executor.initialize();
	        return executor;
	    }
	
	/*
	 * Engine initialization & Mappings reading
	 */
	private static void initializeEngine(String mappingsDirectory) {
		// 1. Init engine
		log.info("Starting semantic engine");
		// 2. Read mappings & load them into the engine
		log.info("Reading maps directory " + mappingsDirectory);
		File folder = new File(mappingsDirectory);
		File[] listOfFiles = folder.listFiles();
		if (listOfFiles != null) {
			JsonTranslator translator = new JsonTranslator();
			for (File file : listOfFiles) {
				if (file.isFile() && file.getName().endsWith(".json")) {
					includeMapping(translator, file.getAbsolutePath());
				}
			}
		} else {
			log.severe("No files found in directory " + mappingsDirectory);
			//TODO: REMOVE THIS System.exit(1);
		}
	}



	/**
	 * This method initializes a {@link Mapping} object using a file, then includes the mapping in the engine
	 * @param translator A mapping translator
	 * @param file A file containing the mapping
	 */
	private static void includeMapping(JsonTranslator translator, String file) {
		log.info("Reading mapping file: " + file);
		try {
			String jsonContent = new String(Files.readAllBytes(Paths.get(file)));
			Mapping mapping = translator.translate(jsonContent);
			SemanticGatewayApplication.mapping.addMapping(mapping);
			if (engine !=null && engine.getMapping() != null) {
				engine.getMapping().addMapping(mapping);
			} else {
				engine = new EngineImp(mapping);
			}
		} catch (IOException | MalformedMappingException e) {
			log.severe(e.toString());
			System.exit(1);
		}
	}

	@PostConstruct
	public void firstVirtualization() {
		log.info("Initializing semantic gateway");
		SemanticGatewayApplication.engine.initialize();
		log.info("Semantic gateway ready");
		log.info("\t-version: "+SemanticGatewayApplication.engine.getVersion());
	}
	
	@PreDestroy
	public void closeEngine() {
		SemanticGatewayApplication.engine.close();
	}
	

	


}
