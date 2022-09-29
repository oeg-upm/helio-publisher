package semanticgateway.exceptions;

public class NoMappingFoundException extends Exception{


	private static final long serialVersionUID = 4053005617547795026L;

	public NoMappingFoundException() {
		super("No mapping was found for Helio Gateway to be used");
	}
}
