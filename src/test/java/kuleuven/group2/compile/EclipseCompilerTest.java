package kuleuven.group2.compile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;

import kuleuven.group2.store.MemoryStore;
import kuleuven.group2.store.Store;
import kuleuven.group2.store.StoreClassLoader;

import org.junit.Before;
import org.junit.Test;

public class EclipseCompilerTest {

	protected Store sourceStore;
	protected Store binaryStore;
	protected EclipseCompiler compiler;
	protected ClassLoader classLoader;

	@Before
	public void setup() {
		sourceStore = new MemoryStore();
		binaryStore = new MemoryStore();
		compiler = new EclipseCompiler(sourceStore, binaryStore);
		classLoader = new StoreClassLoader(binaryStore);
	}

	@Test
	public void compileSingle() throws Exception {
		String className = "A";
		//@formatter:off
		String source =
				"public class A {\n" +
						"public boolean foo() { return true; }\n" +
				"}";
		//@formatter:on
		sourceStore.write(NameUtils.toSourceName(className), source.getBytes());

		CompilationResult result = compiler.compile(classLoader);
		assertTrue(result.isSuccess());
		assertEquals(1, result.getCompiledResources().size());
		assertTrue(result.getCompiledResources().contains(NameUtils.toBinaryName(className)));

		assertEquals(Boolean.TRUE, invokeMethod(className, "foo"));
	}

	@Test
	public void compileMultiple() throws Exception {
		//@formatter:off
		String sourceA =
				"public class A {\n" +
						"public boolean foo() { return new B().bar(); }\n" +
				"}";
		String sourceB =
				"public class B {\n" +
						"public boolean bar() { return true; }\n" +
				"}";
		//@formatter:on
		sourceStore.write(NameUtils.toSourceName("A"), sourceA.getBytes());
		sourceStore.write(NameUtils.toSourceName("B"), sourceB.getBytes());

		CompilationResult result = compiler.compile(classLoader);
		assertTrue(result.isSuccess());
		assertEquals(2, result.getCompiledResources().size());

		assertEquals(Boolean.TRUE, invokeMethod("A", "foo"));
	}

	@Test
	public void compileInner() throws Exception {
		//@formatter:off
		String source =
				"public class A {\n" +
						"public boolean foo() { return new B().bar(); }\n" +
						"public class B {\n" +
							"public boolean bar() { return true; }\n" +
						"}\n" +
				"}";
		//@formatter:on
		sourceStore.write(NameUtils.toSourceName("A"), source.getBytes());

		CompilationResult result = compiler.compile(classLoader);
		assertTrue(result.isSuccess());
		assertEquals(2, result.getCompiledResources().size());

		assertEquals(Boolean.TRUE, invokeMethod("A", "foo"));
	}

	protected Object invokeMethod(String className, String methodName) throws ReflectiveOperationException {
		Class<?> loadedClass = classLoader.loadClass(className);
		Method method = loadedClass.getMethod(methodName);
		Object instance = loadedClass.getConstructor().newInstance();
		return method.invoke(instance);
	}

}
