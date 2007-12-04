package mordorGame;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import mordorMessenger.MordorMessenger;
import mordorMessenger.MordorMessengerDestination;
import mordorMessenger.MordorMessengerEvent;
import mordorMessenger.MordorMessengerListener;

public class MessageFrame extends JInternalFrame implements MordorMessengerListener 
{
	private JTextArea messageWindow;
	private MordorMessenger messenger;
	
	MessageFrame(MordorMessenger nMessenger)
	{
		messenger = nMessenger;
		messenger.addMordorMessengerListener(this);
		
		messageWindow = new JTextArea(3, 20);
		messageWindow.setEditable(false);
		messageWindow.append("Welcome!\n");
		
		add(new JScrollPane(messageWindow));
		pack();
	}
	
	public void messagePosted(MordorMessengerEvent message)
	{
		if(message.getDestination() == MordorMessengerDestination.MessagePane)
		{
			messageWindow.insert(message.getMessage() + "\n", 0);
			messageWindow.setCaretPosition(0);
		}
	}
}
