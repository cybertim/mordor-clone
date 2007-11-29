package mordorData;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import mordorEnums.Alignment;
import mordorEnums.DropTypes;
import mordorEnums.ItemTypes;
import mordorEnums.MonsterAbility;
import mordorEnums.MonsterClass;
import mordorEnums.Resistance;
import mordorEnums.Size;
import mordorEnums.SpellClass;
import mordorEnums.Stats;
import mordorHelpers.Util;

/**
 * Object for the different kinds of monsters. E.g. the definition of what
 * constitutes a Kobold would be declared using an instance of this class.
 * This differs from a monster the player has as a companion, for example,
 * which would be a MonsterInstance.
 * @author August Junkala
 *
 */
public class Monster extends MObject<MonsterClass>
{	
	public static final byte MONSTERMAXWEALTHMULTI = Byte.MAX_VALUE;
	public static final byte MONSTERMAXCHANCEFORRARE = 15;
	public static final byte MONSTER_USECLASSWEALTH = -1;

	private short monsterImg;
//	private String name, 
	private String description;
//	private byte minimumMapLevel;
//	private MonsterClass monsterClass;
	private Alignment alignment;
	private Size size;
//	private byte strength, constitution, dexterity;
	private short averageHits;//, attack, defense;
	private byte wealthMulti, chanceOfAppear;
	private DropTypes dropType;
//	private boolean aquatic;//, laired;
	private boolean[] monsterAbilities, itemType, spellClass;
	private byte[] resistances;
	private byte groupSize;				// Number of monsters to appear in a stack.
	private byte groupNumber;			// Number of stacks of this monster to normally appear.
	private MonsterClass companionType; // MonsterClass of potential companions.
	private short itemID, secondItemID, specificCompanionID;	// ID Of an item the monster will always drop.
	
	public Monster(short newID)
	{
		super(newID);
		//monsterID = newID;
		
		//name = Util.NOSTRING;
		description = "";
		size = Size.Normal;
		alignment = Alignment.Neutral;
		mType = MonsterClass.Animals;
		mLevel = 0;
		mStats[Stats.Strength.value()] = 1;
		mStats[Stats.Constitution.value()] = 1;
		mStats[Stats.Dexterity.value()] = 1;
		wealthMulti = MONSTER_USECLASSWEALTH;
		mAttack = 1;
		mDefense = 1;
		dropType = DropTypes.None;
		monsterImg = 0;
	//	aquatic = false;
		//laired = false;
		monsterAbilities = new boolean[MonsterAbility.values().length];
		itemType = new boolean[ItemTypes.values().length];
		spellClass = new boolean[SpellClass.values().length];
		resistances = new byte[Resistance.values().length];
	//	for(byte i = 0; i < MonsterAbility.values().length; i++)
		for(MonsterAbility ma : MonsterAbility.values())
			monsterAbilities[ma.value()] = false;
		for(ItemTypes it : ItemTypes.values())
	//	for(byte i = 0; i < itemType.length; i++)
			itemType[it.value()] = false;
	//	for(byte i = 0; i < spellClass.length; i++)
		for(SpellClass sc : SpellClass.values())
			spellClass[sc.value()] = false;
		for(Resistance al : Resistance.values())
			resistances[al.value()] = (byte)0;
		groupSize = 1;
		groupNumber = 1;
	//	groupFriendIDs = new short[MonsterEden.GROUP_MAXNUM];
		companionType = null;
		itemID = Util.NOTHING;
		secondItemID = Util.NOTHING;
		specificCompanionID = Util.NOTHING;
	}
	
	/**
	 * Retrieves the monsterID (that is, the ID of its type)
	 * @return short	The monster's ID
	 */
	/*public short getID()
	{
		return monsterID;
	}*/
	
	/**
	 * Retrieves the name of the monster.
	 * @return String
	 */
	/*public String getName()
	{
		return name;
	}*/
	
	public String getDescription()
	{
		return description;
	}
	
