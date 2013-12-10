package kuleuven.group2.compile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import kuleuven.group2.classloader.StoreClassLoader;
import kuleuven.group2.store.DirectoryStore;
import kuleuven.group2.store.MemoryStore;
import kuleuven.group2.store.Store;
import kuleuven.group2.util.FileUtils;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class EclipseCompilerTest {

	protected static Path testFolder;
	protected Store sourceStore;
	protected Store binaryStore;
	protected EclipseCompiler compiler;
	protected ClassLoader classLoader;
	
	@BeforeClass
	public static void setUpBeforeClass() throws IOException {
		testFolder = Files.createTempDirectory(EclipseCompilerTest.class.getSimpleName());
	}

	@Before
	public void setup() throws IOException {
		sourceStore = new MemoryStore();
		binaryStore = new MemoryStore();
		classLoader = new StoreClassLoader(binaryStore);
		compiler = new EclipseCompiler(sourceStore, binaryStore, classLoader);
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		FileUtils.deleteRecursively(testFolder, true);
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

		CompilationResult result = compiler.compileAll();
		assertTrue(result.isSuccess());
		assertEquals(1, result.getCompiledClasses().size());
		assertNotNull(result.getCompiledClass(className));

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

		CompilationResult result = compiler.compileAll();
		assertTrue(result.isSuccess());
		assertEquals(2, result.getCompiledClasses().size());

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

		CompilationResult result = compiler.compileAll();
		assertTrue(result.isSuccess());
		assertEquals(2, result.getCompiledClasses().size());

		assertEquals(Boolean.TRUE, invokeMethod("A", "foo"));
	}

	@Test
	public void compileTestAnnotation() throws Exception {
		//@formatter:off
		String source =
				"import org.junit.Test;" + 
				"public class A {\n" +
						"@Test public boolean foo() { return true; }\n" +
				"}";
		//@formatter:on
		sourceStore.write(NameUtils.toSourceName("A"), source.getBytes());

		CompilationResult result = compiler.compileAll();
		assertTrue(result.isSuccess());
		assertEquals(1, result.getCompiledClasses().size());

		assertEquals(Boolean.TRUE, invokeMethod("A", "foo"));
	}

	@Test
	public void compilationsAddedToBinaryStore() {
		String className = "A";
		//@formatter:off
		String source =
				"public class A {\n" +
						"public boolean foo() { return true; }\n" +
				"}";
		//@formatter:on
		sourceStore.write(NameUtils.toSourceName(className), source.getBytes());

		compiler.compileAll();
		
		assertTrue(binaryStore.contains(NameUtils.toBinaryName(className)));
	}
	
	@Test
	public void testCompilePackage() throws IllegalArgumentException, IOException {
		Files.createDirectory(Paths.get(testFolder + "\\src"));
		Files.createDirectory(Paths.get(testFolder + "\\bin"));
		
		sourceStore = new DirectoryStore(testFolder + "\\src");
		binaryStore = new DirectoryStore(testFolder + "\\bin");
		classLoader = new StoreClassLoader(binaryStore);
		compiler = new EclipseCompiler(sourceStore, binaryStore, classLoader);
		
		String className = "A";
		//@formatter:off
		String source =
				"package sub;\n" + 
				"public class A {\n" +
						"public boolean foo() { return true; }\n" +
				"}";
		//@formatter:on
		sourceStore.write(testFolder + "\\src\\sub\\" + NameUtils.toSourceName(className), source.getBytes());

		compiler.compileAll();
		
		//System.out.println(sourceStore.getFiltered(StoreFilter.SOURCE));
		//System.out.println(binaryStore.getAll());
		
		assertTrue(binaryStore.contains("sub/" + NameUtils.toBinaryName(className)));
		
	}
	
	@Test
	public void testLoadClassSyntaxError() {
		String className = "ATest";
		String source =
				"import org.junit.Test; \n" + 
						"public class " + className + " {\n" +
						"public void foo() { int i = 0; }\n";
						//"}";
		
		sourceStore.write(NameUtils.toSourceName(className), source.getBytes());
		
		CompilationResult compilationResult = compiler.compileAll();
		
		assertTrue(compilationResult.getErrors().size() > 0);
	}
	
	protected Object invokeMethod(String className, String methodName) throws ReflectiveOperationException {
		Class<?> loadedClass = classLoader.loadClass(className);
		Method method = loadedClass.getMethod(methodName);
		Object instance = loadedClass.getConstructor().newInstance();
		return method.invoke(instance);
	}

}
