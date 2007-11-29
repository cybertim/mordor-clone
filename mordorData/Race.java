package mordorData;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import structures.ListNode;
import mordorEnums.Alignment;
import mordorEnums.ItemTypes;
import mordorEnums.Resistance;
import mordorEnums.Size;
import mordorEnums.Stats;
import mordorHelpers.Util;

/**
 * Race class
 * @author August Junkala, April 8, 2007
 *
 */
public class Race
{
	private String name;
	private String description;
	private short maxAge;
	private byte experienceRate, raceID;
	private Size size;
	private byte[][] baseStats;
	private boolean[] alignments;
	private byte[] resistances;
	
	// note rates stored is the array entry in this
	public static final double[] EXPERIENCERATES = {1.0, 1.0, 1.0, 1.0, 1.05819132268, 1.10551994263, 1.14714602440, 1.18544741085, 1.22165868435, 1.25647909518};
	
	private static final byte STATS_MIN = 0;
	private static final byte STATS_MAX = 1;
	
	public static final short MINAGE = 18;
	public static final short MAXAGE = 16129;
	
	Race(byte newID)
	{
		name = Util.NOSTRING;
		raceID = newID;
		description = "";
		maxAge = 100;
		experienceRate = 3;
		size = Size.Normal;
		alignments = new boolean[Alignment.values().length];
		alignments[Alignment.Neutral.value()] = true;
		
		baseStats = new byte[Stats.values().length][2];
		for(byte i = 0; i < baseStats.length; i++)
		{
			baseStats[i][STATS_MIN] = Stats.DEFAULTMINSTAT;
			baseStats[i][STATS_MAX] = Stats.DEFAULTMAXSTAT;
		}
		
		resistances = new byte[Resistance.values().length];
		for(Resistance re : Resistance.values())
			resistances[re.value()] = (byte)0;
	}
	
