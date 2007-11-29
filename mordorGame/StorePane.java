package mordorGame;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import mordorData.DataBank;
import mordorData.Player;
import mordorData.Store;
import mordorMessenger.MordorMessengerDestination;
import mordorMessenger.MordorMessengerEvent;
import mordorMessenger.MordorMessengerListener;
import mordorShared.StoreInventory;

/**
 * Class for the store pane in the game.
 * @author August Junkala, Nov 26, 2007
 *
 */
public class StorePane extends JPanel implements ActionListener,
		MordorMessengerListener {
	
	private StoreInventory inventory;
	private JButton jbUncurse, jbCombine, jbInfoSell, jbSell, jbID, jbBuy, jbInfoBuy;
	private JLabel jlBuyCost, jlSellValue, jlIDCost, jlUncurseCost, jlCombineItems;
	private SICItemPanel ipSell, ipBuy, ipCombine, ipUncurse;
	
	private Player player;
	private DataBank databank;
	private Store store;
	
	public StorePane(Player activePlayer, DataBank theDatabank, Store theStore)
	{
		player = activePlayer;
		databank = theDatabank;
		store = theStore;
		
		inventory = new StoreInventory(store, false, databank.getMessenger());
		
		JPanel leftPane = new JPanel();
		JPanel rightPane = new JPanel();

		JPanel lTopPane = new JPanel();
		JPanel ltTopPane = new JPanel();
		JPanel ltBotPane = new JPanel();
		JPanel lBotPane = new JPanel();
		
		ltTopPane.setBorder(BorderFactory.createLoweredBevelBorder());
		ltTopPane.setLayout(new GridLayout(3, 1));
		
		JPanel ucItem = new JPanel();
		JPanel ucFunc = new JPanel();
		ipUncurse = new 
		
		
		
		lTopPane.setLayout(new BorderLayout());
		lTopPane.add(ltTopPane, BorderLayout.NORTH);
		lTopPane.add(ltBotPane, BorderLayout.SOUTH);
		
		leftPane.setLayout(new BorderLayout());
		leftPane.add(lTopPane, BorderLayout.NORTH);
		leftPane.add(lBotPane, BorderLayout.SOUTH);
		
		setLayout(new BorderLayout());
		add(leftPane, BorderLayout.WEST);
		add(rightPane, BorderLayout.EAST);
	}
	

	public void actionPerformed(ActionEvent e)
	{
		// TODO Auto-generated method stub

	}

	public void messagePosted(MordorMessengerEvent message)
	{
		if(message.getDestination() == MordorMessengerDestination.StoreBuy)
		{
			// Coming from store's inventory
		}
		else if(message.getDestination() == MordorMessengerDestination.StoreMod)
		{
			// Coming from player's inventory
		}
	}
}
