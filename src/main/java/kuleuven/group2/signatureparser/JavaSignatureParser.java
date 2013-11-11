package kuleuven.group2.signatureparser;

import java.util.List;



public class JavaSignatureParser {
	
	protected String signature;
	
	public JavaSignatureParser(String signature) {
		this.signature = signature;
	}

	// TODO: uncomment this and implement correctly once we decided on a Method class we will pass through our project
	/*public Method parseSignature() {
	    String name = parseMethodName();
	    String packageName = parsePackageName();
	    List<String> arguments = parseArguments();
	    String returnType = parseReturnType();
	    
	    Method method = new Method(name, packageName, arguments, returnType);
	    
		return method;
	}*/
	
	protected String parsedPackageName = null;
	
	public String parsePackageName() {
		if (parsedPackageName == null) 
			parsedPackageName =  new JavaSignaturePackageNameParser(signature)
				.parseComponent();
		return parsedPackageName;
	}
	
	protected String parsedMethodName = null;
	
	public String parseMethodName() {
		if (parsedMethodName == null) 
			parsedMethodName =  new JavaSignatureMethodNameParser(signature, parsePackageName())
				.parseComponent();
		return parsedMethodName;
	}
	
	protected String parsedReturnType = null;
	
	public String parseReturnType() {
		if (parsedReturnType == null) 
			parsedReturnType =  new JavaSignatureReturnTypeParser(signature)
				.parseComponent();
		return parsedReturnType;
	}
	
	protected List<String> parsedArguments = null;
	
	public List<String> parseArguments() {
		if (parsedArguments == null) 
			parsedArguments = new JavaSignatureArgumentParser(signature)
				.parseComponent();
		return parsedArguments;
	}
	
}
