package mordorEnums;

public enum Identification
{
	Nothing(0, "You know nothing."),
	Little(1, "You know a little."),
	Lots(2, "You know a lot."),
	Everything(3, "You know everything.");
	
	private byte typeVal;
	private String idString;
	Identification(int nVal, String nIdString)
	{
		typeVal = (byte)nVal;
		idString = nIdString;
	}
	
	public byte value() { return typeVal; }
	public String idString() { return idString; }
	
	public static final Identification type(byte nVal)
	{
		for(Identification id : Identification.values())
			if(id.value() == nVal)
				return id;
		
		return Nothing;
	}
	
}
