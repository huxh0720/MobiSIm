package mobilesim.core;

import java.util.*;

import mobilesim.console.DebugLogger;

/** 
 * A set of data fields have received an update from the instrument that is either based
 * on a periodic timer or a specific trigger
 * 
 * @author Striegel
 */
public class EventDataUpdate extends Event {
	
	// The set of data fields
	Hashtable<String, String> 	m_Fields;	
	int							m_nType;

	// Default / unset data update type. This should throw a warning or error as it has
	// not been appropriately configured
	public static final int 	EVENTDATA_UPDATE_NONE = 0;
	
	// The mobile data status update that contains data tonnage (usage) for the cellular
	// and WiFi adapters
	public static final int 	EVENTDATA_UPDATE_NW_USAGE = 1;

	/** 
	 * Constructor for the EventDataUpdate class that initializes the type to be the 
	 * default (unset) type and to create the core Hashtable that underpins the storage
	 * 
	 */
	public EventDataUpdate ()
	{
		m_Fields = new Hashtable<String, String>();
		m_nType = EVENTDATA_UPDATE_NONE;
	}

	/** 
	 * Get a set containing the list of keys (fields) in this particular data update
	 * 
	 * @return
	 */
	public Set<String> getFieldNames ()
	{
		return m_Fields.keySet();
	}

	/** 
	 * Retrieve a value associated with a specific key
	 * 
	 * @param sName
	 * @return
	 */
	public String getFieldValue (String sName)
	{
		if(m_Fields.containsKey(sName))
		{
			return m_Fields.get(sName);
		}
		else
		{
			return "";
		}
	}
	
	/** 
	 * Set the type for this particular data field update that will in turn be used by the
	 * SimObject to determine what fields to look for
	 * 
	 * @param nType
	 */
	public void setType (int nType)
	{
		m_nType = nType;
	}
	
	/** 
	 * Retrieve the type associated with this particular data update
	 * 
	 * @return
	 */
	public int getType ()
	{
		return m_nType;
	}
	
	/** 
	 * Add a name / value pair to this particular collection of updates. Name / value
	 * pairs are strings with the value then parsed as appropriate by the object that
	 * receives the data update allowing for the use of JSON or other types.
	 * 
	 * @param sName Unique name (within the context of this update)
	 * @param sValue Value to be associated with the name
	 * @return 1 if successful, 0 if it was a duplicate key (but value was the same), -1 if it was a duplicate key but value changed
	 */
	public int addNameValue (String sName, String sValue)
	{
		if(m_Fields.containsKey(sName)) 
		{
			if(m_Fields.get(sName).compareTo(sValue) == 0)
			{
				// TimeCollide-NoChange
				// Silently discard
				return 0;
			}
			else 
			{	// TimeCollide-Change		
                //DebugLogger.theLogger.logInfo_Warning(" Populating an EventDataUpdate that already has the field (" + sName + "), replacing value");
                //DebugLogger.theLogger.logInfo_Warning("    Old value is " + m_Fields.get(sName) + " vs. " + sValue);
                //DebugLogger.theLogger.logInfo_Warning("     t=" + getTime() + "  Object = " + getObject().getName());
				return -1;
			}			
		}
		else
		{
			// Consolidation
			m_Fields.put(sName, sValue);
			return 1;
		}
	}
	
	/** 
	 * Retrieve the number of unique fields (keys) present in the update
	 * 
	 * @return Number of fields present (zero or more)
	 */
	public int getNumberFields ()
	{
		return m_Fields.size();
	}
}
