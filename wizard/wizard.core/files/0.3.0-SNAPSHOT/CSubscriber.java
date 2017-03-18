/*TAG:PACKAGE*/

/* More on how to use this class at: 
 * https://github.com/universAAL/platform/wiki/RD-Managing-Context-Information#Context_Subscribers_and_Restrictions */
import org.osgi.framework.BundleContext;
import org.universAAL.middleware.context.ContextEvent;
import org.universAAL.middleware.context.ContextEventPattern;
import org.universAAL.middleware.context.ContextSubscriber;

public class /*TAG:CLASSNAME*/ extends ContextSubscriber{

	protected /*TAG:CLASSNAME*/(BundleContext context,
			ContextEventPattern[] initialSubscriptions) {
		super(context, initialSubscriptions);
		// TODO Auto-generated constructor stub
	}
	
	protected /*TAG:CLASSNAME*/(BundleContext context) {
		super(context, getPermanentSubscriptions());
		// TODO Auto-generated constructor stub
	}

	private static ContextEventPattern[] getPermanentSubscriptions() {
		// TODO Auto-generated method stub
		return null;
	}

	public void communicationChannelBroken() {
		// TODO Auto-generated method stub
		
	}

	public void handleContextEvent(ContextEvent event) {
		// TODO Auto-generated method stub
		
	}

}
