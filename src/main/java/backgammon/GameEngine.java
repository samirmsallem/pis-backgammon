package backgammon;

import java.util.ArrayList;
import java.util.Map;
import java.util.Observer;
import java.util.Set;

public interface GameEngine{
	
	/**
	 * 
	 * @return a map with the positons of the white stones; key: field, value: amount of stones
	 */
	public Map<Integer, Integer> getWhitePos();
	
	/**
	 * 
	 * @return a map with the positons of the white stones; key: field, value: amount of stones
	 */
	public Map<Integer, Integer> getBlackPos();
	
	/**
	 * Set the position map of the white player
	 * @param map : the positions stored in a map (field : amount of stones)
	 */
	public void setWhitePos(Map<Integer, Integer> map);
	
	/**
	 * Set the position map of the black player
	 * @param map : the positions stored in a map (field : amount of stones)
	 */
	public void setBlackPos(Map<Integer, Integer> map);
	
	/**
	 * 
	 * @return the current player playing
	 */
	public int getCurrentPlayer();
	
	/**
	 * Set the current player
	 * @param player : either 1 or -1
	 */
	public void setCurrentPlayer(int player);
	
	
	/**
	 * 
	 * @param player the desired player
	 * @return	the count of stones the player has on the board
	 */
	public int getStoneCount(int player);
	
	/**
	 * 
	 * Sets the current player to the opponent of the before player
	 */
	public void nextPlayer();

	/**
	 * 
	 * @return the amout of kicked stones of the white player
	 */
	public int getWhiteKicked();
	
	/**
	 * 
	 * @return the amout of kicked stones of the black player
	 */
	public int getBlackKicked();
	
	/**
	 * 
	 * @return a list of numbers, representing the dice values the player has, with which he can perform a move
	 */
	public ArrayList<Integer> getDiceValues();
	
	/**
	 * Add a dice value to the dice stack
	 * @param n the number that should be added
	 */
	public void addDiceValue(int n);
	
	/**
	 * Removes a number from the dice values 
	 * @param n the number that should be removed
	 */
	public void removeFromDiceStack(int n);

    /**
     *
     * Organizes the board to the default stone layout
     */
    public void defaultStoneStructure();
    
    /**
     * 
     * Chooses a random player that starts the game
     */
    public void chooseRandomPlayer();

    /**
     *
     * Checks if the players stone count on the board is zero.
     * @return	player has won or not
     */
    public boolean checkforWin();

    /**
     *
     * Rolls two dices
     */
    public void rollDices();

    /**
     *
     * Moves a players stone from a specific location to another
     * @param from: the field the stone is on currently, to: the field the stone should land on
     * @return the move was successful or not
     */
    public boolean move(int from, int to);

    /**
     *
     * Removes a stone from the specific field
     * @param field the field the stone is on
     */
    public void removeOne(int field);
    
    
    /**
     * Opposite to the removeOne() method
     * @param field the field the stone should be placed on
     */
    public void addOne(int field);
    
    /**
     * Taking out a stone out of the game
     * Calling the removeOne() method for that
     * @param field the position the stone is taken from
     * @return true if the operation was successful
     */
    public boolean takeOut(int field);
    
    /**
     * 
     * @param field the desired field the player should enter the game
     * @return whether the player is able to enter the field at this position
     */
    public boolean canReenter(int field);
    
    /**
     * After getting kicked, a stone needs to reenter to the board
     * Calling the addOne() method for that
     * @param field the position the stone should reenter on
     */
    public void reenter(int field);

    /**
     * Checking on a player whether he is able to perform a move based on his stone positions and dice numbers
     * @return whether the player is blocked or can perform another move
     */
    public boolean isBlocked();
    
    /**
     * Calculate possible moves from a desired starting point
     * @param field, the given starting point
     * @return a set of field numbers, representing the possible fields the player can move to from the given starting point
     */
    public Set<Integer> calcNewPos(int field);
    
    /**
     * Calculate which stones can be used as starting points (which stones can be moved in first place)
     * @return a set with all the stones that can be moved before choosing one of them!
     */
    public Set<Integer> getMovableStones();
    
    /**
     *
     * Kicks a specific stone from the field and wait for the next round to put it in again
     * Calls removeOne() but the stone will be readded to the game in the next round
     * Incremets the kicked stone count of the player
     * @param field the field the stone gets kicked from
     */
    public void kick(int field);
    
    /**
     * Checks whether the player has all of his stones in his homefield
     * @return true, if he has all of his stones in his homefield, false otherwise
     */
    public boolean canPutOut();
	

}

