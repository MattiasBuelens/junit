package kuleuven.group2.classloader;

import java.io.InputStream;
import java.net.URL;

/**
 * A {@link ClassLoader} which can be reset to allow for class reloading.
 * 
 * <p>
 * This class delegates to a wrapped {@link ClassLoader} instance. Such an
 * instance is lazily constructed whenever one of the {@link ClassLoader}
 * methods is called.
 * </p>
 * 
 * <p>
 * {@link #reload()} removes the wrapped instance such that the next time a
 * {@link ClassLoader} method is called, a new instance must be created. This
 * new instance won't have the loaded classes from the previous instance,
 * forcing it to load them again.
 * </p>
 * 
 * @author Group2
 * @version 18 November 2013
 */
public abstract class ReloadingClassLoader extends ClassLoader {

	private final ClassLoader parent;

	private final Object delegateLock = new Object();
	private volatile ClassLoader delegate;

	public ReloadingClassLoader() {
		this(ClassLoader.getSystemClassLoader());
	}

	public ReloadingClassLoader(ClassLoader parent) {
		this.parent = parent;
	}

	public void reload() {
		synchronized (delegateLock) {
			// To be lazily reloaded later on
			delegate = null;
		}
	}

	protected ClassLoader getDelegate() {
		/*
		 * Implementation note: "double-checked locking" for correctly
		 * synchronized lazy initialization
		 * 
		 * See http://en.wikipedia.org/wiki/Double-checked_locking#Usage_in_Java
		 */
		ClassLoader delegate = this.delegate;
		if (delegate == null) {
			synchronized (delegateLock) {
				delegate = this.delegate;
				if (delegate == null) {
					this.delegate = delegate = createClassLoader(parent);
				}
			}
		}
		return delegate;
	}

	/**
	 * Create a new class loader to load updated classes.
	 * 
	 * @param parent
	 *            The parent class loader.
	 * @return The newly created class loader.
	 */
	protected abstract ClassLoader createClassLoader(ClassLoader parent);

	/*
	 * Delegates
	 */

	@Override
	public void clearAssertionStatus() {
		getDelegate().clearAssertionStatus();
	}

	@Override
	public URL getResource(String name) {
		return getDelegate().getResource(name);
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		return getDelegate().getResourceAsStream(name);
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		return getDelegate().loadClass(name);
	}

	@Override
	public void setClassAssertionStatus(String className, boolean enabled) {
		getDelegate().setClassAssertionStatus(className, enabled);
	}

	@Override
	public void setDefaultAssertionStatus(boolean enabled) {
		getDelegate().setDefaultAssertionStatus(enabled);
	}

	@Override
	public void setPackageAssertionStatus(String packageName, boolean enabled) {
		getDelegate().setPackageAssertionStatus(packageName, enabled);
	}

}