	/**
	 * Using the information in the monster, generate its description.
	 * @return String[] Array for each bit of text.
	 */
	public String generateDescription()
	{
		String description;
		description = "<HTML>" + mName + "s are " + size.name() + " size " + mType.name() + " who are generally " + alignment.name() + " in nature.<BR><BR>";
		description += "With an average A/D of " + mAttack + "/" + mDefense + " and average hits of " + averageHits
							+ " and a " + mStats[Stats.Strength.value()] + " Strength, " + mStats[Stats.Constitution.value()] + " Constitution, " + mStats[Stats.Dexterity.value()] + 
							" Dexterity, it is also known that these creatures' abilities and attacks include: ";
		// parse abilities/spells
		
		// Resistances
		description += mName + " is ";
		int count = 0;
		for(Resistance al : Resistance.values())
			if(this.resistances[al.value()] != 0)
			{
				if(count != 0)
					description += ", ";
				description += resistances[al.value()] + "% " + al.name();
				count++;
			}
		description += (count == 0) ? " not" : "";
		description += " resistant.";
		
		description += "";
		if(this.isLaired())
			description += "<BR><BR>These monsters are only found in their Lair.</HTML>";
		else
			description += "<BR><BR>These monsters are found on level " + mLevel + "</HTML>";
		
		return description;
	}
	
	/**
	 * Retrieves the size of the monster.
	 * @return
	 */
	public Size getSize()
	{
		return size;
	}
	
	/**
	 * Retrieves the alignment of this mosnter.
	 * @return
	 */
	public Alignment getAlignment()
	{
		return alignment;
	}
	
	/**
	 * Retrieve the monster class of this monster.
	 * @return
	 */
	public MonsterClass getMonsterClass()
	{
		return mType;
	}
	
	public byte getMinMapLevel()
	{
		return (byte)mLevel;
	}
	
	/*
	public byte getStrength()
	{
		return m;
	}
	
	public byte getConstitution()
	{
		return constitution;
	}
	
	public byte getDexterity()
	{
		return dexterity;
	}*/
	
	public byte getWealthMultiplier()
	{
		return (wealthMulti == MONSTER_USECLASSWEALTH) ? mType.getWealthMultiplier() : wealthMulti;
	}
	
	public byte getChanceOfAppearance()
	{
		return chanceOfAppear;
	}
	
	/**
	 * Retrieve the method this monster ususually drops stuff.
	 * @return
	 */
	public DropTypes getDropType()
	{
		return dropType;
	}
	
	/**
	 * Retrieves the ID of a specific item that this monster drops.
	 * @return short
	 */
	public short getItemDropID()
	{
		return itemID;
	}
	
	/**
	 * Retrieve the ID of the second specific item that this monster drops.
	 * @return short	ItemID
	 */
	public short getSecondItemDropID()
	{
		return secondItemID;
	}
	
	/**
	 * If this monster has a specific companion, return its ID
	 * @return short	Util.NOTHING if no companion
	 */
	public short getSpecificCompanionID()
	{
		return specificCompanionID;
	}
	
	/**
	 * Retrieve a boolean array which indicates if this monster can drop each
	 * item type.
	 * @return boolean[]
	 */
	public boolean[] getItemDropTypes()
	{
		return itemType;
	}
	
	public short getMonsterImageID()
	{
		return monsterImg;
	}
	
	public short getAvgHits()
	{
		return averageHits;
	}
	
	/*public short getAttack()
	{
		return attack;
	}
	
	public short getDefense()
	{
		return defense;
	}*/
	
	/**
	 * Retrieves the amount of resistance for a resistance type.
	 * @param resistType
	 * @return
	 */
	public byte getResistance(Resistance resistType)
	{
		return resistances[resistType.value()];
	}
	
	/**
	 * Retrieve the size of groups this monster forms per stack.
	 * @return	byte
	 */
	public byte getGroupSize()
	{
		return groupSize;
	}
	
