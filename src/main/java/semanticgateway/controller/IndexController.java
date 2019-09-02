package semanticgateway.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController extends AbstractRDFController{
	@Autowired
	public ResourceLoader resourceLoader;
	
	@RequestMapping(method = RequestMethod.GET, value="/", produces = {"text/html", "application/xhtml+xml", "application/xml"})
	public String indexRoute(@RequestHeader Map<String, String> headers, HttpServletResponse response, Model model) {
		return "redirect:sparql";
	}
	
	
	@RequestMapping(method = RequestMethod.GET, value="/static/**")
	@ResponseBody
	public byte[] indexSatic(final HttpServletRequest request) throws IOException {
		byte[] result = null;
		try {
			String place = request.getServletPath().replace("/static/", "/templates/");
			Resource resource = resourceLoader.getResource("classpath:"+place);
			System.out.println("Requested: "+place +", exists: "+resource.getFile().exists());
			InputStream in = resource.getInputStream();
			result = IOUtils.toByteArray(in);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	
	
	
}

