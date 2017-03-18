/*
 * Copyright 2007 Alin Dreghiciu.
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.cursor.ui;

import java.util.ArrayList;

/**
 * TODO Add JavaDoc
 * 
 * @author Alin Dreghiciu
 * @since 0.2.0, December 16, 2007
 */
public class ProvisionURL extends Model {

	private String m_url;
	private boolean m_selected;
	private boolean m_start;
	private Integer m_startLevel;
	private boolean m_update;
	private ProvisionURL parent;
	private ProvisionURL[] children;
	private static IModelVisitor adder = new Adder();
	protected ArrayList<ProvisionURL> boxes;

	private static class Adder implements IModelVisitor {
		/*
		 * @see ModelVisitorI#visitBoardgame(BoardGame)
		 */

		/*
		 * @see ModelVisitorI#visitBook(MovingBox)
		 */

		/*
		 * @see ModelVisitorI#visitMovingBox(MovingBox)
		 */

		/*
		 * @see ModelVisitorI#visitBoardgame(BoardGame, Object)
		 */

		/*
		 * @see ModelVisitorI#visitMovingBox(MovingBox, Object)
		 */
		public void visitMovingBox(ProvisionURL box, Object argument) {
			((ProvisionURL) argument).addProvisionURL(box);
		}

	}

	protected void addProvisionURL(ProvisionURL box) {
		boxes.add(box);
		box.parent = this;
		fireAdd(box);
	}

	public ProvisionURL() {
		boxes = new ArrayList<ProvisionURL>();
		m_selected = true;
		m_start = true;
		m_update = false;
	}

	public ProvisionURL(final String url, final boolean selected,
			boolean start, final Integer startLevel, final boolean update) {
		m_url = url;
		m_selected = selected;
		m_start = start;
		m_startLevel = startLevel;
		m_update = update;
	}

	public String getUrl() {
		return m_url;
	}

	public void setUrl(String url) {
		this.m_url = url;
	}

	public boolean isSelected() {
		return m_selected;
	}

	public void setSelected(boolean selected) {
		this.m_selected = selected;
	}

	public boolean isStart() {
		return m_start;
	}

	public void setStart(boolean start) {
		this.m_start = start;
	}

	public Integer getStartLevel() {
		return m_startLevel;
	}

	public void setStartLevel(Integer startLevel) {
		this.m_startLevel = startLevel;
	}

	public boolean isUpdate() {
		return m_update;
	}

	public void setUpdate(boolean update) {
		this.m_update = update;
	}

	public ProvisionURL getParent() {
		return parent;
	}

	public void setParent(ProvisionURL parent) {
		this.parent = parent;
	}

	public ProvisionURL[] getChildren() {
		return children;
	}

	public void setChildren(ProvisionURL[] children) {
		this.children = children;
	}

	public void add(Model toAdd) {
		toAdd.accept(adder, this);
	}

	/*
	 * @see Model#accept(ModelVisitorI, Object)
	 */
	public void accept(IModelVisitor visitor, Object passAlongArgument) {
		visitor.visitMovingBox(this, passAlongArgument);
	}

	public boolean equals(ProvisionURL obj) {
		try {
			if (this.getUrl().equals(obj.getUrl())
					&& this.getStartLevel().equals(obj.getStartLevel())
					&& this.isSelected() == (obj.isSelected())
					&& this.isStart() == (obj.isStart())
					&& this.isUpdate() == (obj.isUpdate()))
				return true;
			else
				return false;
		} catch (Exception ex) {
			return false;
		}
	}

}
