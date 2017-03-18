/*TAG:PACKAGE*/

import org.universAAL.middleware.owl.OntologyManagement;
import org.universAAL.middleware.owl.SimpleOntology;
import org.universAAL.middleware.rdf.Resource;
import org.universAAL.middleware.rdf.TypeMapper;
import org.universAAL.middleware.rdf.impl.ResourceFactoryImpl;
import org.universAAL.middleware.service.owls.profile.ServiceProfile;
import org.universAAL.ontology.phThing.DeviceService;
import org.universAAL.ontology.phThing.OnOffActuator;

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
	public static ServiceProfile[] profiles = new ServiceProfile[2];

	static {
		/*
		 * -Example- This piece of code tells ontology management that these
		 * provided services extend the DeviceService ontology, without having
		 * to code a full Ontology class
		 */
		OntologyManagement.getInstance().register(
			new SimpleOntology(MY_URI, DeviceService.MY_URI,
				new ResourceFactoryImpl() {
					@Override
					public Resource createInstance(String classURI,
						String instanceURI, int factoryIndex) {
					return new SCalleeProvidedService(instanceURI);
					}
				}));

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
