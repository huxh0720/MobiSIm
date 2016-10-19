package mobilesim.core;

import java.util.*;

import mobilesim.console.*;

/**
 * The list of data fields is a large hash of name / value pairs augmented with the 
 * notion of time as well as functionality for selectively copying / updating information
 * as processed by the mobile agent. The key is the field name (which must be unique)
 * and the value may range from a double precision floating point to a string or various 
 * other types.
 * 
 * The most typical type is the DATAFIELD_DOUBLE whose value is a double precision floating
 * point.
 * 
 * @author Striegel
 *
 */
public class ListDataFields extends Hashtable<String, DataField> {
	// Definitions for the selection of type for the contained information
	public static final int		DATAFIELD_DOUBLE = 1;
	public static final int		DATAFIELD_STRING = 2;
	
	// The time of the last update
	Double	m_fLastUpdate;
	
	/**
	 * Default constructor
	 */
	public ListDataFields ()
	{
		m_fLastUpdate = Double.MIN_VALUE;
	}
	
	/**
	 * Set the time of the last update
	 * 
	 * @param fTime the new value (in seconds) for the last update
	 */
	public void setLastUpdate (Double fTime)
	{
		m_fLastUpdate = fTime;
	}
	
	/**
	 * Retrieve the time (in seconds) of the last update
	 * @return
	 */
	public Double getLastUpdate ()
	{
		return m_fLastUpdate;
	}
	
	// Other candidates
	// 	Fields that can have children (though that gets crazy performance-wise)
	// 	JSON-like fields
	
	/**
	 * Instantiate a field with a particular floating point value
	 * 
	 * @param sName		The name of the field
	 * @param fValue	The value for the field (Double)  
	 * @return 			True if successful, false otherwise
	 */
	public boolean instantiateFieldWithValue (String sName, Double fValue)
	{
		DataField		theField;
		
		theField = instantiateField(sName, DATAFIELD_DOUBLE);
		
		if(theField == null)
		{
			return false;
		}
		else
		{
			theField.setValue(fValue);
			return true;
		}
	}
	
	/**
	 * The instantiatedField function creates the field in the list with the
	 * field name and type specified
	 * 
	 * @param sName	The name of the field to create
	 * @param nType	The type of filed to create that is publicly defined as part of class
	 * 
	 * @return	Valid reference if successful, null otherwise
	 */
	public DataField instantiateField (String sName, int nType)
	{
		// the key must be unique
		if(this.containsKey(sName))
		{
			DebugLogger.theLogger.logInfo_Error("** Error: cannot instantiate field in ListDataField as the field has already been defined.");
			DebugLogger.theLogger.logInfo_Error("*  Filed name was " + sName + ", type was " + nType);
			return null;
		}
		
		// create the appropriate derived data field based on the requested type
		switch(nType)
		{
			case DATAFIELD_DOUBLE:
				DataFieldDouble theFieldD;
				theFieldD = new DataFieldDouble();
				theFieldD.setName(sName);
				this.put(sName, theFieldD);
				return theFieldD;
				
			case DATAFIELD_STRING:
				DataFieldString theFieldS;
				theFieldS = new DataFieldString();
				theFieldS.setName(sName);
				this.put(sName, theFieldS);
				return theFieldS;
				
			default:
				DebugLogger.theLogger.logInfo_Error("** Error: Unknown type requested for instantiation in the ListDataFields object");
				DebugLogger.theLogger.logInfo_Error("*  Field Name was " + sName + ", type was " + nType);
				return null;
		}
	}
	
	/**
	 * Process an update for an individual field
	 * 
	 * @param sField	The name of the field to map from the data update
	 * @param theUpdate	The update from which to draw the field from
	 * @return	True if successful, false otherwise
	 */
	public boolean processUpdate (String sField, EventDataUpdate theUpdate)
	{
	    DataField		theField;
		
		if(!this.containsKey(sField))
		{
			return false;
		}
		else
		{
			theField = this.get(sField);
			if(theField.setValue(theUpdate.getFieldValue(sField)))
			{
				theField.markUpdate(theUpdate.getTime());	
				setLastUpdate(theUpdate.getTime());
				return true;
			}
			else
			{
				return false;
			}
		}
	}
	
	/**
	 * Compute a delta value for the update versus these respective changes. Generally, the
	 * time of the update should be more than our current values. Both differences with respect to
	 * update time (versus the update event) and the fields themselves are computed. All fields that
	 * are present in both the update as well as overall list (this object) are included in this 
	 * list (essentially a subset).
	 * 
	 * @param theUpdata The data update that occurs as part of an event
	 * @return A newly populated list of data fields preserving type and contained in both this object and the update
	 */
	public ListDataFields computeChanges (EventDataUpdate theUpdate)
	{
		ListDataFields	theFieldChanges;
		theFieldChanges = new ListDataFields();
		
		// Get the set of fields and iterate through them
		Set<String> theFields;
		theFields = theUpdate.getFieldNames();
		
		// Note that we can't do dynamic field creation as we would not know that type it
		// is, for now we just silently eat the fields;
		
		for(String sField : theFields)
		{
			if(!this.containsKey(sField))
			{
				continue;
			}
			else
			{
				DataField	theField;
				DataField	theComparison;
				
				theField = this.get(sField);
				theComparison = theField.computeDifference(theUpdate.getFieldValue(sField), theUpdate.getTime());
				
				if(theComparison != null)
				{
					theFieldChanges.put(sField, theComparison);
				}
			}
		}
		
		// Set the time for the newly derived list
		theFieldChanges.setLastUpdate(theUpdate.getTime() - this.getLastUpdate());
		
		return theFieldChanges;
	}
	
	/**
	 * Copy all of the fields contained within the update into our list of fields. Note that
	 * each field must be defined a priori in order to ensure that types are set correctly for
	 * the respective field.
	 * 
	 * @param theUpdate The field with the various data field updates
	 */
	public void copyAllFields (EventDataUpdate theUpdate)
	{
		// Get the set of fields and iterate through them
		Set<String>	  theFields;

		theFields = theUpdate.getFieldNames();

		// Note that we can't do dynamic field creation as we would not know what type it
		// is, for now we just silently eat the fields

		setLastUpdate(theUpdate.getTime());
				
		for(String sField : theFields)
		{
			if(!this.containsKey(sField))
			{
				continue;
			}
			else
			{
				DataField		theField;
				
				theField = this.get(sField);		
				if (theField.setValue(theUpdate.getFieldValue(sField)))
				{
					theField.markUpdate(theUpdate.getTime());
				}
			}
		}
	}
	
	public void dumpInfoToConsole ()
	{
		System.out.println("List of Data Fields with " + this.size() + " fields updated at " + m_fLastUpdate);
		
		Set<String>	theKeys;
		theKeys = this.keySet();
		
		for(String sField : theKeys)
		{
			System.out.println("   " + sField + ": " + this.get(sField).getValueString());
		}
	}
	
	/**
	 * Copy the value of one data field to another assuming compatible types (or at least enough to fake it)
	 * @param sTarget
	 * @param sSource
	 * @return
	 */
	public boolean copyFieldValue (String sTarget, String sSource)
	{
		DataField	theTarget;
		DataField	theSource;
		
		theTarget = this.get(sTarget);
		theSource = this.get(sSource);
		
		if(theTarget == null || theSource == null)
		{
			System.err.println("* Error: Unable to find field value of " + sTarget + " vs. " + sSource);
			return false;
		}
		
		theTarget.setValue(theSource.getValueString());
		return true;
	}
	
}
