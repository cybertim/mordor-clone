package mordorEditor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import mordorData.DataBank;
import mordorEnums.ItemTypes;
import mordorHelpers.Util;
import mordorShared.StoreInventory;

/**
 * A store panel for the editor. 
 * @author August Junkala, Nov 24, 2007
 *
 */
public class StorePanel extends JPanel implements ActionListener
{
	private DataBank databank;
	private StoreInventory inventory;
	private JButton jbAdd, jbUpdate;
	private JComboBox jcbItemTypes, jcbItems;
	
	StorePanel(DataBank nDatabank)
	{
		databank = nDatabank;
		inventory = new StoreInventory(databank.getStore(), true, null);
		
		JPanel topBar = new JPanel();
		jbAdd = new JButton("+");
		jbUpdate = new JButton("Update Store");
		jbAdd.setToolTipText("Add this item");
		jbUpdate.setToolTipText("Update the store");
		jbAdd.addActionListener(this);
		jbUpdate.addActionListener(this);
		
		jcbItemTypes = new JComboBox(ItemTypes.values());
		jcbItems = new JComboBox(Util.NOSTRINGARRAY);
		jcbItemTypes.addActionListener(this);
		
		topBar.add(jcbItemTypes);
		topBar.add(jcbItems);
		topBar.add(jbAdd);
		topBar.add(jbUpdate);
		
		setLayout(new BorderLayout());
		add(topBar, BorderLayout.NORTH);
		
		JScrollPane inventoryPane = new JScrollPane(inventory);
		inventory.setBackground(this.getBackground());
		add(inventoryPane, BorderLayout.CENTER);
		
		// Set up the item list.
		changeItemType();
	}
	
	private void changeItemType()
	{
		String[] itemNames = databank.getItemNamesInClass((ItemTypes)jcbItemTypes.getSelectedItem());
		
		if(itemNames == null)
			jcbItems.setModel(new DefaultComboBoxModel(Util.NOSTRINGARRAY));
		else
			jcbItems.setModel(new DefaultComboBoxModel(itemNames));
	}
	
	public boolean updateStore()
	{
		return inventory.updateStore();
	}
	
	public void updatePanel()
	{
		inventory.updateInventory();
		inventory.updateList();
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == jbAdd)
		{
			// Get the item selected and add it
			if(!((String)jcbItems.getSelectedItem()).trim().equalsIgnoreCase(Util.NOSTRING))
			{
				inventory.addRecord(databank.getItem((String)jcbItems.getSelectedItem()));
				revalidate();
			}
		}
		else if(e.getSource() == jbUpdate)
			inventory.updateStore();
		else if(e.getSource() == jcbItemTypes)
			changeItemType();
	}

}
