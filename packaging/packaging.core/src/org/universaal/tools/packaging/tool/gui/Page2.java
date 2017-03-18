/*
        Copyright 2007-2014 CNR-ISTI, http://isti.cnr.it
        Institute of Information Science and Technologies
        of the Italian National Research Council

        See the NOTICE file distributed with this work for additional
        information regarding copyright ownership

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

          http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
 */
package org.universaal.tools.packaging.tool.gui;

import java.io.File;

import java.net.MalformedURLException;
import java.net.URI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import org.universaal.tools.packaging.tool.impl.PageImpl;
import org.universaal.tools.packaging.tool.parts.OtherChannel;
import org.universaal.tools.packaging.tool.util.DefaultLogger;
import org.universaal.tools.packaging.tool.util.XSDParser;
import org.universaal.tools.packaging.tool.validators.AlphabeticV;
import org.universaal.tools.packaging.tool.validators.PhoneV;
import org.universaal.tools.packaging.tool.validators.UriV;

/**
 * 
 * @author <a href="mailto:manlio.bacco@isti.cnr.it">Manlio Bacco</a>
 * @author <a href="mailto:stefano.lenzi@isti.cnr.it">Stefano Lenzi</a>
 * @author <a href="mailto:federico.volpini@isti.cnr.it">Federico Volpini</a>
 * @version $LastChangedRevision$ ( $LastChangedDate$ )
 */

public class Page2 extends PageImpl {

	private TextExt certificate, person, email, organization, phone, address, /* web, */ othChNm1, othChnDtl1, othChNm2, othChnDtl2;

	private static final String EMAIL_PATTERN = 
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
					+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	protected Page2(String pageName) {
		super(pageName, "Specify contact details");
	}

