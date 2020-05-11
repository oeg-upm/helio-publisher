package semanticgateway.controller.management;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import io.jsonwebtoken.ExpiredJwtException;
import semanticgateway.controller.AbstractController;
import semanticgateway.controller.views.AbstractRDFController;
import semanticgateway.security.JwtTokenUtil;
import semanticgateway.service.HelioUserService;

@Controller
public class AbstractSecureController  extends AbstractRDFController{

	@Autowired
	protected HelioUserService userHelioService;
	@Autowired
	protected JwtTokenUtil jwtTokenUtil;
	
	// -- Ancilliary
	
			protected Boolean authenticated(HttpServletRequest request) {
				//HttpSession session = request.getSession();
				
				//final String requestTokenHeader = request.getHeader("Authorization");
				String username = null;
				String jwtToken = retrieveTokenFromCookie(request); 
				// Check for the token in the headers
				/*System.out.println(">>>>>>>>>>"+jwtToken);
				if (jwtToken==null && requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
					jwtToken = requestTokenHeader.substring(7);
					try {
						username = jwtTokenUtil.getUsernameFromToken(jwtToken);
					} catch (IllegalArgumentException e) {
						System.out.println("Unable to get JWT Token");
					} catch (ExpiredJwtException e) {
						System.out.println("JWT Token has expired");
					}
				} else*/ if(jwtToken!=null){
					try {
						username = jwtTokenUtil.getUsernameFromToken(jwtToken);
					} catch (IllegalArgumentException e) {
						System.out.println("Unable to get JWT Token");
					} catch (ExpiredJwtException e) {
						System.out.println("JWT Token has expired");
					}
				}else {
					System.out.println(request.getRequestURI());
					System.out.println("JWT Token does not begin with Bearer String");
				}
				
				return username!=null && userHelioService.existUsername(username) && jwtTokenUtil.validateToken(jwtToken,username);
				
				
			}
			
			protected String retrieveTokenFromCookie(HttpServletRequest request) {
				String jwt = null;
				 Cookie[] cookies = request.getCookies();
				    if (cookies != null) {
				       for(Cookie cookie :cookies) {
				    	   	if(cookie.getName().equals("jwt")) {
				    	   		jwt = cookie.getValue();
				    	   		break;
				    	   	}
				       }   
				    }
				    return jwt;
			}
	
	
	
}
