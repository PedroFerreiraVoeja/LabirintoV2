import java.util.PriorityQueue;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.awt.image.*;




public class GamePanel extends Canvas implements Runnable
{
private static final int PWIDTH = 960;
private static final int PHEIGHT = 800;
private Thread animator;
private boolean running = false;
private boolean gameOver = false; 


int FPS,SFPS;
int fpscount;

public static Random rnd = new Random();

//BufferedImage imagemcharsets;

boolean LEFT, RIGHT,UP,DOWN;

public static int mousex,mousey; 

public static ArrayList<Agente> listadeagentes = new ArrayList<Agente>();

Mapa_Grid mapa;

double posx,posy;

MeuAgente meuHeroi = null;
Personagem meuPersonagem = null;

private int currentPathNode = 0;

//TODO ESSE È O RESULTADO
int caminho[] = null;

float zoom = 1;

int ntileW = 60;
int ntileH = 50;

Font f = new Font("", Font.BOLD, 20);

public GamePanel()
{

	setBackground(Color.white);
	setPreferredSize( new Dimension(PWIDTH, PHEIGHT));

	// create game components
	setFocusable(true);

	requestFocus(); // JPanel now receives key events	
	
	
	// Adiciona um Key Listner
	addKeyListener( new KeyAdapter() {
		public void keyPressed(KeyEvent e)
			{ 
				int keyCode = e.getKeyCode();
				
				if(keyCode == KeyEvent.VK_LEFT){
					LEFT = true;
				}
				if(keyCode == KeyEvent.VK_RIGHT){
					RIGHT = true;
				}
				if(keyCode == KeyEvent.VK_UP){
					UP = true;
				}
				if(keyCode == KeyEvent.VK_DOWN){
					DOWN = true;
				}	
			}
		@Override
			public void keyReleased(KeyEvent e ) {
				int keyCode = e.getKeyCode();
				
				if(keyCode == KeyEvent.VK_LEFT){
					LEFT = false;
				}
				if(keyCode == KeyEvent.VK_RIGHT){
					RIGHT = false;
				}
				if(keyCode == KeyEvent.VK_UP){
					UP = false;
				}
				if(keyCode == KeyEvent.VK_DOWN){
					DOWN = false;
				}
			}
	});
	
	addMouseMotionListener(new MouseMotionListener() {
		
		@Override
		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub
			mousex = e.getX(); 
			mousey = e.getY();
			

		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			// TODO Auto-generated method stub
			if(e.getButton()==3){
				int mousex = (int)((e.getX()+mapa.MapX)/zoom);
				int mousey = (int)((e.getY()+mapa.MapY)/zoom);
				
				int mx = mousex/16;
				int my = mousey/16;
				
				if(mx>mapa.Altura) {
					return;
				}
				if(my>mapa.Largura) {
					return;
				}
				
				mapa.mapa[my][mx] = 1;
			}
		}
	});
	
	addMouseListener(new MouseListener() {
		
		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			//System.out.println(" "+arg0.getButton());
			int mousex = (int)((arg0.getX()+mapa.MapX)/zoom);
			int mousey = (int)((arg0.getY()+mapa.MapY)/zoom);
			
			//System.out.println(""+arg0.getX()+" "+mapa.MapX+" "+zoom);
			//System.out.println(""+mousex+" "+mousey);
			
			int mx = mousex/16;
			int my = mousey/16;
			
			if(mx>mapa.Altura) {
				return;
			}
			if(my>mapa.Largura) {
				return;
			}
			
			if(arg0.getButton()==3){

				
				if(mapa.mapa[my][mx]==0){
					mapa.mapa[my][mx] = 1;
				}else{
					mapa.mapa[my][mx] = 0;
				}
			}
			if(arg0.getButton()==1){
				if(mapa.mapa[my][mx]==0) {
					caminho = null;
					long timeini = System.currentTimeMillis();

					// TODO Executa Algoritmo
					System.out.println(""+my+" "+mx);
					System.out.println("meueroi "+(int)(meuHeroi.X/16)+" "+(int)(meuHeroi.Y/16));
					rodaBuscaAEstrela((int)(meuPersonagem.X/16),(int)(meuPersonagem.Y/16),mx,my);
					currentPathNode = 0;
					long timefin = System.currentTimeMillis() - timeini;
					System.out.println("Tempo Final: "+timefin);
				}else {
					System.out.println("Caminho Final Bloqueado");
				}
			}
		}
		
		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
	});
	
	addMouseWheelListener(new MouseWheelListener() {
		
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			//System.out.println("w "+e.getWheelRotation());
			if(e.getWheelRotation()>0) {
				zoom= zoom*1.1f;
			}else if(e.getWheelRotation()<0) {
				zoom= zoom*0.90f;
			}
			
			ntileW = (int)((960/zoom)/16)+1;
			ntileH = (int)((800/zoom)/16)+1;
			
			if(ntileW>=1000) {
				ntileW = 1000;
			}
			if(ntileH>=1000) {
				ntileH = 1000;
			}
			mapa.NumeroTilesX = ntileW;
			mapa.NumeroTilesY = ntileH;
		}
	});

	meuHeroi = new MeuAgente(10, 10, Color.RED);
	meuPersonagem = new Personagem("Chara1.png");
	meuPersonagem.X = 10;
	meuPersonagem.Y = 10;
	
