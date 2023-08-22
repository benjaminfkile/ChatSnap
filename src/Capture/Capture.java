package Capture;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Capture {
	public void capture(Rectangle r) {
		try {
			Robot robot = new Robot();
			BufferedImage image = robot.createScreenCapture(r);
			ImageIO.write(image, "png", new File("foo.png"));
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
