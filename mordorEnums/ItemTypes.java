package mordorEnums;

public enum ItemTypes
{
	Hands(0),
	Dagger(1),
	Cross(2),
	Sword(3),
	Staff(4),
	Mace(5),
	Axe(6),
	Hammer(7), // Last weapon type in this group.
	LeatherArmor(8),
	ChainArmor(9),
	PlateArmor(10),
	Shield(11),
	Cap(12),
	Helmet(13),
	Gloves(14),
	Gauntlets(15),
	Cloak(16),
	Bracers(17),
	Sash(18),
	Belt(19),
	Boots(20),
	Ring(21),
	Amulet(22),
	Potion(23),
	Scroll(24),
	Tome(25),
	Dust(26),
	Crystal(27),
	Rod(28), // Weapon
	Stone(29),
	Sphere(30),
	Cube(31),
	Artifact(32),
	GuildCrest(33),
	Miscellaneous(34);

	private final byte itemType;
	ItemTypes(int nItemType)
	{
		itemType = (byte)nItemType;
	}
	
	public byte value()
	{
		return itemType;
	}
	
	/**
	 * Is this type a weapon.
	 * @return True if it is a weapon.
	 */
	public boolean isWeapon()
	{
		return (itemType == 28 || itemType <= 7);
	}
	
	/**
	 * Is this a usable type of item (e.g. potions)
	 * This does not include if a spell can be cast from the item.
	 * @return	True if it is a usable type.
	 */
	public boolean isUsable()
	{
		return (itemType != 28 && itemType <= 31 && itemType >= 23);
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
	
	public static boolean isWeaponStatic(byte nItemType)
	{
		return nItemType == ItemTypes.Rod.value() || (nItemType >= ItemTypes.Hands.value() && nItemType <= ItemTypes.Hammer.value());
	}
}
