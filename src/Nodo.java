
public class Nodo {
	int x;
	int y;
	int g;
	int h;
	int f;
	Nodo pai;
	
	public Nodo(int x, int y, int g, int h, Nodo pai) {
		super();
		this.x = x;
		this.y = y;
		 this.g = g;
         this.h = h;
         this.f = g + h;
         this.pai = pai;
	}
}
