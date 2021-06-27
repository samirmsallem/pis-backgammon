package backgammon;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;

import org.junit.Test;


public class AppTest {
    private GameEngine b;


    @Test
    public void testStoneConst() {
    	int x = 100;
    	int y = 300;
    	int color = 0;
    	Stone s = new Stone(x,y,color);
    	
    	assertTrue("Konstruktor setzt Werte nicht korrekt.", s.x == x && s.y == y && s.color == color);
    }
    
    @Test
    public void testCanReenter() {
    	b = new AL();
    	b.setCurrentPlayer(-1);
    	
    	
    	IntStream.iterate(0, i -> i + 1).limit(2).forEach((count) -> { 
    		List.of(1,2,3,4,6).stream().forEach(n -> b.addOne(n)); //adding two opponent stones to every field the list contains (leaving only the homefield 5 open for the other player)
    	});
    	
    	b.nextPlayer(); //switching to next player
    	
    	Set.of(3,6).stream().forEach(n -> b.addDiceValue(n)); // adding 3 and 6 to the dice values

    	
    	IntStream.range(1, 7).forEach(field -> { //checking for every homefield (range 1-6) whether the player can reenter. as he has numbers 3 and 6 and both fields are blocked this should return false 
        	assertFalse("Aufgrund der Würfelzahlen und der Steine des Gegenspielers, darf es kein Feld geben, auf dem der Spieler wiedereinsteigen kann!", b.canReenter(field));
    	});
    	
    }
    
    @Test
    public void testStoneStructure() {
    	b = new AL();
    	b.defaultStoneStructure();
    	
    	assertTrue("Standardaufstellung wurde nicht richtig gesetzt!", !b.getWhitePos().isEmpty() && !b.getBlackPos().isEmpty());
    }
    
    @Test
    public void testStoneCount() {
    	b = new AL();
    	
    	assertTrue("Spielfeld ist nicht leer zu beginn!", b.getStoneCount(1) == 0 && b.getStoneCount(-1) == 0);
    	
    	b.defaultStoneStructure();
    	
    	assertSame("Die Steinanzahl muss auf beiden Seiten zu Anfang identisch sein!", b.getStoneCount(1), b.getStoneCount(-1));
    }
    
    @Test
    public void testPlayer() {
    	b = new AL();
    	b.chooseRandomPlayer();
    	
    	assertTrue("Zufällige Spielerauswahl trifft nicht die mögliche Menge der Spieler!", List.of(1,-1).contains(b.getCurrentPlayer()));
    }
    
    @Test
    public void testNextPlayer() {
    	b = new AL();
    	b.chooseRandomPlayer();
    	List<Integer> players = List.of(1,-1);
    	int first = b.getCurrentPlayer();
    	b.nextPlayer();
    	int next = b.getCurrentPlayer();
    	
    	assertNotSame("Erster und Folgespieler dürfen nicht identisch sein!", first, next);
    	assertTrue("Beide möglichen Spielerausprägungen müssen vorkommen!", players.containsAll(List.of(first, next)));
    }
    
    
    @Test
    public void testDice() {
    	b = new AL();
    	b.rollDices();
    	assertTrue("Die Menge an gewürfelten Zahlen entspricht nicht den Erwartungen!", b.getDiceValues().size() == 2 || b.getDiceValues().size() == 4);
    }
    
    @Test
    public void testDice2() {
    	b = new AL();
    	b.rollDices();
    	
    	int dice_num = b.getDiceValues().get(0);
    	
    	if(b.getDiceValues().size() == 2) {
    		b.removeFromDiceStack(dice_num);
    		assertTrue("Bereits genutzte Würfelzahl wurde nicht richtig entfernt!", b.getDiceValues().size() == 1);
    		assertFalse("Zahl muss nach dem Nutzen entfernt werden!", b.getDiceValues().contains(dice_num));
    	} 
    	else {
    		b.removeFromDiceStack(dice_num);
    		
    		assertTrue("Bereits genutzte Würfelzahl wurde nicht richtig entfernt!", b.getDiceValues().size() == 3);
    		assertTrue("Wenn Pasch darf die Zahl nur einmal entfernt werden!", b.getDiceValues().contains(dice_num));
    	}
    }
    
    @Test
    public void testMoving() {
    	b = new AL();
    	b.defaultStoneStructure();
    	b.chooseRandomPlayer();
    	
    	b.addDiceValue(3);
    	int startfrom = b.getCurrentPlayer() == 1 ? 1 : 24;
    	int useddicenum = b.getDiceValues().get(0);
    	int destinatedfield = b.getCurrentPlayer() == 1 ? (startfrom + useddicenum) : (startfrom - useddicenum);
    	
    	int sizeOfDiceSet = b.getDiceValues().size();
    	
    	
    	assertTrue("Das Bewegen von der Startposition muss mit jeder Würfelzahl möglich sein!", b.move(startfrom, destinatedfield));
    	
    	assertEquals("Nach dem Zug wurde die genutzte Zahl nicht entfernt!", sizeOfDiceSet - 1, b.getDiceValues().size());
    }
    
