package mordorEnums;

public enum ItemTypes
{
	Hands(0, BodyParts.Weapon),
	Dagger(1, BodyParts.Weapon),
	Cross(2, BodyParts.Weapon),
	Sword(3, BodyParts.Weapon),
	Staff(4, BodyParts.Weapon),
	Mace(5, BodyParts.Weapon),
	Axe(6, BodyParts.Weapon),
	Hammer(7, BodyParts.Weapon),
	LeatherArmor(8, BodyParts.Torso),
	ChainArmor(9, BodyParts.Torso),
	PlateArmor(10, BodyParts.Torso),
	Shield(11, BodyParts.Shield),
	Cap(12, BodyParts.Head),
	Helmet(13, BodyParts.Head),
	Gloves(14, BodyParts.Hands),
	Gauntlets(15, BodyParts.Hands),
	Cloak(16, BodyParts.Shoulders),
	Bracers(17, BodyParts.Wrist),
	Sash(18, BodyParts.Sash),
	Belt(19, BodyParts.Waist),
	Boots(20, BodyParts.Foot),
	Ring(21, BodyParts.Finger),
	Amulet(22, BodyParts.Neck),
	Potion(23, BodyParts.Objects),
	Scroll(24, BodyParts.Objects),
	Tome(25, BodyParts.Objects),
	Dust(26, BodyParts.Objects),
	Crystal(27, BodyParts.Objects),
	Rod(28, BodyParts.Weapon),
	Stone(29, BodyParts.Objects),
	Sphere(30, BodyParts.Objects),
	Cube(31, BodyParts.Objects),
	Artifact(32, BodyParts.Bindable),
	GuildCrest(33, BodyParts.Objects),
	Miscellaneous(34, BodyParts.Bindable);

	private final byte itemType;
	private BodyParts equippingPart;
	ItemTypes(int nItemType, BodyParts equipped)
	{
		itemType = (byte)nItemType;
		equippingPart = equipped;
	}
	
	public byte value()
	{
		return itemType;
	}
	
	/**
	 * Retrieve the part of the body that equips this item type.
	 * @return BodyParts
	 */
	public BodyParts getEquippingPart()
	{
		return equippingPart;
	}
	
	/**
	 * Get the type from a value.
	 * @param val int.
	 * @return
	 */
	public static ItemTypes type(int val)
	{
		for(ItemTypes it : ItemTypes.values())
			if(it.value() == val)
				return it;
		
		return Hands;
	}
}
