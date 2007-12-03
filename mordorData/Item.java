package mordorData;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import mordorEnums.Alignment;
import mordorEnums.BodyParts;
import mordorEnums.ItemTypes;
import mordorEnums.PlayerState;
import mordorEnums.Resistance;
import mordorEnums.Stats;

import structures.LinkedList;
import structures.ListIter;

public class Item extends MObject<ItemTypes>
{
//	This is for saving. This should be ID based on item type, NOT
//	incidence. e.g. all bronze swords give this value
	private byte chance, swings;
	private byte[] modifyStats;
	private ItemTypes itemType;
	private boolean[] alignment;
	private boolean unaligned;
	private boolean cursed;
	private boolean twoHanded;
	private float damageModifier;
	private LinkedList<GuildReference> guilds;
	private ItemSpecials specials[];
	private long itemValue;

	public static final byte ITEMSPECIAL_MAX = 16;
	public static final byte ITEMSPECIAL_MAXSPELLS = 1;
	
	public static final short MAXCHANCE = 100;
	public static final short MAXCHANCE_RARE = 4;
	public static final float MAXDAMAGEMODIFIER = 50.0f;
	public static final float MINDAMAGEMODIFIER = -50.0f;
	public static final short MAXATTACK = Short.MAX_VALUE;
	public static final short MAXDEFENSE = Short.MAX_VALUE;
	public static final short MINDEFENSE = Short.MIN_VALUE;
	public static final short MINATTACK = Short.MIN_VALUE;
	public static final byte STOREITEM = -1; // Level that indciates this is a store item.
	// Maximum number of swings for an item. Note: Differs from guild swings
	// since guild swings are earned.
	public static final byte MAXITEMSWINGS = 32;
	
	
	Item(short newID)
	{
		super(newID);

		level = -1;
		chance = 100;
		itemType = ItemTypes.Hands;
		cursed = false;
		swings = 0;
		twoHanded = false;
		damageModifier = 1.0f;
		itemValue = 125;
		
		modifyStats = new byte[Stats.values().length];
		for(byte i = 0; i < Stats.values().length; i++)
			modifyStats[i] = (byte)0;

		alignment = new boolean[Alignment.values().length];
		for(Alignment al : Alignment.values())
			alignment[al.value()] = true;
		
		unaligned = false;
		
		guilds = new LinkedList<GuildReference>();
		
		specials = new ItemSpecials[ITEMSPECIAL_MAX];
		
		for(int i = 0; i < ITEMSPECIAL_MAX; i++)
			specials[i] = new ItemSpecials(ItemSpecials.ITEMSPECIAL_NONE, (short)0, (short)0, (short)0);
	}
	
	/**
	 * Generates the description for this item.
	 * @param dataBank
	 * @return
	 */
	public String generateDescription(DataBank dataBank, boolean html)
	{
		String eol = (html) ? "<BR>" : "\n";
		String desc = (html) ? "<HTML>" : "";
		
		desc += "This item is a member of the " + itemType.toString() + " class." + eol + eol;
		desc += "Offering an A/D of " + attack + "/" + defense + " to a properly aligned character, ";
		BodyParts bp = itemType.getEquippingPart();
		desc += (bp != BodyParts.Weapon) ? " this item requires no hands." : (twoHanded) ? " this item requires two hands." : " this item requires one hand.";
		desc += eol + eol;
		desc += "This item also offers the following when equipped: ";
		int count = 0;
		for(byte i = 0; i < specials.length; i++)
		{
			if(specials[i].getType() != ItemSpecials.ITEMSPECIAL_NONE)
			{
				desc += " " + specials[i].getString(dataBank);
				count++;
			}
		}
		desc += (count == 0) ? "nothing." : "";
		desc += eol + eol;
		
		desc += "Requiring";
		count = 0;
		for(byte i = 0; i < stats.length; i++)
			if(stats[i] != 0)
			{
				desc += " " + stats[i] + Stats.type(i).toString();
				count++;
			}
		if(count == 0)
			desc += " nothing";
		desc += " to equip or use, this is a non-Class Restricted Item that is usable by the following Guilds: ";
		
		ListIter<GuildReference> tGuild = guilds.getIterator();
		count = 0;
		
		while(tGuild.next())
		{
			if(count != 0)
				desc += ", ";
			desc += tGuild.element().getGuild().getName() + " (" + tGuild.element().getLevel() + ")";
			count++;
		}
		
		if(count == 0)
			desc += "none";
		desc += (html) ? ".</HTML>" : ".";
		
		return desc;
	}
	
