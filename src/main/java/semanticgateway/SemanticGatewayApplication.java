package semanticgateway;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import helio.components.engine.EngineImp;
import helio.plugins.PluginDiscovery;
import helio.writer.components.Engine;
import helio.writer.components.translator.LowerMappingTranslator;
import helio.mappings.translators.AutomaticTranslator;
import helio.framework.MappingTranslator;
import helio.framework.exceptions.MalformedMappingException;
import helio.framework.mapping.Mapping;
import helio.framework.writing.mapping.LowerMapping;



@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class ) // This prevents Spring to create default error-handling route /error 
@EnableAsync
@EnableAutoConfiguration
public class SemanticGatewayApplication {


	private static Logger log = Logger.getLogger(SemanticGatewayApplication.class.getName());
	
	public static EngineImp engine;
	public static Mapping mapping = new Mapping();
	public static Integer httpPort = 8080;
	private static final String MAPPING_DIRECTORY_ARGUMENT = "--server.mappings=";
	private static final String PLUGGINS_DIRECTORY_ARGUMENT = "--server.plugins=";
	private static final String PORT_ARGUMENT = "--server.port=";
	
	// --
	public static Engine writtingEngine;
	public static LowerMapping writtinMappings = new LowerMapping();
	// --
	
	public static void main(String[] args) {
		// main
		String mappingsDirectory = null;
		for(String arg:args) {
			if(arg.startsWith(MAPPING_DIRECTORY_ARGUMENT))
				mappingsDirectory = arg.replace(MAPPING_DIRECTORY_ARGUMENT, "").trim();
			if(arg.startsWith(PLUGGINS_DIRECTORY_ARGUMENT)) {
				String pluginsDirectory = arg.replace(PLUGGINS_DIRECTORY_ARGUMENT, "").trim();
				System.out.println(">>>>>>>>>>>>>>Executing pluging discovery: "+pluginsDirectory);
				PluginDiscovery.setPluginsDirectory(pluginsDirectory);
			}
			
			if(arg.startsWith(PORT_ARGUMENT))
				httpPort = Integer.valueOf(arg.replace(PORT_ARGUMENT, "").trim());
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
	        executor.setThreadNamePrefix("Helio-");
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
			MappingTranslator translator = new AutomaticTranslator();
			for (File file : listOfFiles) {
				if(file.isFile() && !file.getName().startsWith(".")) {
					includeMapping(translator, file.getAbsolutePath());
					
				}
			}
		} else {
			log.severe("No files found in directory " + mappingsDirectory);
			System.exit(1);
		}
	}



	/**
	 * This method initializes a {@link Mapping} object using a file, then includes the mapping in the engine
	 * @param translator A mapping translator
	 * @param file A file containing the mapping
	 */
	private static void includeMapping(MappingTranslator translator, String file) {
		log.info("Reading mapping file: " + file);
		LowerMappingTranslator writtingTranslator = new LowerMappingTranslator();
		try {
			String jsonContent = new String(Files.readAllBytes(Paths.get(file)));
			System.out.println("reading:"+file);
			if(writtingTranslator.isCompatible(jsonContent)) {
				
				System.out.println("compatible [WRITTING]:"+file);
				LowerMapping lowerMappingTmp = writtingTranslator.translate(jsonContent);
				SemanticGatewayApplication.writtinMappings.getLoweringSpecifications().addAll(lowerMappingTmp.getLoweringSpecifications());
				if (writtingEngine !=null && writtingEngine.getMapping() != null) {
					writtingEngine.addMapping(lowerMappingTmp);
				} else {
					writtingEngine = new Engine(SemanticGatewayApplication.writtinMappings);
				}
						
								
			}else if(translator.isCompatible(jsonContent)) {
				System.out.println("compatible [READING]:"+file);
				Mapping mapping = translator.translate(jsonContent);
				SemanticGatewayApplication.mapping.addMapping(mapping);
				if (engine !=null && engine.getMapping() != null) {
					engine.getMapping().addMapping(mapping);
				} else {
					engine = new EngineImp(mapping);
				}
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
		log.info("Helio ready");
		log.info("\t-version: "+SemanticGatewayApplication.engine.getVersion());
		System.out.println(">>>>>>>>>>>>>>Executing pluging discovery: "+PluginDiscovery.getPluginsDirectory());
	}
	
	@PreDestroy
	public void closeEngine() {
		SemanticGatewayApplication.engine.close();
	}

	
}
