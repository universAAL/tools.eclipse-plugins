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
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Content provider for provisioning table.<br/>
 * Expects the input element to be a list of Provisioning URLS's.
 *
 * @author Alin Dreghiciu
 * @since 0.2.0, December 16, 2007
 */
public class ProvisionContentProvider
  //  extends DefaultContentProvider
    implements ITreeContentProvider 
{

	public Object[] getChildren(Object arg0) {
	    return ((ProvisionURL) arg0).getChildren();
	  }

	  public Object getParent(Object arg0) {
	    return ((ProvisionURL) arg0).getParent();
	  }

	  public boolean hasChildren(Object arg0) {
	    Object[] obj = getChildren(arg0);
	    return obj == null ? false : obj.length > 0;
	  }

	  public Object[] getElements(Object arg0) {
	  //  return File.listRoots();
		  if (arg0 instanceof ProvisionURL){			 
		  return getChildren(arg0);
		  }
		  else
		  {
			  ArrayList ar=(ArrayList)arg0;
			  return ar.toArray();
		  }
  }

	  public void dispose() {
	  }

	  public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
	  }


	
	
	
	
    /**
     * Returns an array of ProvisionURL's.
     *
     * @see IStructuredContentProvider#getElements(Object)
     */
//    public Object[] getElements( final Object inputElement )
//    {
//        if( !( inputElement instanceof List ) )
//        {
//            throw new IllegalArgumentException( "Input element must be List of " + ProvisionURL.class.getName() );
//        }
//        //return ( (List) inputElement ).toArray();
//        return new Object[0]; 
//    }

}
