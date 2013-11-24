package kuleuven.group2.classloader;

import java.security.SecureClassLoader;

import kuleuven.group2.compile.NameUtils;
import kuleuven.group2.store.Store;

/**
 * A ClassLoader which uses the resources in its store to find the bytes of
 * classes to load.
 * 
 * @author Group2
 * @version 19 November 2013
 */
public class StoreClassLoader extends SecureClassLoader {

	protected final Store classStore;

	public StoreClassLoader(Store classStore) {
		super();
		this.classStore = classStore;
	}

	public StoreClassLoader(Store classStore, ClassLoader parent) {
		super(parent);
		this.classStore = classStore;
	}

	@Override
	protected Class<?> findClass(String className) throws ClassNotFoundException {
		String resourceName = NameUtils.toBinaryName(className);
		if (classStore.contains(resourceName)) {
			byte[] classBytes = classStore.read(resourceName);
			return defineClass(className, classBytes, 0, classBytes.length);
		}
		return super.findClass(className);
	}

}
