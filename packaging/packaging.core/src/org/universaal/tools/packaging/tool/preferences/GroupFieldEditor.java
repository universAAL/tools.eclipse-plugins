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
package org.universaal.tools.packaging.tool.preferences;

import java.util.Collection;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * This class allows the creation of Group Widgets, inside of the
 * {@link FieldEditorPreferencePage} objects.<br>
 * Please use as following:
 * <ul>
 * <li>for all {@link FieldEditor} use the {@link #getFieldEditorParent()} to get the parent, 
 * creating them instead of {@link FieldEditorPreferencePage#getFieldEditorParent()}.</li>
 * 
 * <li>after creating the need {@link FieldEditor}  add them all to this {@link GroupFieldEditor} with
 * the method {@link #setFieldEditors(Collection)}</li>
 * 
 * <li>the layout of {@link FieldEditorPreferencePage} must be {@link FieldEditorPreferencePage#FLAT} </li>
 * </ul>
 * 
 * @author <a href="mailto:stefano.lenzi@isti.cnr.it">Stefano Lenzi</a>
 * @version $LastChangedRevision$ ( $LastChangedDate$ )
 */
public class GroupFieldEditor extends FieldEditor implements IPropertyChangeListener {

    private String name;
    private Collection<FieldEditor> members;
    private int numcolumns;
    private Group group;
    private Composite parent;
    private IPropertyChangeListener propertyChangeListener;

    /**
     * The gap in pixels between the group-frame and the widgets outside
     */
    private static final int GROUP_PADDING = 5; 

    /**
     * The gap in pixels between the group-frame and the content
     */
    private static final int GROUP_VERTICAL_MARGIN = 5; 

    /**
     * The inside-distance creates a new boolean field editor
     */
    protected GroupFieldEditor() {
    }

    /**
     * Creates a Group of {@link FieldEditor} objects
     * 
     * @param name
     * @param fieldEditorParent
     */
    public GroupFieldEditor(String name, Composite fieldEditorParent) {
	this.name = name;
	//System.out.println(fieldEditorParent.getClass()+" -> "+fieldEditorParent+"@"+fieldEditorParent.hashCode());
	
	this.parent = fieldEditorParent;
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.marginHeight = GROUP_VERTICAL_MARGIN;
	FillLayout fillLayout = new FillLayout();
	fillLayout.marginHeight = GROUP_VERTICAL_MARGIN;
	this.parent.setLayout(fillLayout);

	this.group = new Group(parent, SWT.DEFAULT);
	this.group.setText(this.name);
	
    }

    
    /**
     * The parent for all the FieldEditors inside of this Group.
     * 
     * @return - the parent
     */
    public Composite getFieldEditorParent() {
	return group;
    }

    /**
     * Sets the FieldeditorChildren for this {@link GroupFieldEditor}
     * 
     * @param membersParam
     */
    public void setFieldEditors(Collection membersParam) {
	this.members = membersParam;	
	doFillIntoGrid(getFieldEditorParent(), numcolumns);
    }

    /*
     * (non-Javadoc) Method declared on FieldEditor.
     */
    @Override
    protected void adjustForNumColumns(int numColumns) {
	this.numcolumns = numColumns;
    }

    /*
     * (non-Javadoc) Method declared on FieldEditor.
     */
    @Override
    protected void doFillIntoGrid(Composite parentParam, int numColumns) {

	GridLayout gridLayout = new GridLayout();
	gridLayout.marginLeft = GROUP_PADDING;
	gridLayout.marginRight = GROUP_PADDING;
	gridLayout.marginTop = GROUP_PADDING;
	gridLayout.marginBottom = GROUP_PADDING;
	this.group.setLayout(gridLayout);

	this.parent.layout();
	this.parent.redraw();

	if (members != null) {
	    for (FieldEditor editor : members) {
		editor.fillIntoGrid(getFieldEditorParent(), 2);
		editor.setPropertyChangeListener(this);
	    }
	}
    }

    /*
     * (non-Javadoc) Method declared on FieldEditor. Loads the value from the
     * preference store and sets it to the check box.
     */
    @Override
    protected void doLoad() {
	if (members != null) {
	    for (FieldEditor editor : members) {
		editor.load();
	    }
	}
    }

    /*
     * (non-Javadoc) Method declared on FieldEditor. Loads the default value
     * from the preference store and sets it to the check box.
     */
    @Override
    protected void doLoadDefault() {
	if (members != null) {
	    for (FieldEditor editor : members) {
		editor.loadDefault();
	    }
	}
    }

    /*
     * (non-Javadoc) Method declared on FieldEditor.
     */
    @Override
    protected void doStore() {
	if (members != null) {
	    for (FieldEditor editor : members) {
		editor.store();
	    }
	}
    }

    @Override
    public void store() {
	super.store();
	doStore();
    }

    /*
     * (non-Javadoc) Method declared on FieldEditor.
     */
    @Override
    public int getNumberOfControls() {
	return 1;
    }

    /*
     * (non-Javadoc) Method declared on FieldEditor.
     */
    @Override
    public void setFocus() {
	if (members != null && !members.isEmpty()) {
	    members.iterator().next().setFocus();
	}
    }

    /*
     * @see FieldEditor.setEnabled
     */
    @Override
    public void setEnabled(boolean enabled, Composite parentParam) {
	if (members != null) {
	    for (FieldEditor editor : members) {
		editor.setEnabled(enabled, parentParam);
	    }
	}
    }

    @Override
    public void setPreferenceStore(IPreferenceStore store) {
	super.setPreferenceStore(store);
	if (members != null) {
	    for (FieldEditor editor : members) {
		editor.setPreferenceStore(store);
	    }
	}
    }

    public void setPropertyChangeListener(IPropertyChangeListener listener) {
	this.propertyChangeListener = listener;
    }
    
    public void propertyChange(PropertyChangeEvent event) {
	try {
	    propertyChangeListener.propertyChange(event);
	}catch(Throwable t){
	    t.printStackTrace();	    
	}
    }

}