package mordorMessenger;
import java.util.EventObject;
/**
 * MorderMessengerEvent class used with MordorMessenger
 * @author August Junkala, April 16 2007
 *
 */


public class MordorMessengerEvent extends EventObject
{
	private String message;
	private MordorMessengerDestination destination;
	private Object thing;
	
	public MordorMessengerEvent(MordorMessenger source, String nMessage, MordorMessengerDestination dest, Object nThing)
	{
		super(source);
		message = nMessage;
		destination = dest;
		thing = nThing;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public MordorMessengerDestination getDestination()
	{
		return destination;
	}
	
	public Object getThing()
	{
		return thing;
	}
}