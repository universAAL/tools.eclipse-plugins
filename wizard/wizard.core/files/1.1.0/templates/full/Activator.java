/*TAG:PACKAGE*/

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.osgi.uAALBundleContainer;

public class Activator implements BundleActivator {
	public static BundleContext osgiContext = null;
	public static ModuleContext context = null;

	public static SCallee scallee = null;
	public static SCaller scaller = null;
	public static UIfCaller uifcaller = null;
	public static CSubscriber csubscriber = null;
	public static CPublisher cpublisher = null;

	public void start(BundleContext bcontext) throws Exception {
		Activator.osgiContext = bcontext;
		Activator.context = uAALBundleContainer.THE_CONTAINER
			.registerModule(new Object[] { bcontext });
		scallee = new SCallee(context);
		scaller = new SCaller(context);
		uifcaller = new UIfCaller(context);
		csubscriber = new CSubscriber(context);
		cpublisher = new CPublisher(context);
	}

	public void stop(BundleContext arg0) throws Exception {
		scallee.close();
		scaller.close();
		uifcaller.close();
		csubscriber.close();
		cpublisher.close();
	}

}
