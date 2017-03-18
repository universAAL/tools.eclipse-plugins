/*TAG:PACKAGE*/

import java.util.Locale;

import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.owl.MergedRestriction;
import org.universAAL.middleware.owl.supply.LevelRating;
import org.universAAL.middleware.rdf.PropertyPath;
import org.universAAL.middleware.rdf.TypeMapper;
import org.universAAL.middleware.rdf.Resource;
import org.universAAL.middleware.ui.UICaller;
import org.universAAL.middleware.ui.UIRequest;
import org.universAAL.middleware.ui.UIResponse;
import org.universAAL.middleware.ui.owl.PrivacyLevel;
import org.universAAL.middleware.ui.rdf.Form;
import org.universAAL.middleware.ui.rdf.Group;
import org.universAAL.middleware.ui.rdf.InputField;
import org.universAAL.middleware.ui.rdf.Label;
import org.universAAL.middleware.ui.rdf.MediaObject;
import org.universAAL.middleware.ui.rdf.Select;
import org.universAAL.middleware.ui.rdf.Select1;
import org.universAAL.middleware.ui.rdf.SimpleOutput;
import org.universAAL.middleware.ui.rdf.Submit;
import org.universAAL.ontology.profile.User;

public class UIfCaller extends UICaller {

	/* -Example- URI Constants for handling and identifying inputs and submits */
	protected static final String INPUT_1 = SCalleeProvidedService.SERVICE_OWN_NAMESPACE
		+ "input1";
	protected static final String INPUT_2 = SCalleeProvidedService.SERVICE_OWN_NAMESPACE
		+ "input2";
	protected static final String INPUT_3 = SCalleeProvidedService.SERVICE_OWN_NAMESPACE
		+ "input3";
	protected static final String INPUT_4 = SCalleeProvidedService.SERVICE_OWN_NAMESPACE
		+ "input4";
	protected static final String INPUT_5 = SCalleeProvidedService.SERVICE_OWN_NAMESPACE
		+ "input5";
	protected static final String INPUT_6 = SCalleeProvidedService.SERVICE_OWN_NAMESPACE
		+ "input6";
	protected static final String INPUT_7 = SCalleeProvidedService.SERVICE_OWN_NAMESPACE
		+ "input7";
	protected static final String INPUT_8 = SCalleeProvidedService.SERVICE_OWN_NAMESPACE
		+ "input8";
	protected static final String INPUT_9 = SCalleeProvidedService.SERVICE_OWN_NAMESPACE
		+ "input9";
	protected static final String SUBMIT_1 = SCalleeProvidedService.SERVICE_OWN_NAMESPACE
		+ "submit1";
	protected static final String SUBMIT_2 = SCalleeProvidedService.SERVICE_OWN_NAMESPACE
		+ "submit2";
	protected static final String SUBMIT_3 = SCalleeProvidedService.SERVICE_OWN_NAMESPACE
		+ "submit3";
	protected static final String SUBMIT_4 = SCalleeProvidedService.SERVICE_OWN_NAMESPACE
		+ "submit4";
	protected static final String SUBMIT_5 = SCalleeProvidedService.SERVICE_OWN_NAMESPACE
		+ "submit5";

