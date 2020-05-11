package semanticgateway.controller.management;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import helio.framework.objects.RDF;
import io.jsonwebtoken.ExpiredJwtException;
import semanticgateway.SemanticGatewayApplication;
import semanticgateway.model.DynamicView;
import semanticgateway.model.Shape;
import semanticgateway.model.User;
import semanticgateway.security.JwtTokenUtil;
import semanticgateway.service.DynamicViewService;
import semanticgateway.service.HelioUserService;
import semanticgateway.service.RDFService;

@Controller
@RequestMapping("/helio-api")
public class ManagementController extends AbstractSecureController{

	

	
	private Logger log = Logger.getLogger(ManagementController.class.getName());

	@PostConstruct
	public void createDefaultUser() {
		if(!userHelioService.existUsername("root")) {
			User admin = new User();
			admin.setUsername("root");
			admin.setPassword("root");
			userHelioService.save(admin);
		}
	}
	
	
	@RequestMapping(value="", method = RequestMethod.GET, produces = {"text/html", "application/xhtml+xml", "application/xml"})
	public String api(HttpServletRequest request) {
		return "redirect:/helio-api/login";
	}
	
	/**
	 * Login Methods
	 **/
	
	@RequestMapping(value="/login", method = RequestMethod.GET, produces = {"text/html", "application/xhtml+xml", "application/xml"})
	public String loginGui(HttpServletRequest request) {
		String template = "helio-gateway-login";
		if(authenticated(request)) {
			template = "redirect:/helio-api/dashboard";
		}
		return template;
	}
	
	@RequestMapping(value="/login", method = RequestMethod.POST, consumes="application/json", produces = "application/json")
	public @ResponseBody String login(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = true) @Valid User user) {
		prepareResponse(response);
		response.setStatus(200);
		String output = null;
		if(!authenticated(request)) {
			if(userHelioService.checkLogin(user)) {
				Cookie cookie = jwtTokenUtil.createTokenCookie(user.getUsername());
				response.addCookie(cookie);
			}else{
				response.setStatus(401);
			}
		}
		return output;
	}
	
	
	/**
	 * Dashboard Methods
	 **/

	@RequestMapping(value="/dashboard", method = RequestMethod.GET, produces = {"text/html", "application/xhtml+xml", "application/xml"})
	public String dashboardGUI(HttpServletRequest request, HttpServletResponse response) {
		String template = "helio-gateway-dashboard";
		if(!authenticated(request)) {
			template = "redirect:/helio-api/login";
		}
		return template;
	}
	
	/**
	 * REST methods users
	 */
	
	@RequestMapping(value="/account", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody String account(HttpServletRequest request, HttpServletResponse response) {
		prepareResponse(response);
		String responseJson = null;
		if(authenticated(request)) {
			String jwtToken = retrieveTokenFromCookie(request);
			String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
			StringBuilder account = new StringBuilder();
			account.append("{\"username\":\"").append(username).append("\"}");
			responseJson = account.toString();
			response.setStatus(200);
		}
		return responseJson;
	}
	
	@RequestMapping(value="/account", method = RequestMethod.POST, consumes="application/json")
	public void register(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = true) @Valid User user) {
		prepareResponse(response);
		System.out.println("updating");
		if(authenticated(request)) {
			String jwtToken = retrieveTokenFromCookie(request);
			String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
			/*if(username.equals(user.getUsername())) { // new user is the same user than the one now
				
			}*/
			System.out.println("saving: "+user.getUsername());
			userHelioService.remove(username);
			userHelioService.save(user);
			response.setStatus(201);
		}
	}
	

	
	/**
	 * Validation methods
	 */
	
	@Autowired
	private RDFService rdfService;

	@RequestMapping(value="/shapes/validate", method = RequestMethod.POST, consumes="application/json", produces = { "text/rdf+n3",
			"text/n3", "text/ntriples", "text/rdf+ttl", "text/rdf+nt", "text/plain", "text/rdf+turtle", "text/turtle",
			"application/turtle", "application/x-turtle", "application/x-nice-turtle", "application/json",
			"application/odata+json", "application/ld+json", "application/x-trig", "application/rdf+xml" })
	public @ResponseBody String validate(@RequestHeader Map<String, String> headers, HttpServletRequest request, HttpServletResponse response, @RequestBody(required = true)  Shape shape) {
		prepareResponse(response);
		String output = null;
		if(authenticated(request)) {
			try {
				RDF data = rdfService.getRDF(SemanticGatewayApplication.mapping);
				RDF report = data.validateShape(shape.toString("TURTLE"));
				String serialisation = extractResponseAnswerFormat(headers).getLabel();
				output = report.toString(serialisation);
				response.setStatus(200);
				
			}catch(Exception e) {
				log.severe(e.toString());
			}
		}
		return output;
	}
	
	

	/**
	 * Views methods
	 */
	
	@Autowired
	public DynamicViewService dynamicViewsService;
		

	@RequestMapping(value="/views", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody String viewsRead(HttpServletRequest request, HttpServletResponse response) {
		prepareResponse(response);
		String responseJson = null;
		if(authenticated(request)) {
			try {
				JSONObject views = new JSONObject();
				JSONArray arrayOfViews = new JSONArray();
				dynamicViewsService.findAll().map(DynamicView::toJSON).forEach(arrayOfViews::put);
				views.put("views", arrayOfViews);
				responseJson = views.toString();
				response.setStatus(200);
			}catch(Exception e ) {
				log.severe(e.toString());
			}
		}
		return responseJson;
	}
	
	@RequestMapping(value="/views", method = RequestMethod.POST, consumes="application/json" )
	public void viewsWrite(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = true) @Valid DynamicView view) {
		prepareResponse(response);
		
		if(authenticated(request)) {
			try {
				dynamicViewsService.remove(view);
				dynamicViewsService.save(view);
				response.setStatus(201);
			}catch(Exception e) {
				log.severe(e.toString());
			}
		}

	}
	
	@RequestMapping(value="/views", method = RequestMethod.DELETE)
	public void viewsDelete(HttpServletRequest request, HttpServletResponse response, @RequestParam(required = true) String viewId) {
		prepareResponse(response);
		
		if(authenticated(request)) {
			try {
				dynamicViewsService.removeById(viewId);
				response.setStatus(200);
			}catch(Exception e) {
				log.severe(e.toString());
			}
		}

	}
	
}
