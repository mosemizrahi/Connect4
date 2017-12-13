import edu.illinois.cs.cs125.lib.zen.Zen;
import edu.illinois.cs.cs125.lib.zen.Rectangle;
import edu.illinois.cs.cs125.lib.zen.Circle;
import edu.illinois.cs.cs125.lib.zen.Text;
/** the connectN class.*/
public class DrawBoard {

	private static void emptyBoard() {
        for (int x = 5; x < 700; x += 100) {
        		for (int y = 5; y < 600; y+= 100) {
        			new Rectangle(x, y, 90, 90, "white").colorAndDraw();
        		}
        }
        new Rectangle(0, 600, 700, 100, "white").colorAndDraw();
	}
	
	private static void drawTile(int xPos, int yPos, String color) {
		new Circle(100 * xPos + 50, 600 - 100 * yPos - 50, 90, color).colorAndDraw();
	}
	
	private static void writeText(String phrase, String color, int size, int xPos, int yPos) {
		Text myText = new Text(phrase, color);
		myText.setSize(size);
		myText.set(xPos, yPos);
		myText.colorAndDraw();
	}
	
	private static int[] getClick() {
		int x = Zen.getMouseClickX();
		int y = Zen.getMouseClickY();
		int xMod = x % 100;
		if (xMod < 5 || xMod >= 95) {
			return new int[] {-1, -1}; //I am error
		}
		return new int[] {x / 100, 5 - y / 100};
	}
	
    public static void main(final String[] unused) {
    		Zen.addColor("white", 255, 255, 255);
    		Zen.addColor("black", 0, 0, 0);
    		Zen.addColor("red", 255, 0, 0);
    		Zen.addColor("blue", 0, 85, 170);
    		Zen.create(700,700);
    		
    		while (true) {
    			emptyBoard();
    			drawTile(0, -1, "red");
    			drawTile(6, -1, "blue");
    			writeText("SELECT", "black", 75, 200, 675);
    			Zen.waitForClick();
    			int[] positions = getClick();
    			while ((positions[0] != 0 && positions[0] != 6) || positions[1] != -1) {
    				Zen.waitForClick();
    				positions = getClick();
    			}
    			Rectangle myRect = new Rectangle(0, 600, 700, 100, "white");
    			myRect.colorAndDraw();
    			boolean playerIsRed = (positions[0] == 0);
    			String playerTile = playerIsRed ? "red" : "blue";
    			String aiTile = playerIsRed ? "blue" : "red";
    			Connect4AI myAI = new Connect4AI();
    			int[] decision = new int[] {0, 0, 0};
    			if (!playerIsRed) {
    				decision = myAI.play();
    				drawTile(decision[1], decision[2], aiTile);
    			}
    			while (decision[0] == 0) {
    				Zen.waitForClick();
    				positions = getClick();
    				while (positions[0] == -1) {
    					Zen.waitForClick();
        				positions = getClick();
    				}
    				decision = myAI.play(positions[0]);
    				while (decision[0] == -1) {
    					Zen.waitForClick();
        				positions = getClick();
        				while (positions[0] == -1) {
        					Zen.waitForClick();
            				positions = getClick();
        				}
        				decision = myAI.play(positions[0]);
    				}
    				if (decision[0] == 1) {
    					drawTile(decision[1], decision[2], playerTile);
    					drawTile(decision[3], decision[4], aiTile);
    					drawTile(0, -1, aiTile);
    					writeText("WINS", "black", 75, 100, 675);
    					Zen.waitForClick();
    					break;
    				}
    				if (decision[0] == 2) {
    					drawTile(decision[1], decision[2], playerTile);
    					drawTile(0, -1, playerTile);
    					writeText("WINS", "black", 75, 100, 675);
    					Zen.waitForClick();
    					break;
    				}
    				if (decision[0] == 3) {
    					drawTile(decision[1], decision[2], playerTile);
    					if (playerIsRed) {
    						drawTile(decision[3], decision[4], aiTile);
    					}
    					writeText("DRAW", "black", 250, 100, 675);
    					Zen.waitForClick();
    					break;
    				}
    				drawTile(decision[1], decision[2], playerTile);
				drawTile(decision[3], decision[4], aiTile);
    			}
    		}
    }
}