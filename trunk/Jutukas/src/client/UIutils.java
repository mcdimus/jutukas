package client;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class UIutils {

	public static void centerFrameOnScreen(JFrame frame) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension monitorDimensions = toolkit.getScreenSize();
		Dimension frameDimensions = frame.getSize();
		frame.setLocation(monitorDimensions.width / 2 - frameDimensions.width
				/ 2, monitorDimensions.height / 2 - frameDimensions.height / 2);
	}

}
