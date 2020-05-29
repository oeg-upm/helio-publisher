package semanticgateway.exceptions;

import semanticgateway.SemanticGatewayApplication;

public class ViewsFolderMissing extends Exception{


	private static final long serialVersionUID = 1L;

	public ViewsFolderMissing() { 
		super("Folder for dynamic views is missing, currently it should exist a folder in the root directory called "+SemanticGatewayApplication.VIEWS_DIRECTORY); 
	}
}
