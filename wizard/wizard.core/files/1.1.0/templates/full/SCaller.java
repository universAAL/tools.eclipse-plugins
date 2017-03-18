/*TAG:PACKAGE*/

import java.util.List;

import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.service.CallStatus;
import org.universAAL.middleware.service.ServiceCaller;
import org.universAAL.middleware.service.ServiceRequest;
import org.universAAL.middleware.service.ServiceResponse;
import org.universAAL.ontology.phThing.DeviceService;
import org.universAAL.ontology.phThing.OnOffActuator;

public class SCaller extends ServiceCaller {

	/* -Example- This namespace should only be used here */
	private static final String CLIENT_OWN_NAMESPACE = "http://your.ontology.URL.com/YourClientDomainOntology.owl#";
	// TODO: Change Namespace
	/* -Example- URI Constants for handling and identifying outputs */
	private static final String EXPECT_OUTPUT = CLIENT_OWN_NAMESPACE + "output";

	protected SCaller(ModuleContext context) {
	super(context);
		// TODO Auto-generated constructor stub
	}

	public void communicationChannelBroken() {
		// TODO Auto-generated method stub

	}

	public void handleResponse(String reqID, ServiceResponse response) {
		// TODO Auto-generated method stub

	}

	/*
	 * -Example- These shortcut methods allow other classes to quickly invoke
	 * service calls and get the result. This example can call its own callee.
	 */
	protected Boolean callSetStatus(boolean status) {
		ServiceResponse sr = this.call(getSetStatus(status));
		return new Boolean(sr.getCallStatus() == CallStatus.succeeded);
	}

	protected Boolean callGetStatus() {
		ServiceResponse sr = this.call(getGetStatus());
		if (sr.getCallStatus() == CallStatus.succeeded) {
			List outs = sr.getOutput(EXPECT_OUTPUT, false);
			if (outs == null || outs.isEmpty()) {
				return null;
			} else {
				return (Boolean) outs.get(0);
			}
		} else {
			return null;
		}
	}

	/*
	 * -Example- These "get" methods elaborate the Service Request for each
	 * appropriate call
	 */
	private ServiceRequest getSetStatus(boolean status) {
		// This ServiceRequest matches the first profile provided by the callee
		ServiceRequest setStatus = new ServiceRequest(new DeviceService(), null);
		setStatus.addValueFilter(new String[] { DeviceService.PROP_CONTROLS },
			new OnOffActuator(CPublisher.DEVICE_OWN_URI));
		setStatus.addChangeEffect(new String[] { DeviceService.PROP_CONTROLS,
			OnOffActuator.PROP_STATUS }, new Boolean(status));
		return setStatus;
	}

	private ServiceRequest getGetStatus() {
		// This ServiceRequest matches the second profile provided by the callee
		ServiceRequest setStatus = new ServiceRequest(new DeviceService(), null);
		setStatus.addValueFilter(new String[] { DeviceService.PROP_CONTROLS },
			new OnOffActuator(CPublisher.DEVICE_OWN_URI));
		setStatus.addRequiredOutput(EXPECT_OUTPUT, new String[] {
			DeviceService.PROP_CONTROLS, OnOffActuator.PROP_STATUS });
		return setStatus;
	}

}
