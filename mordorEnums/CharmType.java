package mordorEnums;

/**
 * Enum for types for charms.
 * @author August Junkala (Sept 18, 2007)
 *
 */
public enum CharmType
{
	All(0, null),			// NOTE: These should be the ONLY null types.
	Monster(1, null),		// classAllowed parses them differently.
	Animals(2, new MonsterClass[]{ MonsterClass.Animals, MonsterClass.Reptiles }),
	Humanoids(3, new MonsterClass[]{ MonsterClass.Humanoids, MonsterClass.Mages, MonsterClass.Thieves, MonsterClass.Warriors }),
	Elementals(4, new MonsterClass[]{ MonsterClass.Elementals }),
	Giants(5, new MonsterClass[]{ MonsterClass.Giants }),
	Demons(6, new MonsterClass[]{ MonsterClass.Demons }),
	Devils(7, new MonsterClass[]{ MonsterClass.Devils }),
	Dragons(8, new MonsterClass[]{ MonsterClass.Dragons }),
	Insects(9, new MonsterClass[]{ MonsterClass.Insects }),
	Undead(10, new MonsterClass[]{ MonsterClass.Undeads });
	
	private byte typeVal;
	private MonsterClass[] acceptedClasses;
	CharmType(int nVal, MonsterClass[] nAcceptedClasses)
	{
		typeVal = (byte)nVal;
		acceptedClasses = nAcceptedClasses;
	}
	public byte value() { return typeVal; }
	public boolean classAllowed(MonsterClass mc)
	{
		if(this == All)
			return true;
		
		if(this == Monster)
		{
			// Parse through the whole list, if at any time this monster class shows up somewhere,
			// than the monster class isn't for it (there is a specific charm type for it that should
			// be used instead.)
			for(CharmType ct : CharmType.values())
				if(ct != Monster && ct != All)
					if(ct.classAllowed(mc))
						return false;
		}
		
		// A non-standard null accepted list, obviously we don't accept it.
		if(acceptedClasses == null)
			return false;
		
		for(byte i = 0; i < acceptedClasses.length; i++)
			if(acceptedClasses[i] == mc)
				return true;
		
		return false;
	}
	
	public static final CharmType type(byte nVal)
	{
		for(CharmType ct : CharmType.values())
			if(ct.value() == nVal)
				return ct;
		
		return All;
	}
}
