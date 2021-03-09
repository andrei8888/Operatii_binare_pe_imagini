package bufferedImage;
import java.awt.image.BufferedImage;

public class BufferedPixels {				//manipuleaza datele in comun pentru cele doua thread-uri
	private Pixels[][] pixels;				//matrice in care se stocheaza pixelii celor doua imagini
	private BufferedImage imagA;			//handler pentru prima imagine
	private BufferedImage imagB;			//handler pentru a doua imagine
	private String numeDest;				//numele fisierului destinatie dat ca parametru in main
	private int operatie;					//numarul operatiei dat ca parametru in main
	private boolean available = false ;		//folosit pentru sincronizarea threadurilor
	private boolean readAll = false ;		//semnifica faptul ca au fost cititi toti pixelii din imagini in matricea pixels
	
	public BufferedPixels(BufferedImage imageA, BufferedImage imageB, String nume,int op){
			//constructor apelat in main, transmite datele comune celor doua threaduri
		numeDest=nume;	//da valoarea numele fisierului destinatie (in this)
		operatie=op;	//da valoarea operatiei
		imagA=imageA;	//initializeaza handlerii
		imagB=imageB;
		pixels=new Pixels[imageA.getHeight()][imageB.getWidth()];	//creaza matricea
		for (int y = 0; y < imageA.getHeight(); ++y) 
	        for (int x = 0; x < imageA.getWidth(); ++x)
	        	pixels[y][x]=new Pixels();							//initializeaza pixelii
	}

	public synchronized Pixels get(int x,int y) {	//functie apelata in ImageCalculator, returneaza pixelii de pe coloana x, linia y
		if(!readAll){				//daca s-a citit toata informatia nu e nevoie sa mai stea pe wait
			while (!available) {	//sincronizeaza threadurile, asteapta ca functia put sa faca available pe true
				try {
					wait ();		//asteapta pana se face available true
				} catch (InterruptedException e) {
					e.printStackTrace ();
					}
			}
		}
		available = false ;		//face available false 
		notifyAll ();
		return pixels[y][x];	//returneaza pixelii doriti
	}
	
	public synchronized void put(int number1,int number2, int x, int y) {	//functie apelata in ImageReader
				//completeaza matricea pixels cu valorile din cei doi handleri
		while ( available ) {		//sincronizeaza threadurile, asteapta ca functia get sa faca available pe false
		try {
			wait ();				//asteapta pana se face available false
		} catch (InterruptedException e) {
			e.printStackTrace ();
			}
		}
		pixels[y][x].put(number1, number2);		//apeleaza functia put din clasa Pixels
		available = true;						//face available true
		notifyAll ();
	}
	
	public void setReadAll(){		//seteaza readAll pe true atunci cand ImageReader a terminat de citit imaginile 
		readAll=true;
	}

	public BufferedImage getImagA() {		//getter pentru handler-ul primei imagini
		return imagA;
	}

	public BufferedImage getImagB() {		//getter pentru handler-ul celei de-a doua imagini
		return imagB;
	}

	public Pixels[][] getPixels() {			//getter pentru matricea de pixeli
		return pixels;
	}

	public String getNumeDest() {			//getter pentru numele fisierului destinatiei
		return numeDest;
	}

	public int getOperatie() {				//getter pentru operatie
		return operatie;
	}
}