    @Test
    public void testMoving2() {
    	b = new AL();
    	b.defaultStoneStructure();
    	b.setCurrentPlayer(1);
    	b.addDiceValue(3);
    	
    	int useddicenum = b.getDiceValues().get(0);
    	int destinatedfield = 24 - useddicenum;

		assertFalse("Der Spieler darf keine Steine des anderen Bewegen auch wenn es Zugtechnisch möglich wäre!", b.move(24, destinatedfield));
    }
    
    @Test
    public void testMoving3() {
    	b = new AL();
    	b.defaultStoneStructure();
    	b.setCurrentPlayer(1);
    	b.addDiceValue(5);
    	
    	int useddicenum = b.getDiceValues().get(0);
    	int destinatedfield = 1 + useddicenum;

		assertFalse("Der Spieler darf keine Position betreten, die von einem anderen Stein blockiert wird!", b.move(1, destinatedfield));
    }
    
    @Test
    public void testMoving4() {
    	b = new AL();
    	b.defaultStoneStructure();
    	b.setCurrentPlayer(1);
    	b.addDiceValue(2); 

    	b.move(17, 19);
    	
		assertTrue("Würfelzahl wurde nicht entfernt!", b.getDiceValues().isEmpty());
		assertTrue("Der Stein wurde nicht richtig umgesetzt!", b.getWhitePos().get(17) == 2 && b.getWhitePos().get(19) == 6);
    }
    
    @Test
    public void testRemoving() {
    	b = new AL();
    	b.defaultStoneStructure();
    	b.setCurrentPlayer(1);
    	
    	b.removeOne(1);
		assertTrue("Bei entfernen eines Steins muss die Steinanzahl sinken!", b.getStoneCount(1) == b.getStoneCount(-1) - 1 && b.getWhitePos().get(1) == 1);
    }
    
    @Test
    public void testAdding() {
    	b = new AL();
    	b.defaultStoneStructure();
    	b.setCurrentPlayer(1);
    	int amount_before = b.getWhitePos().get(1);
    	b.addOne(1);
    	int amount_after = b.getWhitePos().get(1);
    	assertSame("Stein draufspielen funktioniert nicht!", amount_before, amount_after - 1);
    }
    
    @Test
    public void testReenter() {
    	b = new AL();
    	b.setCurrentPlayer(1);
    	b.addOne(3); //adding a stone on field 3 for player 1
 
    	b.nextPlayer(); //changing player to -1
    	b.addOne(6); 
    	b.addOne(6); //adding two stones to field 6 for player -1, so that the field is blocked
    	
    	b.kick(3); //player -1 kicking the stone from field 3
    	
    	assertSame("Nach dem Kick darf der Spieler keinen Stein mehr haben!", b.getStoneCount(1), 0);
    	assertSame("Der Counter des herausgeworfenen weißen Spielers muss bei 1 liegen!", b.getWhiteKicked(), 1);
    	
    	b.nextPlayer();
    	
    	b.addDiceValue(6); //adding dice number 6
    	if(b.canReenter(6)) b.reenter(6); //trying to reenter on field 6
    	
    	assertSame("Der Stein wurde wiedereingesetzt, obwohl dies nicht erlaubt ist, da das Feld blockiert ist!", b.getWhiteKicked(), 1);
    	
    	b.addDiceValue(2); //adding another dice number
    	if(b.canReenter(2)) b.reenter(2); //as no stone is on field 2, this should affect that the stone of player 1 can reenter
    	
    	assertSame("Anzahl muss 0 sein, da Stein wiedereingestiegen ist!", b.getWhiteKicked(), 0);
    	
    }
    
    @Test
    public void testBlocked() {
    	b = new AL();
    	b.chooseRandomPlayer();
    	b.defaultStoneStructure();
    	
    	assertTrue("Der Spieler kann ohne Würfel keinen Zug machen!", b.isBlocked());
    }
    
    @Test
    public void testBlocked2() {
    	b = new AL();
    	b.setCurrentPlayer(-1);    	
    	b.addOne(6);
    	b.addOne(6); //blocking field 6 with 2 stones of player -1
    	
    	b.nextPlayer();
    	
    	b.addOne(4); //player 1 putting one stone on field 4
    	b.addDiceValue(2); //adding the dice number two to the field -> only possible move would be field 4 + 2 and that is not possible!
    	
    	assertTrue("Der Spieler kann keinen Zug machen!", b.isBlocked());
    	
    	b.addDiceValue(3); //adding the number 3 to the dice values, now a move should be possible!
    	
    	assertFalse("Der Spieler kann einen Zug machen!", b.isBlocked());
    }
    
