INSTRUCTIONS FOR INCLUDING SUPPORT FOR A NEW PLATFORM RELEASE TO THE WIZARDS

1) Modify IMWVersion.java to include new version constant IMWVersion.VER_XXX with increased number, where XXX matches the version
2) Create a new MWVersionXXX.java class
	a) Extend from the most similar MWVersionXXX class
	b) Override all methods and constants of the class that vary from that previous version to the new one
		b.1) Mandatory: getMWVersionNumber()
		b.2) Almost for sure: XXX_DEPS constants
		b.3) If changed XXX_DEPS, also implement all methods that use them (e.g. POM methods), even if just a copy of the superclass
		b.4) Most probably: getMainFolder()
		b.5) Anything else...
	c) Remember to update Javadoc if necessary, and imports!
3) Modify MWVersionFactory.java to include new MWVersionXXX
	a) getMWVersion: Return a new MWVersionXXX when asked for its version ID (also when default)
	b) getVERname: Return a label for the version when asked for its version ID (also when default)
	c) getAllVERnames: Add the label for the version to the end of returned array. Notice that order follows the version IDs
4) Add (if necessary) the template files for the version in the "files" folder. Make sure it matches references from MWVersionXXX.java
5) Increase plugin version