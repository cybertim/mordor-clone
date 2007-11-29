package mordorEnums;

/**
 * Enum for the different player skills.
 * @author August Junkala (Sept 15, 2007)
 *
 */
public enum PlayerSkill 
{
	Fighting(0),
	Magical(1),
	Thieving(2);
	
	private byte typeVal;
	PlayerSkill(int nVal) { typeVal = (byte)nVal; }
	public byte value() { return typeVal; }
	public static PlayerSkill type (byte nVal) 
	{
		for(PlayerSkill ps : PlayerSkill.values())
			if(ps.value() == nVal)
				return ps;
		
		return Fighting;
	}
}
