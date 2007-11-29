package mordorMessenger;
import java.util.EventListener;

/**
 * Listener class for sending text strings via the MordorMessesngerSphere
 * @author August Junkala, April 16, 2007
 *
 */

public interface MordorMessengerListener extends EventListener
{
	void messagePosted(MordorMessengerEvent message);
}
