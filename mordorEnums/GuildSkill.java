package mordorEnums;

public enum GuildSkill
{
	Thieving(0),
	BackStabbing(1),
	CriticalHit(2),
	MultipleSwings(3),
	Fighting(4),
	Perception(5);
	
	private byte typeVal;
	GuildSkill(int nVal) { typeVal = (byte)nVal; }
	public byte value() { return typeVal; }
	public static GuildSkill type (byte nVal) 
	{
		for(GuildSkill gs : GuildSkill.values())
			if(gs.value() == nVal)
				return gs;
		
		return Thieving;
	}
}