    @Test
    public void testNewPosCalc() {
    	b = new AL();
    	b.setCurrentPlayer(-1);
    	
    	b.addOne(6);
    	b.addOne(6);
    	
    	b.nextPlayer();
    	
    	b.addOne(1);
    	
    	List.of(3,5).stream().forEach(n -> 	b.addDiceValue(n));

    	assertEquals("Der Zug von Feld 1 zu 4 muss möglich sein, da kein blockierender Stein dort ist!", Set.of(4), b.calcNewPos(1));
    	
    	b.kick(6); //kicking the two stones of the opponent from the field
    	
    	assertEquals("Der Zug von Feld 1 zu 4 und 6 muss möglich sein, da dort keine Steine des Gegners liegen!", Set.of(4,6), b.calcNewPos(1));
    }
    
    @Test
    public void testMovableStones() {
    	b = new AL();
    	
    	b.setCurrentPlayer(-1);
    	
    	b.addOne(6);
    	b.addOne(6);
    	
    	
    	b.nextPlayer();
    	
    	List.of(1,2,3).stream().forEach(n -> b.addOne(n));
    	
    	b.addDiceValue(5);
    	
    	assertEquals("Der Stein auf Feld 1 darf nicht zu den verschiebbaren Steinen zählen!", b.getMovableStones(), Set.of(2,3));
    }
    
    @Test
    public void testMovableStones2() {
    	b = new AL();
    	
    	b.setCurrentPlayer(1);
    	
    	List.of(20,22).stream().forEach(n -> b.addOne(n));
    	
    	b.addDiceValue(5);
    	
    	assertTrue("Der Stein auf Feld 1 darf nicht zu den verschiebbaren Steinen zählen!", b.getMovableStones().isEmpty());
    }
    
    @Test
    public void testCanPutOut() {
    	b = new AL();
    	b.chooseRandomPlayer();
    	
    	b.defaultStoneStructure();
    	
    	assertFalse("Zu Anfang des Spiels können noch keine Steine herausgenommen werden!", b.canPutOut());
    }
    
    @Test
    public void testTakeOut() {
    	b = new AL();
    	b.defaultStoneStructure();
    	
    	b.setCurrentPlayer(-1);
    	b.addDiceValue(6);
    	
    	assertFalse("Solange nicht alle Steine im Heimbereich stehen, kann keiner entnommen werden!", b.takeOut(6));
    }
    
    @Test
    public void testTakeOut2() {
    	b = new AL();
    	
    	b.setCurrentPlayer(-1);
    	List.of(1,2,3,4).forEach(n -> b.addOne(n));
    	
    	b.addDiceValue(5);
    	b.addDiceValue(4);
    	
    	assertTrue("Es muss möglich sein Steine herauszunehmen, wenn alle im Heimbereich sind und eine Würfelzahl >= dem Feld existiert auf dem der Stein entnommen werden soll!", b.takeOut(4));
    	
    	assertTrue("Wenn alle Würfelzahlen aufgebraucht sind, ist ein Herausnehmen bis zur nächsten Runde nicht möglich!", b.takeOut(3));
    }
    
    @Test
    public void testTakeOut3() {
    	b = new AL();
    	
    	b.setCurrentPlayer(-1);
    	List.of(2,3,4,5).forEach(n -> b.addOne(n));
    	
    	b.addDiceValue(4);
    	b.addDiceValue(1);
    	
    	assertTrue("Der Zug von 5 zu 1 muss möglich sein, auch wenn der Spieler herausnehmen kann!", b.move(5, 1));
    	
    	assertTrue("Der auf 1 bewegte Stein muss mit der dem Feld entsprechenden Würfelzahl herausgenommen werden können!", b.takeOut(1));
    }
    
    @Test
    public void testWin() {
    	b = new AL();
    	
    	b.setCurrentPlayer(-1);
    	List.of(1,2,3).forEach(n -> b.addOne(n));
    	
    	b.addDiceValue(1);
    	b.addDiceValue(2);
    	
    	
    	b.takeOut(1);
    	b.takeOut(2);
    	
    	assertFalse("Spieler hat noch einen Stein übrig, kann also nicht gewonnen haben!", b.checkforWin());
    	
    	b.nextPlayer();
    	
    	assertTrue("Der andere Spieler hat keine Steine auf dem Feld -> Muss gewonnen haben nach Spiellogik!", b.checkforWin());
    }
    
    @Test
    public void testAbnormal() {
    	b = new AL();
    	
    	b.defaultStoneStructure();
    	
    	Map<Integer, Integer> tmp = b.getBlackPos();
    	
    	b.setBlackPos(new HashMap<>());
    	
    	assertFalse("Nach Änderung ist die Map der enthaltenen Felder anders!", Objects.deepEquals(tmp, b.getBlackPos()));
    	
    	b.defaultStoneStructure();
    	
    	assertTrue("Nach reset müssen Felder wieder die gleichen Steine halten!", Objects.deepEquals(tmp, b.getBlackPos()));
    	
    }
    
    
    
}
