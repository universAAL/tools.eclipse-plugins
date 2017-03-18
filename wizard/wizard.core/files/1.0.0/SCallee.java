/*TAG:PACKAGE*/

/* More on how to use this class at: 
 * https://github.com/universAAL/platform/wiki/RD-Using-Services#Service_Callees_and_Service_Profiles */
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.service.ServiceCall;
import org.universAAL.middleware.service.ServiceCallee;
import org.universAAL.middleware.service.ServiceResponse;
import org.universAAL.middleware.service.owls.profile.ServiceProfile;

public class /*TAG:CLASSNAME*/ extends ServiceCallee {

    protected /*TAG:CLASSNAME*/(ModuleContext context, ServiceProfile[] realizedServices) {
	super(context, realizedServices);
	// TODO Auto-generated constructor stub
    }

    protected /*TAG:CLASSNAME*/(ModuleContext context) {
	super(context, /*TAG:CLASSNAME*/ProvidedService.profiles);
	// TODO Auto-generated constructor stub
    }

    public void communicationChannelBroken() {
	// TODO Auto-generated method stub

    }

    public ServiceResponse handleCall(ServiceCall call) {
	// TODO Auto-generated method stub
	return null;
    }

}
