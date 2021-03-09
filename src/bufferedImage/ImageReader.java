package bufferedImage;
import java.awt.image.BufferedImage;


public class ImageReader extends AbstractThread{		//clasa ce implementeaza threadul de citire
	private BufferedPixels buffer;						//variabila in care se baga valorile din imagini si main
	
	public ImageReader (BufferedPixels b,String str) {	//constructor
		super(str,true);		//apeleaza constructor din AbstractThread si da nume thread-ului
		buffer = b;				//initializeaza handler-ul buffer cu date din main
	}
	
	@Override
	public void run () {		//descrie ce face threadul de citire
		super.run();
		BufferedImage imageA=buffer.getImagA();	//handlerul primei imagini
		BufferedImage imageB=buffer.getImagB();	//handlerul pentru cea de-a doua imagine
		int sleepCase=0;	//variabila care arata al catelea sfert de informatie a fost citit
		int h=imageA.getHeight();	//inaltimea imaginii
		int w=imageA.getWidth();	//lungimea imaginii
		for (int y = 0; y < h; ++y) {	
	        for (int x = 0; x < w; ++x) {	//parcurgem imaginile cu doua for-uri pe fiecare linie 
	           int pixelA = imageA.getRGB(x, y);	//pixel din prima imagine
	           int pixelB = imageB.getRGB(x, y);	//pixel din a doua imagine
	           buffer.put(pixelA, pixelB, x, y);	//punem pixelii in matrice
	           if((y+1)*(x+1)==h*w/4 && sleepCase==0){	//sleep pentru primul sfert de imagine citita
	        	   System.out.println("A fost citit primul sfert de imagine ("+x+", "+y+")"); //mesaj informativ + pozitie
	        	   sleepCase=1;	//urmatorul caz de sleep
	        	   try {
	        		   sleep((int)1000);
	        	   } catch (InterruptedException e) { e.printStackTrace(); }
	        	   System.out.println("Se reia citirea");
	           }
	           if((y+1)*(x+1)==h*w/2 && sleepCase==1){	//sleep pentru al doilea sfert de imagine citita
	        	   System.out.println("A fost citit al doilea sfert de imagine ("+x+", "+y+")");
	        	   sleepCase=2;
	        	   try {
	        		   sleep((int)1000);
	        	   } catch (InterruptedException e) { e.printStackTrace(); }
	        	   System.out.println("Se reia citirea");
	           }
	           if((y+1)*(x+1)==3*h*w/4 && sleepCase==2){	//sleep pentru al treilea sfert de imagine citita
	        	   System.out.println("A fost citit al treilea sfert de imagine ("+x+", "+y+")");
	        	   sleepCase=3;
	        	   try {
	        		   sleep((int)1000);
	        	   } catch (InterruptedException e) { e.printStackTrace(); }
	        	   System.out.println("Se reia citirea");
	           }
	        }
		}
		if(sleepCase==3)
			System.out.println("A fost citit si ultimul sfert de imagine");	//mesaj ca a fost citita toata informatia
		System.out.println("Threadul "+ getName()+" s-a terminat");		//mesaj ca s-a terminat threadul de citire
	}
}
