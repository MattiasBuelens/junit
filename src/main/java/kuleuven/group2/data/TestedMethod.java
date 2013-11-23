package kuleuven.group2.data;

import java.util.Date;

import kuleuven.group2.data.hash.MethodHash;
import kuleuven.group2.data.signature.JavaSignature;
import kuleuven.group2.data.signature.JavaSignatureParser;

/**
 * A class that represents a method that is used by at least one test. It also
 * keeps the time of its latest change.
 * 
 * @author Group2
 * @version 7 November 2013
 */
public class TestedMethod {

	protected final JavaSignature signature;
	protected MethodHash hash;
	protected Date lastChanged;

	public TestedMethod(String signature) {
		this(new JavaSignatureParser(signature).parseSignature());
	}

	public TestedMethod(JavaSignature signature) {
		this.signature = signature;
	}

	public JavaSignature getSignature() {
		return signature;
	}

	public MethodHash getMethodHash() {
		return hash;
	}

	public void setMethodHash(MethodHash hash) {
		this.hash = hash;
	}

	public Date getLastChange() {
		return lastChanged;
	}

	public void setLastChange(Date time) {
		this.lastChanged = time;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((signature == null) ? 0 : signature.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		TestedMethod other = (TestedMethod) obj;
		if (signature == null) {
			if (other.signature != null) return false;
		} else if (!signature.equals(other.signature)) return false;
		return true;
	}

	@Override
	public String toString() {
		return "TestedMethod [signature=" + signature + ", hash=" + hash + ", lastChanged=" + lastChanged + "]";
	}

}
