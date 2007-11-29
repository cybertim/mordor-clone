package mordorEnums;

public enum PlayerState
{
	Female(0, true, false, true),
	Dead(1, true, true, true),
	InDejenol(2, false, false, true),
	Drowning(3, false, false, true),
	Dropping(4, false, false, true),
	Fogged(5, false, false, true),
	Blind(6, true, true, false),
	SeeInvisible(7, true, true, false),
	Invisible(8, true, true, true),
	NoMagic(9, false, false, true),
	NoDirectionChange(10, false, false, true),
	Levitating(11, true, true, false),
	Poisoned(12, true, true, false),
	Stoned(13, true, true, false),
	Paralyzed(14, true, true, false),
	Diseased(15, true, true, false),
	LostLocation(16, false, false, true),
	LostDirection(17, false, false, true),
	LostLevel(18, false, false, true),
	Protected(19, true, true, true),
	None(20, true, true, true);
	
	private final byte state;
	private final boolean editor; // Can this state be changed in the editor.
	private final boolean effect; // Can this state be an effect
	private final boolean natural; // This is a natural state. I.E. not stackable.
	// Generally it is states that are set either overtly (e.g. female) or states
	// that are due to location (e.g. Fogged is caused by being in a fog square.)
	PlayerState(int nPlayerState, boolean nEdit, boolean nEffect, boolean nNatural)
	{
		state = (byte)nPlayerState;
		editor = nEdit;
		effect = nEffect;
		natural = nNatural;
	}
	
	public byte value()
	{
		return state;
	}
	
	/**
	 * Can this state be modified in the editor. E.g. removing 'fogged' when the
	 * character is in a foggy room makes no sense.
	 * @return true if it can be changed.
	 */
	public boolean canBeEdited()
	{
		return editor;
	}
	
	/**
	 * Can this state be the effect of an item, spell, etc.
	 * @return
	 */
	public boolean canBeEffect()
	{
		return effect;
	}
	
	public boolean isNaturalState()
	{
		return natural;
	}
	
	public static PlayerState type(int val)
	{
		for(PlayerState ps : PlayerState.values())
			if(ps.value() == val)
				return ps;
		
		return Female;
	}
	
	/**
	 * Retrieve the player state this effect value is equivalent to.
	 * @param val
	 * @return
	 */
	public static PlayerState effectType(int val)
	{
		PlayerState state;
		byte count = 0;
		
		for(PlayerState ps : PlayerState.values())
			if(ps.canBeEffect())
			{
				if(count == val)
					return ps;
				else
					count++;
			}
		
		return None;
	}
	
	public static PlayerState editableType(int val)
	{
		PlayerState state;
		byte count = 0;
		
		for(PlayerState ps : PlayerState.values())
			if(ps.canBeEdited())
			{
				if(count == val)
					return ps;
				else
					count++;
			}
		
		return None;
	}
	
	/**
	 * Retrieve a list of states that can be effects of items, spells, etc.
	 * @return PlayerState[]
	 */
	public static PlayerState[] effects()
	{
		byte count = 0;
		for(PlayerState ps : PlayerState.values())
			if(ps.canBeEffect())
				count++;

		PlayerState[] effectStates = new PlayerState[count];
		count = 0;
		for(PlayerState ps : PlayerState.values())
			if(ps.canBeEffect())
			{
				effectStates[count] = ps;
				count++;
			}
		
		return effectStates;
	}
	
	/**
	 * Retrieve a list of editable states.
	 * @param editable True if want editable, false if want uneditables.
	 * @return PlayerState[]
	 */
	public static PlayerState[] editables(boolean getEdit)
	{
		byte count = 0;
		for(PlayerState ps : PlayerState.values())
			if(ps.canBeEdited() == getEdit)
				count++;

		PlayerState[] editStates = new PlayerState[count];
		count = 0;
		for(PlayerState ps : PlayerState.values())
			if(ps.canBeEdited() == getEdit)
			{
				editStates[count] = ps;
				count++;
			}
		
		return editStates;
	}
}
