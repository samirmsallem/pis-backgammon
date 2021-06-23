package backgammon;

import processing.core.PGraphics;

public class Stone {

	int x, y, color;
	Stone(int x, int y, int color){ this.x = x; this.y = y; this.color = color;}
	
	public void draw(PGraphics g) {
		g.circle(x,y,50);
		g.fill(color);
	}

}
