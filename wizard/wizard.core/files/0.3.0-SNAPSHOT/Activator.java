/*TAG:PACKAGE*/

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
/*TAG:IMPORT*/

public class Activator implements BundleActivator{
	public static BundleContext context=null;
	/*TAG:INIT*/

	public void start(BundleContext context) throws Exception {
		Activator.context=context;
		/*TAG:START*/
	}

	public void stop(BundleContext arg0) throws Exception {
		/*TAG:STOP*/
	}

}
