/*TAG:PACKAGE*/

import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.context.ContextEvent;
import org.universAAL.middleware.context.ContextPublisher;
import org.universAAL.middleware.context.ContextEventPattern;
import org.universAAL.middleware.context.owl.ContextProvider;
import org.universAAL.middleware.context.owl.ContextProviderType;
import org.universAAL.ontology.phThing.OnOffActuator;

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
		+ "yourOwnOnOffActuator";

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
		 * context about a source you can control: an OnOffActuator
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
	 * simulated OnOffActuator \n Predicate: has Status \n Object: The passed
	 * argument (on/off)
	 * 
	 * @param status
	 *			Status of the Subject
	 */
	protected void publishStatusEvent(boolean status) {
		OnOffActuator theDevice = new OnOffActuator(DEVICE_OWN_URI);
		theDevice.setStatus(status);
		ContextEvent ev = new ContextEvent(theDevice, OnOffActuator.PROP_STATUS);
		publish(ev);
	}

}
