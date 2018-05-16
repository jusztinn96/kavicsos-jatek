package kavicsos_jatek.kavics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Ez az osztály felel a szabályok megjelenítéséért.
 *  
 * @see kavicsos_jatek.kavics
 */
public class szabalyok implements MouseListener {
	/**
	 * Segítségével megvizsgáljuk a képernyő méretét.
	 */
	Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	/**
	 * Grafikus felület kirajzolásához használjuk.
	 */
	Drawing draw = new Drawing();
	/**
	 * Ablak címsorának elnevezésére használjuk.
	 */
	JFrame szabaly = new JFrame("4x4 kavics játék - szabályok");
	/**
	 * Üzenet megjelenítésére szolgál a játékosok számára.
	 */
	JLabel uzenet = new JLabel("Olvasd el a szabályzatot figyelmesen!");
	/**
	 * Vissza gombhoz változó deklarálás.
	 */
	ImageIcon visszagomb;
	/**
	 * Háttérképhez változó deklarálás.
	 */
	ImageIcon hatter;
	/**
	 * Start gombhoz változó deklarálás.
	 */
	ImageIcon elsomenu;
	/**
	 * Szabályok gombhoz változó deklarálás.
	 */
	ImageIcon szabalyok;
	/**
	 * Előzmények gombhoz változó deklarálás.
	 */
	ImageIcon harmadikmenu;

	/**
	 * Ablak megjelenítését szolgálja.
	 *  
	 * @see kavicsos_jatek.kavics
	 */
	public szabalyok() {
		
		hatter = new ImageIcon(getClass().getClassLoader().getResource("hatter_2.jpg"));
		elsomenu = new ImageIcon(getClass().getClassLoader().getResource("start-gomb.jpg"));
		harmadikmenu = new ImageIcon(getClass().getClassLoader().getResource("elozo-gomb.jpg"));
		szabalyok = new ImageIcon(getClass().getClassLoader().getResource("szabalyzat.jpg"));
		visszagomb = new ImageIcon(getClass().getClassLoader().getResource("visszagomb.png"));
		szabaly.add(draw);
		draw.addMouseListener(this);
		szabaly.setSize(800, 600);
		szabaly.setLocation(dim.width/2-szabaly.getSize().width/2, dim.height/2-szabaly.getSize().height/2);
		uzenet.setFont(new Font("Serif", Font.BOLD, 20));
		uzenet.setForeground(Color.black);
		uzenet.setHorizontalAlignment(SwingConstants.CENTER);
		szabaly.add(uzenet, "South");
		szabaly.setVisible(true);
	}

	/**
	 * A háttérkép és a menüpontok elhelyezésére szolgál az ablakban.
	 *
	 */
	class Drawing extends JComponent {
		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			g.drawImage(hatter.getImage(), 0, 0, this);
			g.drawImage(elsomenu.getImage(), 75, 425, this);
			g.drawImage(visszagomb.getImage(), 0, 0, this);
			g.drawImage(szabalyok.getImage(), 100, 175, this);
			g.drawImage(harmadikmenu.getImage(), 425, 425, this);
		}
	}
	
	public void mousePressed(MouseEvent e) {
	}

	/**
	 * Egér kattintások érzékelésére és kezelésére szolgál.
	 * Menüpontokra kattintva az megfelelő eseményt hajtja végre.
	 */
	public void mouseReleased(MouseEvent e) {
		int row = e.getX();
		int col = e.getY();
		if (row >= 1 && row <= 40 && col >= 0 && col <= 40)
			{
			szabaly.dispose();
			new kezdokepernyo();
			}
		if (row >= 75 && row <= 375 && col >= 425 && col <= 505)
			{
			szabaly.dispose();
			new jatekablak();
			}
		if (row >= 425 && row <= 725 && col >= 425 && col <= 505)
			{
			szabaly.dispose();
			new elozmenyek();
			}
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}
}