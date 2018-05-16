package kavicsos_jatek.kavics;

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ez az osztály felel a játéktábla megjelenítéséért.
 *  
 * @see kavicsos_jatek.kavics
 */
public class jatekablak implements KeyListener, MouseListener {
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
	JFrame jatek = new JFrame("4x4 kavics játék - játékterep");
	/**
	 * A fajl megmutatja, hogy az XML fájl, melyben az adatok tárolásra kerülnek, létezik-e?
	 */
	static boolean fajl;
	/**
	 * x,y változókat ciklusokhoz használjuk fel.
	 * sor, oszlop, sor2, oszlop2, sor3, oszlop3 változókat a szomszédos sorok eltárolására használjuk egy-egy kör során.
	 */
	int x, y, szamlalo=0, sor, oszlop, sor2, oszlop2, sor3, oszlop3;
	/**
	 * A tömb játéktábla mezőinek eltárolására szolgál (4x4).
	 */
	int[][] board = new int[4][4];
	/**
	 * Ezen változót segítségével tartjuk számon, hogy éppen melyik játékos következik. Kezdőértéke 1, mely az elő játékos.
	 */
	int xTurn = (1);
	/**
	 * Üzenet megjelenítésére szolgál a játékosok számára.
	 */
	JLabel message = new JLabel("1. játékos lép - zöld");
	/**
	 * Ez a tömb a változó képek eltárolására szolgál.
	 */
	ImageIcon[] boardPictures = new ImageIcon[3];
	/**
	 * Üzenet megjelenítését szolgálja a játék végén.
	 */
	String uzenet;
	/**
	 * Az XML dokumentumban a nyertes játékos eltárolására szolgál.
	 */
	static String nyertes;
	/**
	 * Az XML dokumentumban a mérkőzés befejezésének dátumát tárolja el.
	 */
	static String pontosdatum;
	/**
	 * Az XML fájlra ezen változó segítségével hivatkozunk.
	 */
	static File fjl = null;

	/**
	 * Ablak megjelenítését szolgálja.
	 *  
	 * @see kavicsos_jatek.kavics
	 */
	
	static int mezok;
	
	public jatekablak() 
	{
		for (int i = 0; i < boardPictures.length; i++)
			boardPictures[i] = new ImageIcon(getClass().getClassLoader().getResource(i + ".jpg"));
		jatek.add(draw);
		jatek.addKeyListener(this);
		jatek.addMouseListener(this);
		jatek.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jatek.setSize(800, 600);
		jatek.setLocation(dim.width/2-jatek.getSize().width/2, dim.height/2-jatek.getSize().height/2);
		message.setFont(new Font("Serif", Font.BOLD, 20));
		message.setForeground(Color.blue);
		message.setHorizontalAlignment(SwingConstants.CENTER);
		jatek.add(message, "South");
		jatek.setVisible(true);
	}
	
	/**
	 * Naplózási metódus
	 */
	private static final Logger logger = LoggerFactory.getLogger(jatekablak.class);

	/**
	 * Ezen logikai változó segítségével vizsgáljuk meg, hogy üres-e a tábla, azaz nincs már rajta egy kavics sem.
	 *  
	 * @return A tábla üres, azaz nincs rajta egy kavics sem.
	 */
	public boolean fullBoard() 
	{
		/**
		* Ezen változó segítségével tartjuk számon, hogy hány mezőn nincs már kavics, azaz hány mező üres.
		*/
		int countBlank = 0;
		for (int r = 0; r < 4; r++) {
			for (int col = 0; col < 4; col++) {
				if (board[r][col] == 0)
					countBlank++;
			}
		}
		return (countBlank == 0);
	}
	
	/**
	 * Megszámolja, hogy a táblánk valóban 4x4 mezőből áll
	 * @return Mezők darabszáma
	 */
	int mezok_ellenorzese() 
	{
		/**
		* Ezen változó segítségével tartjuk számon, hogy hány mezőt számoltunk már meg.
		*/
		mezok = 0;
		for (int r = 0; r < 4; r++) {
			for (int col = 0; col < 4; col++) {
				if (board[r][col] == 1)
					mezok++;
			}
		}
		return (mezok);
	}
	
