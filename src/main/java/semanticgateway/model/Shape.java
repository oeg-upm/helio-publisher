package semanticgateway.model;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;

import helio.framework.objects.RDF;

public class Shape implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotEmpty
	private String name;
	@NotEmpty
	private String format;
	@NotEmpty
	private String content;
	
	public Shape() {
		
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}


	@Override
	public String toString() {
		RDF rdf = new RDF();
		System.out.println(">"+content);
		System.out.println(">"+format);
		rdf.parseRDF(content, format);
		return rdf.toString(format);
	}
	
	
	public String toString(String serialisation) {
		RDF rdf = new RDF();
		rdf.parseRDF(content, format);
		return rdf.toString(serialisation);
	}
	
	
}