	/**
	 * The class of monster this monster tends to have as a companion.
	 * @return MonsterClass
	 */
	public MonsterClass getCompanionType()
	{
		return companionType;
	}
	
	/**
	 * Retrieve the number of stacks of friends for this monster.
	 * @return	byte
	 */
	public byte getGroupNumber()
	{
		return groupNumber;
	}
	
	/**
	 * Is this monster acquatic.
	 * @return	boolean
	 */
	/*public boolean isAquatic()
	{
		return aquatic;
	}*/
	
	/**
	 * Is this a laired monster.
	 * @return boolean
	 */
	public boolean isLaired()
	{
		return (chanceOfAppear == 0);
	}
	
	/**
	 * Can this drop an item of this type.
	 * @param it	ItemTypes
	 * @return boolean
	 */
	public boolean canDropItemType(ItemTypes it)
	{
		return itemType[it.value()];
	}
	
	/**
	 * Can this monster cast a spell of this class.
	 * @param nSpellClass
	 * @return boolean
	 */
	public boolean canCastSpellClass(SpellClass nSpellClass)
	{
		return spellClass[nSpellClass.value()];
	}
	
	/**
	 * Determine if the monster has a specific ability.
	 * @param monAbility
	 * @return
	 */
	public boolean hasAbility(MonsterAbility monAbility)
	{
		return monsterAbilities[monAbility.value()];
	}
	
	public boolean dropsItems()
	{
		for(byte i = 0; i < itemType.length; i++)
			if(itemType[i])
				return true;
		
		return false;
	}
	
	/**
	 * Determine if monster can cast any spells.
	 * @return True if monster can cast a spell.
	 */
	public boolean castsSpells()
	{
		for(byte i = 0; i < spellClass.length; i++)
			if(spellClass[i])
				return true;
		
		return false;
	}
	
	/**
	 * Set the ID for this monster.
	 * WARNING: For use with loading (from file) or creating monsters.
	 * @param newID
	 */
	/*public void setID(short newID)
	{
		monsterID = Util.FITSHORT(newID, 0, Short.MAX_VALUE);
	}*/
	
	/**
	 * Set the name of the monster.
	 * @param newName
	 */
	/*public void setName(String newName)
	{
		name = newName;
	}*/
	
	/**
	 * Set the description of the monster.
	 * @param newDescription
	 */
	public void setDescription(String newDescription)
	{
		description = newDescription;
	}
	
	/**
	 * Set the size of the monster.
	 * @param newSize
	 */
	public void setSize(Size newSize)
	{
		size = newSize;
	}
	
	/**
	 * Set the alignment of the monster.
	 * @param newAlignment
	 */
	public void setAlignment(Alignment newAlignment)
	{
		alignment = newAlignment;
	}
	
	/**
	 * Set the class of monster.
	 * @param newMonType
	 */
	public void setMonsterClass(MonsterClass newMonClass)
	{
		mType = newMonClass;
	}
	
	/**
	 * Set the minimum level at which this monster should generated in
	 * the dungeon.
	 * @param newMinMapLevel
	 */
	public void setMinMapLevel(byte newMinMapLevel)
	{
		mLevel = Util.FITBYTE(newMinMapLevel, 0, Map.MAXDEPTH);
	}
	
	/**
	 * Set the strength of the monster.
	 * @param newStrength
	 */
	/*public void setStrength(byte newStrength)
	{
		strength = Util.FITBYTE(newStrength, Stats.MINIMUMVALUE, Stats.MAXIMUMVALUE);
	}
	
	/**
	 * Set the constitution of the monster.
	 * @param newConstitution
	 */
	/*public void setConstitution(byte newConstitution)
	{
		constitution = Util.FITBYTE(newConstitution, Stats.MINIMUMVALUE, Stats.MAXIMUMVALUE);
	}
	
	/**
	 * Set the dexterity of the monster.
	 * @param newDexterity
	 */
	/*public void setDexterity(byte newDexterity)
	{
		dexterity = Util.FITBYTE(newDexterity, Stats.MINIMUMVALUE, Stats.MAXIMUMVALUE);
	}*/
	
