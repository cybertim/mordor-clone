package mordorData;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Extension of the monster instance class for companions.
 * @author August Junkala, Nov 17, 2007
 *
 */
public class Companion extends MonsterInstance
{
	private byte binding;
	
	public Companion(MonsterInstance nMonster, byte nBinding)
	{
		monster = nMonster.monster;
		hits = nMonster.hits;
		idLevel = nMonster.idLevel;
		maxHits = nMonster.maxHits;
		monsterID = nMonster.monsterID;
		
		binding = nBinding;
	}
	
	public byte getBinding()
	{
		return binding;
	}
	
	public void setBinding(byte nBinding)
	{
		binding = nBinding;
	}
	
	/**
	 * Changes the binding level by the amount provided by
	 * adding that amount to the current binding level.
	 * @param nBindingChange	byte
	 */
	public void changeBinding(byte nBindingChange)
	{
		binding += nBindingChange;
	}
	
	public boolean writeCompanion(DataOutputStream dos)
	{
		try
		{
			super.writeMonsterInstance(dos);
			dos.writeByte(binding);
		}
		catch(Exception e)
		{
			System.err.println("Companion write error : " + e);
			return false;
		}
		
		return true;
	}
	
	public static final Companion readCompanion(DataInputStream dis)
	{
		Companion companion = null;
		try
		{
			MonsterInstance m = MonsterInstance.readMonsterInstance(dis);
			
			if(m != null)
				companion = new Companion(m, dis.readByte());
		}
		catch(Exception e)
		{
			
			System.err.println("Companion read error : " + e);
			return null;
		}
		
		return companion;
	}
}
