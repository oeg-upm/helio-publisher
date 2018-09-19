package semanticgateway.controller;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import semanticgateway.SemanticGatewayApplication;

@RestController
@RequestMapping("/sparql")
public class QueryController extends AbstractController {

	public static Logger log = Logger.getLogger(QueryController.class.getName());

	// http://localhost:8080/sparql?query=select+distinct+*+where+%7B%3Fs+%3Fp+%3Fo+.%7D+LIMIT+100
	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public String answerQuery(@RequestParam(value = "query") String query, HttpServletResponse response) {
		JSONArray queryResults = new JSONArray();
		prepareResponse(response);

		try {
			// Check that the engine has a mapping
			if (SemanticGatewayApplication.engine.getMapping() != null) {
				queryResults = SemanticGatewayApplication.engine.query(query);
				response.setStatus(HttpServletResponse.SC_ACCEPTED);
			} else {
				response.setStatus(HttpServletResponse.SC_CONFLICT);
				log.warning("No mapping was found for the semantic engine to be used");
			}
		} catch (Exception e) {
			log.severe(e.getMessage());
		}

		return queryResults.toString();
	}

}