	/**
	 * Set the wealth multiplier for the monster.
	 * @param newWealthMultiplier
	 */
	public void setWealthMultiplier(byte newWealthMultiplier)
	{
		// TODO replace this
		wealthMulti = MONSTER_USECLASSWEALTH;
		//wealthMulti = (newWealthMultiplier == MONSTER_USECLASSWEALTH) ? MONSTER_USECLASSWEALTH : Util.FITBYTE(newWealthMultiplier, MonsterClass.MINWEALTHMULTIPLIER, MonsterClass.MAXWEALTHMULTIPLIER);
	}
	
	/**
	 * Set the probability of appearance for this monster.
	 * @param newChanceOfAppearance
	 */
	public void setChanceOfAppearance(byte newChanceOfAppearance)
	{
		chanceOfAppear = Util.FITPERCENTAGE(newChanceOfAppearance);
	}
	
	/**
	 * Set the way in which this monster drops treasure.
	 * @param newDropType
	 */
	public void setDropType(DropTypes newDropType)
	{
		dropType = newDropType;
	}
	
	/**
	 * Set ID for the image this monster uses.
	 * @param newMonsterImg
	 */
	public void setMonsterImg(short newMonsterImg)
	{
		monsterImg = Util.FITSHORT(newMonsterImg, 0, ImageBank.MONSTERFILES.length - 1);
	}
	
	/**
	 * Set the average amount of hits a new instance of this monster
	 * starts with.
	 * @param newAvgHits
	 */
	public void setAverageHits(short newAvgHits)
	{
		averageHits = Util.FITSHORT(newAvgHits, 0, Short.MAX_VALUE);
	}
	
	/**
	 * Set the monster's attack value.
	 * @param newAttack
	 */
	/*public void setAttack(short newAttack)
	{
		attack = Util.FITSHORT(newAttack, 0, Short.MAX_VALUE);
	}
	
	/**
	 * Set the monster's defense value.
	 * @param newDefense
	 */
/*	public void setDefense(short newDefense)
	{
		defense = Util.FITSHORT(newDefense, 0, Short.MAX_VALUE);
	}
	
	/**
	 * Set whether this monster is most likely to be found in
	 * water squares.
	 * @param newAquatic
	 */
	/*public void setAquatic(boolean newAquatic)
	{
		aquatic = newAquatic;
	}
	
	/**
	 * Set whether the monster can drop a specific item type.
	 * @param newItemDropType
	 * @param canDrop
	 */
	public void setItemDropType(ItemTypes it, boolean canDrop)
	{
		itemType[it.value()] = canDrop;
	}
	
	/**
	 * Retrieve the second specific item a monster may drop.
	 * @param nItemID
	 */
	public void setSecondItemDropID(short nItemID)
	{
		secondItemID = nItemID;
	}
	
	/**
	 * Retrieve the specific companion the monster has.
	 * @param nCompID
	 */
	public void setSpecificCompanionID(short nCompID)
	{
		specificCompanionID = nCompID;
	}
	
	/**
	 * Set the specific item that this monster drops.
	 * @param nItemID
	 */
	public void setItemDropID(short nItemID)
	{
		itemID = nItemID;
	}
	
	public void setSpellClass(SpellClass newSpellClass, boolean canCast)
	{
		spellClass[newSpellClass.value()] = canCast;
	}
	
	/**
	 * Set whether a monster has a specific ability.
	 * @param monAbility
	 * @param hasAbility
	 */
	public void setAbility(MonsterAbility monAbility, boolean hasAbility)
	{
		monsterAbilities[monAbility.value()] = hasAbility;
	}
	
