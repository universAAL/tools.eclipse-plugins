/*TAG:PACKAGE*/

import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.service.CallStatus;
import org.universAAL.middleware.service.ServiceCall;
import org.universAAL.middleware.service.ServiceCallee;
import org.universAAL.middleware.service.ServiceResponse;
import org.universAAL.middleware.service.owls.process.ProcessOutput;
import org.universAAL.middleware.service.owls.profile.ServiceProfile;
import org.universAAL.ontology.device.StatusValue;
import org.universAAL.ontology.device.SwitchController;

public class SCallee extends ServiceCallee {
	private SwitchController theDevice;
	/* -Example- An error response for common use */
	private static final ServiceResponse failure = new ServiceResponse(
			CallStatus.serviceSpecificFailure);

	protected SCallee(ModuleContext context, ServiceProfile[] realizedServices) {
		super(context, realizedServices);
		/* -Example- Instantiate a virtual SwitchController */
		theDevice = new SwitchController(CPublisher.DEVICE_OWN_URI);
		theDevice.setValue(StatusValue.NoCondition);
	}

	protected SCallee(ModuleContext context) {
		/* -Example- Instantiate a virtual SwitchController */
		super(context, SCalleeProvidedService.profiles);
		theDevice = new SwitchController(CPublisher.DEVICE_OWN_URI);
		theDevice.setValue(StatusValue.NoCondition);
	}

	public void communicationChannelBroken() {
		/* -Example- Remove the virtual SwitchController */
		theDevice = null;
	}

	public ServiceResponse handleCall(ServiceCall call) {
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

		/*
		 * -Example- This returns the status of the requested OnOffActuator. it
		 * doesnt need to check the input because the Service restriction of
		 * this server specifies that it only handles its single virtual
		 * SwitchController. If the request got here, it means it addressed this
		 * virtual SwitchController
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
						SCalleeProvidedService.OUTPUT_STATUS, theDevice
								.getValue()));
				return response;
			}
		}

		/*
		 * -Example- This changes the status of the requested OnOffActuator,
		 * according to the additional input. it doesnt need to check the input
		 * because the Service restriction of this server specifies that it only
		 * handles its single virtual SwitchController. If the request got here,
		 * it means it addressed this virtual SwitchController
		 */
		if (operation.startsWith(SCalleeProvidedService.SERVICE_SET_STATUS_URI)) {
			StatusValue status = (StatusValue) call
					.getInputValue(SCalleeProvidedService.INPUT_STATUS);
			if (theDevice == null) {
				failure.addOutput(new ProcessOutput(
						ServiceResponse.PROP_SERVICE_SPECIFIC_ERROR,
						"Device not ready!"));
				return failure;
			} else if (status != null) {
				theDevice.setValue(status);
				Activator.cpublisher.publishStatusEvent(status);
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
