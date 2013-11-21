package kuleuven.group2;

import kuleuven.group2.compile.EclipseCompilerTest;
import kuleuven.group2.data.DatabaseTests;
import kuleuven.group2.data.TestDatabaseTest;
import kuleuven.group2.data.TestTest;
import kuleuven.group2.data.hash.MethodHasherTest;
import kuleuven.group2.data.signature.JavaSignatureParserTest;
import kuleuven.group2.data.updating.MethodChangeUpdaterTest;
import kuleuven.group2.data.updating.MethodTestLinkUpdaterTest;
import kuleuven.group2.data.updating.TestChangeUpdaterTest;
import kuleuven.group2.data.updating.TestResultUpdaterTest;
import kuleuven.group2.filewatch.FolderWatcherTest;
import kuleuven.group2.policy.PolicyTest;
import kuleuven.group2.runner.TestRunnerTest;
import kuleuven.group2.store.DirectoryStoreTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	EclipseCompilerTest.class,
	DatabaseTests.class,
	TestDatabaseTest.class,
	TestTest.class,
	MethodHasherTest.class,
	JavaSignatureParserTest.class,
	MethodChangeUpdaterTest.class,
	MethodTestLinkUpdaterTest.class,
	TestResultUpdaterTest.class,
	FolderWatcherTest.class,
	PolicyTest.class,
	TestRunnerTest.class,
	DirectoryStoreTest.class,
	TestChangeUpdaterTest.class,
	})
public class AllTests {
	
}
