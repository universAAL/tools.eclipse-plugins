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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.universaal.tools.packaging.tool.api.Page;
import org.universaal.tools.packaging.tool.impl.PageImpl;
import org.universaal.tools.packaging.tool.parts.LogicalCriteria;
import org.universaal.tools.packaging.tool.parts.LogicalRelation;
import org.universaal.tools.packaging.tool.parts.Requirement;
import org.universaal.tools.packaging.tool.parts.RequirementsGroup;
import org.universaal.tools.packaging.tool.parts.SingleRequirement;
import org.universaal.tools.packaging.tool.validators.AlphabeticV;

/**
 * 
 * @author <a href="mailto:manlio.bacco@isti.cnr.it">Manlio Bacco</a>
 * @author <a href="mailto:stefano.lenzi@isti.cnr.it">Stefano Lenzi</a>
 * @author <a href="mailto:federico.volpini@isti.cnr.it">Federico Volpini</a>
 * @version $LastChangedRevision$ ( $LastChangedDate$ )
 */

public class PagePartPR extends PageImpl {

	private List<String> reqs, vals, logicalCriteria, logicalRelations;
	private TextExt req1, req2, req3, req4, req5;
	private Text val1, val2, val3, val4, val5;
	private Combo c1, c2, c3, c4, c5, c01, c12, c23, c34, c45, c56;

	private int partNumber;

	private int offset;
	private boolean moreRequirementsInNextPage;
	private LogicalRelation lrFromPreviousPage;
	private SingleRequirement srFromPreviousPage;
	private Map<Requirement, REQ_STATE> thisReqsPage;

	protected PagePartPR(String pageName, int pn, int offset, LogicalRelation lrFromPreviousPage, SingleRequirement srFromPreviousPage) {
		super(pageName, "Part "+(pn+1)+"/"+GUI.getInstance().getPartsCount()+
				" - Specify requirements of this part of your Application");
		this.partNumber = pn;

		this.offset = offset;
		this.moreRequirementsInNextPage = false;
		this.lrFromPreviousPage = lrFromPreviousPage;	
		this.srFromPreviousPage = srFromPreviousPage;

		this.thisReqsPage = new HashMap<Requirement, REQ_STATE>();
	}

