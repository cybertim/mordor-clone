package mordorGame;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import mordorData.ItemInstance;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

public class ItemInstanceTransferHandler extends TransferHandler
{
	private String mimeType = DataFlavor.javaJVMLocalObjectMimeType + ";class=mordorData.ItemInstance";
	private DataFlavor dataFlavour;
	private SICItemPanel curItem;
	
	public ItemInstanceTransferHandler()
	{
		try
		{
			dataFlavour = new DataFlavor(mimeType);
		} catch (ClassNotFoundException e) { }
	}

	public boolean importData(JComponent c, Transferable t)
	{
        ItemInstance item;
        
        if (canImport(c, t.getTransferDataFlavors()))
        {
            SICItemPanel nItem = (SICItemPanel)c;
            //Don't drop on myself.
            if (curItem == nItem)
            {
     //           shouldRemove = false;
                return true;
            }
            
            try
            {
            	item = (ItemInstance)t.getTransferData(dataFlavour);
            	
                //Set the component to the new picture.
            	nItem.changeItem(curItem);
                return true;
            } catch (UnsupportedFlavorException ufe) {
                System.out.println("importData: unsupported data flavor");
            } catch (IOException ioe) {
                System.out.println("importData: I/O exception");
            }
        }
        return false;
    }

    protected Transferable createTransferable(JComponent c)
    {
        curItem = (SICItemPanel)c;
    //    shouldRemove = true;
        return new ItemInstanceTransferable(curItem);
    }

    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    protected void exportDone(JComponent c, Transferable data, int action)
    {
    	// What is done after the drop
    /*    if (shouldRemove && (action == MOVE)) {
            sourcePic.setImage(null);
        }
        sourcePic = null;*/
    }

    public boolean canImport(JComponent c, DataFlavor[] flavors)
    {
        for (int i = 0; i < flavors.length; i++)
            if (dataFlavour.equals(flavors[i]))
                return true;

        return false;
    }

    class ItemInstanceTransferable implements Transferable
    {
        private ItemInstance item;
        private boolean equipped;

        ItemInstanceTransferable(SICItemPanel nItem)
        {
        	item = nItem.getItem();
        	equipped = nItem.getEquipped();
        }

        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException
        {
            if (!isDataFlavorSupported(flavor))
            {
                throw new UnsupportedFlavorException(flavor);
            }
            return item;
        }

        public DataFlavor[] getTransferDataFlavors()
        {
            return new DataFlavor[] { dataFlavour };
        }

        public boolean isDataFlavorSupported(DataFlavor flavor)
        {
            return dataFlavour.equals(flavor);
        }
    }
}
