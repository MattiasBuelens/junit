package kuleuven.group2.methodlink;

public class Test {

	protected String testClassName;
	protected String testMethodName;
	
	public Test(String testClassName, String testMethodName) {
		super();
		this.testClassName= testClassName;
		this.testMethodName= testMethodName;
	}

	public String getTestClassName() {
		return testClassName;
	}

	public String getTestMethodName() {
		return testMethodName;
	}

	@Override
	public int hashCode() {
		final int prime= 31;
		int result= 1;
		result= prime * result
				+ ((testClassName == null) ? 0 : testClassName.hashCode());
		result= prime * result
				+ ((testMethodName == null) ? 0 : testMethodName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Test other= (Test) obj;
		if (testClassName == null) {
			if (other.testClassName != null)
				return false;
		} else if (!testClassName.equals(other.testClassName))
			return false;
		if (testMethodName == null) {
			if (other.testMethodName != null)
				return false;
		} else if (!testMethodName.equals(other.testMethodName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Test [testClassName=" + testClassName + ", testMethodName="
				+ testMethodName + "]";
	}
	
}
