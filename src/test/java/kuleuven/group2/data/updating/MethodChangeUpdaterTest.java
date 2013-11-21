package kuleuven.group2.data.updating;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.Map;

import kuleuven.group2.compile.CompilationResult;
import kuleuven.group2.compile.EclipseCompiler;
import kuleuven.group2.compile.NameUtils;
import kuleuven.group2.data.TestDatabase;
import kuleuven.group2.data.hash.MethodHash;
import kuleuven.group2.data.hash.MethodHasher;
import kuleuven.group2.data.signature.JavaSignature;
import kuleuven.group2.data.signature.JavaSignatureParser;
import kuleuven.group2.store.MemoryStore;
import kuleuven.group2.store.Store;
import kuleuven.group2.store.StoreClassLoader;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MethodChangeUpdaterTest {
	
	protected TestDatabase testDatabase;
	protected MethodChangeUpdater methodChangeUpdater;
	
	private static String testClassName = "A";
	private static String testSignatureString = testClassName + ".foo()Z";
	private static JavaSignature testSignature
		= new JavaSignatureParser(testSignatureString)
		.parseSignature();

	protected Store sourceStore;
	protected Store binaryStore;
	protected EclipseCompiler compiler;
	protected ClassLoader classLoader;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	
	@Before
	public void setUp() throws Exception {
		sourceStore = new MemoryStore();
		binaryStore = new MemoryStore();
		classLoader = new StoreClassLoader(binaryStore);
		compiler = new EclipseCompiler(sourceStore, binaryStore, classLoader);
		
		testDatabase = new TestDatabase();
		methodChangeUpdater = new MethodChangeUpdater(testDatabase);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testInitialChangeMethodUpdatedInDatabase() {
		String className = "A";
		String source =
				"public class A {\n" +
						"public boolean foo() { return true; }\n" +
						"}";
		sourceStore.write(NameUtils.toSourceName(className), source.getBytes());

		CompilationResult result = compiler.compileAll();
		
		methodChangeUpdater.detectChanges(testClassName, result.getCompiledClass(testClassName), new Date(1));
		
		assertTrue(testDatabase.containsMethod(testSignature));
	}

	@Test
	public void testChangeMethodUpdatedInDatabase() {
		String className = "A";
		String source =
				"public class A {\n" +
						"public boolean foo() { return true; }\n" +
						"}";
		sourceStore.write(NameUtils.toSourceName(className), source.getBytes());
		CompilationResult result = compiler.compileAll();
		methodChangeUpdater.detectChanges(testClassName, result.getCompiledClass(testClassName), new Date(1));

		String source2 =
				"public class A {\n" +
						"public boolean foo() { return false; }\n" +
						"}";
		sourceStore.write(NameUtils.toSourceName(className), source2.getBytes());
		result = compiler.compileAll();
		methodChangeUpdater.detectChanges(testClassName, result.getCompiledClass(testClassName), new Date(2));
		
		MethodHasher methodHasher = new MethodHasher(result.getCompiledClass(testClassName));
		
		assertTrue(testDatabase.containsMethod(testSignature));
		assertEquals(new Date(2), testDatabase.getMethod(testSignature).getLastChange());
		assertEquals(methodHasher.getHashes().get("foo()Z"), testDatabase.getMethod(testSignature).getMethodHash());
	}

}
