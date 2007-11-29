package mordorEnums;

public enum MonsterAbility
{	
	AllMagicResist(0, "Resistant to all magic"),
	WeaponResist(1, "Resistant to weapons"),
	ChriticalHit(2, "Can critically hit"),
	SeeInvisible(3, "See Invisible"),
	Invisible(4, "Invisible"),
	CharmResist(5, "Resistant to charms"),
	Steal(6, "Can steal"),
	Poison(7, "Can poison"),
	Stone(8, "Can stone"),
	Disease(9, "Can disease"),
	Paralyze(10, "Can paralyze"),
	Drain(11, "Can drain"),
	Age(12, "Can age"),
	Backstab(13, "Can backstab"),
	Electrocute(14, "Can electrocute"),
	BreatheFire(15, "Can breathe fire"),
	BreatheCold(16, "Can breathe cold"),
	DestroyItems(17, "Can destroy items"),
	SpitAcd(18, "Can spit acid");
	
	private byte typeVal;
	private String abilityText;
	MonsterAbility(int nVal, String nAbilityText)
	{
		typeVal = (byte)nVal;
		abilityText = nAbilityText;
	}
	public byte value() { return typeVal; }
	public String toString() { return abilityText; }
	public String[] getStrings()
	{
		String[] strings = new String[MonsterAbility.values().length];
		for(byte i = 0; i < strings.length; i++)
			strings[i] = MonsterAbility.type(i).toString();
		return strings;
	}
	public static final MonsterAbility type(byte nVal)
	{
		for(MonsterAbility ma : MonsterAbility.values())
			if(ma.value() == nVal)
				return ma;
		
		return Steal;
	}
}
