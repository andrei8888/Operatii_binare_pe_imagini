package binaryMain;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import javax.imageio.ImageIO;

import bufferedImage.*;

public class BinaryOperationsMain {
	

	public static void main(String[] args)  {		//functia principala ce trateaza modul in care utilizatorul introduce datele si
													//lanseaza thredurile in executie
		Scanner scanner = new Scanner(System.in);	//handler pentru citirea de la tastatura
		int operatie = 0;							//tipul operatiei(0-executa toate operatiile, 1-AND, 2-OR, 3-XOR
		BufferedImage imgfile1 = null;				//handler pentru prima imagine
		BufferedImage imgfile2 = null;				//handler pentru a doua imagine
		String rezultatFisier = null;				//numele fisierului rezultat (in cazul uneia din operatiile 1, 2 sau 3)
		if(args.length!=0){							//trateaza argumentele in linie de comanda
			int argumenteCorecte = 0;				//verifica integritatea argumentelor
			if(args.length!=3 && args.length!=4){	//mesaj de eroare in cazul in care numarul argumentelor nu este cel asteptat
				System.out.print("Nu ati introdus suficiente argumente!\nArgumentele pot fi:\n\t");
				System.out.print("operatie[1/2/3] nume_fisier_1.extensie numer_fisier_2.extensie\n\t");
				System.out.print("operatie[1/2/3] nume_fisier_1.extensie numer_fisier_2.extensie nume_fisier_iesire\n\t");
				System.out.print("operatie[0] nume_fisier_1.extensie numer_fisier_2.extensie");
				scanner.close();					//inchiderea handler-ului de consola
				return;								//inchiderea programului
			}
			try{
				operatie=Integer.parseInt(args[0]);	//parsarea operatiei din primul argument
				if(!(0<=operatie && operatie<=3))	//verifica daca operatia este valida
					argumenteCorecte=1;
				
			} catch(java.lang.NumberFormatException e){ argumenteCorecte=1; }
			
			imgfile1 = argumentFisierDeIntrare(args[1]);	//handler parsat din al doilea argument
			imgfile2 = argumentFisierDeIntrare(args[2]);	//handler parsat din al treilea argument
			if(imgfile1==null || imgfile2==null)			//trateaza cazul in care nu a fost gasita imaginea
				argumenteCorecte=2;
			if(operatie==0 && argumenteCorecte==0 && args.length==4)	//trateaza cazul in care se introduce nume pentru fisier de iesire dar se selecteaza toate operatiile
				argumenteCorecte=3;
			if(operatie==0 && argumenteCorecte==0){			//mesaj de atentionare in cazul in care se selecteaza operatiea 0
				System.out.println("Atentie! Fisierele vor avea denumirea \'ResAND\' \'ResOR\' si \'ResXOR\' si vor suprascrie orice fisier cu un astfel de nume (si extensie .bmp)");
			}
			else if(argumenteCorecte==0){					//trateaza numele fisierului de iesire in cazul in care se selecteaza operatiea 1, 2 sau 3
				if(args.length==3){							//numele fisierului poate fi omis in lista de argumente
					if(operatie==1){						//va avea un nume implicit in functie de operatia selectata
						rezultatFisier="ResAND";
						System.out.println("Fisierul va avea denumirea \'ResAND\': ");
					}
					else if(operatie==2){
						rezultatFisier="ResOR";
						System.out.println("Fisierul va avea denumirea \'ResOR\': ");
					}
					else if(operatie==3){
						rezultatFisier="ResXOR";
						System.out.println("Fisierul va avea denumirea \'ResXOR\': ");
					}
				}
				else if(args.length==4)						//numele fisierului parsat din al patrulea argument
					rezultatFisier=args[3];
			}
			
			if(argumenteCorecte!=0){						//trateaza cazurile de introducere gresita argumentelor
		    	debug(argumenteCorecte);					//trimite mesajul de eroare asociat codului din argumenteCorecte
		    	scanner.close();
		    	return;										//inchiderea programului
		    }
		}
		else{												//trateaza introducerea datelor de la tastatura
			operatie = citesteOperatia(scanner);			//citeste si verfica codul operatiei
			
			System.out.print("Introduceti numele primului fisier (inclusiv extensie): ");
			imgfile1 = citesteFisierDeIntrare(scanner);		//handler rezultat din inputul utilizatorului
			
			System.out.print("Introduceti numele celui de-al doilea fisier (inclusiv extensie de preferat aceeasi): ");
			imgfile2 = citesteFisierDeIntrare(scanner);		//handler rezultat din inputul utilizatorului
			
			if (imgfile1.getWidth() != imgfile2.getWidth() || imgfile1.getHeight() != imgfile2.getHeight()){
				scanner.close();
		        System.out.println("Imaginile selectate nu au aceeasi dimensiune!");
		        return;		//mesaj de eroare pentru cazul in care cele doua imagini sunt incompatibile si inchiderea aplicatiei
		    }
			
			rezultatFisier = citesteNumeFisierDeIesire(scanner, operatie);		//numele fisierului de iesire citit de la tastatura
		}
		creeazaFolder("rezultate");						//creaza folderul (daca nu exista) rezultate unde este/sunt pus/e fisierul/ele rezultat/e
		
		
		BufferedPixels date=new BufferedPixels(imgfile1,	//datele utilizate pentru comunicatia dintre cele doua threaduri
												imgfile2,
												rezultatFisier,
												operatie);
		ImageReader threadCitire=new ImageReader(date,"Citeste_imagine");				//creaza threadul de citire imagine
		ImageCalculator threadConsumare=new ImageCalculator(date,"Primeste_imagine");	//creaza threadul de consumare si procesare imagine
		threadCitire.start();															//lansarea threadului de citire imagine
		threadConsumare.start();														//lansarea threadului de primire imagine
		scanner.close();
	}
	
