package mobilesim.core;

import java.util.*;

public class ListProximityInstances extends Hashtable<String, ProximityInstance> {
	/** This compares two proximity instances (this one versus comparisonInstances) and gives a new
	 * subset that contains anything that has changed (positive or negative)
	 * 
	 * @param comparisonInstances
	 * @return
	 */
	public static ListProximityInstances getSubsetChanged (ListProximityInstances comparisonInstances)
	{
		ListProximityInstances		changeList;
		
		changeList = new ListProximityInstances();
		
		// Fill in the changes
		
		return changeList;
	}
	
	/** This compares two proximity instances (this one versus comparisonInstances) and gives a new
	 * subset that contains only nodes that were in this object but were not in comparisonInstances.
	 * This would be nodes that have disappeared.
	 * 
	 * @param comparisonInstances
	 * @return
	 */
	public static ListProximityInstances getSubsetDeparted (ListProximityInstances comparisonInstances)
	{
		ListProximityInstances		changeList;
		
		changeList = new ListProximityInstances();
		
		// Fill in the departures
		
		return changeList;
	}

	/** This compares two proximity instances (this one versus comparisonInstances) and gives a new
	 * subset that contains only nodes that were not in this object but were in comparisonInstances.
	 * This would be newly arriving nodes.
	 * 
	 * @param comparisonInstances
	 * @return
	 */
	public static ListProximityInstances getSubsetArrived (ListProximityInstances comparisonInstances)
	{
		ListProximityInstances		changeList;
		
		changeList = new ListProximityInstances();
		
		// Fill in the arrivals
		
		return changeList;
	}
}
