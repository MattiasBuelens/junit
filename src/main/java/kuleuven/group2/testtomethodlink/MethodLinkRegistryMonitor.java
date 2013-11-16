package kuleuven.group2.testtomethodlink;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kuleuven.group2.signature.JavaSignatureParser;
import be.kuleuven.cs.ossrewriter.Monitor;

public class MethodLinkRegistryMonitor extends Monitor {
	
	protected MethodLinkRegistry methodLinkRegistry;
	protected ICurrentRunningTestHolder currentRunningTestHolder;

	public MethodLinkRegistryMonitor(MethodLinkRegistry methodLinkRegistry, ICurrentRunningTestHolder currentRunningTestHolder) {
		super();
		this.methodLinkRegistry = methodLinkRegistry;
		this.currentRunningTestHolder = currentRunningTestHolder;
	}

	@Override
	public void enterMethod(String methodName) {
		Test currentRunningTest = currentRunningTestHolder.getCurrentRunningTest();
		Method enteredMethod = new JavaSignatureParser(methodName)
			.parseSignature();
		methodLinkRegistry.addLink(currentRunningTest, enteredMethod);
	};
	
}
