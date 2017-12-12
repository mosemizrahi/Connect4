import edu.illinois.cs.cs125.lib.zen.Zen;
import edu.illinois.cs.cs125.lib.zen.Rectangle;
import edu.illinois.cs.cs125.lib.zen.Circle;
import edu.illinois.cs.cs125.lib.zen.Text;
/** the connectN class.*/
public class ConnectN {
    /**
     * This example draws rectangles at random locations near mouse clicks when the mouse button is
     * held down.
     *
     * @param unused unused input parameters
     */
    public static void main(final String[] unused) {
        Zen.create(700, 700);
        Zen.addColor("red", 1000, 0, 0);
        Zen.addColor("white", 0, 0, 0);
        Zen.addColor("blue", 0, 0, 100);
        if (Zen.isRunning()) {
            for (int x = 5; x < 700; x = x + 100) {
                for (int y = 5; y < 600; y = y + 100) {
                    new Rectangle(x, y, 90, 90, "white").draw();
                }
            }
        }
        new Rectangle(0, 600, 700, 100, "white").draw();
        Zen.waitForClick();
        int x = Zen.getMouseClickX();
        System.out.println(x);
        x = 100 * (x / 100) + 50;
        System.out.println(x);
        Circle a = new Circle(x, 50, 80, "red");
        a.colorAndDraw();
        Zen.waitForClick();
        Circle c = new Circle(150, 50, 80, "blue");
        c.colorAndDraw();
        Zen.waitForClick();
        Text gameover = new Text("GAMEOVER", 75);
        gameover.set(150, 675);
        gameover.draw();
    }
}