//	listadeagentes.add(meuHeroi);
	listadeagentes.add(meuPersonagem);
	
	mousex = mousey = 0;
	
	mapa = new Mapa_Grid(100,100,ntileW, ntileH);
	mapa.loadmapfromimage("/imagemlabirinto1000.png");
	
} // end of GamePanel()

private final PriorityQueue<Nodo> listaAberta = new PriorityQueue<>(new Comparator<Nodo>() {
    @Override
    public int compare(Nodo n1, Nodo n2) {
        return Integer.compare(n1.f, n2.f);
    }
});

// Lista fechada (já processada)
private final HashSet<Integer> listaFechada = new HashSet<Integer>();

public boolean rodaBuscaAEstrela(int iniX, int iniY, int objX, int objY) {
    // Resetar as listas abertas e fechadas
	 synchronized(listaAberta) { 
	        listaAberta.clear(); 
	    }
	    synchronized(listaFechada) { 
	        listaFechada.clear(); 
	    }
    caminho = null;

    Nodo start = new Nodo(iniX, iniY, 0, calcularHeuristica(iniX, iniY, objX, objY), null);
    synchronized(listaAberta) {
        listaAberta.add(start);
    }

    while (!listaAberta.isEmpty()) {
        Nodo nodoAtual;
        synchronized(listaAberta) {
            if (listaAberta.isEmpty()) break;
            nodoAtual = listaAberta.poll();
        }
        System.out.println(""+nodoAtual.x+" "+nodoAtual.y+" | "+objX+" "+objY);
        
        // Se atingiu o objetivo
        if (nodoAtual.x == objX && nodoAtual.y == objY) {
            reconstruirCaminho(nodoAtual);
            return true;
        }

        synchronized(listaFechada) {
            listaFechada.add(nodoAtual.x + nodoAtual.y*1000);
        }


        // Avaliar os 4 vizinhos (cima, baixo, esquerda, direita)
        Nodo[] vizinhos = new Nodo[8];
        vizinhos[0] = new Nodo(nodoAtual.x, nodoAtual.y + 1, nodoAtual.g + 10, calcularHeuristica(nodoAtual.x, nodoAtual.y + 1, objX, objY), nodoAtual); // Baixo
        vizinhos[1] = new Nodo(nodoAtual.x + 1, nodoAtual.y, nodoAtual.g + 10, calcularHeuristica(nodoAtual.x + 1, nodoAtual.y, objX, objY), nodoAtual); // Direita
        vizinhos[2] = new Nodo(nodoAtual.x, nodoAtual.y - 1, nodoAtual.g + 10, calcularHeuristica(nodoAtual.x, nodoAtual.y - 1, objX, objY), nodoAtual); // Cima
        vizinhos[3] = new Nodo(nodoAtual.x - 1, nodoAtual.y, nodoAtual.g + 10, calcularHeuristica(nodoAtual.x - 1, nodoAtual.y, objX, objY), nodoAtual); // Esquerda
        vizinhos[4] = new Nodo(nodoAtual.x + 1, nodoAtual.y + 1, nodoAtual.g + 14, calcularHeuristica(nodoAtual.x + 1, nodoAtual.y + 1, objX, objY), nodoAtual); // Baixo-Direita
        vizinhos[5] = new Nodo(nodoAtual.x - 1, nodoAtual.y + 1, nodoAtual.g + 14, calcularHeuristica(nodoAtual.x - 1, nodoAtual.y + 1, objX, objY), nodoAtual); // Baixo-Esquerda
        vizinhos[6] = new Nodo(nodoAtual.x + 1, nodoAtual.y - 1, nodoAtual.g + 14, calcularHeuristica(nodoAtual.x + 1, nodoAtual.y - 1, objX, objY), nodoAtual); // Cima-Direita
        vizinhos[7] = new Nodo(nodoAtual.x - 1, nodoAtual.y - 1, nodoAtual.g + 14, calcularHeuristica(nodoAtual.x - 1, nodoAtual.y - 1, objX, objY), nodoAtual); // Cima-Esquerda

        for (Nodo vizinho : vizinhos) {
        	if (isProximoDeBarreira(vizinho.x, vizinho.y) && (vizinho.x != nodoAtual.x && vizinho.y != nodoAtual.y)) {
                continue; // Ignora movimentos diagonais próximos a uma barreira
            }
        	
            if (vizinho.x < 0 || vizinho.y < 0 || vizinho.x >= mapa.Largura || vizinho.y >= mapa.Altura || mapa.mapa[vizinho.y][vizinho.x] == 1 || listaFechada.contains(vizinho.x + vizinho.y * 1000)) {
                continue; // Ignora obstáculos e nós já processados
            }

            // Verifica se o nó já está na lista aberta com um custo melhor
            synchronized(listaAberta) {
                if (!listaAberta.contains(vizinho) 
                  || vizinho.f < getNodoInListaAberta(vizinho).f) {
                    listaAberta.add(vizinho);
                }
            }
        }
    }

    return false;
}

