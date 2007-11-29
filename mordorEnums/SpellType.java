package mordorEnums;

/**
 * Enum created for the different types of spells. Differs from spell class
 * in that this controls what goes on in the game, while spell class (for the most part)
 * controls how things are organized for the player.
 * @author August Junkala Sept 10, 2007
 *
 */
public enum SpellType
{
	Damage(0),
	Kill(1),
	State(2),
	Charm(3),
	Bind(4),
	Health(5),
	Movement(6),
	Map(7),
	Resistance(8),
	Special(9);
	
	public enum SubtypeDamage
	{
		Class(0), 	// Based on the SpellClass
		Drain(1), 	// Drains opponent
		Acid(2); 	// Sprays opponent 
		private byte typeVal;
		SubtypeDamage(int nVal) { typeVal = (byte)nVal; };
		public byte value() { return typeVal; }
		public static SubtypeDamage type(int val)
		{
			for(SubtypeDamage st : SubtypeDamage.values())
				if(st.value() == val)
					return st;
			return Class;
		}
	}
	
	public enum SubtypeKill
	{
		Class(0), 		// Based on the SpellClass
		Undead(1), 		// Dispels any undead.
		Elemental(2), 	// Dispels any elemental
		Devil(3),		// Banish any devil
		Demon(4);		// Banish any demon
		private byte typeVal;
		SubtypeKill(int nVal) { typeVal = (byte)nVal; };
		public byte value() { return typeVal; }
		public static SubtypeKill type(int val)
		{
			for(SubtypeKill st : SubtypeKill.values())
				if(st.value() == val)
					return st;
			return Class;
		}
	}
	
	public enum SubtypeHealth
	{
		Heal(0), 		// Adds health points
		Cure(1),		// Adds health points, removes poison/disease
		Raise(2),		// Removes dead state, sets health to 1
		Resurrect(3);	// Removes dead state, refills health points.
		private byte typeVal;
		SubtypeHealth(int nVal) { typeVal = (byte)nVal; };
		public byte value() { return typeVal; }
		public static SubtypeHealth type(int val)
		{
			for(SubtypeHealth st : SubtypeHealth.values())
				if(st.value() == val)
					return st;
			return Heal;
		}
	}
	
	public enum SubtypeSpecial
	{
		Open(0), 	// Open a locked ches
		Fate(1);	// Casts fate (randomizes HP/MP
		private byte typeVal;
		SubtypeSpecial(int nVal) { typeVal = (byte)nVal; };
		public byte value() { return typeVal; }
		public static SubtypeSpecial type(int val)
		{
			for(SubtypeSpecial st : SubtypeSpecial.values())
				if(st.value() == val)
					return st;
			return Open;
		}
	}
	
	public enum SubtypeMap
	{
		DetectRock(0), DetectDepth(1), DetectDirection(2), DetectLocation(3), SoulSearch(4), RetrieveSoul(5);
		private byte typeVal;
		SubtypeMap(int nVal) { typeVal = (byte)nVal; }
		public byte value() { return typeVal; }
		public static SubtypeMap type(int val)
		{
			for(SubtypeMap st : SubtypeMap.values())
				if(st.value() == val)
					return st;
			return DetectRock;
		}
	}
	
	public enum SubtypeMovement
	{
		// Teleport: Any x, y, or +/-3 z, Displacement: Any x, y, GetSanctuary: Specific x, y
		Displacement(0), Teleport(1), SetSanctuary(2), GetSanctuary(3), Portal(4), Levitate(5);
		private byte typeVal;
		SubtypeMovement(int nVal) { typeVal = (byte)nVal; }
		public byte value() { return typeVal; }
		public static SubtypeMovement type(int val)
		{
			for(SubtypeMovement st : SubtypeMovement.values())
				if(st.value() == val)
					return st;
			return Teleport;
		}
	}
	
	private byte typeValue;
	SpellType(int nTypeValue)
	{
		typeValue = (byte)nTypeValue;
	}
	
	public byte value()
	{
		return typeValue;
	}
	
	/**
	 * Get the type from a value.
	 * @param val int.
	 * @return
	 */
	public static SpellType type(int val)
	{
		for(SpellType st : SpellType.values())
			if(st.value() == val)
				return st;
		
		return Damage;
	}
}
