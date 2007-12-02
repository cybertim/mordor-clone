package mordorEnums;

public enum BodyParts
{
	Objects(0), // For none body parts.
	Bindable(0), // For objects that can bind to the player.
	Weapon(1),
	Shield(2),
	Torso(3),
	Head(4),
	Hands(5),
	Shoulders(6),
	Wrist(7),
	Sash(8),
	Waist(9),
	Foot(10),
	Finger(11),
	Neck(12),
	Crest(13);
	
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