	/**
	 * Sets the amount of resistance.
	 * @param resistType
	 * @param resistVal
	 */
	public void setResistances(Resistance resistType, byte resistVal)
	{
		resistances[resistType.value()] = Util.FITPERCENTAGE(resistVal);
	}
	
	/**
	 * Set the group size this monster normally forms.
	 * @param nGSize
	 */
	public void setGroupSize(byte nGSize)
	{
		groupSize = Util.FITPERCENTAGE(nGSize);
	}
	
	/**
	 * Set the number of stacks this monster usually forms.
	 * @param nGNumber
	 */
	public void setGroupNumber(byte nGNumber)
	{
		groupNumber = Util.FITBYTE(nGNumber, 1, Util.MON_MAXSTACKSIZE);
	}
	
	/**
	 * Sets the type of companion this monster normally has.
	 * @param MonsterClass nType
	 */
	public void setCompanionType(MonsterClass nType)
	{
		companionType = nType;
	}
	
	/**
	 * Create a new instance of this monster.
	 * @return
	 */
	public MonsterInstance createInstance()
	{
		MonsterInstance newMonster = new MonsterInstance(this);
		
		return newMonster;
	}
	
	/**
	 * Attacks the provided player. Returns number of hits points of
	 * damage done.
	 * @param player
	 * @return int
	 */
	public int attackPlayer(Player player)
	{
		return 0;
	}
	
	/**
	 * Determines if this monster is rare.
	 * @return
	 */
	public boolean isRare()
	{
		return (chanceOfAppear < MONSTERMAXCHANCEFORRARE);
	}
	
	public boolean writeMonster(DataOutputStream dos)
	{
		try
		{
			dos.writeShort(mID);
			
			dos.writeByte(size.value());
			dos.writeByte(alignment.value());
			dos.writeByte(mType.value());
			dos.writeByte(mLevel);
			dos.writeByte(mStats[Stats.Strength.value()]);
			dos.writeByte(mStats[Stats.Constitution.value()]);
			dos.writeByte(mStats[Stats.Dexterity.value()]);
			dos.writeByte(wealthMulti);
			dos.writeByte(chanceOfAppear);
			dos.writeByte(dropType.value());
			
	//		dos.writeBoolean(false);
//			dos.writeBoolean(laired);
			
			dos.writeShort(monsterImg);
			dos.writeShort(averageHits);
			dos.writeShort(mAttack);
			dos.writeShort(mDefense);
			
			long[] longs;
			
			longs = Util.COMPRESSBOOLTOLONG(monsterAbilities);
			dos.writeInt(longs.length);
			dos.writeInt(monsterAbilities.length);
			for(int i = 0; i < longs.length; i++)
				dos.writeLong(longs[i]);

			longs = Util.COMPRESSBOOLTOLONG(itemType);
			dos.writeInt(longs.length);
			dos.writeInt(itemType.length);
			for(int i = 0; i < longs.length; i++)
				dos.writeLong(longs[i]);

			longs = Util.COMPRESSBOOLTOLONG(spellClass);	
			dos.writeInt(longs.length);
			dos.writeInt(spellClass.length);
			for(int i = 0; i < longs.length; i++)
				dos.writeLong(longs[i]);
			
			dos.writeInt(resistances.length);
			for(byte i = 0; i < resistances.length; i++)
				dos.writeByte(resistances[i]);
			
			// group values
			dos.writeByte(groupSize);
			dos.writeByte(groupNumber);
			
			if(companionType == null)
				dos.writeByte(Util.NOTHING);
			else
				dos.writeByte(companionType.value());
			
			// Item drop
			dos.writeShort(itemID);
			dos.writeShort(secondItemID);
			dos.writeShort(specificCompanionID);
			
			dos.writeUTF(mName);
			dos.writeUTF(description);
			
		}
		catch(Exception e)
		{
			System.err.println("Error saving monster " + mID + "\nError: " + e);
			return false;
		}
		return true; // Why was this false?
	}
	
