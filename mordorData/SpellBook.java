package mordorData;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import structures.LinkedList;
import structures.ListIter;
import structures.SkipIter;
import structures.SkipList;

import mordorEnums.SpellClass;
import mordorHelpers.Util;


public class SpellBook
{
	private SkipList<SpellReference> spells;
	
	private static final short MAXSPELLS = Short.MAX_VALUE;
	
	SpellBook()
	{
		spells = new SkipList<SpellReference>();
	}
	
	/**
	 * Retrieve a linked list off all the spells in a particular class.
	 * @param classToFind	The spell class to search for.
	 * @return LinkedList<SpellReference>
	 */
	public LinkedList<SpellReference> getSpellClass(SpellClass classToFind)
	{
		LinkedList<SpellReference> classSpells = new LinkedList<SpellReference>();
		SkipIter<SpellReference> tSpell = spells.getIterator();
		
		if(tSpell == null)
			return null;
		
		while(tSpell.next())
			if(tSpell.element().getSpellClass() == classToFind)
				classSpells.insert(tSpell.element());
		
		return classSpells;
	}
	
	/**
	 * Retrieves a string array of the names of all the spells of a particular class.
	 * @param classToFind
	 * @return
	 */
	public String[] getSpellClassNames(SpellClass classToFind)
	{
		LinkedList<SpellReference> classSpells = getSpellClass(classToFind);
		if(classSpells == null || classSpells.getSize() < 1)
			return null;
		
		String[] spellNames = new String[classSpells.getSize()];
		ListIter<SpellReference> sNode = classSpells.getIterator();
		int count = 0;
		while(sNode.next())
		{
			spellNames[count] = sNode.element().getSpell().getName();
			count++;
		}
		
		return spellNames;
	}
	
	public short[] getSpellClassIDs(SpellClass classToFind)
	{
		LinkedList<SpellReference> classSpells = getSpellClass(classToFind);
		if(classSpells.getSize() < 1)
			return null;
		
		short[] spellIDs = new short[classSpells.getSize()];
		ListIter<SpellReference> sNode = classSpells.getIterator();
		int count = 0;
		while(sNode.next())
		{
			spellIDs[count] = sNode.element().getSpellID();
			count++;
		}
		
		return spellIDs;
	}
	
	/**
	 * Retrieves all spells
	 * @return SkipList<Spell>
	 */
	public SkipList<SpellReference> getAllSpells()
	{
		return spells;
	}
	
	/**
	 * Inserts a new spell into the spell book, if it doesn't already
	 * exist.
	 * @param newSpell	The new spell.
	 * @return boolean True if the spell was inserted.
	 */
	public boolean insertSpell(SpellReference newSpell)
	{
		if(spells.isEmpty())
		{
			spells.insert(newSpell, (int)newSpell.getSpell().getID());
			return true;
		}
		
		// Check if this ID or Name already exists. If so we can't add this spell.
		if(getSpell(newSpell.getSpellID()) != null || newSpell.getSpellID() < 0 || getSpell(newSpell.getSpell().getName()) != null)
			return false;
		
		spells.insert(newSpell, (int)newSpell.getSpellID());
		return true;
	}
	
	/**
	 * For use with player's spellbook only. The either inserts the 
	 * spell (if it doesn't already exist) Or it updates the spell already
	 * in the book with the level of this one. Intended for when the player
	 * learns a spell with a lower level at another guild.
	 * @param newSpell	The spell being updated (Spell)
	 */
	public void updateSpell(SpellReference newSpell)
	{
		SpellReference tSpell = spells.find((int)newSpell.getSpell().getID());
		
		if(tSpell == null)
			insertSpell(newSpell);
		else
		{
			// purpose? update spell level. for when a player learns a 
			// new spell in a different, better guild.
			tSpell.setLevel(newSpell.getLevel());
		}
	}
	
	/**
	 * Retrieves a spell from the spell book
	 * @param spellID
	 * @return
	 */
	public SpellReference getSpell(short spellID)
	{
		return spells.find((int)spellID);
	}
	
	public SpellReference getSpell(String spellName)
	{
		SkipIter<SpellReference> tNode = spells.getIterator();
		
		while(tNode.next())
			if(tNode.element().getSpell().getName().equalsIgnoreCase(spellName))
				return tNode.element();
		
		return null;
	}
	
	/**
	 * Creates a new, default, spell of the specified spell family.
	 * @param spellType	Family type the spell is of.
	 * @return Spell	The spell created.
	 */
	public SpellReference newSpell(SpellClass newSpellClass)
	{
		if(spells.getSize() > MAXSPELLS)
			return null;

		SkipIter<SpellReference> tSpell = spells.getIterator();
		short newSpellID = 0;
			
		while(tSpell.next())
		{
			if(tSpell.key() > newSpellID)
				break;
			
			newSpellID++;
		}
		
		SpellReference newSpell = new SpellReference(new Spell(Util.NOSTRING, newSpellID), (short)0, newSpellClass);
		
		return (insertSpell(newSpell)) ? newSpell : null;
	}
	
	/**
	 * Removes a spell from the spell book.
	 * @param oldSpell		Spell to remove (Spell)
	 * @param spellFamily	Spell family it is in (byte, see TYPE_X)
	 * @return Always return true.
	 */
	public boolean removeSpell(SpellReference oldSpell)
	{
		spells.remove((int)oldSpell.getSpell().getID());
	//	spellClasses[oldSpell.getSpellFamily()].removeSpell(oldSpell);
		return true;
	}
	
	/**
	 * Clears all empty spells.
	 *
	 */
	public void clearEmptySpells()
	{
		SkipIter<SpellReference> tNode = spells.getIterator();
		while(tNode.next())
		{
			if(tNode.element().getSpell().getName().equalsIgnoreCase(Util.NOSTRING))
				spells.remove(tNode.key());
		}
	}
	
	
	public short getNumberSpells()
	{
		return (short)spells.getSize();
	}
    
    public boolean writeSpellBook(DataOutputStream dos)
    {
        
        try
        {
            dos.writeInt(spells.getSize());
            if(spells.getSize() < 1)
                return true;
            
            SkipIter<SpellReference> tNode = spells.getIterator();
            
            while(tNode.next())
                tNode.element().writeSpellRef(dos);
            
        }
        catch(Exception e)
        {
            System.err.println("Error writing spellBook. Error: " + e);
            return false;
        }
        
        return true;
    }
    
    public static final SpellBook readSpellBook(DataInputStream dis, LinkedList<SpellReference> spellRefLoadList)
    {
        SpellBook newSpellBook = null;
        try
        {
            newSpellBook = new SpellBook();
            int spellCount = dis.readInt();
            
            if(spellCount < 1)
                return newSpellBook;
            
            SpellReference tSpell = null;
            
            for(int i = 0; i < spellCount; i++)
            {
                tSpell = SpellReference.readSpellRef(dis);
                if(tSpell != null)
                {
                    newSpellBook.getAllSpells().insert(tSpell, (int)tSpell.getSpellID());
                    spellRefLoadList.insert(tSpell);
                }
            }
        }
        catch(Exception e)
        {
            System.err.println("Error reading spellBook. Error: " + e);
            return null;
        }
        return newSpellBook;
    }
}
