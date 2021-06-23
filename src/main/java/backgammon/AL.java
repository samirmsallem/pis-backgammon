package backgammon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class AL implements Backend{
	
	
	private Map<Integer, Integer> white = new HashMap<>(); // int 1
	private Map<Integer, Integer> black = new HashMap<>(); // int -1
	
	public ArrayList<Integer> dice_values = new ArrayList<>(); //public, in order to access from test class, otherwise i would do it private
	public int currentplayer;
	
	private int white_kicked;
	private int black_kicked;
	
	private int ct = 0;
	
	@Override
	public Map<Integer, Integer> getWhitePos() { return white; }
	
	@Override
	public Map<Integer, Integer> getBlackPos() { return black; }
	
	@Override
	public int getCurrentPlayer() { return currentplayer; }
	
	@Override
	public int getStoneCount(int player) {
		ct = 0;
		if(player == 1) white.values().forEach(n -> ct += n);
		else black.values().forEach(n -> ct += n);
		return ct;
	}
	
	@Override
	public void nextPlayer() { currentplayer = -currentplayer; }
	
	@Override
	public int getWhiteKicked() { return white_kicked; }
	
	@Override
	public int getBlackKicked() { return black_kicked; }
	
	@Override
	public ArrayList<Integer> getDiceValues() { return dice_values; }
	
	@Override
	public void removeFromDiceStack(int n) { dice_values.remove((Integer) n); }
	
	@Override
	public void defaultStoneStructure() { //places the stones at the standard positions
		white.clear(); black.clear();
		
		white.put(1,2);
		white.put(12,5);
		white.put(17,3);
		white.put(19,5);
		
		black.put(6,5);
		black.put(8,3);
		black.put(13,5);
		black.put(24,2);
	}
	
	@Override
	public void chooseRandomPlayer() {
		currentplayer = ThreadLocalRandom.current().nextInt(0, 2) == 1 ? 1 : -1;
	}
	
	@Override
	public boolean checkforWin() { //checks whether the map that contains all stones is empty (-> if empty win)
		return getCurrentPlayer() == 1 ? white.isEmpty() : black.isEmpty();
	}
	
	@Override
	public void rollDices() {
		// TODO Auto-generated method stub
		
		if(!dice_values.isEmpty()) dice_values.clear(); //clear dice values first of all
		
		int left = ThreadLocalRandom.current().nextInt(1, 6 + 1);
		int right = ThreadLocalRandom.current().nextInt(1, 6 + 1); // generate two random dice values between 1-6
		
		if(left == right) { //if both numbers are the same -> double! -> the player gets the value 4 times and can perform 4 moves
			for(int i=0; i<4; i++) {
				dice_values.add(left);
			}
		} else { //otherwise just add the two values
			dice_values.add(left);
			dice_values.add(right);
		}
		
	}
	
	public boolean move(int from, int to) { //moves a stone from a field to another given in the parameters, returns true if success, false otherwise
		if(getCurrentPlayer() == 1 && white.containsKey(from) && to <=24 && white_kicked == 0 && dice_values.contains(to-from)) { //check if the player moves in the field range and he has the dice value he needs to perform a movement
			if(black.containsKey(to) && black.get(to) > 1) { //if more than 1 stones are on the destination field the player is UNNABLE to move
				return false; //abort move
			} else { //if there is 1 or less he is able to move
				if(black.containsKey(to) && black.get(to) == 1) kick(to); //if opponent stone is on it and its only one -> kick that stone
				removeFromDiceStack(to-from); //remove the dice number because it is used to perform the move
				removeOne(from); //remove the stone from the starting position
				addOne(to); //add the stone to the ending point
				return true; //return true as the move was successful
			}
		} //analogous for black player
		if(getCurrentPlayer() == -1 && black.containsKey(from) && to >= 1 && black_kicked == 0 && dice_values.contains(from-to)) {
			if(white.containsKey(to) && white.get(to) > 1) {
				return false;
			} else {
				if(white.containsKey(to) && white.get(to) == 1) kick(to);
				removeFromDiceStack(from-to);
				removeOne(from);
				addOne(to);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void removeOne(int field) { //removes a stone at a specific field position
		if(getCurrentPlayer() == 1 && white.containsKey(field)) {
			if(white.get(field) <= 1) white.remove(field); //remove field entry as no stone is there anymore
			else white.put(field, white.get(field)-1); //set field = field -1
		} else if(getCurrentPlayer() == -1 && black.containsKey(field)){
			if(black.get(field) == 1) black.remove(field);
			else black.put(field, black.get(field)-1);
		}
	}
	
	@Override
	public void addOne(int field) { //adds a new stone to a certain field
		if(getCurrentPlayer() == 1) {
			if(white.containsKey(field)) white.put(field, white.get(field)+1); //if previous stones were on the field increment the amount of stones by 1
			else white.put(field, 1); //initialize new field entry with one stone on it
			
		} else if(getCurrentPlayer() == -1){
			if(black.containsKey(field)) black.put(field, black.get(field)+1);
			else black.put(field, 1);
		}
	}
	
	
	@Override
	public boolean takeOut(int field) { //takes a stone out of the game
		/*
		 * Conditions for takeout:
		 * Player can put out, that means, all his stones are in his homefield
		 * The given field has to be in his homefield
		 * 
		 * Besides the conditions above one of the following conditions has to be true:
		 * Player has a dice number matching the field number (i.e: dice = [5,4] -> player clicks on field 5 and has a stone there)
		 * Player has a dice number higher than every field number he has a stone on, and he clicked the highest field number 
		 * (i.e: dice = [6,5], player has stones on fields 1-4. that means he can use his 5 and 6 to remove the next highest stone (in this case 4))
		 *  
		 */
		if(getCurrentPlayer() == 1 && canPutOut() && field >= 19 && field <= 24 && (dice_values.contains(25-field) || (dice_values.stream().filter(n -> n > 25-field).noneMatch(m -> white.containsKey(m)) && Collections.min(white.keySet()) == field))) {
			removeOne(field);
			removeFromDiceStack(dice_values.stream().filter(n -> n > 25-field).findFirst().orElse(25-field));
			
			return true;
		}
		
		if(getCurrentPlayer() == -1 && canPutOut() && field >= 1 && field <= 6 && (dice_values.contains(field) || (dice_values.stream().filter(n -> n > field).noneMatch(m -> black.containsKey(m)) && Collections.max(black.keySet()) == field))) {
			removeOne(field);
			removeFromDiceStack(dice_values.stream().filter(n -> n > field).findFirst().orElse(field));
			
			return true;
		}
		
		return false;
	}
	
	@Override 
	public boolean canReenter(int field) {
		switch(currentplayer) {
		case 1:
			return dice_values.contains((Integer) field) && (!black.containsKey(field) || black.get(field) <= 1); //checks whether the white player has the right dice number to reenter and 1 or less stones of black are on the field
		default:
			return dice_values.contains((Integer) 25-field) && (!white.containsKey(field) || white.get(field) <= 1); //analogous for black player
		}
	}
	
	@Override
	public void reenter(int field) {
		// reentering by using a dice number matching the field number, than removing it from the dice stack and decrement the kicked stone count
		switch(currentplayer) {
			case 1:
				removeFromDiceStack(field);
				white_kicked--;
				break;
			case -1:
				removeFromDiceStack(25-field);
				black_kicked--;
				break;
		}
	
		addOne(field);
		kick(field);
		
	}
		
	@Override
	public boolean isBlocked() { //check whether a player can do any move by his current stone positions and dice values
		if(getCurrentPlayer() == 1) {
			//if white player has kicked stones check if every possible field he can reenter on has more than 1 opponent stones on it, if yes he is blocked
			if(white_kicked > 0) return dice_values.stream().allMatch(n -> black.containsKey(n) && dice_values.stream().allMatch(p -> black.get(p) > 1));
			//if he can perform a putout, he has two possibilities, he can either still continue moving stones, or he can take out stones. Both ways are tested in this case:
			//if the set of movable stones is empty and he cannot take out a stone because he has no matching dice number, he is blocked
			else if(canPutOut()) return getMovableStones().isEmpty() && dice_values.stream().noneMatch(n -> white.containsKey(25-n)) && dice_values.stream().noneMatch(n -> white.keySet().stream().allMatch(m -> 25-m <= n));
		}
					
		//analogous to white player
		if(getCurrentPlayer() == -1) {
			if(black_kicked > 0) return dice_values.stream().allMatch(n -> white.containsKey(25-n) && dice_values.stream().allMatch(p -> white.get(25-p) > 1)); 
			else if(canPutOut()) return getMovableStones().isEmpty() && dice_values.stream().noneMatch(n -> black.containsKey(n)) && dice_values.stream().noneMatch(n -> black.keySet().stream().allMatch(m -> m <= n));
		}	
		
		
		//IF THE PLAYERS NEITHER HAVE KICKED STONES LEFT, NOR CAN PUT OUT A STONE, CHECK IF THE CALCULATED SET OF MOVABLE STONES IS EMPTY, IF YES THE PLAYER IS BLOCKED
		return getMovableStones().isEmpty();
	}
		

	
	
	@Override
	public Set<Integer> calcNewPos(int field) { // from a given field (starting pos) calculate the new possible ending points by adding up every dice number with the field
		Set<Integer> tmp = new HashSet<Integer>();
		if(getCurrentPlayer() == 1) {
			
			dice_values.stream().filter(n -> field+n < 25).forEach(n -> { //filter for fields in range of the board (i.e: from field 23, you can only go 1 more field)
				if(!black.containsKey(field+n) || (black.get(field+n) < 2)) tmp.add(field+n); //if the opponent has one or less stones at the field, a move to this field is possible --> add it to the set
				//why set? because dice numbers 3-3-3-3 should result in only one field and not 4 times the same field
			});
		} else { //analogous for other player
			dice_values.stream().filter(n -> field-n > 0).forEach(n -> { 
				if(!white.containsKey(field-n) || (white.get(field-n) < 2)) tmp.add(field-n);  
			});
		}
		
		return tmp;

	}
	
	@Override
	public Set<Integer> getMovableStones() { //from the current stone positions and the dice numbers, calculate the stones that could be used for a move
		Set<Integer> tmp = new HashSet<Integer>();
		if(getCurrentPlayer() == 1 && white_kicked == 0) { //only possible if player has no kicked stones
			white.forEach((field, amount) -> {
				dice_values.forEach(num -> { //cross product, combine each field, with each dice number
					if(field+num < 25 && (!black.containsKey(field+num) || black.get(field+num) < 2)) tmp.add(field); 
				});
			});
			
		} else if(getCurrentPlayer() == -1 && black_kicked == 0){
			
			black.forEach((field, amount) -> {
				dice_values.forEach(num -> {
					if(field-num > 0 && (!white.containsKey(field-num) || white.get(field-num) < 2)) tmp.add(field);
				});
			});
		}
		
		return tmp;
	}
	
	@Override
	public void kick(int field) { //kicks a stone from the field and increments the kicked stone counter for the certain player
		if(getCurrentPlayer() == 1 && black.containsKey(field)) {
			black.remove(field);
			black_kicked++;
		} else if(getCurrentPlayer() == -1 && white.containsKey(field)){
			white.remove(field);
			white_kicked++;
		}
	}

	@Override
	public boolean canPutOut() { //checks if all stones are in the homefield and player has no kicked stones
		if(getCurrentPlayer() == 1) return white.keySet().stream().allMatch(n -> n > 18) && white_kicked == 0;
		else return black.keySet().stream().allMatch(n -> n < 7) && black_kicked == 0;
	}
}
