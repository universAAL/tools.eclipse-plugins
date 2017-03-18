/*TAG:PACKAGE*/

import java.util.Locale;

import org.osgi.framework.BundleContext;
import org.universAAL.middleware.io.owl.PrivacyLevel;
import org.universAAL.middleware.io.rdf.Form;
import org.universAAL.middleware.io.rdf.Group;
import org.universAAL.middleware.io.rdf.InputField;
import org.universAAL.middleware.io.rdf.Label;
import org.universAAL.middleware.io.rdf.MediaObject;
import org.universAAL.middleware.io.rdf.Range;
import org.universAAL.middleware.io.rdf.Select;
import org.universAAL.middleware.io.rdf.Select1;
import org.universAAL.middleware.io.rdf.SimpleOutput;
import org.universAAL.middleware.io.rdf.Submit;
import org.universAAL.middleware.output.OutputEvent;
import org.universAAL.middleware.output.OutputPublisher;
import org.universAAL.middleware.owl.OrderingRestriction;
import org.universAAL.middleware.owl.Restriction;
import org.universAAL.middleware.owl.supply.LevelRating;
import org.universAAL.middleware.rdf.PropertyPath;
import org.universAAL.middleware.rdf.TypeMapper;
import org.universAAL.ontology.profile.User;

public class OPublisher extends OutputPublisher {

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

    protected OPublisher(BundleContext context) {
	super(context);
	// TODO Auto-generated constructor stub
    }

    public void communicationChannelBroken() {
	// TODO Auto-generated method stub
    }

    /* -Example- These "show" methods send output events to be rendered */
    protected void showMainDialog(User user) {
	Form f = getMainForm();
	OutputEvent oe = new OutputEvent(user, f, LevelRating.middle,
		Locale.ENGLISH, PrivacyLevel.insensible);
	Activator.isubscriber.subscribe(f.getDialogID());
	publish(oe);
    }

    public void showResponseDialog(User user, String[] formsNames,
	    String[] formsResults) {
	Form f = getResponseForm(formsNames, formsResults);
	OutputEvent oe = new OutputEvent(user, f, LevelRating.middle,
		Locale.ENGLISH, PrivacyLevel.insensible);
	Activator.isubscriber.subscribe(f.getDialogID());
	publish(oe);
    }

    public void showStatusDialog(User user, String string) {
	Form f = getStatusForm(string);
	OutputEvent oe = new OutputEvent(user, f, LevelRating.middle,
		Locale.ENGLISH, PrivacyLevel.insensible);
	Activator.isubscriber.subscribe(f.getDialogID());
	publish(oe);
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
	new InputField(controls,
		new Label("Boolean Input Field", (String) null),
		new PropertyPath(null, false, new String[] { INPUT_2 }),
		Restriction.getAllValuesRestrictionWithCardinality(INPUT_2,
			TypeMapper.getDatatypeURI(Boolean.class), 1, 1),
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
	new Range(controls, new Label("Range", (String) null),
		new PropertyPath(null, false, new String[] { INPUT_9 }),
		OrderingRestriction.newOrderingRestriction(Integer.valueOf(12),
			Integer.valueOf(3), true, true, INPUT_9),
		new Integer(5));

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

}
