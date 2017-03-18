/*TAG:PACKAGE*/

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator{
	public static BundleContext context=null;
	public static SCallee scallee=null;
	public static CPublisher cpublisher=null;

	public void start(BundleContext context) throws Exception {
		Activator.context=context;
		scallee=new SCallee(context);
		cpublisher=new CPublisher(context);
	}

	public void stop(BundleContext arg0) throws Exception {
		scallee.close();
		cpublisher.close();
	}

}
