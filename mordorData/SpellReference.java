package mordorData;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import mordorEnums.SpellClass;
import mordorHelpers.Util;

/**
 * Special structure for using spells with guilds and players. Stores a
 * reference to the spell and the current spell base level.
 * @author August Junkala. April 26, 2007
 *
 */
public class SpellReference
{
	private short spellID;
    private Spell spell;
	private short level;
	private SpellClass spellClass;
	
	SpellReference(Spell newSpell, short newLevel, SpellClass newSpellClass)
	{
		spellID = newSpell.getID();
        spell = newSpell;
		level = newLevel;
		spellClass = newSpellClass;
	}
    
    SpellReference()
    {
        spell = null;
        spellID = Util.NOTHING;
        level = 0;
        spellClass = SpellClass.Fire;
    }
	
	/**
	 * Retrieves the spell this reference references.
	 * @return Spell
	 */
	public short getSpellID()
	{
		return spellID;
	}
    
    public Spell getSpell()
    {
        return spell;
    }
	
	/**
	 * Retrieves the spell level linked to this spell.
	 * @return short Spell's incidence level.
	 */
	public short getLevel()
	{
		return level;
	}
	
	/**
	 * Retrieves the family a spell is in.
	 * @return
	 */
	public SpellClass getSpellClass()
	{
		return spellClass;
	}
	
	/**
	 * Changes the level linked to the spell in this instance.
	 * @param newLevel
	 */
	public void setLevel(short newLevel)
	{
		level = newLevel;
	}
    
    public void setSpellClass(SpellClass newSpellClass)
    {
        spellClass = newSpellClass;
    }
    
    public void setSpellID(short nSpellID)
    {
        spellID = nSpellID;
    }
	
	/**
	 * Replicate this spell reference.
	 * @return SpellReference the new copy.
	 */
	public SpellReference copyRef()
	{
		return new SpellReference(spell, level, spellClass);
	}
    
	/**
	 * Resets this reference to a new spell.
	 * @param newSpell Spell to be set to.
	 * @return true if successful
	 */
    public boolean setSpell(Spell newSpell)
    {
        if(newSpell == null)
            return false;
        
        spell = newSpell;
        spellID = newSpell.getID();
        return true;
    }
    
    /**
     * Write this reference to a data stream
     * @param dos DataOutputStream to write to
     * @return true if successful
     */
    public boolean writeSpellRef(DataOutputStream dos)
    {
        try
        {
            dos.writeShort(spellID);
            dos.writeShort(level);
            dos.writeByte((byte)spellClass.value());
        }
        catch(Exception e)
        {
            System.err.println("Error writing spell. ID: " + spellID + " Error: " + e);
            return false;
        }
        return false;
    }
    
    /**
     * Read a spell reference from a data stream.
     * @param dis DataInputStream to read from
     * @return true if successful
     */
    public static final SpellReference readSpellRef(DataInputStream dis)
    {
        SpellReference nSpell = null;
        try
        {
            nSpell = new SpellReference();
            
            nSpell.setSpellID(dis.readShort());
            nSpell.setLevel(dis.readShort());
            nSpell.setSpellClass(SpellClass.type(dis.readByte()));
        }
        catch(Exception e)
        {
            System.err.println("Error reading spell. Error: " + e);
            return null;
        }
        return nSpell;
    }
}
