package mordorEnums;

public enum BodyParts
{
	None(0), // For none body parts.
	Hands(1),
	Weapon(2),
	Shield(3),
	Torso(4),
	Head(5),
	Gloves(6),
	Cloak(7),
	Bracers(8),
	Sash(9),
	Belt(10),
	Boots(11),
	Finger(12),
	Neck(13),
	Crest(14);
	
	private final byte bodyPart;
	BodyParts(int nBodyPart)
	{
		bodyPart = (byte)nBodyPart;
	}
	
	public byte value()
	{
		return bodyPart;
	}
	
	public static BodyParts type(int val)
	{
		for(BodyParts bp : BodyParts.values())
			if(bp.value() == val)
				return bp;
		
		return Hands;
	}
}