	public byte getRaceID()
	{
		return raceID;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	/**
	 * Retrieve the maximum age for this race.
	 * Note: maximum age is in years NOT days
	 * @return
	 */
	public short getMaxAge()
	{
		return maxAge;
	}
	
	/**
	 * Retrieves the maximum age for this race in days.
	 * @return
	 */
	public int getMaxAgeDays()
	{
		return maxAge * 365;
	}
	
	/**
	 * Return the representative value of the experience rate.
	 * @return
	 */
	public byte getXPrate()
	{
		return experienceRate;
	}
	
	/**
	 * Retrieve the double value for this rates experience rate.
	 * Used for internal calculations.
	 * @return
	 */
	public double getTrueXPRate()
	{
		return EXPERIENCERATES[experienceRate];
	}
	
	/**
	 * Retrieve the size of this race.
	 * @return Size the size
	 */
	public Size getSize()
	{
		return size;
	}
	
	/**
	 * Retrieve the value for a base stat.
	 * @param statType
	 * @param minimum Retrieve the minimum (true) or maximum (false) 
	 * @return
	 */
	public byte getBaseStat(Stats statType, boolean minimum)
	{
		return (minimum) ? baseStats[statType.value()][STATS_MIN] : baseStats[statType.value()][STATS_MAX];
	}
	
	/**
	 * Retrieves the amount of resistances of the specified type.
	 * @param resistanceType
	 * @return
	 */
	public byte getResistance(Resistance resistanceType)
	{
		return resistances[resistanceType.value()];
	}
	
	/**
	 * Determines if a certain alignment is allowed for this race
	 * @param newAlignment
	 * @return True if the alignment is allowed.
	 */
	public boolean canBeAligned(Alignment newAlignment)
	{
		return alignments[newAlignment.value()];
	}
	
	public void setRaceID(byte nRaceID)
	{
		raceID = nRaceID;
	}
	
	public void setName(String nName)
	{
		name = nName;
	}
	
	public void setDescription(String nDescription)
	{
		description = nDescription;
	}
	
	public void setMaxAge(short nMaxAge)
	{
		maxAge = nMaxAge;
	}
	
	public void setXPRate(byte nXPRate)
	{
		experienceRate = nXPRate;
	}
	
	/**
	 * Set the size of the race.
	 * @param nSize
	 */
	public void setSize(Size nSize)
	{
		size = nSize;
	}
	
	/**
	 * Set the value for a base stat
	 * @param statType	Stats type to change
	 * @param minimum	True if changing the minimal value.
	 * @param statValue
	 */
	public void setBaseStat(Stats statType, boolean minimum, byte statValue)
	{
		if(minimum)
			baseStats[statType.value()][STATS_MIN] = Util.FITBYTE(statValue, Stats.MINIMUMVALUE, Stats.MAXIMUMVALUE);
		else
			baseStats[statType.value()][STATS_MAX] = Util.FITBYTE(statValue, Stats.MINIMUMVALUE, Stats.MAXIMUMVALUE);
	}
	
	/**
	 * Sets if whether a specific aligment is allowed.
	 * @param nAlignment
	 * @param allowed
	 */
	public void setAlignment(Alignment nAlignment, boolean allowed)
	{
		alignments[nAlignment.value()] = allowed;
	}
	
	/**
	 * Sets a specific resistance to this value.
	 * @param resistanceType
	 * @param resistanceValue
	 */
	public void setResistance(Resistance resistanceType, byte resistanceValue)
	{
		resistances[resistanceType.value()] = Util.FITPERCENTAGE(resistanceValue);
	}
	
	public boolean writeRace(DataOutputStream dos)
	{
		try
		{
			dos.writeByte(raceID);
			
			dos.writeByte(experienceRate);
			dos.writeByte(size.value());
			dos.writeShort(maxAge);
			
			dos.writeInt(alignments.length);
			for(byte i = 0; i < alignments.length; i++)
				dos.writeBoolean(alignments[i]);
			
			dos.writeInt(resistances.length);
			dos.write(resistances);
			
			dos.writeInt(baseStats.length);
			dos.writeInt(baseStats[0].length);
			for(byte i = 0; i < baseStats.length; i++)
				dos.write(baseStats[i]);

			dos.writeUTF(name);
			dos.writeUTF(description);
		}
		catch(Exception e)
		{
			System.err.println("Race - writeRace : " + e);
			return false;
		}
		
		return true;
	}
	
	public static final Race readRace(DataInputStream dis)
	{
		Race newRace = null;
		try
		{
			newRace = new Race(dis.readByte());
			
			newRace.setXPRate(dis.readByte());
			newRace.setSize(Size.type(dis.readByte()));
			newRace.maxAge = dis.readShort();
			
			int dim1, dim2;
			byte i;
			dim1 = dis.readInt();
			
			for(i = 0; i < Alignment.values().length; i++)
			{
				if(i < dim1)
					newRace.alignments[i] = dis.readBoolean();
				else
					newRace.alignments[i] = false;
			}
			
			for(; i < dim1; i++)
				dis.readBoolean();
			
			dim1 = dis.readInt();
			
			for(i = 0; i < Resistance.values().length; i++)
			{
				if(i < dim1)
					newRace.resistances[i] = dis.readByte();
				else
					newRace.resistances[i] = (byte)0;
			}
			
			for(;i < dim1; i++)
				dis.readByte();
			
			//Read off the size of the dimensions of baseStats
			dim1 = dis.readInt();
			dim2 = dis.readInt();
			// Read the actual and accepted values
			for(i = 0; i < Stats.values().length; i++)
			{
				if(i < dim1)
				{
					newRace.baseStats[i][STATS_MIN] = dis.readByte();
					newRace.baseStats[i][STATS_MAX] = dis.readByte();
					
					// If for some reason, dim2 is bigger than it should be, read off those bytes.
					for(byte j = 2; j < dim2; j++)
						dis.readByte();
				}
				else
				{
					newRace.baseStats[i][STATS_MIN] = Stats.DEFAULTMINSTAT;
					newRace.baseStats[i][STATS_MAX] = Stats.DEFAULTMAXSTAT;
				}
			}
			// Read off any excess bytes.
			for(; i < dim1; i++)
				for(byte j = 0; j < dim2; j++)
					dis.readByte();

			// Read the name and description
			newRace.name = dis.readUTF();
			newRace.description = dis.readUTF();
		}
		catch(Exception e)
		{
			
			System.err.println("Race - readRace : " + e);
			return null;
		}
		
		return newRace;
	}
	
	public String toString()
	{
		return name;
	}
}