	/**
	 * Az XML fájlkezelésre szolgál.
	 * Ha az XML fájl nem létezik, akkor létrehoz egyet és beleírja a legutóbbi eredményt.
	 * Ha létezik az XML fájl, akkor hozzáfűzi a legutóbbi eredményt a fájlhoz.
	 */
	public void fajlkezeles() {
		fjl = new File(getClass().getClassLoader().getResource("ered.xml").getFile());
		fajl = fjl.exists();
		if (!fajl)
		{
        DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder icBuilder;
        try {
            icBuilder = icFactory.newDocumentBuilder();
            Document doc = icBuilder.newDocument();
            Element mainRootElement = doc.createElement("Mérkőzések");
            doc.appendChild(mainRootElement);
 
            mainRootElement.appendChild(jatszmak(doc, nyertes, pontosdatum));
 
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(getClass().getClassLoader().getResource("ered.xml").getFile()));
            transformer.transform(source, result);
 
            logger.info("XML fájl létrehozva.");
 
        	} 	
        	catch (Exception e) {
            e.printStackTrace();
        	}
		}
		else
		{
			
			try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(fjl);
			Element root = doc.getDocumentElement();

            root.appendChild(jatszmak(doc, nyertes, pontosdatum));

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(getClass().getClassLoader().getResource("ered.xml").getFile()));
            transformer.transform(source, result);
            
