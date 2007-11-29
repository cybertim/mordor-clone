package mordorData;


/**
 * Guild Reference. For use with items to store the guilds allowed for an item
 * and at which level they are usable;
 * @author Augo
 *
 */
public class GuildReference
{
	private Guild guild;
	private short level; // Minimum guild level at which it is usable.
	
	GuildReference(Guild newGuild, short newLevel)
	{
		guild = newGuild;
		setLevel(newLevel);
	}
	
	public Guild getGuild()
	{
		return guild;
	}
	
	public short getLevel()
	{
		return level;
	}
	
	public void setLevel(short newLevel)
	{
		if(newLevel < 0)
			level = 0;
		else if(newLevel > Short.MAX_VALUE)
			level = Short.MAX_VALUE;
		else
			level = newLevel;
	}
}
