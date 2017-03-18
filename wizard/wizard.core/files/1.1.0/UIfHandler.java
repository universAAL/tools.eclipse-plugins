/*TAG:PACKAGE*/

/* More on how to use this class at: 
 * https://github.com/universAAL/platform/wiki/RD-Handling-User-Interaction */
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.rdf.Resource;
import org.universAAL.middleware.ui.UIHandler;
import org.universAAL.middleware.ui.UIHandlerProfile;
import org.universAAL.middleware.ui.UIRequest;

public class /*TAG:CLASSNAME*/ extends UIHandler {

	protected /*TAG:CLASSNAME*/(ModuleContext context, UIHandlerProfile initialSubscription) {
		super(context, initialSubscription);
		// TODO Auto-generated constructor stub
	}
    
	protected /*TAG:CLASSNAME*/(ModuleContext context) {
		super(context, getInitialSubscription());
		// TODO Auto-generated constructor stub
	}
    
	private static UIHandlerProfile getInitialSubscription() {
		// TODO Auto-generated method stub
		return null;
	}

	public void adaptationParametersChanged(String dialogID,
			String changedProp, Object newVal) {
		// TODO Auto-generated method stub

	}

	public void communicationChannelBroken() {
		// TODO Auto-generated method stub

	}

	public Resource cutDialog(String dialogID) {
		// TODO Auto-generated method stub
		return null;
	}

	public void handleUICall(UIRequest uicall) {
		// TODO Auto-generated method stub

	}

}