	public void createControl(final Composite parent) {
		
		XSDParser XSDtooltip = XSDParser.get(XSD_VERSION);
		
		container = new Composite(parent, SWT.NULL);
		setControl(container);

		GridLayout layout = new GridLayout();
		container.setLayout(layout);

		layout.numColumns = 3;
		gd = new GridData(GridData.FILL, GridData.CENTER, true, false);		
		gd.horizontalSpan = 2;

		Label l1 = new Label(container, SWT.NULL);
		certificate = new TextExt(container, SWT.BORDER | SWT.SINGLE);
		//mandatory.add(certificate);
		l1.setText("Security certificate");
		certificate.setText(app.getApplication().getApplicationProvider().getCertificate().toString());	
		certificate.addVerifyListener(new UriV());
		certificate.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));	
		
		Button b1 = new Button(container, SWT.PUSH);
		b1.setText("Browse");
		b1.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				org.universaal.tools.packaging.tool.util.Dialog d = new org.universaal.tools.packaging.tool.util.Dialog(); 
				File sc = d.open(parent.getShell(), new String[]{"*.pem", "*.cer", "*.crt", "*.der", "*.p7b", "*.p7c", "*.p12", "*.pfx"}, true, "Select a security certificate...");
				/* WIKIPEDIA
			    .pem � (Privacy Enhanced Mail) Base64 encoded DER certificate, enclosed between "-----BEGIN CERTIFICATE-----" and "-----END CERTIFICATE-----"
			    .cer, .crt, .der � usually in binary DER form, but Base64-encoded certificates are common too (see .pem above)
			    .p7b, .p7c � PKCS#7 SignedData structure without data, just certificate(s) or CRL(s)
			    .p12 � PKCS#12, may contain certificate(s) (public) and private keys (password protected)
			    .pfx � PFX, predecessor of PKCS#12 (usually contains data in PKCS#12 format, e.g., with PFX files generated in IIS)
				 */
				if(sc != null) {
					try {
						certificate.setText(sc.toURI().toURL()+"");
					} catch (MalformedURLException e1) {
						e1.printStackTrace();
					}
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});					

		Label l2 = new Label(container, SWT.NULL);
		person = new TextExt(container, SWT.BORDER | SWT.SINGLE);
		//mandatory.add(person);
		l2.setText("Contact person");
		person.setText(app.getApplication().getApplicationProvider().getContactPerson());			
		person.addVerifyListener(new AlphabeticV());
		person.setLayoutData(gd);				
		person.addTooltip(XSDtooltip.find("contactType.contactPerson"));
		
		Label l3 = new Label(container, SWT.NULL);
		email = new TextExt(container, SWT.BORDER | SWT.SINGLE);
		mandatory.add(email);
		l3.setText("* Contact e-mail");
		email.setText(app.getApplication().getApplicationProvider().getEmail());	
		//email.addVerifyListener(new MailV()); //TODO not working
		email.setLayoutData(gd);	
		email.addTooltip(XSDtooltip.find("contactType.email"));
		
		Label l4 = new Label(container, SWT.NULL);
		organization = new TextExt(container, SWT.BORDER | SWT.SINGLE);
		//mandatory.add(organization);
		l4.setText("Organization name");
		organization.setText(app.getApplication().getApplicationProvider().getOrganizationName());			
		organization.addVerifyListener(new AlphabeticV());
		organization.setLayoutData(gd);
		organization.addTooltip(XSDtooltip.find("contactType.organizationName"));
		
		Label l5 = new Label(container, SWT.NULL);
		phone = new TextExt(container, SWT.BORDER | SWT.SINGLE);
		//mandatory.add(phone);
		l5.setText("Phone number");
		phone.setText(app.getApplication().getApplicationProvider().getPhone());			
		phone.addVerifyListener(new PhoneV());
		phone.setLayoutData(gd);
		phone.addTooltip(XSDtooltip.find("contactType.phone"));
		
		Label l6 = new Label(container, SWT.NULL);
		address = new TextExt(container, SWT.BORDER | SWT.SINGLE);
		//mandatory.add(address);
		l6.setText("Street address");
		address.setText(app.getApplication().getApplicationProvider().getStreetAddress());		
		address.addVerifyListener(new AlphabeticV());
		address.setLayoutData(gd);
		address.addTooltip(XSDtooltip.find("contactType.streetAddress"));
		
		/*
		Label l7 = new Label(container, SWT.NULL);
		web = new TextExt(container, SWT.BORDER | SWT.SINGLE);
		//mandatory.add(web);
		l7.setText("Website");
		web.setText(app.getApplication().getApplicationProvider().getWebAddress().toString());			
		web.addVerifyListener(new UriV());
		web.setLayoutData(gd);
		web.addTooltip(XSDtooltip.find("contactType.webAddress"));
		*/
		
		
		Label l8 = new Label(container, SWT.NULL);
		othChNm1 = new TextExt(container, SWT.BORDER | SWT.SINGLE);
		//mandatory.add(web);
		l8.setText("Other contact #1 - Identifier (tel., e-mail, ...)");
		//othChNm1.setText("");			
		//othChNm1.addVerifyListener(new AlphabeticV());
		othChNm1.setLayoutData(gd);
		othChNm1.addTooltip(XSDtooltip.find("otherChannel.channelName"));
		
		Label l9 = new Label(container, SWT.NULL);
		othChnDtl1 = new TextExt(container, SWT.BORDER | SWT.SINGLE);
		//mandatory.add(web);
		l9.setText("Other contact #1 - Details (tel. number, e-mail address, ...)");
		//othChnDtl1.setText("");			
		//othChnDtl1.addVerifyListener(new AlphabeticV());
		othChnDtl1.setLayoutData(gd);
		othChnDtl1.addTooltip(XSDtooltip.find("otherChannel.channelDetails"));

		Label l10 = new Label(container, SWT.NULL);
		othChNm2 = new TextExt(container, SWT.BORDER | SWT.SINGLE);
		//mandatory.add(web);
		l10.setText("Other contact #2 - Identifier (fax, other...)");
		//othChNm2.setText("");			
		//othChNm2.addVerifyListener(new AlphabeticV());
		othChNm2.setLayoutData(gd);
		othChNm2.addTooltip(XSDtooltip.find("otherChannel.channelName"));
		
		Label l11 = new Label(container, SWT.NULL);
		othChnDtl2 = new TextExt(container, SWT.BORDER | SWT.SINGLE);
		//mandatory.add(web);
		l11.setText("Other contact #2 - Details (fax number, other...)");
		//othChnDtl2.setText("");			
		//othChnDtl2.addVerifyListener(new AlphabeticV());
		othChnDtl2.setLayoutData(gd);
		othChnDtl2.addTooltip(XSDtooltip.find("otherChannel.channelDetails"));

		certificate.addKeyListener(new QL() {

			@Override
			public void keyReleased(KeyEvent e) {
				//				try{
				//					//app.getApplication().getApplicationProvider().setCertificate(URI.create(removeBlanks(certificate.getText())));
				//				}
				//				catch(Exception ex){
				//					ex.printStackTrace();
				//				}				
			}
		});
		person.addKeyListener(new QL() {

			@Override
			public void keyReleased(KeyEvent e) {
				app.getApplication().getApplicationProvider().setContactPerson(person.getText());				
			}
		});	
		email.addKeyListener(new QL() {

			@Override
			public void keyPressed(KeyEvent e) {
				//setPageComplete(validate());
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				FontData[] fD = email.getFont().getFontData();
				
				if(email.getText().trim().length()>0 &&!email.getText().matches(EMAIL_PATTERN)){
					fD[0].setStyle(SWT.BOLD);
					email.setFont(new Font(container.getDisplay(), fD[0]));		
					email.setBackground(new Color(null, 255, 128, 128));
					setPageComplete(false);
				} else {
					fD[0].setStyle(SWT.NORMAL);
					email.setFont(new Font(container.getDisplay(), fD[0]));		
					email.setBackground(new Color(null, 255, 255, 255));
					setPageComplete(validate());
				}

				app.getApplication().getApplicationProvider().setEmail(email.getText());		
			}
		});	
		organization.addKeyListener(new QL() {

			@Override
			public void keyReleased(KeyEvent e) {
				app.getApplication().getApplicationProvider().setOrganizationName(organization.getText());				
			}
		});		
		phone.addKeyListener(new QL() {

			@Override
			public void keyReleased(KeyEvent e) {
				app.getApplication().getApplicationProvider().setPhone(phone.getText());				
			}
		});
		address.addKeyListener(new QL() {

			@Override
			public void keyReleased(KeyEvent e) {
				app.getApplication().getApplicationProvider().setStreetAddress(address.getText());				
			}
		});
		/*
		web.addKeyListener(new QL() {

			@Override
			public void keyReleased(KeyEvent e) {
				//				try{
				//					// app.getApplication().getApplicationProvider().setWebAddress(URI.create(removeBlanks(web.getText())));
				//				}
				//				catch(Exception ex){
				//					ex.printStackTrace();
				//				}				
			}
		});
		*/
		othChNm1.addKeyListener(new QL() {

			@Override
			public void keyReleased(KeyEvent e) {
				otherContacts(1);
			}
		});
		othChnDtl1.addKeyListener(new QL() {

			@Override
			public void keyReleased(KeyEvent e) {
				otherContacts(2);
			}
		});
		othChNm2.addKeyListener(new QL() {

			@Override
			public void keyReleased(KeyEvent e) {
				otherContacts(3);
			}
		});
		othChnDtl2.addKeyListener(new QL() {

			@Override
			public void keyReleased(KeyEvent e) {
				otherContacts(4);				
			}
		});	
		
		loadDefaultValues();
		setPageComplete(validate());
		
	}

	private void otherContacts(int choose){

		switch (choose) {
		case 1:
			//1.1
			if(othChNm1.getText().isEmpty() && othChnDtl1.getText().isEmpty()){
				mandatory.remove(othChNm1);
				mandatory.remove(othChnDtl1);
			}
			else{
				if(mandatory(othChNm1))
					mandatory.add(othChNm1);
				if(mandatory(othChnDtl1))
					mandatory.add(othChnDtl1);
			}
			break;

		case 2:
			//1.2
			if(othChnDtl1.getText().isEmpty() && othChNm1.getText().isEmpty()){
				mandatory.remove(othChnDtl1);
				mandatory.remove(othChNm1);					
			}
			else{
				if(mandatory(othChNm1))
					mandatory.add(othChNm1);
				if(mandatory(othChnDtl1))
					mandatory.add(othChnDtl1);
			}
			break;

		case 3:
			//2.1
			if(othChNm2.getText().isEmpty() && othChnDtl2.getText().isEmpty()){
				mandatory.remove(othChNm2);
				mandatory.remove(othChnDtl2);
			}
			else{
				if(mandatory(othChNm2))
					mandatory.add(othChNm2);
				if(mandatory(othChnDtl2))
					mandatory.add(othChnDtl2);
			}
			break;

		case 4: 
			//2.2
			if(othChnDtl2.getText().isEmpty() && othChNm2.getText().isEmpty()){
				mandatory.remove(othChnDtl2);
				mandatory.remove(othChNm2);
			}
			else{
				if(mandatory(othChNm2))
					mandatory.add(othChNm2);
				if(mandatory(othChnDtl2))
					mandatory.add(othChnDtl2);
			}
			break;

		default:
			break;
		}
		
		setPageComplete(validate());
	}

	private boolean mandatory(Object o){
		for(int i = 0; i < mandatory.size(); i++)
			if(mandatory.get(i).equals(o))
				return false;

		return true;
	}

	private void loadDefaultValues() {
		if ( app.getApplication() != null ) {
			
			if(app.getApplication().getApplicationProvider().getOtherChannels().size() > 0){
					OtherChannel och = app.getApplication().getApplicationProvider().getOtherChannels().get(0);
					othChNm1.setText(och.getChannelName());
					othChnDtl1.setText(och.getChannelDetails());
					
					DefaultLogger.getInstance().log(och.toString());
					DefaultLogger.getInstance().log(och.getChannelName());
					DefaultLogger.getInstance().log(och.getChannelDetails());
			}
			
			if(app.getApplication().getApplicationProvider().getOtherChannels().size() > 1){
				OtherChannel och = app.getApplication().getApplicationProvider().getOtherChannels().get(1);
				othChNm2.setText(och.getChannelName());
				othChnDtl2.setText(och.getChannelDetails());
				
				DefaultLogger.getInstance().log(och.toString());
				DefaultLogger.getInstance().log(och.getChannelName());
				DefaultLogger.getInstance().log(och.getChannelDetails());
			}
		} 
	} 
	
	@Override
	public boolean nextPressed(){

		try{
			if(!email.getText().matches(EMAIL_PATTERN)){
				return false;
			}			

			OtherChannel oc = null;
			if(!othChNm1.getText().isEmpty() && !othChnDtl1.getText().isEmpty()) {
				if ( app.getApplication().getApplicationProvider().getOtherChannels().size() > 0 ) {
					oc = app.getApplication().getApplicationProvider().getOtherChannels().get(0);
				} else {
					oc = new OtherChannel();
					app.getApplication().getApplicationProvider().getOtherChannels().add(oc);
				}
				oc.setChannelName(othChNm1.getText());
				oc.setChannelDetails(othChnDtl1.getText());
			}

			if(!othChNm2.getText().isEmpty() && !othChnDtl2.getText().isEmpty()) {
				if ( app.getApplication().getApplicationProvider().getOtherChannels().size() > 1 ) {
					oc = app.getApplication().getApplicationProvider().getOtherChannels().get(1);
				} else {
					oc = new OtherChannel();
					app.getApplication().getApplicationProvider().getOtherChannels().add(oc);
				}
				oc.setChannelName(othChNm2.getText());
				oc.setChannelDetails(othChnDtl2.getText());
			}

			if(!certificate.getText().isEmpty()){
				app.getApplication().getApplicationProvider().setCertificate(URI.create(removeBlanks(certificate.getText())));
			}
			
			/*
			if(web.getText() != null && !web.getText().isEmpty())
				app.getApplication().getApplicationProvider().setWebAddress(URI.create(removeBlanks(web.getText())));
			*/
			
			//serializeMPA();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}

		return true;
	}
}