	public void createControl(Composite parent) {

		container = new Composite(parent, SWT.NULL);
		setControl(container);		

		GridLayout layout = new GridLayout();
		container.setLayout(layout);

		layout.numColumns = 3;
		gd = new GridData(GridData.FILL_HORIZONTAL);

		reqs = new ArrayList<String>();
		vals = new ArrayList<String>();
		logicalCriteria = new ArrayList<String>();
		logicalRelations = new ArrayList<String>();

		List<Requirement> list = app.getAppParts().get(partNumber).getPartRequirements();

		for(int i = offset; (i < list.size() && i < offset+5); i++){

			if(list.get(i).isSingleReq()){
				reqs.add(list.get(i).getSingleRequirement().getRequirementName().toString());
				vals.add(list.get(i).getSingleRequirement().getRequirementValue().toString());
				logicalCriteria.add(list.get(i).getSingleRequirement().getRequirementCriteria().toString());

				logicalRelations.add(LogicalRelation.none.toString());
			}
			else{
				reqs.add(list.get(i).getRequirementGroup().getReq1().getRequirementName().toString());
				vals.add(list.get(i).getRequirementGroup().getReq1().getRequirementValue().toString());
				logicalCriteria.add(list.get(i).getRequirementGroup().getReq1().getRequirementCriteria().toString());

				reqs.add(list.get(i).getRequirementGroup().getReq2().getRequirementName().toString());
				vals.add(list.get(i).getRequirementGroup().getReq2().getRequirementValue().toString());
				logicalCriteria.add(list.get(i).getRequirementGroup().getReq2().getRequirementCriteria().toString());

				logicalRelations.add(list.get(i).getRequirementGroup().getRelation().toString());
			}
		}

		if((reqs.isEmpty() && vals.isEmpty()) || (reqs.size() < 5)) // create five elements for current page if you can't read them from current MPA
			for(int i = reqs.size(); i < 5; i++){
				reqs.add("");
				vals.add("");
				logicalCriteria.add(LogicalCriteria.equal.toString());
			}

		if(logicalRelations.isEmpty() || logicalRelations.size() < 5)
			for(int i = logicalRelations.size(); i < 5; i++)
				logicalRelations.add(LogicalRelation.none.toString());

		Label l1 = new Label(container, SWT.NULL);
		l1.setText("Requirement name");

		Label l2 = new Label(container, SWT.NULL);
		l2.setText("Relation");

		Label l3 = new Label(container, SWT.NULL);
		l3.setText("Requirement value");

		if(offset != 0){
			// consider previous page logical relation
			Label l0 = new Label(container, SWT.NULL);
			c01 = new Combo(container, SWT.READ_ONLY);
			l0.setText("Relation between rule #"+(offset)+" (previous page) and #"+(1+offset));
			for(int i = 0; i < LogicalRelation.values().length; i++)
				c01.add(LogicalRelation.values()[i].toString());
			c01.setText(this.lrFromPreviousPage.toString());			
			c01.setLayoutData(gd);

			Label empty0 = new Label(container, SWT.NULL);
			empty0.setText("");
		}

		req1 = new TextExt(container, SWT.BORDER | SWT.SINGLE);
		req1.setText("");	
		req1.addVerifyListener(new AlphabeticV());
		req1.setLayoutData(gd);	
		req1.addTooltip(Tooltips.REQUIREMENT_TOOLTIP);
		
		//LogicalCriteria
		c1 = new Combo(container, SWT.READ_ONLY);
		for(int i = 0; i < LogicalCriteria.values().length; i++)
			c1.add(LogicalCriteria.values()[i].toString());
		c1.setText(LogicalCriteria.equal.toString());
		c1.setLayoutData(gd);

		val1 = new Text(container, SWT.BORDER | SWT.SINGLE);
		val1.setText("");
		val1.addVerifyListener(new AlphabeticV());
		val1.setLayoutData(gd);


		//LogicalRelation
		Label l4 = new Label(container, SWT.NULL);
		c12 = new Combo(container, SWT.READ_ONLY);
		l4.setText("Relation between rule #"+(1+offset)+" and #"+(2+offset));
		for(int i = 0; i < LogicalRelation.values().length; i++)
			c12.add(LogicalRelation.values()[i].toString());
		c12.setText(LogicalRelation.none.toString());
		c12.setLayoutData(gd);

		Label empty1 = new Label(container, SWT.NULL);
		empty1.setText("");


		req2 = new TextExt(container, SWT.BORDER | SWT.SINGLE);
		req2.setText("");
		req2.addVerifyListener(new AlphabeticV());
		req2.setLayoutData(gd);
		req2.addTooltip(Tooltips.REQUIREMENT_TOOLTIP);
		
		c2 = new Combo(container, SWT.READ_ONLY);
		for(int i = 0; i < LogicalCriteria.values().length; i++)
			c2.add(LogicalCriteria.values()[i].toString());
		c2.setText(LogicalCriteria.equal.toString());
		c2.setLayoutData(gd);

		val2 = new Text(container, SWT.BORDER | SWT.SINGLE);
		val2.setText("");
		val2.addVerifyListener(new AlphabeticV());
		val2.setLayoutData(gd);


		//LogicalRelation
		Label l5 = new Label(container, SWT.NULL);
		c23 = new Combo(container, SWT.READ_ONLY);
		l5.setText("Relation between rule #"+(2+offset)+" and #"+(3+offset));
		for(int i = 0; i < LogicalRelation.values().length; i++)
			c23.add(LogicalRelation.values()[i].toString());
		c23.setText(LogicalRelation.none.toString());
		c23.setLayoutData(gd);

		Label empty2 = new Label(container, SWT.NULL);
		empty2.setText("");


		req3 = new TextExt(container, SWT.BORDER | SWT.SINGLE);
		req3.setText("");
		req3.addVerifyListener(new AlphabeticV());
		req3.setLayoutData(gd);	
		req3.addTooltip(Tooltips.REQUIREMENT_TOOLTIP);
				
		c3 = new Combo(container, SWT.READ_ONLY);
		for(int i = 0; i < LogicalCriteria.values().length; i++)
			c3.add(LogicalCriteria.values()[i].toString());
		c3.setText(LogicalCriteria.equal.toString());
		c3.setLayoutData(gd);

		val3 = new Text(container, SWT.BORDER | SWT.SINGLE);
		val3.setText("");	
		val3.addVerifyListener(new AlphabeticV());
		val3.setLayoutData(gd);


		//LogicalRelation
		Label l6 = new Label(container, SWT.NULL);
		c34 = new Combo(container, SWT.READ_ONLY);
		l6.setText("Relation between rule #"+(3+offset)+" and #"+(4+offset));
		for(int i = 0; i < LogicalRelation.values().length; i++)
			c34.add(LogicalRelation.values()[i].toString());
		c34.setText(LogicalRelation.none.toString());
		c34.setLayoutData(gd);

		Label empty3 = new Label(container, SWT.NULL);
		empty3.setText("");


		req4 = new TextExt(container, SWT.BORDER | SWT.SINGLE);
		req4.setText("");
		req4.addVerifyListener(new AlphabeticV());
		req4.setLayoutData(gd);	
		req4.addTooltip(Tooltips.REQUIREMENT_TOOLTIP);
		
		//LogicalCriteria
		c4 = new Combo(container, SWT.READ_ONLY);
		for(int i = 0; i < LogicalCriteria.values().length; i++)
			c4.add(LogicalCriteria.values()[i].toString());
		c4.setText(LogicalCriteria.equal.toString());
		c4.setLayoutData(gd);

		val4 = new Text(container, SWT.BORDER | SWT.SINGLE);
		val4.setText("");
		val4.addVerifyListener(new AlphabeticV());
		val4.setLayoutData(gd);


		//LogicalRelation
		Label l7 = new Label(container, SWT.NULL);
		c45 = new Combo(container, SWT.READ_ONLY);
		l7.setText("Relation between rule #"+(4+offset)+" and #"+(5+offset));
		for(int i = 0; i < LogicalRelation.values().length; i++)
			c45.add(LogicalRelation.values()[i].toString());
		c45.setText(LogicalRelation.none.toString());
		c45.setLayoutData(gd);

		Label empty4 = new Label(container, SWT.NULL);
		empty4.setText("");

		req5 = new TextExt(container, SWT.BORDER | SWT.SINGLE);
		req5.setText("");
		req5.addVerifyListener(new AlphabeticV());
		req5.setLayoutData(gd);	
		req5.addTooltip(Tooltips.REQUIREMENT_TOOLTIP);
		
		//LogicalCriteria
		c5 = new Combo(container, SWT.READ_ONLY);
		for(int i = 0; i < LogicalCriteria.values().length; i++)
			c5.add(LogicalCriteria.values()[i].toString());
		c5.setText(LogicalCriteria.equal.toString());
		c5.setLayoutData(gd);

		val5 = new Text(container, SWT.BORDER | SWT.SINGLE);
		val5.setText("");
		val5.addVerifyListener(new AlphabeticV());
		val5.setLayoutData(gd);

		Label l8 = new Label(container, SWT.NULL);
		l8.setText("Relation between rule #"+(5+offset)+" and #"+(6+offset)+" (next page)");
		c56 = new Combo(container, SWT.READ_ONLY);

		for(int i = 0; i < LogicalRelation.values().length; i++)
			c56.add(LogicalRelation.values()[i].toString());
		c56.setText(LogicalRelation.none.toString());
		c56.setLayoutData(gd);
		final Button b = new Button(container, SWT.PUSH);
		b.setText("Click to add more requirements");
		b.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				moreRequirementsInNextPage = !moreRequirementsInNextPage;
				if(!moreRequirementsInNextPage){
					b.setText("Click to add more requirements");
				}
				else{
					b.setText("No more requirements");
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		b.setLayoutData(gd);	
		

		final Button b1 = new Button(container, SWT.PUSH);
		b1.setText("Clear form");
		b1.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				clearForm();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		b1.setLayoutData(gd);

		req1.addKeyListener(new FullListener());
		req2.addKeyListener(new FullListener());
		req3.addKeyListener(new FullListener());
		req4.addKeyListener(new FullListener());
		req5.addKeyListener(new FullListener());
		val1.addKeyListener(new FullListener());
		val2.addKeyListener(new FullListener());
		val3.addKeyListener(new FullListener());
		val4.addKeyListener(new FullListener());
		val5.addKeyListener(new FullListener());
		c1.addKeyListener(new FullListener());
		c2.addKeyListener(new FullListener());
		c3.addKeyListener(new FullListener());
		c4.addKeyListener(new FullListener());
		c5.addKeyListener(new FullListener());
		c12.addKeyListener(new FullListener());
		c23.addKeyListener(new FullListener());
		c34.addKeyListener(new FullListener());
		c45.addKeyListener(new FullListener());
		c56.addKeyListener(new FullListener());

		setPageComplete(validate());
	}

	private void clearForm(){
		req1.setText("");
		req2.setText("");
		req3.setText("");
		req4.setText("");
		req5.setText("");
		val1.setText("");
		val2.setText("");
		val3.setText("");
		val4.setText("");
		val5.setText("");
		c1.setText(LogicalCriteria.equal.toString());
		c2.setText(LogicalCriteria.equal.toString());
		c3.setText(LogicalCriteria.equal.toString());
		c4.setText(LogicalCriteria.equal.toString());
		c5.setText(LogicalCriteria.equal.toString());
		c12.setText(LogicalRelation.none.toString());
		c23.setText(LogicalRelation.none.toString());
		c34.setText(LogicalRelation.none.toString());
		c45.setText(LogicalRelation.none.toString());
		c56.setText(LogicalRelation.none.toString());
}
	
	@Override
	public void setVisible(boolean visible){
		super.setVisible(visible);
		//System.out.println("Page 4 - reqs size: "+app.getAppRequirements().getRequirementsList().size());
		if(visible && app.getAppRequirements().getRequirementsList().size() == 0){
			clearForm();
		} 
	}
	@Override
	public boolean nextPressed() {

		//debugPrint("b handleOldReqs");
		handleOldReqs();

		if(offset == 0){
			// first page of requirements
			if(c12.getText().equals(LogicalRelation.none.toString())){
				if(isValid(req1, val1, c1))
					single(req1, val1, LogicalCriteria.valueOf(c1.getText()));
				if(isValid(req2, val2, c2) && c23.getText().equals(LogicalRelation.none.toString()))
					single(req2, val2, LogicalCriteria.valueOf(c2.getText()));
			}			
			else{
				if(isValid(req1, val1, c1) && isValid(req2, val2, c2) && isValid(c12))
					group(LogicalRelation.valueOf(c12.getText()), req1, val1, LogicalCriteria.valueOf(c1.getText()), req2, val2, LogicalCriteria.valueOf(c2.getText()));
			}
			if(c23.getText().equals(LogicalRelation.none.toString())){
				//single(req2, val2, LogicalCriteria.valueOf(c2.getText()));
				if(isValid(req3, val3, c3) && c34.getText().equals(LogicalRelation.none.toString()))
					single(req3, val3, LogicalCriteria.valueOf(c3.getText()));
			}			
			else{
				if(isValid(req2, val2, c2) && isValid(req3,  val3, c3) && isValid(c23))
					group(LogicalRelation.valueOf(c23.getText()), req2, val2, LogicalCriteria.valueOf(c2.getText()), req3, val3, LogicalCriteria.valueOf(c3.getText()));
			}
			if(c34.getText().equals(LogicalRelation.none.toString())){
				//single(req2, val2, LogicalCriteria.valueOf(c2.getText()));
				if(isValid(req4, val4, c4) && c45.getText().equals(LogicalRelation.none.toString()))
					single(req4, val4, LogicalCriteria.valueOf(c4.getText()));
			}			
			else{
				if(isValid(req3, val3, c3) && isValid(req4, val4, c4) && isValid(c34))
					group(LogicalRelation.valueOf(c34.getText()), req3, val3, LogicalCriteria.valueOf(c3.getText()), req4, val4, LogicalCriteria.valueOf(c4.getText()));
			}
			if(c45.getText().equals(LogicalRelation.none.toString())){
				//single(req2, val2, LogicalCriteria.valueOf(c2.getText()));
				if(isValid(req5, val5, c5) && 
						(!moreRequirementsInNextPage || // no more pages
								(c56.getText().equals(LogicalRelation.none.toString()) && moreRequirementsInNextPage))) //new page and NONE
					single(req5, val5, LogicalCriteria.valueOf(c5.getText()));
			}			
			else{
				if(isValid(req4, val4, c4) && isValid(req5, val5, c5) && isValid(c45))
					group(LogicalRelation.valueOf(c45.getText()), req4, val4, LogicalCriteria.valueOf(c4.getText()), req5, val5, LogicalCriteria.valueOf(c5.getText()));
			}
		}
		else{
			// new page of requirements
			if(c01.getText().equals(LogicalRelation.none.toString())){
				// nothing to do - already added as single requirement in previous page	
			}			
			else{
				// get last requirement inserted
				SingleRequirement last = this.srFromPreviousPage;

				if(isValid(req1, val1, c1))
					group(LogicalRelation.valueOf(c01.getText()), last.getRequirementName(), last.getRequirementValue(), LogicalCriteria.valueOf(c1.getText()), 
							req1.getText(), val1.getText(), LogicalCriteria.valueOf(c1.getText()));

			}
			if(c12.getText().equals(LogicalRelation.none.toString())){
				if(isValid(req1, val1, c1) && c01.getText().equals(LogicalRelation.none.toString()))
					single(req1, val1, LogicalCriteria.valueOf(c1.getText()));
				if(isValid(req2, val2, c2) && c23.getText().equals(LogicalRelation.none.toString()))
					single(req2, val2, LogicalCriteria.valueOf(c2.getText()));
			}			
			else{
				if(isValid(req1, val1, c1) && isValid(req2, val2, c2) && isValid(c12))
					group(LogicalRelation.valueOf(c12.getText()), req1, val1, LogicalCriteria.valueOf(c1.getText()), req2, val2, LogicalCriteria.valueOf(c2.getText()));
			}
			if(c23.getText().equals(LogicalRelation.none.toString())){
				//single(req2, val2, LogicalCriteria.valueOf(c2.getText()));
				if(isValid(req3, val3, c3) && c34.getText().equals(LogicalRelation.none.toString()))
					single(req3, val3, LogicalCriteria.valueOf(c3.getText()));
			}			
			else{
				if(isValid(req2, val2, c2) && isValid(req3,  val3, c3) && isValid(c23))
					group(LogicalRelation.valueOf(c23.getText()), req2, val2, LogicalCriteria.valueOf(c2.getText()), req3, val3, LogicalCriteria.valueOf(c3.getText()));
			}
			if(c34.getText().equals(LogicalRelation.none.toString())){
				//single(req2, val2, LogicalCriteria.valueOf(c2.getText()));
				if(isValid(req4, val4, c4) && c45.getText().equals(LogicalRelation.none.toString()))
					single(req4, val4, LogicalCriteria.valueOf(c4.getText()));
			}			
			else{
				if(isValid(req3, val3, c3) && isValid(req4, val4, c4) && isValid(c34))
					group(LogicalRelation.valueOf(c34.getText()), req3, val3, LogicalCriteria.valueOf(c3.getText()), req4, val4, LogicalCriteria.valueOf(c4.getText()));
			}
			if(c45.getText().equals(LogicalRelation.none.toString())){
				//single(req2, val2, LogicalCriteria.valueOf(c2.getText()));
				if(isValid(req5, val5, c5) && 
						(!moreRequirementsInNextPage || // no more pages
								(c56.getText().equals(LogicalRelation.none.toString()) && moreRequirementsInNextPage))) //new page and NONE
					single(req5, val5, LogicalCriteria.valueOf(c5.getText()));
			}			
			else{
				if(isValid(req4, val4, c4) && isValid(req5, val5, c5) && isValid(c45))
					group(LogicalRelation.valueOf(c45.getText()), req4, val4, LogicalCriteria.valueOf(c4.getText()), req5, val5, LogicalCriteria.valueOf(c5.getText()));
			}
		}

		if(moreRequirementsInNextPage && !(getNextPage() instanceof PagePartPR)){

			//System.out.println("OtherPartReqs:"+otherPartReqs);
			
			//Integer index = otherPartReqs.get(partNumber);
			int index = otherPartReqs;

			PagePartPR p_req = new PagePartPR(Page.PAGE_PART_PR+" #"+index, 
					partNumber,
					offset+5, 
					LogicalRelation.valueOf(c56.getText()), 
					new SingleRequirement(req5.getText(), val5.getText(), LogicalCriteria.valueOf(c5.getText())));

			p_req.setMPA(multipartApplication);
			p_req.setPercentage(this.getPercentage());
			
			addPageCustom(this, p_req);

			//otherPartReqs.set(partNumber, index+1);
			otherPartReqs++;
		}

		//debugPrint("b handlePreviousToBeDeleted");
		handlePreviousToBeDeleted();

		//debugPrint("b removeModifiedReqs");
		removeModifiedReqs();

		updateMap();

		//debugPrint("b end");

		//serializeMPA();
		return true;
	}

	private void group(LogicalRelation lr, Text req1, Text val1, LogicalCriteria lc1, Text req2, Text val2, LogicalCriteria lc2){

		SingleRequirement r1 = new SingleRequirement(req1.getText(), val1.getText(), lc1);
		SingleRequirement r2 = new SingleRequirement(req2.getText(), val2.getText(), lc2);

		RequirementsGroup r = new RequirementsGroup(r1, r2, lr);
		Requirement rr = new Requirement(r, false);

		if(!alreadyIn(rr))
			app.getAppParts().get(partNumber).getPartRequirements().add(rr);
	}

	private void single(Text req1, Text val1, LogicalCriteria lc1){

		SingleRequirement r = new SingleRequirement(req1.getText(), val1.getText(), lc1);
		Requirement rr = new Requirement(r, false);

		if(!alreadyIn(rr))
			app.getAppParts().get(partNumber).getPartRequirements().add(rr);
	}

	private void group(LogicalRelation lr, String req1, String val1, LogicalCriteria lc1, String req2, String val2, LogicalCriteria lc2){

		SingleRequirement r1 = new SingleRequirement(req1, val1, lc1);
		SingleRequirement r2 = new SingleRequirement(req2, val2, lc2);

		RequirementsGroup r = new RequirementsGroup(r1, r2, lr);
		Requirement rr = new Requirement(r, false);

		if(!alreadyIn(rr))
			app.getAppParts().get(partNumber).getPartRequirements().add(rr);
	}

	private boolean alreadyIn(Requirement r){

		if(r != null){
			for(int i = 0; i < app.getAppParts().get(partNumber).getPartRequirements().size(); i++){
				if(this.app.getAppParts().get(partNumber).getPartRequirements().get(i).equals(r)){

					Set<Entry<Requirement, REQ_STATE>> entryset = this.thisReqsPage.entrySet();
					Iterator<Entry<Requirement, REQ_STATE>> it = entryset.iterator();
					while(it.hasNext()){
						Entry<Requirement, REQ_STATE> current = it.next();

						if(current.getKey().equals(r)){
							current.setValue(REQ_STATE.ALREADY_IN);
							return true;
						}
					}					
				}
			}
		}

		this.thisReqsPage.put(r, REQ_STATE.NEW);
		return false;
	}

	private void handleOldReqs(){

		Set<Entry<Requirement, REQ_STATE>> entryset = this.thisReqsPage.entrySet();
		Iterator<Entry<Requirement, REQ_STATE>> it = entryset.iterator();
		while(it.hasNext()){
			Entry<Requirement, REQ_STATE> current = it.next();
			current.setValue(REQ_STATE.PREVIOUS); 
		}
	}

	private void handlePreviousToBeDeleted(){

		Set<Entry<Requirement, REQ_STATE>> entryset = this.thisReqsPage.entrySet();
		Iterator<Entry<Requirement, REQ_STATE>> it = entryset.iterator();
		while(it.hasNext()){
			Entry<Requirement, REQ_STATE> current = it.next();
			if(current.getValue() == REQ_STATE.PREVIOUS){
				current.setValue(REQ_STATE.TO_BE_DELETED); 
			}
		}
	}

	private void removeModifiedReqs(){

		Set<Entry<Requirement, REQ_STATE>> entryset = this.thisReqsPage.entrySet();
		Iterator<Entry<Requirement, REQ_STATE>> it = entryset.iterator();
		while(it.hasNext()){
			Entry<Requirement, REQ_STATE> current = it.next();

			if(current.getValue() == REQ_STATE.TO_BE_DELETED){

				for(int i = 0; i < this.app.getAppParts().get(partNumber).getPartRequirements().size(); i++){
					if(this.app.getAppParts().get(partNumber).getPartRequirements().get(i).equals(current.getKey())){
						this.app.getAppParts().get(partNumber).getPartRequirements().set(i, null);
					}
				}
			}
		}
	}

	private void updateMap(){

		List<Requirement> removeMe = new ArrayList<Requirement>();

		Set<Entry<Requirement, REQ_STATE>> entryset = this.thisReqsPage.entrySet();
		Iterator<Entry<Requirement, REQ_STATE>> it = entryset.iterator();
		while(it.hasNext()){
			Entry<Requirement, REQ_STATE> current = it.next();
			if(current.getValue() == REQ_STATE.TO_BE_DELETED)
				removeMe.add(current.getKey());
		}

		for(int i = 0; i < removeMe.size(); i++)
			this.thisReqsPage.remove(removeMe.get(i));
	}

	private enum REQ_STATE{
		NEW, PREVIOUS, ALREADY_IN, TO_BE_DELETED
	}
}