	/**
	 * Reads monster data from a data stream.
	 * @param dataBank
	 * @param dis
	 * @return
	 */
	public static final Monster loadMonster(DataBank dataBank, DataInputStream dis)
	{
		Monster newMonster = null;
		
		try
		{
			newMonster = new Monster(dis.readShort());
			
			newMonster.setSize(Size.type(dis.readByte()));
			newMonster.setAlignment(Alignment.type(dis.readByte()));
			newMonster.setMonsterClass(MonsterClass.type(dis.readByte()));
			newMonster.setMinMapLevel(dis.readByte());
			newMonster.mStats[Stats.Strength.value()] = dis.readByte();
			newMonster.mStats[Stats.Constitution.value()] = dis.readByte();
			newMonster.mStats[Stats.Dexterity.value()] = dis.readByte();
			newMonster.setWealthMultiplier(dis.readByte());
			newMonster.setChanceOfAppearance(dis.readByte());
			newMonster.setDropType(DropTypes.type(dis.readByte()));
			
			//newMonster.setAquatic(dis.readBoolean());
			//dis.readBoolean(); // TODO DEelete
			
			newMonster.setMonsterImg(dis.readShort());
			newMonster.setAverageHits(dis.readShort());
			newMonster.setAttack(dis.readShort());
			newMonster.setDefense(dis.readShort());
			
			int numLongs, numBools;
			long[] longs;
			boolean[] bools;
			
			numLongs = dis.readInt();
			numBools = dis.readInt();
			longs = new long[numLongs];
			if(numBools > MonsterAbility.values().length)
				numBools = MonsterAbility.values().length;
			
			for(int i = 0; i < numLongs; i++)
				longs[i] = dis.readLong();
			
			bools = Util.UNCOMPRESSLONGTOBOOL(longs, numBools);
			
			for(byte i = 0; i < bools.length; i++)
				newMonster.setAbility(MonsterAbility.type(i), bools[i]);
			
			numLongs = dis.readInt();
			numBools = dis.readInt();
			longs = new long[numLongs];
			if(numBools > ItemTypes.values().length)
				numBools = ItemTypes.values().length;
			
			for(int i = 0; i < numLongs; i++)
				longs[i] = dis.readLong();
			
			bools = Util.UNCOMPRESSLONGTOBOOL(longs, numBools);
			
			for(byte i = 0; i < bools.length; i++)
				newMonster.setItemDropType(ItemTypes.type(i), bools[i]);
			
			numLongs = dis.readInt();
			numBools = dis.readInt();
			longs = new long[numLongs];
			if(numBools > SpellClass.values().length)
				numBools = SpellClass.values().length;
			
			for(int i = 0; i < numLongs; i++)
				longs[i] = dis.readLong();
			
			bools = Util.UNCOMPRESSLONGTOBOOL(longs, numBools);
			
			for(byte i = 0; i < bools.length; i++)
				newMonster.setSpellClass(SpellClass.type(i), bools[i]);
			
			numLongs = dis.readInt();
			byte i = 0;
			for(; i < Resistance.values().length; i++)
			{
				if(i < numLongs)
					newMonster.setResistances(Resistance.type(i), dis.readByte());
			}
			
			for(; i < numLongs; i++)
				dis.readByte();

			// group values
			newMonster.groupSize = dis.readByte();
			newMonster.groupNumber = dis.readByte();
			
			byte temp = dis.readByte();
			if(temp != Util.NOTHING)
				newMonster.companionType = MonsterClass.type(temp);
			else
				newMonster.companionType = null;
			
			newMonster.itemID = dis.readShort();
			newMonster.secondItemID = dis.readShort();
			newMonster.specificCompanionID = dis.readShort();
			
			newMonster.setName(dis.readUTF());
			newMonster.setDescription(dis.readUTF());			
		}
		catch(Exception e)
		{
			System.err.println("Error loading monster.\nError: " + e);
			return null;
		}
		return newMonster;
	}
}