            logger.info("XML fájl sikeresen módosítva.");
            
			}
			catch (Exception e) {
	        e.printStackTrace();
	        }
		}
    }
 
	/**
	 * 
	 * @param doc Az XML dokumentum.
	 * @param name Nyertes eltárolására szolgál.
	 * @param date A játék befejezésének pontos ideje.
	 * @return Játszma tag és a benne lévő nyertes neve és a játszma ideje.
	 */
    private static Node jatszmak(Document doc, String name, String date) {
        Element jatszma = doc.createElement("Játszma");
        jatszma.appendChild(jatszmaelem(doc, jatszma, "Nyertes", name));
        jatszma.appendChild(jatszmaelem(doc, jatszma, "Dátum", date));
        return jatszma;
    }
    
    /**
     * 
     * @param doc Az XML dokumentum.
     * @param element Az XML dokumentum adott eleme.
     * @param name Elemnév.
     * @param value Érték hozzáadása az adott elemhez.
     * @return Játszam elemei.
     */
 
    private static Node jatszmaelem(Document doc, Element element, String name, String value) {
        Element node = doc.createElement(name);
        node.appendChild(doc.createTextNode(value));
        return node;
    }
    
    /**
     * A mérkőzés befejezésének ideje.
     */
    public void datum()
    {
		Date dNow = new Date( );
		SimpleDateFormat ft = new SimpleDateFormat ("yyyy.MM.dd. HH:mm:ss");
		pontosdatum = ft.format(dNow);
    }
    
    /**
     * A játék vége térése utáni feladatok elvégzésére szolgál.
     * Megadja a nyertes nevét, a befejezés dátumát.
     * Elindítja a fájlkezelést.
     * Felugró ablakban közli a játék végét.
     * Bezárja a játékteret és visszairányít a kezdőképernyőre.
     */
	
	public void jatekvege()
	{
		datum();
		if (xTurn == (1)) {uzenet = "2. játékos (piros) nyert! Eredmények elmentve."; nyertes ="2. játékos (piros)";}
		if (xTurn == (-1)) {uzenet = "1. játékos (zöld) nyert! Eredmények elmentve."; nyertes ="1. játékos (zöld)";}
		fajlkezeles();
		JOptionPane.showMessageDialog(null, uzenet , "Eredmény", JOptionPane.INFORMATION_MESSAGE);
		jatek.dispose();
		new kezdokepernyo();
	}
	
	/**
	 * Az első játékos egy körét kezeli.
	 * Véget ér, ha a maximális 4 lépést eléri vagy SPACE gombbal nem történik váltás.
	 * Körben minden lépés után vizsgáljuk, hogy a táblán van-e még kavics.
	 * Figyelünk arra, hogy egy körben csak a mellette lévő kavicsot lehet levenni.
	 * @param row A tábla mezői.
	 * @param col A tábla sorai.
	 */
	public void elso (int row, int col) {
		
			if ((board[row][col] == 0) && (szamlalo == 0))
			{
				board[row][col] = 1;
				sor=row;
				oszlop=col;
				szamlalo ++;
				draw.repaint();
			}
			
			if ((board[row][col] == 0) && (szamlalo == 1) && ((((row == sor) && ((col-oszlop==1) || (col-oszlop==-1))) || (((col==oszlop) && ((row-sor==1) || (row-sor==-1)))))))
			{
				board[row][col] = 1;
				sor2=row;
				oszlop2=col;
				szamlalo ++;
				draw.repaint();
				
			}
			
			if ((board[row][col] == 0) && (szamlalo == 2) && 
					((((row == sor) && ((col-oszlop==1) || (col-oszlop==-1))) || (((col==oszlop) && ((row-sor==1) || (row-sor==-1))))) 
					||
					(((row == sor2) && ((col-oszlop2==1) || (col-oszlop2==-1))) || (((col==oszlop2) && ((row-sor2==1) || (row-sor2==-1)))))))
			{
				board[row][col] = 1;
				sor3=row;
				oszlop3=col;
				szamlalo ++;
				draw.repaint();
			}
			
			if ((board[row][col] == 0) && (szamlalo == 3) &&
					((((row == sor) && ((col-oszlop==1) || (col-oszlop==-1))) || (((col==oszlop) && ((row-sor==1) || (row-sor==-1))))) 
					|| 
					(((row == sor2) && ((col-oszlop2==1) || (col-oszlop2==-1))) || (((col==oszlop2) && ((row-sor2==1) || (row-sor2==-1)))))
					||
					(((row == sor3) && ((col-oszlop3==1) || (col-oszlop3==-1))) || (((col==oszlop3) && ((row-sor3==1) || (row-sor3==-1)))))))
			{
				board[row][col] = 1;
				szamlalo ++;
				draw.repaint();				
			}
			
			if (fullBoard())
			{jatekvege();}
			
			if (szamlalo > 3 && (!fullBoard()))
				{
					xTurn = (-1);
					message.setText("2. játékos lép - piros");
					szamlalo = 0; sor=0; sor2=0; sor3=0; oszlop=0; oszlop2=0; oszlop3=0;
				}
	}
	
	/**
	 * Az első játékos egy körét kezeli.
	 * Véget ér, ha a maximális 4 lépést eléri vagy SPACE gombbal nem történik váltás.
	 * Körben minden lépés után vizsgáljuk, hogy a táblán van-e még kavics.
	 * Figyelünk arra, hogy egy körben csak a mellette lévő kavicsot lehet levenni.
	 * @param row A tábla mezői.
	 * @param col A tábla sorai.
	 */
	public void masodik (int row, int col) 
	{
		if ((board[row][col] == 0) && (szamlalo == 0))
		{
			board[row][col] = 2;
			sor=row;
			oszlop=col;
			szamlalo ++;
			draw.repaint();
		}
		
		if ((board[row][col] == 0) && (szamlalo == 1) && ((((row == sor) && ((col-oszlop==1) || (col-oszlop==-1))) || (((col==oszlop) && ((row-sor==1) || (row-sor==-1)))))))
		{
			board[row][col] = 2;
			sor2=row;
			oszlop2=col;
			szamlalo ++;
			draw.repaint();
			
		}
		
		if ((board[row][col] == 0) && (szamlalo == 2) && 
				((((row == sor) && ((col-oszlop==1) || (col-oszlop==-1))) || (((col==oszlop) && ((row-sor==1) || (row-sor==-1))))) 
				||
				(((row == sor2) && ((col-oszlop2==1) || (col-oszlop2==-1))) || (((col==oszlop2) && ((row-sor2==1) || (row-sor2==-1)))))))
		{
			board[row][col] = 2;
			sor3=row;
			oszlop3=col;
			szamlalo ++;
			draw.repaint();
		}
		
		if ((board[row][col] == 0) && (szamlalo == 3) &&
				((((row == sor) && ((col-oszlop==1) || (col-oszlop==-1))) || (((col==oszlop) && ((row-sor==1) || (row-sor==-1))))) 
				|| 
				(((row == sor2) && ((col-oszlop2==1) || (col-oszlop2==-1))) || (((col==oszlop2) && ((row-sor2==1) || (row-sor2==-1)))))
				||
				(((row == sor3) && ((col-oszlop3==1) || (col-oszlop3==-1))) || (((col==oszlop3) && ((row-sor3==1) || (row-sor3==-1)))))))
		{
			board[row][col] = 2;
			szamlalo ++;
			draw.repaint();	
		}
		
		if (fullBoard())
			{jatekvege();}
		
		if (szamlalo > 3 && (!fullBoard()))
			{
				xTurn = (1);
				message.setText("1. játékos lép - zöld");
				szamlalo = 0; sor=0; sor2=0; sor3=0; oszlop=0; oszlop2=0; oszlop3=0;
			}
	}

	/**
	 * Mezőt elválasztó vonalak kirajzolására szolgál.
	 * Kattintás megvizsgálása után a kavics képének megváltoztatása.
	 *
	 */
	class Drawing extends JComponent {
		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			for (int row = 0; row < 4; row++)
				for (int col = 0; col < 4; col++) {
					g.drawImage(boardPictures[board[row][col]].getImage(), col * 196, row * 132, 196, 132, this);
				}
			g2.setStroke(new BasicStroke(10));
			g.fillRect(196, 0, 5, 528);
			g.fillRect(392, 0, 5, 528);
			g.fillRect(588, 0, 5, 528);

			g.fillRect(0, 132, 784, 5);
			g.fillRect(0, 264, 784, 5);
			g.fillRect(0, 396, 784, 5);
	}
	}
	
	/**
	 * Az egér kattintásának érzékelésére szolgál.
	 * A kör átadása a másik játékosnak.
	 */
	public void mouseReleased(MouseEvent e) 
	{
		if (e.getButton() == MouseEvent.BUTTON1) 
		{
			/**
			 * A mezők sorainak érzékelése.
			 */
			int row = e.getY() / 142;
			/**
			 * A mezők oszlopainak érzékelése.
			 */
			int col = e.getX() / 206;
			if (xTurn == (1))
			elso(row, col);
			if (xTurn == (-1))
			masodik(row, col);	
		}		
	}
	
	/**
	 * SPACE, valamint az ESC billentyűk kezelése.
	 */
	public void keyPressed(KeyEvent e) {
		/**
		 * Lenyomott billentyű eltárolására szolgál.
		 */
		int keyCode = e.getKeyCode();
		if ((keyCode == KeyEvent.VK_SPACE) && (szamlalo >= 1)) 
		{
			xTurn *= (-1);
			szamlalo = 0; sor=0; sor2=0; sor3=0; oszlop=0; oszlop2=0; oszlop3=0;
			if (xTurn == (1))
				message.setText("1. játékos lép - zöld");
			if (xTurn == (-1))
				message.setText("2. játékos lép - piros");
		}
		if (keyCode == KeyEvent.VK_ESCAPE) 
		{
			datum();
			nyertes ="Játék félbeszakítva";
			uzenet ="Játék félbeszakítva.";
			fajlkezeles();
			JOptionPane.showMessageDialog(null, uzenet , "Eredmény", JOptionPane.INFORMATION_MESSAGE);
			jatek.dispose();
			new kezdokepernyo();
		}
		
	}
	
	public void keyReleased(KeyEvent e) {
    }

	public void keyTyped(KeyEvent e) {
	}  
	
	public void mouseClicked(MouseEvent e) {	
	}

	public void mouseEntered(MouseEvent e) {		
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}
}
