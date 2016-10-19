package mobilesim.statistics;

import java.util.Vector;

/** 
 * A collection of historical values for a particular statistic
 * 
 * @author Striegel
 *
 */
public class StatisticHistory extends Vector<StatisticHistoricalInstance> {

	public void dumpSummary ()
	{
		System.out.println("Statistic History with " + this.size() + " elements.");
		
		for(StatisticHistoricalInstance shi : this)
		{
			System.out.print("  SHI (t = " + shi.getSampleTime() + ") -> " + shi.getName() + " ");
			
			if(shi.getChildGroup() != null)
			{
				System.out.print(" " + shi.getChildGroup().size() + " elements");
			}
			else
			{
				System.out.print(" No child elements");
			}
			
			System.out.println("");
		}
	}
	
}
