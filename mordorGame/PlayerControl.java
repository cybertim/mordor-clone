package mordorGame;

import mordorData.DataBank;
import mordorData.MapSquare;
import mordorData.Player;
import mordorData.SquareFeature;
import mordorEnums.Direction;
import mordorEnums.PlayerState;
import mordorHelpers.Coord;

public class PlayerControl
{
	private Player player;
	private DataBank dataBank;

	PlayerControl(DataBank nDataBank, Player nPlayer)
	{
		player = nPlayer;
		dataBank = nDataBank;
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Moves the player forward if possible.
	 *
	 */
	public boolean moveForward()
	{
		Coord coords = player.getCoord();
		Direction dir = coords.getDirection();
		MapSquare thisSquare = dataBank.getMap().getMapSquare(coords);
		MapSquare nextSquare = dataBank.getMap().getMapSquare(coords);
		
		// Can we even move this direction?
		if(!thisSquare.directionPassable(dir))
			return false; // Nope, so we are done.
		
		// First, get the square the player is going to.
		switch(coords.getDirection())
		{
		case North:
			nextSquare = dataBank.getMap().getMapSquare(coords.getNeighbour(Direction.North));
			break;
		case East:
			nextSquare = dataBank.getMap().getMapSquare(coords.getNeighbour(Direction.East));
			break;
		case South:
			nextSquare = dataBank.getMap().getMapSquare(coords.getNeighbour(Direction.South));
			break;
		case West:
			nextSquare = dataBank.getMap().getMapSquare(coords.getNeighbour(Direction.West));
			break;
		}
		
		// Enter the square.
		if(!nextSquare.enterSquare(player, dataBank.getMap()))
			nextSquare = dataBank.getMap().getMapSquare(player.getCoord());
		else if(thisSquare.getWallType(dir) == MapSquare.BYTE_WALLHIDDENDOOR)
		{
			// Since we only went to the neighbour, and we passed through a hidden door
			// Mark the door as found.
			// TODO What if  player passes through secret wall onto teleporter?
			thisSquare.setWall(dir, MapSquare.BYTE_WALLFOUNDDOOR);
			nextSquare.setWall(dir.inverse(), MapSquare.BYTE_WALLFOUNDDOOR);
		}
		
		// Now we need to update the player's room state.
		if(thisSquare.getRoom() != dataBank.getMap().getMapSquare(player.getCoord()).getRoom())
		{
			thisSquare.getRoom().exitRoom(player);
			dataBank.getMap().getMapSquare(player.getCoord()).getRoom().enterRoom(dataBank, player);
		}
		
		return true;
	}
	
	public void moveBackwards()
	{
		if(!player.isInState(PlayerState.NoDirectionChange))
			player.getCoord().setDirection(player.getCoord().getDirection().inverse());
	}
	
	public void turnRight()
	{
		if(!player.isInState(PlayerState.NoDirectionChange))
			player.getCoord().setDirection(player.getCoord().getDirection().clock());
	}
	
	public void turnLeft()
	{
		if(!player.isInState(PlayerState.NoDirectionChange))
			player.getCoord().setDirection(player.getCoord().getDirection().counter());
	}
	
	public boolean takeStairs(Mordor mordor)
	{
		Coord coords = player.getCoord();
	//	MapSquare thisSquare = dataBank.getMap().getMapSquare(coords);
	/*	byte x = coords.getX();
		byte y = coords.getY();
		byte z = coords.getZ();
		boolean success = false;*/
		
		if(dataBank.getMap().getMapSquare(coords).areStairs())
		{
			switch(dataBank.getMap().getMapSquare(coords).getStairs().getType())
			{
			case SquareFeature.TYPE_STAIRSDOWN:
			//	z += 1;
				dataBank.getMap().getMapSquare(coords.getNeighbour(Direction.Down)).enterSquare(player, dataBank.getMap());
				return true;
		//		thisSquare.enterSquare(player, dataBank.getMap());
				
		//		success = true;
		//		break;
			case SquareFeature.TYPE_STAIRSUP:
				dataBank.getMap().getMapSquare(coords.getNeighbour(Direction.Up)).enterSquare(player, dataBank.getMap());
				return true;
			//	z -= 1;
				//thisSquare.enterSquare(player, dataBank.getMap());
		//		success = true;
		//		break;
			case SquareFeature.TYPE_EXIT:
				
				try
				{
					player.setState(PlayerState.InDejenol, true, Player.STATENATURAL);
					mordor.exitDungeon();
					player.getCoord().setZ((byte)0);
					return true;
	//				success = true;
				}
				catch(Exception e)
				{
					System.err.println(e);
				}
				break;
			}
		}
		return false;
		
	//	if(success)
		//	coords.setZ(z);
		
	//	return success;
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public void setPlayer(Player nPlayer)
	{
		player = nPlayer;
	}
	
	public String getCoordString()
	{
		return player.getCoordString();
	}
}