private boolean isProximoDeBarreira(int x, int y) {
    // Somente 4 direções: direita, esquerda, baixo, cima
    int[][] dirs = {
        { 1,  0},  // direita
        {-1,  0},  // esquerda
        { 0,  1},  // baixo
        { 0, -1}   // cima
    };
    for (int[] d : dirs) {
        int nx = x + d[0];
        int ny = y + d[1];
        if (nx >= 0 && ny >= 0 && nx < mapa.Largura && ny < mapa.Altura) {
            if (mapa.mapa[ny][nx] == 1) {
                return true;
            }
        }
    }
    return false;
}

// Método para calcular a heurística (Manhattan)
private int calcularHeuristica(int x, int y, int objX, int objY) {
    return (Math.abs(x - objX) + Math.abs(y - objY)) * 10; // Multiplicamos por 10 para ser consistente com G
}

// Método para reconstruir o caminho
private void reconstruirCaminho(Nodo destino) {
    LinkedList<int[]> caminhoList = new LinkedList<>();
    Nodo atual = destino;

    while (atual != null) {
        caminhoList.addFirst(new int[]{atual.x, atual.y});
        atual = atual.pai;
    }

    caminho = new int[caminhoList.size() * 2];
    int index = 0;
    for (int[] pos : caminhoList) {
        caminho[index] = pos[0];
        caminho[index + 1] = pos[1];
        index += 2;
    }
}

// Função auxiliar para obter o nó da lista aberta
private Nodo getNodoInListaAberta(Nodo nodo) {
    for (Nodo n : listaAberta) {
        if (n.x == nodo.x && n.y == nodo.y) {
            return n;
        }
    }
    return null;
}

private void updateHeroMovement(long DiffTime) {
    if (caminho != null && currentPathNode < caminho.length / 2) {
        // Obter as coordenadas do próximo nodo (multiplicamos por 16 para converter de tile para pixel)
        int targetTileX = caminho[currentPathNode * 2];
        int targetTileY = caminho[currentPathNode * 2 + 1];
        // Calcula a posição central do tile
        double targetPosX = targetTileX * 16 + 8;
        double targetPosY = targetTileY * 16 + 8;
        // Calcula a distância da posição atual do herói até o alvo
        double dx = targetPosX - meuPersonagem.X;
        double dy = targetPosY - meuPersonagem.Y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        // Define uma velocidade (em pixels por frame, ajustada pelo DiffTime)
        double speed = 0.2 * DiffTime;
        if (dist <= speed) {
        	meuPersonagem.X = targetPosX;
        	meuPersonagem.Y = targetPosY;
            currentPathNode++; // Avança para o próximo nó do caminho
        } else {
            double nx = (dx / dist) * speed;
            double ny = (dy / dist) * speed;
            meuPersonagem.X += nx;
            meuPersonagem.Y += ny;
        }
    }
}


