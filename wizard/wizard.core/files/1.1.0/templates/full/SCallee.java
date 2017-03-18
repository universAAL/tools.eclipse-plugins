/*TAG:PACKAGE*/

import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.service.CallStatus;
import org.universAAL.middleware.service.ServiceCall;
import org.universAAL.middleware.service.ServiceCallee;
import org.universAAL.middleware.service.ServiceRequest;
import org.universAAL.middleware.service.ServiceResponse;
import org.universAAL.middleware.service.owls.process.ProcessOutput;
import org.universAAL.middleware.service.owls.profile.ServiceProfile;
import org.universAAL.ontology.phThing.OnOffActuator;
import org.universAAL.ontology.profile.User;

public class SCallee extends ServiceCallee {
	private OnOffActuator theDevice;
	/* -Example- An error response for common use */
	private static final ServiceResponse failure = new ServiceResponse(
		CallStatus.serviceSpecificFailure);

	protected SCallee(ModuleContext context, ServiceProfile[] realizedServices) {
		super(context, realizedServices);
		/* -Example- Instantiate a virtual OnOffActuator */
		theDevice = new OnOffActuator(CPublisher.DEVICE_OWN_URI);
		theDevice.setStatus(false);
	}

	protected SCallee(ModuleContext context) {
		/* -Example- Instantiate a virtual OnOffActuator */
		super(context, SCalleeProvidedService.profiles);
		theDevice = new OnOffActuator(CPublisher.DEVICE_OWN_URI);
		theDevice.setStatus(false);
	}

	public void communicationChannelBroken() {
		/* -Example- Remove the virtual OnOffActuator */
		theDevice = null;
	}

	public ServiceResponse handleCall(ServiceCall call) {
		User user = null;
		if (call == null) {
			failure.addOutput(new ProcessOutput(
				ServiceResponse.PROP_SERVICE_SPECIFIC_ERROR, "Null call!?!"));
			return failure;
		}

		String operation = call.getProcessURI();
		if (operation == null) {
			failure.addOutput(new ProcessOutput(
				ServiceResponse.PROP_SERVICE_SPECIFIC_ERROR,
				"Null operation!?!"));
			return failure;
		}

		/* -Example- This is the code to show the first application screen */
		if (operation.startsWith(SCalleeProvidedService.SERVICE_START_UI)) {
			Object inputUser = call
				.getProperty(ServiceRequest.PROP_uAAL_INVOLVED_HUMAN_USER);
			if ((inputUser == null) || !(inputUser instanceof User)) {
				failure.addOutput(new ProcessOutput(
					ServiceResponse.PROP_SERVICE_SPECIFIC_ERROR,
					"Invalid User Input!"));
				return failure;
			} else {
					user = (User) inputUser;
			}
			Activator.uifcaller.showMainDialog(user);
			ServiceResponse response = new ServiceResponse(CallStatus.succeeded);
			return response;
		}

		/*
		 * -Example- This returns the status of the requested OnOffActuator. it
		 * doesnt need to check the input because the Service restriction of
		 * this server specifies that it only handles its single virtual
		 * OnOffActuator. If the request got here, it means it addressed this
		 * virtual OnOffActuator
		 */
		if (operation.startsWith(SCalleeProvidedService.SERVICE_GET_STATUS_URI)) {
			if (theDevice == null) {
				failure.addOutput(new ProcessOutput(
					ServiceResponse.PROP_SERVICE_SPECIFIC_ERROR,
					"Device not ready!"));
				return failure;
			} else {
				ServiceResponse response = new ServiceResponse(
					CallStatus.succeeded);
				response.addOutput(new ProcessOutput(
					SCalleeProvidedService.OUTPUT_STATUS, new Boolean(
						theDevice.getStatus())));
				return response;
			}
		}

		/*
		 * -Example- This changes the status of the requested OnOffActuator,
		 * according to the additonal input. it doesnt need to check the input
		 * because the Service restriction of this server specifies that it only
		 * handles its single virtual OnOffActuator. If the request got here, it
		 * means it addressed this virtual OnOffActuator
		 */
		if (operation.startsWith(SCalleeProvidedService.SERVICE_SET_STATUS_URI)) {
			Boolean status = (Boolean) call
				.getInputValue(SCalleeProvidedService.INPUT_STATUS);
			if (theDevice == null) {
				failure.addOutput(new ProcessOutput(
					ServiceResponse.PROP_SERVICE_SPECIFIC_ERROR,
					"Device not ready!"));
				return failure;
			} else if (status != null) {
				theDevice.setStatus(status.booleanValue());
				Activator.cpublisher.publishStatusEvent(status.booleanValue());
				return new ServiceResponse(CallStatus.succeeded);
			} else {
				failure.addOutput(new ProcessOutput(
					ServiceResponse.PROP_SERVICE_SPECIFIC_ERROR,
					"Device not ready or wrong input!"));
				return failure;
			}
		}

		return failure;
	}
}
