package mordorGame;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;

import mordorMessenger.MordorMessenger;
import mordorMessenger.MordorMessengerEvent;
import mordorMessenger.MordorMessengerListener;


public class LoadingScreen extends JFrame implements MordorMessengerListener
{
	private JLabel display;
	private JLabel status;
	
	public LoadingScreen(String welcomeString, MordorMessenger nMessenger)
	{
		nMessenger.addMordorMessengerListener(this);
		display = new JLabel(welcomeString);
		status = new JLabel();
		nMessenger.postMessage("Intializing...");
		
		setLayout(new GridLayout(2, 1));
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(300, 300);
		setLocation((screenSize.width >> 1) - 150, (screenSize.height >> 1) - 150);
		add(display);
		add(status);
		setVisible(true);
	}

	public void messagePosted(MordorMessengerEvent message)
	{
		status.setText(message.getMessage());
	}

}
