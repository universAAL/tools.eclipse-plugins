/*TAG:PACKAGE*/

/* More on how to use this class at: 
 * https://github.com/universAAL/platform/wiki/RD-Managing-Context-Information#Context_Publishers_and_Context_Events */
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.context.ContextPublisher;
import org.universAAL.middleware.context.owl.ContextProvider;

public class /*TAG:CLASSNAME*/ extends ContextPublisher {

	protected /*TAG:CLASSNAME*/(ModuleContext context, ContextProvider providerInfo) {
		super(context, providerInfo);
		// TODO Auto-generated constructor stub
	}

	protected /*TAG:CLASSNAME*/(ModuleContext context) {
		super(context, getProviderInfo());
		// TODO Auto-generated constructor stub
	}

	private static ContextProvider getProviderInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public void communicationChannelBroken() {
		// TODO Auto-generated method stub

	}

}
