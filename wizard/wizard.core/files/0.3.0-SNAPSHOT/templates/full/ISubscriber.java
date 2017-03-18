/*TAG:PACKAGE*/

import org.osgi.framework.BundleContext;
import org.universAAL.middleware.input.InputEvent;
import org.universAAL.middleware.input.InputSubscriber;
import org.universAAL.ontology.profile.User;

public class ISubscriber extends InputSubscriber {

    protected ISubscriber(BundleContext context) {
	super(context);
	// TODO Auto-generated constructor stub
    }

    public void communicationChannelBroken() {
	// TODO Auto-generated method stub

    }

    public void dialogAborted(String dialogID) {
	// TODO Auto-generated method stub

    }

    public void handleInputEvent(InputEvent event) {
	/*
	 * -Example- This reads the submit that originated the input and
	 * processes its attached inputs and/or generates the next output to
	 * show
	 */
	User user = (User) event.getUser();
	String submit = event.getSubmissionID();
	try {
	    if (submit.equals(OPublisher.SUBMIT_1)) {
		/*
		 * -Example- Button pressed was "Test Input Forms"
		 */
		String[] formsNames = new String[] { "Input Field",
			"Boolean Input Field", "Select 1", "Select",
			"Text Area", "Range" };
		String[] formsResults = new String[5];
		formsResults[0] = event.getUserInput(
			new String[] { (OPublisher.INPUT_1) }).toString();
		formsResults[1] = event.getUserInput(
			new String[] { (OPublisher.INPUT_2) }).toString();
		formsResults[2] = event.getUserInput(
			new String[] { (OPublisher.INPUT_3) }).toString();
		formsResults[3] = event.getUserInput(
			new String[] { (OPublisher.INPUT_4) }).toString();
		formsResults[4] = event.getUserInput(
			new String[] { (OPublisher.INPUT_9) }).toString();
		Activator.opublisher.showResponseDialog(user, formsNames,
			formsResults);
	    }
	    if (submit.equals(OPublisher.SUBMIT_2)) {
		/* -Example- Button pressed was "Set Actuator On" */
		if (Activator.scaller.callSetStatus(true).booleanValue()) {
		    Activator.opublisher.showStatusDialog(user,
			    "Actuator set to On");
		} else {
		    Activator.opublisher.showStatusDialog(user,
			    "Actuator not responding");
		}
	    }
	    if (submit.equals(OPublisher.SUBMIT_3)) {
		/* -Example- Button pressed was "Set Actuator Off" */
		if (Activator.scaller.callSetStatus(false).booleanValue()) {
		    Activator.opublisher.showStatusDialog(user,
			    "Actuator set to Off");
		} else {
		    Activator.opublisher.showStatusDialog(user,
			    "Actuator not responding");
		}
	    }
	    if (submit.equals(OPublisher.SUBMIT_4)) {
		/* -Example- Button pressed was "Get Actuator Status" */
		if (Activator.scaller.callGetStatus().booleanValue()) {
		    Activator.opublisher.showStatusDialog(user,
			    "Actuator is set to On");
		} else {
		    Activator.opublisher.showStatusDialog(user,
			    "Actuator is set to Off");
		}
	    }
	    if (submit.equals(OPublisher.SUBMIT_5)) {
		/*
		 * -Example- Button pressed was "OK" after viewing the Form
		 * results. Do nothing: it will return to main menu
		 */
	    }
	    /* -Example- Do nothing to let the system return to main menu */
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    /**
     * -Example- Shortcut method to facilitate other classes access to
     * "addNewRegParams()" so they can register outputs
     * 
     * @param dialogID
     *            ID of the output to be handled
     */
    protected void subscribe(String dialogID) {
	addNewRegParams(dialogID);
    }

}
