package mordorHelpers;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import mordorEnums.BodyParts;
import mordorEnums.ItemTypes;

public final class Util
{
	/* Constants */
	public static final byte MAXLOOPS = 40; // Maximum names to try something
	
	/* General */
	public static final byte NOTHING = Byte.MIN_VALUE;
	public static final String NOSTRING = "Nothing"; // Identify non-existent things.
	public static final String[] NOSTRINGARRAY = {"Nothing"}; // Array when nothing exists.
	
	public static final byte MAX_MEAINGFUL_COAVALUES = 66;
	
	/* Items */
	
	/* Player */
	
	/* Monsters */
	public static final byte MON_MAXSTACKSIZE = 4;
	public static final byte MON_MINSTACKSIZE = 1;
	public static final byte MON_MAXGROUPSIZE = 64;
	public static final byte MON_MINGROUPSIZE = 1;
	
	/* Rooms */
	
	protected Util()
	{
		
	}
	
	public static BodyParts getEquippingBodyPart(ItemTypes itemType)
	{
		switch(itemType)
		{
		case Hands:
			return BodyParts.Hands;
		case Dagger:
		case Cross:
		case Sword:
		case Staff:
		case Mace:
		case Axe:
		case Hammer:
		case Rod:
			return BodyParts.Weapon;
		case LeatherArmor:
		case ChainArmor:
		case PlateArmor:
			return BodyParts.Torso;
		case Shield:
			return BodyParts.Shield;
		case Cap:
		case Helmet:
			return BodyParts.Head;
		case Gloves:
		case Gauntlets:
			return BodyParts.Gloves;
		case Cloak:
			return BodyParts.Cloak;
		case Bracers:
			return BodyParts.Bracers;
		case Sash:
			return BodyParts.Sash;
		case Belt:
			return BodyParts.Belt;
		case Boots:
			return BodyParts.Boots;
		case Ring:
			return BodyParts.Finger;
		case Amulet:
			return BodyParts.Neck;
		case GuildCrest:
			return BodyParts.Crest;
		}
		
		return BodyParts.None;
	}
	
	public static boolean isEquipment(ItemTypes itemType)
	{
		return (getEquippingBodyPart(itemType) != BodyParts.None);
	}
	
	/**
	 * Takes an integer and if it is below min, or above max, sets
	 * it to min or max respectively. 
	 * @param value
	 * @param min
	 * @param max
	 * @return byte
	 */
	public static final byte FITBYTE(int value, int min, int max)
	{
		return (byte)((value < min) ? min : (value > max) ? max : value);
	}
	
	public static final short FITSHORT(int value, int min, int max)
	{
		return (short)((value < min) ? min : (value > max) ? max : value);
	}
	
	public static final int FITINT(int value, int min, int max)
	{
		return (value < min) ? min : (value > max) ? max : value;
	}
	
	public static final long FITLONG(long value, long min, long max)
	{
		return (value < min) ? min : (value > max) ? max : value;
	}
	
	public static final float FITFLOAT(float value, float min, float max)
	{
		return (value < min) ? min : (value > max) ? max : value;
	}
	
	public static final byte FITPERCENTAGE(int value)
	{
		return (value < -100) ? (byte)-100 : (value > 100) ? (byte)100 : (byte)value;
	}
	
	public static final boolean validityCheckTf(JComponent parent, JTextField textField, long min, long max, String errText)
	{
		if(textField.getText() == null || textField.getText().trim().length() < 1)
			textField.setText("" + min);
		
		long temp;
		
		try
		{
			String text = textField.getText().trim();

			if(max < Byte.MAX_VALUE)
			{
				temp = Byte.parseByte(text);
				textField.setText("" + Util.FITBYTE((byte)temp, (byte)min, (byte)max));
			}
			else if(max < Short.MAX_VALUE)
			{
				temp = Short.parseShort(text);
				textField.setText("" + Util.FITSHORT((short)temp, (short)min, (short)max));
			}
			else if(max < Integer.MAX_VALUE)
			{
				temp = Integer.parseInt(text);
				textField.setText("" + Util.FITINT((int)temp, (int)min, (int)max));
			}
			else
			{
				temp = Long.parseLong(text);
				textField.setText("" + Util.FITLONG((long)temp, (long)min, (long)max));
			}
		}
		catch(NumberFormatException NFE)
		{
			if(errText != null)
				JOptionPane.showMessageDialog(parent, "Invalid " + errText + ".");
			return false;
		}
		return true;
	}
	
	/**
	 * Retrieve integer to the power of factor
	 * @param integer
	 * @param factor
	 * @return int
	 */
	public static final int power(int integer, int factor)
	{
		int value = integer;
		for(int i = 0; i < factor; i++)
		{
			if(Integer.MAX_VALUE - integer < integer)
				return Integer.MAX_VALUE;
			value *= integer;
		}
		
		return value;
	}
	
	public static final long[] COMPRESSBOOLTOLONG(boolean[] booleans)
	{
		long[] longs;
		
		int numLongs = (booleans.length >> 6) + 1;
		int longCount = 0;
		long boolCount = 0;
		
		longs = new long[numLongs];
		for(int i = 0; i < numLongs; i++)
			longs[i] = 0;
		
		for(int i = 0; i < booleans.length; i++, boolCount++)
		{
			longs[longCount] |= (booleans[i]) ? (long)((long)1 << boolCount) : 0;
			if(i != 0 && i % 64 == 0)
			{
				longCount++;
				boolCount = -1;
			}
		}
		
		return longs;
	}
	
	/**
	 * Takes an array of longs, how many bools should be in it, and reads the long to the
	 * booleans. Returns the array of booleans.
	 * @param longs	Array of longs
	 * @param numBools	Number of bools to read.
	 * @return boolean[numBools]
	 */
	public static final boolean[] UNCOMPRESSLONGTOBOOL(long[] longs, int numBools)
	{
		boolean[] bools = new boolean[numBools];

		int longCount = 0;
		int boolCount = 0;
		
		for(int i = 0; i < bools.length; i++, boolCount++)
		{
			bools[i] = ((longs[longCount] & (long)((long)1 << boolCount)) != 0);
			
			if(i != 0 && i % 64 == 0)
			{
				longCount++;
				boolCount = -1;
			}
		}
		
		return bools;
	}
}
