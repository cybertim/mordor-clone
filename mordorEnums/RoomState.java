package mordorEnums;

public enum RoomState
{
	Peace(0),
	Melee(1),
	Spell(2),
	OpeningChest(3);
	
	private byte type;
	RoomState(int nType) { type = (byte)nType; }
	public byte value() { return type; }
	public static final RoomState type(int nType)
	{
		for(RoomState rs : RoomState.values())
			if(rs.value() == nType)
				return rs;
		return Peace;
	}
	
}
