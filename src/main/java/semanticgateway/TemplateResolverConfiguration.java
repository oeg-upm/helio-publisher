package semanticgateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Configuration
public class TemplateResolverConfiguration {

	 
		@Bean
	    public SpringResourceTemplateResolver auxiliarTemplateResolver() {
			SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
	        templateResolver.setPrefix(SemanticGatewayApplication.VIEWS_DIRECTORY);
	        templateResolver.setSuffix(".html");
	        templateResolver.setTemplateMode(TemplateMode.HTML);
	        templateResolver.setCharacterEncoding("UTF-8");
	        templateResolver.setOrder(0);
	        templateResolver.setCheckExistence(true);

	        return templateResolver;
	    }
}
