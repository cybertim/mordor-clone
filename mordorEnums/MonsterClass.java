package mordorEnums;

import mordorHelpers.Util;

public enum MonsterClass
{
	Animals(0, 2),
	Cleanups(1, 2),
	Demons(2, 3),
	Devils(3, 3),
	Dragons(4, 6),
	Elementals(5, 3),
	Giants(6, 4),
	Humanoids(7, 4),
	Indiginis(8, 3),
	Insects(9, 2),
	Lycanthropes(10, 4),
	Mages(11, 4),
	Mythicals(12, 5),
	Reptiles(13, 2),
	Thieves(14, 4),
	Undeads(15, 3),
	Warriors(16, 4),
	WaterDwellers(17, 4);
	
	public static final byte MINWEALTHMULTIPLIER = 2;
	public static final byte MAXWEALTHMULTIPLIER = 9;
	
	private byte typeVal, scroogeFactor;
	MonsterClass(int nVal, int wealthMultiplier) { typeVal = (byte)nVal; scroogeFactor = Util.FITBYTE(wealthMultiplier, MINWEALTHMULTIPLIER, MAXWEALTHMULTIPLIER); }
	public byte value() { return typeVal; }
	public byte getWealthMultiplier() { return scroogeFactor; }
	public static final MonsterClass type(byte nVal)
	{
		for(MonsterClass mc : MonsterClass.values())
			if(mc.value() == nVal)
				return mc;
		
		return Animals;
	}
}
