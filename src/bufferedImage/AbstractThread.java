package bufferedImage;

public abstract class AbstractThread extends Thread{	//clasa abstracta ce extinde clasa Thread, din ea extindem clasele ImageReader si ImageCalculator
	protected boolean available = false ;
	
	public AbstractThread(String name,boolean av) {		//constructor
		super(name);		//apeleaza constructorul din Thread si da nume thread-ului
		available=av;		//initializeaza available
	}
	
	@Override
	public void start(){
		System.out.println("Porneste threadul " + getName());	//mesaj informativ
		super.start();		//apeleaza metoda de creare a threadului
	}
	
	public void run(){		//suprascrie functia run din Threa si afiseaza un mesaj informativ 
		System.out.println("Ruleaza threadul " + getName());
	}
	
}