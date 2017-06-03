package assignment2;
import javax.swing.JFrame;

public class GuiMain {
	
	// launch interface
	public static void main(String[] args) {
		Gui GUI = new Gui();
		GUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// set size and visibility
		int Mywidth = (int) (GUI.widthScreen*0.6);
		int Myheight = (int) (GUI.heightScreen*0.9);
		GUI.setSize(Mywidth, Myheight);
		GUI.setResizable(false);
		GUI.setVisible(true);
	}
}
