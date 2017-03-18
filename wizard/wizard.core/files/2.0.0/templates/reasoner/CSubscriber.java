/*TAG:PACKAGE*/

import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.context.ContextEvent;
import org.universAAL.middleware.context.ContextEventPattern;
import org.universAAL.middleware.context.ContextSubscriber;
import org.universAAL.middleware.owl.MergedRestriction;
import org.universAAL.middleware.owl.supply.LevelRating;
import org.universAAL.ontology.device.StatusValue;
import org.universAAL.ontology.device.SwitchController;

public class CSubscriber extends ContextSubscriber {

	protected CSubscriber(ModuleContext context,
		ContextEventPattern[] initialSubscriptions) {
		super(context, initialSubscriptions);
		// TODO Auto-generated constructor stub
	}

	protected CSubscriber(ModuleContext context) {
		super(context, getPermanentSubscriptions());
		// TODO Auto-generated constructor stub
	}

	private static ContextEventPattern[] getPermanentSubscriptions() {
		/*
		 * -Example- This subscribes for Context Events with: \n Subject: Any
		 * SwitchController \n Predicate: has Status Therefore it will receive its
		 * own published events
		 */
		ContextEventPattern[] ceps = new ContextEventPattern[1];

		ceps[0] = new ContextEventPattern();
		ceps[0].addRestriction(MergedRestriction.getAllValuesRestriction(
			ContextEvent.PROP_RDF_SUBJECT, SwitchController.MY_URI));
		ceps[0].addRestriction(MergedRestriction.getFixedValueRestriction(
			ContextEvent.PROP_RDF_PREDICATE, SwitchController.PROP_HAS_VALUE));

		return ceps;
	}

	public void communicationChannelBroken() {
		// TODO Auto-generated method stub

	}

	public void handleContextEvent(ContextEvent event) {
		Object ob = event.getRDFObject();
		if (ob instanceof StatusValue) {
			StatusValue status = (StatusValue) ob;
			/*
			 * -Example- Do something with the received events. Since this is a
			 * Reasoner, a new event should be the outcome of a received event.
			 * In this case the same received event is sent again but with
			 * battery level, if it didnt have it.
			 */
			// TODO: Customize reasoning.
			SwitchController theDevice = (SwitchController) event.getRDFSubject();
			if (theDevice.getBatteryLevel() == null) {
			theDevice.setBatteryLevel(LevelRating.middle);
			ContextEvent ev = new ContextEvent(theDevice,
					SwitchController.PROP_BATTERY_LEVEL);
			Activator.cpublisher.publish(ev);
			}
		}
	}

}