	protected UIfCaller(ModuleContext context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void communicationChannelBroken() {
		// TODO Auto-generated method stub
	}

	public void dialogAborted(String arg0, Resource data) {
		// TODO Auto-generated method stub

	}

	/* -Example- These "show" methods send output events to be rendered */
	protected void showMainDialog(User user) {
		Form f = getMainForm();
		UIRequest oe = new UIRequest(user, f, LevelRating.middle,
			Locale.ENGLISH, PrivacyLevel.insensible);
		sendUIRequest(oe);
	}

	public void showResponseDialog(User user, String[] formsNames,
			String[] formsResults) {
		Form f = getResponseForm(formsNames, formsResults);
		UIRequest oe = new UIRequest(user, f, LevelRating.middle,
			Locale.ENGLISH, PrivacyLevel.insensible);
		sendUIRequest(oe);
	}

	public void showStatusDialog(User user, String string) {
		Form f = getStatusForm(string);
		UIRequest oe = new UIRequest(user, f, LevelRating.middle,
			Locale.ENGLISH, PrivacyLevel.insensible);
		sendUIRequest(oe);
	}

	/*
	 * -Example- These "getForms" methods build the forms and screens that will
	 * be embedded in the output events to be rendered
	 */
	private Form getMainForm() {
		Form f = Form.newDialog("Dialog Title", (String) null);

		Group controls = f.getIOControls();
		Group submits = f.getSubmits();

		// A simple text output
		new SimpleOutput(controls, new Label("Simple Output", (String) null),
			null, "Simple Output with a Label");

		// A simple text input
		new InputField(controls, new Label("Input Field", (String) null),
			new PropertyPath(null, false, new String[] { INPUT_1 }), null,
			"Input with initial value with a label");
		// A boolean input
		new InputField(
			controls,
			new Label("Boolean Input Field", (String) null),
			new PropertyPath(null, false, new String[] { INPUT_2 }),
			MergedRestriction.getAllValuesRestrictionWithCardinality(
				INPUT_2, TypeMapper.getDatatypeURI(Boolean.class), 1, 1),
			Boolean.TRUE);

		// A Select input to select just 1 option
		Select1 s1 = new Select1(controls,
			new Label("Select 1", (String) null), new PropertyPath(null,
				false, new String[] { INPUT_3 }), null, "Option 1");
		s1.generateChoices(new String[] { "Option 1", "Option 2", "Option 3" });

		// A Select input to select multiple options
		Select ms = new Select(controls, new Label("Select", (String) null),
			new PropertyPath(null, false, new String[] { INPUT_4 }), null,
			"Option A");
		ms.generateChoices(new String[] { "Option A", "Option B", "Option C" });

		// A Media object (here an image)
		new MediaObject(controls, new Label("Image", (String) null), "IMG",
			"img/bt_white.png");

		// An input to select from a numeric range
		// new Range(controls, new Label("Range", (String) null),
		// new PropertyPath(null, false, new String[] { INPUT_9 }),
		// OrderingRestriction.newOrderingRestriction(Integer.valueOf(12),
		// Integer.valueOf(3), true, true, INPUT_9),
		// new Integer(5));

		// Button to test the above forms
		new Submit(submits, new Label("Test Input Forms", (String) null),
			SUBMIT_1);
		// Button to turn actuator on
		new Submit(submits, new Label("Turn Actuator On", (String) null),
			SUBMIT_2);
		// Button to turn actuator off
		new Submit(submits, new Label("Turn Actuator Off", (String) null),
			SUBMIT_3);
		// Button to show actuator status
		new Submit(submits, new Label("Get Actuator Status", (String) null),
			SUBMIT_4);

		return f;
	}

	public Form getResponseForm(String[] formsNames, String[] formsResults) {
		Form f = Form.newDialog("Response Dialog Title", (String) null);
		Group controls = f.getIOControls();
		Group submits = f.getSubmits();

		for (int i = 0; i < formsResults.length; i++) {
			if (formsNames[i] == null)
			formsNames[i] = "";
			if (formsResults[i] == null)
			formsResults[i] = "";
			new SimpleOutput(controls, new Label(formsNames[i], (String) null),
				null, formsResults[i]);
		}
		new Submit(submits, new Label("OK", (String) null), SUBMIT_5);

		return f;

	}

	private Form getStatusForm(String string) {
		// This generates a simple popup message on top of the screen
		Form f = Form.newMessage("Popup Title", string);
		return f;
	}

	/*
	 * -Example- This reads the submit that originated the input and processes
	 * its attached inputs and/or generates the next output to show
	 */
	public void handleUIResponse(UIResponse response) {
		if (response == null) {
			// Do nothing: it will return to main menu
			return;
		}
		User user = (User) response.getUser();
		String submit = response.getSubmissionID();
		try {
			if (submit.equals(UIfCaller.SUBMIT_1)) {
			/*
			 * -Example- Button pressed was "Test Input Forms"
			 */
			String[] formsNames = new String[] { "Input Field",
				"Boolean Input Field", "Select 1", "Select",
				"Text Area", "Range" };
			String[] formsResults = new String[5];
			formsResults[0] = response.getUserInput(
				new String[] { (UIfCaller.INPUT_1) }).toString();
			formsResults[1] = response.getUserInput(
				new String[] { (UIfCaller.INPUT_2) }).toString();
			formsResults[2] = response.getUserInput(
				new String[] { (UIfCaller.INPUT_3) }).toString();
			formsResults[3] = response.getUserInput(
				new String[] { (UIfCaller.INPUT_4) }).toString();
			formsResults[4] = response.getUserInput(
				new String[] { (UIfCaller.INPUT_9) }).toString();
			Activator.uifcaller.showResponseDialog(user, formsNames,
				formsResults);
			}
			if (submit.equals(UIfCaller.SUBMIT_2)) {
				/* -Example- Button pressed was "Set Actuator On" */
				if (Activator.scaller.callSetStatus(true).booleanValue()) {
					Activator.uifcaller.showStatusDialog(user,
						"Actuator set to On");
				} else {
					Activator.uifcaller.showStatusDialog(user,
						"Actuator not responding");
				}
			}
			if (submit.equals(UIfCaller.SUBMIT_3)) {
				/* -Example- Button pressed was "Set Actuator Off" */
				if (Activator.scaller.callSetStatus(false).booleanValue()) {
					Activator.uifcaller.showStatusDialog(user,
						"Actuator set to Off");
				} else {
					Activator.uifcaller.showStatusDialog(user,
						"Actuator not responding");
				}
			}
			if (submit.equals(UIfCaller.SUBMIT_4)) {
				/* -Example- Button pressed was "Get Actuator Status" */
				if (Activator.scaller.callGetStatus().booleanValue()) {
					Activator.uifcaller.showStatusDialog(user,
						"Actuator is set to On");
				} else {
					Activator.uifcaller.showStatusDialog(user,
						"Actuator is set to Off");
				}
			}
			if (submit.equals(UIfCaller.SUBMIT_5)) {
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

}