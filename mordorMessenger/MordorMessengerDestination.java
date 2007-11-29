package mordorMessenger;

/**
 * Enum for MordorMessenger. Offers a selection of destinations for messages.
 * This allows for effective routing of a variety of types of messages through
 * a single listener. Don't know if this is unnecessarily complex.
 * @author August Junkala, Nov. 24, 2007
 *
 */
public enum MordorMessengerDestination
{
	MessagePane(0),
	ItemInfo(1),
	MonsterInfo(2),
	StoreBuy(3),
	StoreMod(4),
	Special(5);
	
	private byte type;
	MordorMessengerDestination(int nType) { type = (byte)nType; }
	
	public byte value() { return type; }
}
