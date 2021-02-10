package semanticgateway.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.json.JSONObject;


@Entity
@Table(name = "dynamic_views")
public class DynamicView implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@NotEmpty
	private String resource;
	@NotNull
	private Boolean isRegex;
	@NotEmpty
	private String template;	
	
	@Column(columnDefinition="TEXT")
	private String sparqlQuery;
	
	public DynamicView() {
		isRegex = false;
	}
	
	public String getResource() {
		return resource;
	}
	public void setResource(String resource) {
		this.resource = resource;
	}
	public Boolean getIsRegex() {
		return isRegex;
	}
	public void setIsRegex(Boolean isRegex) {
		this.isRegex = isRegex;
	}
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	
	

	public String getSparqlQuery() {
		return sparqlQuery;
	}

	public void setSparqlQuery(String sparqlQuery) {
		this.sparqlQuery = sparqlQuery;
	}

	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		try {
			json.put("resource", this.resource);
			json.put("is_regex", this.isRegex);
			json.put("template", this.template);
			json.put("sparql_query", this.sparqlQuery);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return json;	
	}

	@Override
	public String toString() {
		return "DynamicView [resource=" + resource + ", isRegex=" + isRegex + ", template=" + template
				+ ", sparqlQuery=" + sparqlQuery + "]";
	}
	
	
	
}
