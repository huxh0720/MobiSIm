package mobilesim.core;

import java.util.*;

/**
 * A mobile user in the overall study. A mobile user may have one or more
 * mobile nodes and / or other devices. 
 * 
 * @author Striegel
 */
public class MobileUser {
	String		m_sStudyID;
	
	Vector<MobileNode>		m_MobileDevices;
	
	public MobileUser ()
	{
		m_sStudyID = "";
		m_MobileDevices  = new Vector<MobileNode>();
	}
	
	public Vector<MobileNode> getMobileDevices ()
	{
		return m_MobileDevices;
	}
	
	public void setStudyID (String sStudyID)
	{
		m_sStudyID = sStudyID;
	}
	
	public String getStudyID ()
	{
		return m_sStudyID;
	}
}
