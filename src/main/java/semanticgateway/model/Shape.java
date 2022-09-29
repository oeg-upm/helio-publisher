package semanticgateway.model;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;

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
		return name+" "+format;
	}




}
