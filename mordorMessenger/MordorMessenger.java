package mordorMessenger;
import java.util.Vector;


public class MordorMessenger
{
	private Vector<MordorMessengerListener> messageListeners = new Vector<MordorMessengerListener>();
	
	/**
	 * Post a text message.
	 * @param nMessage
	 */
	public void postMessage(String nMessage)
	{
		fireMessagePost(nMessage);
	}
	
	/**
	 * Post a flag for a specific destination
	 * @param destination
	 */
	public void postFlag(MordorMessengerDestination destination)
	{
		fireTypedMessagePost("", destination, null);
	}
	
	/**
	 * Post an object to a specific destination.
	 * @param destination
	 * @param thing
	 */
	public void postThing(MordorMessengerDestination destination, Object thing)
	{
		fireTypedMessagePost("", destination, thing);
	}
	
	public void addMordorMessengerListener(MordorMessengerListener newListener)
	{
		if(messageListeners.contains(newListener))
			return;
		
		messageListeners.addElement(newListener);
	}
	
	private void fireMessagePost(String nMessage)
	{
		fireTypedMessagePost(nMessage, MordorMessengerDestination.MessagePane, null);
	}
	
	private void fireTypedMessagePost(String nMessage, MordorMessengerDestination destination, Object thing)
	{
		int size = messageListeners.size();
		if(size == 0)
			return;
		
		Vector nList = (Vector)messageListeners.clone();
		
		MordorMessengerEvent event = new MordorMessengerEvent(this, nMessage, destination, thing);
		for(int i = 0; i < size; i++)
		{
			MordorMessengerListener tListener = (MordorMessengerListener)nList.elementAt(i);
			tListener.messagePosted(event);
		}
	}
}