	private static BufferedImage citesteFisierDeIntrare(Scanner scanner){		//functie de citire de la tastatura a celor doua fisiere
		boolean fileExist=false;			//verfica daca s-a introdus un nume valid
		String filename1=null;				//numele fisierului
		File file1=null;					//handler pentru fisier;
		do{
			filename1=scanner.next();		//citire de la tastatura
			file1=new File(filename1);		//rturneaza handler pentru fisier
			if (file1.exists()) 			//verifica daca exista fisierul 
	            fileExist=true;
	        else							//mesaj de eroare si posibilitatea introducerii numelui din nou
	            System.out.print("Fisierul nu exista! Incercati din nou: ");
		}while(!fileExist);
		BufferedImage imgfile1 = null;		//handler-ul de returnat
		try {
			imgfile1 = ImageIO.read(file1);	//returneaza handler-ul dorit
		} catch (IOException e1) { e1.printStackTrace(); }
		return imgfile1;
	}
	
	private static int citesteOperatia(Scanner scanner){						//functie de citire de la tastatura a operatiei
		boolean badOp;						//verifica daca s-a introdus o operatie valida
		int operatie;						//operatia de returnat
		System.out.println("Selectati operatia: \n\t0 - Toate\n\t1 - AND\n\t2 - OR\n\t3 - XOR");	//mesaj informativ
		do{
			operatie=scanner.nextInt();		//citire operatie de la tastatura
			if(0<=operatie && operatie<=3)	//verifica daca s-a introdus o operatie valida
				badOp=false;
			else{
				badOp=true;					//mesaj de eroare si posibilitatea introducerii operatiei din nou
				System.out.print("Nu ati selectat operatia corecta! Incercati din nou: ");
			}
		}while(badOp);		
		return operatie;
	}
	
	private static String citesteNumeFisierDeIesire(Scanner scanner, int operatie){	//functie de citire de la tastatura a numelui fisierului rezultat(fara extensie)
		String resFile=null;						//handler de returnat
		if(operatie==0){							//in cazul in care se selecteaza sa se execute toate operatia se salveaza cu nume implicite 
			System.out.println("Atentie! Fisierele vor avea denumirea \'ResAND\' \'ResOR\' si \'ResXOR\' si vor suprascrie orice fisier cu un astfel de nume (si extensie .bmp)");
		}											//se afiseaza un mesaj de atentionare
		else{
			System.out.print("Introduceti numele fisierului destinatie (implicit va avea denumirea ");
			if(operatie==1){						//se afiseaza un mesaj de informare pentru citirea de la tastatura
				resFile="ResAND";					//se apasa enter daca se doreste denumirea implicita
				System.out.print("\'ResAND\'): ");
			}
			else if(operatie==2){
				resFile="ResOR";
				System.out.print("\'ResOR\'): ");
			}
			else if(operatie==3){
				resFile="ResXOR";
				System.out.print("\'ResXOR\'): ");
			}
			scanner.nextLine();						//citeste numele de la tastatura (sau enter pentru nume implicit)
			String s=scanner.nextLine();			//pentru a scapa buffer-ul de new line introdus de tasta enter
			if(!s.equals(""))						//verifica daca nu s-a apasat enter
				resFile=s;
		}
		return resFile;
	}
	
	private static void creeazaFolder(String numeFolder) {	//functie de creare a folderului rezultate
		Path path = Paths.get(numeFolder);					//handler ce contine numele folderului
		try {
			Files.createDirectories(path);					//creaza folderul cu ajutorul variabilei path
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	private static BufferedImage argumentFisierDeIntrare(String nume){	//functie de verificare a argumentelor pentru fisierele de intrare
		File file1=null;
		file1=new File(nume);					//handler de fisier
		BufferedImage imgfile1 = null;			//handler ce trebuie returnat
		if (file1.exists()) {					//verifica daca exista fisierul
			try {
				imgfile1 = ImageIO.read(file1);	//returneaza handler-ul dorit
			} catch (IOException e1) { e1.printStackTrace(); }
		}
		return imgfile1;
	}
	
	private static void debug(int cod){		//afiseaza mesaje utile de eroare pentru argumentele din linia de comanda
		switch(cod){
			case 1: System.out.println("Nu ati selectat o operatia valida!\nOperatiile pot fi: \n\t0 - Toate\n\t1 - AND\n\t2 - OR\n\t3 - XOR");
					break;					//cazul in care nu s-a selectat o operatie valida (nu e 0, 1, 2 sau 3)
			case 2: System.out.println("Cel putin unul din fisiere nu a putut fi gasit!");
					break;					//cazul in care ori al doilea ori al treilea argument nu trimite la un fisier valid
			case 3:	System.out.println("Nu se poate introduce nume de fisier de iesire daca s-a selectat operatia 0");
					break;					//cazul in care s-a introdus un al patrulea argument dar se produc 3 fisiere diferite pentru fiecare operatie
		}
	}
	
}