public void startGame()
// initialise and start the thread
{
	if (animator == null || !running) {
		animator = new Thread(this);
		animator.start();
	}
} // end of startGame()

public void stopGame()
// called by the user to stop execution
{ running = false; }


public void run()
/* Repeatedly update, render, sleep */
{
	running = true;
	
	long DifTime,TempoAnterior;
	
	int segundo = 0;
	DifTime = 0;
	TempoAnterior = System.currentTimeMillis();
	
	this.createBufferStrategy(2);
	BufferStrategy strategy = this.getBufferStrategy();
	
	while(running) {
	
		gameUpdate(DifTime); // game state is updated
		Graphics g = strategy.getDrawGraphics();
		gameRender((Graphics2D)g); // render to a buffer
		strategy.show();
	
		try {
			Thread.sleep(0); // sleep a bit
		}	
		catch(InterruptedException ex){}
		
		DifTime = System.currentTimeMillis() - TempoAnterior;
		TempoAnterior = System.currentTimeMillis();
		
		if(segundo!=((int)(TempoAnterior/1000))){
			FPS = SFPS;
			SFPS = 1;
			segundo = ((int)(TempoAnterior/1000));
		}else{
			SFPS++;
		}
	
	}
System.exit(0); // so enclosing JFrame/JApplet exits
} // end of run()

int timerfps = 0;
private void gameUpdate(long DiffTime)
{ 
	
	if(LEFT){
		posx-=1000*DiffTime/1000.0;
	}
	if(RIGHT){
		posx+=1000*DiffTime/1000.0;
	}	
	if(UP){
		posy-=1000*DiffTime/1000.0;
	}
	if(DOWN){
		posy+=1000*DiffTime/1000.0;
	}
	
	if(posx>mapa.Largura*16) {
		posx=mapa.Largura*16;
	}
	if(posy>mapa.Altura*16) {
		posy=mapa.Altura*16;
	}
	if(posx<0) {
		posx=0;
	}
	if(posy<0) {
		posy=0;
	}
	
	mapa.Posiciona((int)posx,(int)posy);
	
	for(int i = 0;i < listadeagentes.size();i++){
		  listadeagentes.get(i).SimulaSe((int)DiffTime);
	}
	
	updateHeroMovement(DiffTime);
}

private void gameRender(Graphics2D dbg)
// draw the current frame to an image buffer
{
	// clear the background
	dbg.setColor(Color.white);
	dbg.fillRect (0, 0, PWIDTH, PHEIGHT);

	AffineTransform trans = dbg.getTransform();
	dbg.scale(zoom, zoom);
	
	try {
		mapa.DesenhaSe(dbg);
	}catch (Exception e) {
		System.out.println("Erro ao desenhar mapa");
	}
	

		
	
    // 1) desenha nós fechados
    dbg.setColor(Color.RED);
    synchronized(listaFechada) {
        for (Integer nxy: listaFechada) {
            int px = nxy % 1000, py = nxy/1000;
            dbg.fillRect(px*16-mapa.MapX, py*16-mapa.MapY,16,16);
        }
    }

    // 2) desenha nós abertos
    dbg.setColor(Color.GREEN);
    synchronized(listaAberta) {
        for (Nodo n: listaAberta) {
            dbg.fillRect(n.x*16-mapa.MapX, n.y*16-mapa.MapY,16,16);
        }
    }

    // 3) desenha o caminho final
    if (caminho!=null) {
      dbg.setColor(Color.BLUE);
      for (int i=0; i<caminho.length/2; i++){
        int nx=caminho[i*2], ny=caminho[i*2+1];
        dbg.fillRect(nx*16-mapa.MapX, ny*16-mapa.MapY,16,16);
      }
    }
    
    
	for(int i = 0;i < listadeagentes.size();i++){
		  listadeagentes.get(i).DesenhaSe(dbg, mapa.MapX, mapa.MapY);
		}
	
	dbg.setTransform(trans);
	
	dbg.setFont(f);
	dbg.setColor(Color.BLUE);	
	dbg.drawString("FPS: "+FPS, 10, 30);	
	
	dbg.drawString("N: "+listaFechada.size(), 100, 30);	
	//System.out.println("left "+LEFT);
		
}

}

