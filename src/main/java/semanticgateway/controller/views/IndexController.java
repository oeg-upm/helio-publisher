package semanticgateway.controller.views;

import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class IndexController extends AbstractRDFController{
	
	@RequestMapping(method = RequestMethod.GET, value="/", produces = {"text/html", "application/xhtml+xml", "application/xml"})
	public String indexRoute(@RequestHeader Map<String, String> headers, HttpServletResponse response, Model model) {
		return "redirect:sparql";
	}
		
}

