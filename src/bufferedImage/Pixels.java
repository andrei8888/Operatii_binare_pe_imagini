package bufferedImage;

public class Pixels {				//clasa ce stocheaza un pixel din prima imagine si unul din cea de-a doua imagine
	int pixelA=0;
	int pixelB=0;
	
	public Pixels(){				//un constructor default
		pixelA=0;
		pixelB=0;
	}
	
	public int getPixelA() {		//getter pentru primul pixel (apelata in ImageCalculator)
		return pixelA;
	}
	public int getPixelB() {		//getter pentru al doilea pixel (apelata in ImageCalculator)
		return pixelB;
	}
	public void put(int pA,int pB){		//pune pixelii in matricea din BufferedPixels
		pixelA=pA;
		pixelB=pB;
	}
	
	
}
