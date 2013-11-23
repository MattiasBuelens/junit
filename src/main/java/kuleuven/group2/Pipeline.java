package kuleuven.group2;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import kuleuven.group2.classloader.ReloadingStoreClassLoader;
import kuleuven.group2.data.Test;
import kuleuven.group2.data.TestDatabase;
import kuleuven.group2.data.updating.MethodTestLinkUpdater;
import kuleuven.group2.data.updating.OssRewriterLoader;
import kuleuven.group2.data.updating.TestResultUpdater;
import kuleuven.group2.defer.DeferredConsumer;
import kuleuven.group2.policy.Policy;
import kuleuven.group2.testrunner.TestRunner;
import kuleuven.group2.sourcehandler.ClassSourceEventHandler;
import kuleuven.group2.sourcehandler.SourceEventHandler;
import kuleuven.group2.sourcehandler.TestSourceEventHandler;
import kuleuven.group2.store.Store;
import kuleuven.group2.store.StoreEvent;
import kuleuven.group2.store.StoreFilter;
import kuleuven.group2.store.StoreWatcher;
import kuleuven.group2.util.Consumer;

/**
 * Brings all parts of the program together to form a pipeline.
 * TODO [DOC] vervolledig beschrijving can de klasse Pipeline
 * 
 * @author Group2
 * @version 19 November 2013
 */
public class Pipeline {

	protected final Store classSourceStore;
	protected final Store testSourceStore;
	protected final Store binaryStore;
	protected Policy sortPolicy;

	protected final TestDatabase testDatabase;
	protected final ReloadingStoreClassLoader testClassLoader;

	protected final TestRunner testRunner;
	protected final OssRewriterLoader rewriterLoader;
	protected final MethodTestLinkUpdater methodTestLinkUpdater;
	protected final TestResultUpdater testResultUpdater;

	protected final StoreWatcher classSourceWatcher;
	protected final StoreWatcher testSourceWatcher;
	protected final SourceEventHandler classSourceEventHandler;
	protected final SourceEventHandler testSourceEventHandler;

	protected final PipelineTask task;
	protected final DeferredConsumer<StoreEvent> deferredTask;

	public Pipeline(Store classSourceStore, Store testSourceStore, Store binaryStore, Policy sortPolicy) {
		this.classSourceStore = checkNotNull(classSourceStore);
		this.testSourceStore = checkNotNull(testSourceStore);
		this.binaryStore = checkNotNull(binaryStore);
		this.sortPolicy = checkNotNull(sortPolicy);

		this.testDatabase = new TestDatabase();
		this.testClassLoader = new ReloadingStoreClassLoader(binaryStore, getClass().getClassLoader());
		this.testRunner = new TestRunner(testClassLoader);
		this.rewriterLoader = new OssRewriterLoader();
		this.methodTestLinkUpdater = new MethodTestLinkUpdater(testDatabase, rewriterLoader);
		methodTestLinkUpdater.registerTestHolder(testRunner);
		this.testResultUpdater = new TestResultUpdater(testDatabase);
		testRunner.addRunListener(testResultUpdater);

		this.classSourceWatcher = new StoreWatcher(classSourceStore, StoreFilter.SOURCE);
		this.testSourceWatcher = new StoreWatcher(testSourceStore, StoreFilter.SOURCE);
		this.classSourceEventHandler = new ClassSourceEventHandler(classSourceStore, binaryStore, testDatabase,
				testClassLoader);
		this.testSourceEventHandler = new TestSourceEventHandler(testSourceStore, binaryStore, testDatabase,
				testClassLoader);

		this.task = new PipelineTask();
		this.deferredTask = new DeferredConsumer<>(task);
	}

	public Policy getSortPolicy() {
		return sortPolicy;
	}

	public void setSortPolicy(Policy sortPolicy) {
		this.sortPolicy = checkNotNull(sortPolicy);
	}

	public void start() {
		// Start listening
		classSourceWatcher.registerConsumer(deferredTask);
		testSourceWatcher.registerConsumer(deferredTask);
		classSourceStore.startListening();
		testSourceStore.startListening();
	}

	public void stop() {
		// Stop listening
		classSourceWatcher.unregisterConsumer(deferredTask);
		testSourceWatcher.unregisterConsumer(deferredTask);
		classSourceStore.stopListening();
		testSourceStore.stopListening();
		// TODO Stop current test run as well?
	}

	protected class PipelineTask implements Consumer<List<StoreEvent>> {

		@Override
		public void consume(List<StoreEvent> events) {
			// Reload classes
			testClassLoader.reload();

			// Handle events
			try {
				classSourceEventHandler.handleEvents(events);
				testSourceEventHandler.handleEvents(events);
			} catch (Exception e) {
				// TODO Show in GUI?
				System.err.println(e.getMessage());
			}

			// Sort tests
			Test[] tests = sortPolicy.getSortedTestsAccordingToPolicy(testDatabase);

			// Run tests and monitor method calls
			rewriterLoader.registerMonitor(methodTestLinkUpdater);
			testRunner.runTestMethods(tests);
			rewriterLoader.unregisterMonitor(methodTestLinkUpdater);
		}
	}

}