	public byte getSwings()
	{
		return swings;
	}
	
	public short getChance()
	{
		return chance;
	}
	
	/**
	 * Retrieve the stat adjustment for a stat
	 * @param statType
	 * @return
	 */
	public byte getStatAdjustment(Stats statType)
	{
		return modifyStats[statType.value()];
	}
	
	/**
	 * Retrieves the item type of this item.
	 * @return ItemTypes
	 */
	public ItemTypes getItemType()
	{
		return itemType;
	}
	
	/**
	 * Retrieve whether the given alignment is allowed for this item.
	 * @param nAlignment
	 * @return true if the alignment is allowed, or if unaligned.
	 */
	public boolean getAlignment(Alignment nAlignment)
	{
		return (unaligned) ? true : alignment[nAlignment.value()];
	}
	
	/**
	 * Determine whether this item is unaligned. This is based on
	 * @return True if the item is unaligned.
	 */
	public boolean isUnaligned()
	{
		return unaligned;
	}
	
	/**
	 * Determine whether this item is a store specific item.
	 * That is, an item that is guaranteed to be in the store.
	 * @return true if it is a store specific item.
	 */
	public boolean isStoreItem()
	{
		return (level == STOREITEM);
	}
	
	/**
	 * Determine whether this item is normally cursed.
	 * @return true if this is a cursed item
	 */
	public boolean isCursed()
	{
		return cursed;
	}
	
	/**
	 * Determine if this item takes two hands.
	 * @return true if this is a two handed item
	 */
	public boolean isTwoHanded()
	{
		return twoHanded;
	}
	
	/**
	 * Determines if this item has a critical hit enhancement.
	 * @return True if it does.
	 */
	public boolean hasCriticalHit()
	{
		for(int i = 0; i < specials.length; i++)
			if(specials[i].getType() == ItemSpecials.ITEMSPECIAL_OTHER && specials[i].isCriticalHit())
				return true;
		
		return false;
	}
	
	/**
	 * Determines if this item has backstabbing enhancement.
	 * @return True if it does.
	 */
	public boolean hasBackStabbing()
	{
		for(int i = 0; i < specials.length; i++)
			if(specials[i].getType() == ItemSpecials.ITEMSPECIAL_OTHER && specials[i].isBackStab())
				return true;
		
		return false;
	}
	
	/**
	 * Does this item cast spells
	 * @param dataBank
	 * @return
	 */
	public boolean isSpellCaster(DataBank dataBank)
	{
		return (getSpell(dataBank) != null);
	}
	
	public short getAttackModifier()
	{
		return attack;
	}
	
	public short getDefenseModifier()
	{
		return defense;
	}
	
	public short getNumberOfSwings()
	{
		return swings;
	}
	
	public float getDamageModifier()
	{
		return damageModifier;
	}
	
	public LinkedList<GuildReference> getGuilds()
	{
		return guilds;
	}
	
	public GuildReference getGuild(Guild sGuild)
	{
		//ListNode<GuildReference> tNode = guilds.getFirstNode();
		ListIter<GuildReference> tNode = guilds.getIterator();
		
		while(tNode.next())
		{
			if(tNode.element().getGuild() == sGuild)
				return tNode.element();
			//tNode = tNode.getNext();
		}
		
		return null;
	}
	
	public ItemSpecials[] getSpecials()
	{
		return specials;
	}
	
	public SpellReference getSpell(DataBank dataBank)
	{
		for(byte i = 0; i < ITEMSPECIAL_MAX; i++)
		{
			if(specials[i].getType() == ItemSpecials.ITEMSPECIAL_SPELL)
				return specials[i].getSpell(dataBank);
		}
		
		return null;
	}
	
	/**
	 * Retrieve the number of spell casts per trip in the dungeon
	 * this item has.
	 * @return short
	 */
	public short getSpellCasts()
	{
		for(byte i = 0; i < ITEMSPECIAL_MAX; i++)
		{
			if(specials[i].getType() == ItemSpecials.ITEMSPECIAL_SPELL)
				return specials[i].getSpellCasts();
		}
		
		return 0;
	}
	
