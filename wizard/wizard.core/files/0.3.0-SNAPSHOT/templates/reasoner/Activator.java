/*TAG:PACKAGE*/

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator{
	public static BundleContext context=null;
	public static CSubscriber csubscriber=null;
	public static CPublisher cpublisher=null;

	public void start(BundleContext context) throws Exception {
		Activator.context=context;
		csubscriber=new CSubscriber(context);
		cpublisher=new CPublisher(context);
	}

	public void stop(BundleContext arg0) throws Exception {
		csubscriber.close();
		cpublisher.close();
	}

}
