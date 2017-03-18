/*TAG:PACKAGE*/

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator{
	public static BundleContext context=null;
	public static SCallee scallee=null;
	public static SCaller scaller=null;
	public static ISubscriber isubscriber=null;
	public static OPublisher opublisher=null;
	public static CSubscriber csubscriber=null;
	public static CPublisher cpublisher=null;

	public void start(BundleContext context) throws Exception {
		Activator.context=context;
		scallee=new SCallee(context);
		scaller=new SCaller(context);
		isubscriber=new ISubscriber(context);
		opublisher=new OPublisher(context);
		csubscriber=new CSubscriber(context);
		cpublisher=new CPublisher(context);
	}

	public void stop(BundleContext arg0) throws Exception {
		scallee.close();
		scaller.close();
		isubscriber.close();
		opublisher.close();
		csubscriber.close();
		cpublisher.close();
	}

}
