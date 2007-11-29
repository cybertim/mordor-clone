package mordorData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Random;

import mordorEnums.Identification;
import mordorHelpers.Util;

/**
 * Class for actual instances of monsters.
 * @author August Junkala
 *
 */
public class MonsterInstance
{
	protected Monster monster;
	protected short monsterID, hits, maxHits;
	protected Identification idLevel;
	
	public MonsterInstance()
	{
		monster = null;
		monsterID = Util.NOTHING;
		maxHits = 0;
		hits = 0;
		idLevel = Identification.Nothing;
	}
	
	public MonsterInstance(Monster nMonster)
	{
		monster = nMonster;
		monsterID = monster.getID();
		
		Random rand = new Random(System.nanoTime());
		int range = (int)(monster.getAvgHits() * 0.1);
		maxHits = (range > 2) ? (short)(rand.nextInt(range) - (range >> 1)) : (short)range;
		hits = maxHits;
		idLevel = Identification.Nothing;
	}
	
	MonsterInstance(short nMonsterID)
	{
		// TODO Everything should be initialized here.
		monsterID = nMonsterID;
		idLevel = Identification.Nothing;
	}
	
	public short getMonsterID()
	{
		return monsterID;
	}
	
	public Monster getMonster()
	{
		return monster;
	}
	
	public short getHits()
	{
		return hits;
	}
	
	public short getMaxhits()
	{
		return maxHits;
	}
	
	/**
	 * Retrieve the Identification level of this monster.
	 * @return	Identification
	 */
	public Identification getIDLevel()
	{
		return idLevel;
	}
	
	public void setMonsterID(short nMonsterID)
	{
		monsterID = nMonsterID;
	}
	
	public boolean setMonster(Monster nMonster)
	{
		if(nMonster == null)
		{
			System.err.println("MonsterInstance setMonster error. nMonster is null");
			return false;
		}
		monster = nMonster;
		monsterID = monster.getID();
        return true;
	}
	
	public void setHits(short nHits)
	{
		hits = nHits;
	}
	
	public void setMaxHits(short nMaxHits)
	{
		maxHits = nMaxHits;
	}
	
	/**
	 * Check if the monster is dead.
	 * @return
	 */
	public boolean isDead()
	{
		return (hits <= 0);
	}
	
	/**
	 * Changes the amount of its the monster has. If it reaches 0
	 * then the monster is dead.
	 * @param newHits
	 * @return true if monster is now dead.
	 */
	public boolean changeHits(int newHits)
	{
		hits -= (short)newHits;
		
		return (hits <= 0);
	}
	
	public boolean writeMonsterInstance(DataOutputStream dos)
	{
		try
		{
			dos.writeShort(monsterID);
			
			dos.writeShort(hits);
			dos.writeShort(maxHits);
			
			dos.writeByte(idLevel.value());
		}
		catch(Exception e)
		{
			System.err.println("MonsterInstance write error\nID" + this.monsterID + "\nError: " + e);
			return false;
		}
		
		return true;
	}
	
	public static final MonsterInstance readMonsterInstance(DataInputStream dis)
	{
		MonsterInstance tMonster = null;
		short monsterID = Util.NOTHING;
		try
		{
			monsterID = dis.readShort();
			if(monsterID == Util.NOTHING || monsterID < 0)
				return null;
			
			tMonster = new MonsterInstance(monsterID);
			
			tMonster.setHits(dis.readShort());
			tMonster.setMaxHits(dis.readShort());
			
			tMonster.idLevel = Identification.type(dis.readByte());
		}
		catch(Exception e)
		{
			
			System.err.println("MonsterInstance read error\nID: " + monsterID + "\nError: " + e);
			return null;
		}
		
		return tMonster;
	}
}
