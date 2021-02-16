package semanticgateway;

import java.util.logging.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;



@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class ) // This prevents Spring to create default error-handling route /error 
@EnableAutoConfiguration
public class SemanticGatewayApplication {

	
	private static final String MAPPING_DIRECTORY_ARGUMENT = "--mappings=";
	private static final String CONFIG_DIRECTORY_ARGUMENT = "--config=";
	private static final String PORT_ARGUMENT = "--server.port=";
	public static final String VIEWS_DIRECTORY = "file:./views/";
	// --
	public static Integer httpPort = 8080;
	public static String mappingsDirectory = null;
	public static String configDirectory = null;
	//--
	private static Logger log = Logger.getLogger(SemanticGatewayApplication.class.getName());
	// --
	
	public static void main(String[] args) {

		for(String arg:args) {
			if(arg.startsWith(MAPPING_DIRECTORY_ARGUMENT)) // start mappings
				SemanticGatewayApplication.mappingsDirectory = arg.replace(MAPPING_DIRECTORY_ARGUMENT, "").trim();
			
			if(arg.startsWith(PORT_ARGUMENT)) // start port
				SemanticGatewayApplication.httpPort = Integer.valueOf(arg.replace(PORT_ARGUMENT, "").trim());
			
			if(arg.startsWith(CONFIG_DIRECTORY_ARGUMENT)) // start config
				SemanticGatewayApplication.configDirectory = arg.replace(CONFIG_DIRECTORY_ARGUMENT, "").trim();
		}
		if(SemanticGatewayApplication.mappingsDirectory ==null) {
			log.warning("No mappings directory was specifyed");
			
		}
		
		SpringApplication.run(SemanticGatewayApplication.class, args);
	}
	
	
	
	



	
}
