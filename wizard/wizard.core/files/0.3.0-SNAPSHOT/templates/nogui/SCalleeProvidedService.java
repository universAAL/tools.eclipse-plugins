/*TAG:PACKAGE*/

import org.universAAL.middleware.owl.Restriction;
import org.universAAL.middleware.rdf.TypeMapper;
import org.universAAL.middleware.service.owls.profile.ServiceProfile;
import org.universAAL.ontology.phThing.DeviceService;
import org.universAAL.ontology.phThing.OnOffActuator;

import java.util.Hashtable;

/* -Example- This service example provides Device Services*/
public class SCalleeProvidedService extends DeviceService {

    /*
     * -Example- this namespace can be reused in many parts of the code, but not
     * all of them
     */
    protected static final String SERVICE_OWN_NAMESPACE = "http://your.ontology.URL.com/YourServerDomainOntology.owl#";
    // TODO: Change Namespace
    public static final String MY_URI = SERVICE_OWN_NAMESPACE
	    + "OnOffActuatorTemplateService";
    protected static final String SERVICE_GET_STATUS_URI = SERVICE_OWN_NAMESPACE
	    + "getStatus";
    protected static final String SERVICE_SET_STATUS_URI = SERVICE_OWN_NAMESPACE
	    + "setStatus";
    protected static final String OUTPUT_STATUS = SERVICE_OWN_NAMESPACE
	    + "outputStatus";
    protected static final String INPUT_STATUS = SERVICE_OWN_NAMESPACE
	    + "inputStatus";
    protected static final String INPUT_DEVICE = SERVICE_OWN_NAMESPACE
	    + "inputDevice";

    /* -Example- This registers three profiles */
    public static ServiceProfile[] profiles = new ServiceProfile[2];;
    private static Hashtable serverLevelRestrictions = new Hashtable();

    static {
	register(SCalleeProvidedService.class);

	/*
	 * -Example- This example service provides Device Service that only
	 * controls a specific OnOffActuator and no other
	 */
	addRestriction(Restriction.getFixedValueRestriction(PROP_CONTROLS,
		new OnOffActuator(CPublisher.DEVICE_OWN_URI)),
		new String[] { PROP_CONTROLS }, serverLevelRestrictions);

	// Declaration of first profile. In: OnOffActuator; Out: Boolean
	SCalleeProvidedService getOnOffActuatorStatus = new SCalleeProvidedService(
		SERVICE_GET_STATUS_URI);
	getOnOffActuatorStatus.addFilteringInput(INPUT_DEVICE,
		OnOffActuator.MY_URI, 1, 1,
		new String[] { DeviceService.PROP_CONTROLS });
	getOnOffActuatorStatus.addOutput(OUTPUT_STATUS,
		TypeMapper.getDatatypeURI(Boolean.class), 1, 1,
		new String[] { DeviceService.PROP_CONTROLS,
			OnOffActuator.PROP_STATUS });
	profiles[0] = getOnOffActuatorStatus.getProfile();

	// Declaration of second profile. In: OnOffActuator, Boolean
	SCalleeProvidedService setOnOffActuatorStatus = new SCalleeProvidedService(
		SERVICE_SET_STATUS_URI);
	setOnOffActuatorStatus.addFilteringInput(INPUT_DEVICE,
		OnOffActuator.MY_URI, 1, 1,
		new String[] { DeviceService.PROP_CONTROLS });
	setOnOffActuatorStatus.addInputWithChangeEffect(INPUT_STATUS,
		TypeMapper.getDatatypeURI(Boolean.class), 1, 1,
		new String[] { DeviceService.PROP_CONTROLS,
			OnOffActuator.PROP_STATUS });
	profiles[1] = setOnOffActuatorStatus.getProfile();

    }

    protected SCalleeProvidedService(String uri) {
	super(uri);
    }

}