	/**
	 * Retrieve the base value for this item. That is, how much
	 * it costs.
	 * @return long
	 */
	public long getItemBaseValue()
	{
		return itemValue;
	}
	
	/**
	 * Retrieves the amount of resistance of a specific type offered.
	 * @param resistanceType
	 * @return
	 */
	public byte getResistance(Resistance resistanceType)
	{
		for(byte i = 0; i < ITEMSPECIAL_MAX; i++)
			if(specials[i].getType() == ItemSpecials.ITEMSPECIAL_RESISTANCE && specials[i].getResistanceType() == resistanceType)
				return specials[i].getResistanceAmount();
		
		return 0;
	}
	
	public boolean stateOn(PlayerState stateType)
	{
		for(byte i = 0; i < ITEMSPECIAL_MAX; i++)
			if(specials[i].getType() == ItemSpecials.ITEMSPECIAL_STATE && specials[i].getState() == stateType)
				return specials[i].isStateOn();
		
		return false;
	}
	
	public boolean stateOff(PlayerState stateType)
	{
		for(byte i = 0; i < ITEMSPECIAL_MAX; i++)
			if(specials[i].getType() == ItemSpecials.ITEMSPECIAL_STATE && specials[i].getState() == stateType)
				return !specials[i].isStateOn();
		
		return false;
	}
	
	public boolean isRare()
	{
		// true if chance is 4 or less
		return (chance < MAXCHANCE_RARE);
	}
	
	public void setChance(byte newChance)
	{
		if(newChance < 0)
			chance = 0;
		else if(newChance > MAXCHANCE)
			chance = MAXCHANCE;
		else
			chance = newChance;
	}
	
	/**
	 * Set the stat adjustment made for a stat
	 * @param statType
	 * @param newStatValue
	 */
	public void setStatsAdjustment(Stats statType, byte newStatValue)
	{
		modifyStats[statType.value()] = newStatValue;
	}
	
	public void setItemType(ItemTypes nItemType)
	{
		itemType = nItemType;
	}
	
	/**
	 * Sets whether a specific alignment is allowed.
	 * @param nAlignment
	 * @param allowed
	 */
	public void setAlignment(Alignment nAlignment, boolean allowed)
	{
		alignment[nAlignment.value()] = allowed;
	}
	
	public void setCursed(boolean newCursed)
	{
		cursed = newCursed;
	}
	
	public void setTwoHanded(boolean newHandedness)
	{
		twoHanded = newHandedness;
	}
	
	public void setAttack(short newAttack)
	{
		attack = newAttack;
	}
	
	public void setDefense(short newDefense)
	{
		defense = newDefense;
	}
	
	public void setSwings(byte newSwingNumber)
	{
		swings = newSwingNumber;
	}
	
	public void setDamageModifier(float newDamageModifier)
	{
		if(newDamageModifier < 0.0)
			damageModifier = 0.0f;
		else if(newDamageModifier > MAXDAMAGEMODIFIER)
			damageModifier = MAXDAMAGEMODIFIER;
		else
			damageModifier = newDamageModifier;
	}
	
	public GuildReference addGuild(Guild newGuild)
	{	
		if(getGuild(newGuild) != null)
			return null;
		
		GuildReference newGuildRef = new GuildReference(newGuild, (short)0);
		
		guilds.insert(newGuildRef);
		
		return newGuildRef;
	}
	
	public void setGuildLevel(Guild oldGuild, short newLevel)
	{
		GuildReference tGuildRef = getGuild(oldGuild);
		
		if(tGuildRef != null)
			tGuildRef.setLevel(newLevel);
	}
	
	public void removeGuild(Guild oldGuild)
	{
		GuildReference tGuildRef = getGuild(oldGuild);
		
		if(tGuildRef != null)
			guilds.remove(tGuildRef);
	}
	
	public void setItemBaseValue(long newBaseValue)
	{
		itemValue = newBaseValue;
	}
	
	public void setUnaligned(boolean newUnaligned)
	{
		unaligned = newUnaligned;
	}
	
	public ItemInstance createInstance()
	{
		return new ItemInstance(this);
	}
	
