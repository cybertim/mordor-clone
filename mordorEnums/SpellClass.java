package mordorEnums;

public enum SpellClass
{
	Fire(0),
	Cold(1),
	Electrical(2),
	Mind(3),
	Damange(4),
	Element(5),
	Kill(6),
	Charm(7),
	Bind(8),
	Heal(9),
	Movement(10),
	Banish(11),
	Dispel(12),
	Resistant(13),
	Visual(14),
	Magical(15),
	Location(16),
	Protection(17);

	private final byte spellClass;
	SpellClass(int nSpellClass)
	{
		spellClass = (byte)nSpellClass;
	}
	
	public byte value()
	{
		return spellClass;
	}
	
	/**
	 * Get the type from a value.
	 * @param val int.
	 * @return
	 */
	public static SpellClass type(int val)
	{
		for(SpellClass sc : SpellClass.values())
			if(sc.value() == val)
				return sc;
		
		return Fire;
	}
}
