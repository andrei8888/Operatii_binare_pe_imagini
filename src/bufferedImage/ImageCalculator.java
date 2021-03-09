package bufferedImage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageCalculator extends AbstractThread{		//clasa ce implementeaza threadul de consum informatie
	private BufferedPixels buffer;							//variabila in care se baga valorile din imagini si main
	
	public ImageCalculator(BufferedPixels b,String str) {	//constructor
		super(str,false);									//apeleaza constructor din AbstractThread si da nume thread-ului
		buffer = b;											//initializeaza handler-ul buffer cu date din main
	}
	
	@Override
	public void run () {		//descrie ce face threadul de consum informatie
		super.run();
		int y=0,x=0;		//se parcurge buffer.pixels cu ele
		
		int sleepCase=0;	//variabila care arata al catelea sfert de informatie a fost consumata
		int h=buffer.getImagA().getHeight();	//inaltimea imaginii
		int w=buffer.getImagA().getWidth();		//lungimea imaginii
		for (y = 0; y < h; ++y) {
	        for (x = 0; x < w; ++x){	//parcurgem imaginea si apelam functia get pentru sincronizare
				@SuppressWarnings("unused")
				int pixelA=(int)buffer.get(x, y).getPixelA();	//apelam functia get din buffer
				@SuppressWarnings("unused")
				int pixelB=(int)buffer.get(x, y).getPixelB();	//apelam functia get din buffer
				if(x==buffer.getImagA().getWidth()-1 && y==buffer.getImagA().getHeight()/2-1)
					buffer.setReadAll();	//cand consumul ajunge la jumatate se seteaza buffer.readAll (doar asa mi-a functionat)
				if((y+1)*(x+1)==h*w/4 && sleepCase==0){	//sleep pentru primul sfert de imagine consumata
		        	   System.out.println("A fost primit primul sfert de imagine ("+x+", "+y+")");	//mesaj informativ + pozitie
		        	   sleepCase=1;	//urmatorul caz de sleep
		        	   try {
		        		   sleep((int)1000);
		        	   } catch (InterruptedException e) { e.printStackTrace(); }
		           }
		           if((y+1)*(x+1)==h*w/2 && sleepCase==1){	//sleep pentru al doilea sfert de imagine consumata
		        	   System.out.println("A fost primit al doilea sfert de imagine ("+x+", "+y+")");
		        	   sleepCase=2;
		        	   try {
		        		   sleep((int)1000);
		        	   } catch (InterruptedException e) { e.printStackTrace(); }
		           }
		           if((y+1)*(x+1)==3*h*w/4 && sleepCase==2){	//sleep pentru al treilea sfert de imagine consumata
		        	   System.out.println("A fost primit al treilea sfert de imagine ("+x+", "+y+")");
		        	   sleepCase=3;
		        	   try {
		        		   sleep((int)1000);
		        	   } catch (InterruptedException e) { e.printStackTrace(); }
		           }
		    }
		}
		if(sleepCase==3)
			System.out.println("A fost primit si ultimul sfert de imagine");	//mesaj ca a fost consumata toata informatia
		System.out.println("Procesarea imaginii incepe");	//mesaj informativ de incepere a prelucrarii
		
		Pixels[][] pixels=buffer.getPixels();	//matricea de pixeli din buffer
		String nume=buffer.getNumeDest();		//numele fisierului din buffer
		int operatie=buffer.getOperatie();		//operatia din buffer
		if(operatie==1 || operatie==2 || operatie==3){	//prelucreaza o singura imagine pentru operatia selectata
			BufferedImage img=new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);	//handler pentru fisierul de iesire
			int pixel = 0;	//se stocheaza pixelul de calculat
			for (y = 0; y < h; ++y) {
		        for (x = 0; x < w; ++x){	//se parcurge matricea pixels
		        	if(operatie==1)		//in functie de operatie se calculeaza pixelul
		        		pixel=pixels[y][x].getPixelA() & pixels[y][x].getPixelB();	//pentru AND
		        	else if(operatie==2)
		        		pixel=pixels[y][x].getPixelA() | pixels[y][x].getPixelB();	//pentru OR
		        	else if(operatie==3)
		        		pixel=pixels[y][x].getPixelA() ^ pixels[y][x].getPixelB();	//pentru XOR
					img.setRGB(x, y, pixel);	//se pune in handlerul img pixelul calculat in pozitia (x,y) 
		        }
		    }
			System.out.println("Procesarea imaginii s-a terminat. Uita-te in folderul rezultate"); //mesaj informativ de terminare a prelucrarii
			try {
				ImageIO.write(img,"bmp",new File("rezultate\\"+nume+".bmp"));	//se salveaza fisierul cu numele nume in folderul rezultate in format bmp
			} catch (IOException e) { e.printStackTrace(); }
		}
		else{	//prelucreaza cele trei imagini
			BufferedImage imgAND=new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);	//handler pentru imaginea ResAND
			BufferedImage imgOR=new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);	//handler pentru imaginea ResOR
			BufferedImage imgXOR=new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);	//handler pentru imaginea ResXOR
			int pixelAND,pixelOR,pixelXOR;	//pixelul de calculat pentru fiecare imagine
			for (y = 0; y < h; ++y) {
		        for (x = 0; x < w; ++x){	//se parcurge matricea pixels
		        	pixelAND=pixels[y][x].getPixelA() & pixels[y][x].getPixelB();	//se calculeaza pixelul
		        	imgAND.setRGB(x, y, pixelAND);									//si se seteaza in handler-ul asociat
		        	pixelOR=pixels[y][x].getPixelA() | pixels[y][x].getPixelB();
		        	imgOR.setRGB(x, y, pixelOR);
		        	pixelXOR=pixels[y][x].getPixelA() ^ pixels[y][x].getPixelB();
		        	imgXOR.setRGB(x, y, pixelXOR);
		        }
		    }
			System.out.println("Procesarea imaginilor s-a terminat. Uita-te in folderul rezultate");	//mesaj informativ de terminare a prelucrarii
			try {
				ImageIO.write(imgAND,"bmp",new File("rezultate\\"+"ResAND.bmp"));	//se salveaza fiecare fisier in folderul reazultate cu numele implicit in format bmp
				ImageIO.write(imgOR,"bmp",new File("rezultate\\"+"ResOR.bmp"));
				ImageIO.write(imgXOR,"bmp",new File("rezultate\\"+"ResXOR.bmp"));
			} catch (IOException e) { e.printStackTrace(); }
		}
		System.out.println("Threadul "+ getName()+" s-a terminat");	//mesaj ca s-a terminat threadul de consum informatie
	}
}
