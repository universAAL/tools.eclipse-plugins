/*TAG:PACKAGE*/

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.osgi.uAALBundleContainer;

public class Activator implements BundleActivator{
	public static BundleContext osgiContext=null;
	public static ModuleContext context=null;
	public static CPublisher cpublisher=null;

	public void start(BundleContext bcontext) throws Exception {
		Activator.osgiContext=bcontext;
		Activator.context = uAALBundleContainer.THE_CONTAINER
			.registerModule(new Object[] { bcontext });
		cpublisher=new CPublisher(context);
	}

	public void stop(BundleContext arg0) throws Exception {
		cpublisher.close();
	}

}