	public boolean writeItem(DataOutputStream dos)
	{
		try
		{
			dos.writeShort(ID);
			
			dos.writeBoolean(alignment[Alignment.Good.value()]);
			dos.writeBoolean(alignment[Alignment.Neutral.value()]);
			dos.writeBoolean(alignment[Alignment.Evil.value()]);
			dos.writeBoolean(cursed);
			dos.writeBoolean(twoHanded);
			dos.writeBoolean(unaligned);
			
			dos.writeByte(level);
			dos.writeByte(chance);
			dos.writeByte(itemType.value());
			
			dos.writeByte((byte)Stats.values().length);
			
			for(int i = 0; i < Stats.values().length; i++)
			{
				dos.writeByte(stats[i]);
				dos.writeByte(modifyStats[i]);
			}
			
			dos.writeShort(attack);
			dos.writeShort(defense);
			dos.writeByte(swings);
			
			dos.writeLong(itemValue);
			dos.writeFloat(damageModifier);
			
			for(int i = 0; i < ITEMSPECIAL_MAX; i++)
			{
				dos.writeByte(specials[i].getType());
				dos.writeShort(specials[i].getFirstVal());
				dos.writeShort(specials[i].getSecondVal());
				dos.writeShort(specials[i].getThirdVal());
			}
			
			dos.writeInt(guilds.getSize());
			{
				ListIter<GuildReference> tNode = guilds.getIterator();
				
				while(tNode.next())
				{
					dos.writeByte(tNode.element().getGuild().getID());
					dos.writeShort(tNode.element().getLevel());
				}
			}
			
			dos.writeUTF(name);
			dos.writeUTF(description);
		}
		catch(Exception e)
		{
			System.err.println("Item - writeItem : " + e);
			return false;
		}
		
		return true;
	}
	
	public static final Item readItem(DataInputStream dis, DataBank dataBank)
	{
		Item tItem = null;
		try
		{
			tItem = new Item(dis.readShort());
			
			tItem.setAlignment(Alignment.Good, dis.readBoolean());
			tItem.setAlignment(Alignment.Neutral, dis.readBoolean());
			tItem.setAlignment(Alignment.Evil, dis.readBoolean());
			
			tItem.setCursed(dis.readBoolean());
			tItem.setTwoHanded(dis.readBoolean());
			tItem.unaligned = dis.readBoolean();
			
			tItem.level = dis.readByte();
			tItem.setChance(dis.readByte());
			tItem.setItemType(ItemTypes.type(dis.readByte()));
			
			byte i;
			byte temp = dis.readByte();
			
			for(i = 0; i < Stats.values().length; i++)
			{
				if(i < temp)
				{
					tItem.stats[i] = dis.readByte();
					tItem.modifyStats[i] = dis.readByte();
				}
				else
				{
					tItem.stats[i] = 0;
					tItem.modifyStats[i] = 0;
				}
			}
			for(; i < temp; i++)
			{
				dis.readByte();
				dis.readByte();
			}
			
			tItem.setAttack(dis.readShort());
			tItem.setDefense(dis.readShort());
			tItem.setSwings(dis.readByte());
			
			tItem.setItemBaseValue(dis.readLong());
			tItem.setDamageModifier(dis.readFloat());
			
			for(i = 0; i < ITEMSPECIAL_MAX; i++)
			{
				tItem.getSpecials()[i].setType(dis.readByte());
				tItem.getSpecials()[i].setFirstVal(dis.readShort());
				tItem.getSpecials()[i].setSecondVal(dis.readShort());
				tItem.getSpecials()[i].setThirdVal(dis.readShort());
			}
			
			temp = (byte)dis.readInt();
			{
				Guild newGuild;
				GuildReference newGuildRef;
				for(i = 0; i < temp; i++)
				{
					newGuild = dataBank.getGuilds().find(Integer.valueOf(dis.readByte()));
					
					if(newGuild != null)
					{
						newGuildRef = tItem.addGuild(newGuild);
						
						newGuildRef.setLevel(dis.readShort());
					}
				}
			}
			
			tItem.setName(dis.readUTF());
			tItem.setDescription(dis.readUTF());
		}
		catch(Exception e)
		{
			
			System.err.println("Item - readItem : " + e);
			return null;
		}
		
		return tItem;
	}
	
	public String toString()
	{
		return name;
	}

	@Override
	public String generateDescription(boolean html)
	{
		// TODO Auto-generated method stub
		return description;
	}
}
