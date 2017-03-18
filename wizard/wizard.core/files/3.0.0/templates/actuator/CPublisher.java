/*TAG:PACKAGE*/

import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.context.ContextEvent;
import org.universAAL.middleware.context.ContextPublisher;
import org.universAAL.middleware.context.ContextEventPattern;
import org.universAAL.middleware.context.owl.ContextProvider;
import org.universAAL.middleware.context.owl.ContextProviderType;
import org.universAAL.ontology.device.StatusValue;
import org.universAAL.ontology.device.SwitchController;

public class CPublisher extends ContextPublisher {
	/*
	 * -Example- this namespace can be reused in many parts of the code, but not
	 * all of them
	 */
	protected static final String CONTEXT_OWN_NAMESPACE = "http://your.ontology.URL.com/YourProviderDomainOntology.owl#";
	// TODO: Change Namespace
	/*
	 * -Example- URI Constants for handling and identifying inputs, outputs and
	 * services
	 */
	protected static final String DEVICE_OWN_URI = CONTEXT_OWN_NAMESPACE
			+ "yourOwnActuator";

	protected CPublisher(ModuleContext context, ContextProvider providerInfo) {
		super(context, providerInfo);
		// TODO Auto-generated constructor stub
	}

	protected CPublisher(ModuleContext context) {
		super(context, getProviderInfo());
		// TODO Auto-generated constructor stub
	}

	private static ContextProvider getProviderInfo() {
		/*
		 * -Example- This is a Controller ContextProvider, because it provides
		 * context about a source you can control: an SwitchController.
		 */
		ContextProvider cp = new ContextProvider(CONTEXT_OWN_NAMESPACE
				+ "ContextProvider");
		// TODO: Change Namespace and define published events
		cp.setType(ContextProviderType.controller);
		cp.setProvidedEvents(new ContextEventPattern[] { new ContextEventPattern() });
		return cp;
	}

	public void communicationChannelBroken() {
		// TODO Auto-generated method stub

	}

	/**
	 * -Example- Shortcut method to publish a Context Event with: \n Subject: A
	 * simulated SwitchController \n Predicate: has Status \n Object: The passed
	 * argument (on/off)
	 * 
	 * @param status
	 *            Status of the Subject
	 */
	protected void publishStatusEvent(StatusValue status) {
		SwitchController theDevice = new SwitchController(DEVICE_OWN_URI);
		theDevice.setValue(status);
		ContextEvent ev = new ContextEvent(theDevice,
				SwitchController.PROP_HAS_VALUE);
		publish(ev);
	}

}
