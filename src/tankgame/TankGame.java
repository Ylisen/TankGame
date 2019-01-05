package tankgame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class TankGame extends JFrame implements ActionListener{
	MyPanel mypanel;
	StartPanel msp = null;
	//��������Ҫ�Ĳ˵�
	JMenuBar jmb=null;//�˵���
	JMenu jm1=null;//�˵�
	JMenuItem jmi1=null;//�˵���
	boolean start = false;
        
	public static void main(String[] args) {
		new TankGame().setVisible(true);
	}
	
	public TankGame(){
		this.setTitle("̹�˴�ս");
		this.setBounds(100,100,700,630);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		
		msp=new StartPanel();
	    Thread t=new Thread(msp);
	    t.start();
        this.add(msp);
        //this.setSize(700,600);//����С
        //this.setBackground(Color.BLACK);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        
        jmb=new JMenuBar();
		jm1 =new JMenu("��Ϸ(G)");
		//���ÿ�ݷ�ʽ
		jm1.setMnemonic('G');
		jmi1 =new JMenuItem("��ʼ����Ϸ(N)");
		jm1.add(jmi1);//���˵�����ӵ��˵���
		jmb.add(jm1);//���˵���ӵ��˵�����
		this.setJMenuBar(jmb);//���˵�����ӵ�������
		jmi1.addActionListener(this);//Ϊ��ʼ����Ϸ�˵���ע�����
		jmi1.setActionCommand("newgame");//������ActionCommandֵΪ��"newgame"
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("newgame"))// ��������ˡ���ʼ����Ϸ���˵���
		{
			if (start) {
				this.remove(mypanel);// ��ɾ���ɵ�MyPanel���
			}
			start = true;
			// ����ս�����
			mypanel = new MyPanel();
			// ����mp�߳�
			Thread t = new Thread(mypanel);
			t.start();
			// ��ɾ���ɵĿ�ʼ���
			this.remove(msp);
			this.add(mypanel);
			// ע�����
			this.addKeyListener(mypanel);
			// ��ʾ,ˢ��JFrame
			this.setVisible(true);
		}	
	}
}


class MyPanel extends JPanel implements KeyListener,Runnable{
	MyTank myTank;
	public static int ensize=3;
	public static int ensize2 = 5;
	ArrayList<EnemyTank> ets=new ArrayList<EnemyTank>();  //����tank�б�
	Image image1=null,image2=null,image3=null;
	int total = 0;// ����ͳ�ƻ��е��˵�̹������
	int time = 0;
	int level = 1;// ���ڽ���ڶ���
	
	
	public MyPanel(){
		this.setLayout(null);
		//���tank
		 myTank=new MyTank();
		 
		 for(int i=0;i<ensize;i++){
			 EnemyTank et=new EnemyTank(50+i*150,10,6,2);
			 Thread t = new Thread(et);
			 t.start();
			 ets.add(et);
			 et.setEts(ets);   //ets����tank�б�
		 }
		 
		 image1 = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("bomb_1.gif"));
		 image2 = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("bomb_2.gif"));
		 image3 = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("bomb_3.gif"));
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.drawImage(image1, 0,0,1,1, this);
		//��������
		g.setColor(Color.black);
		g.fill3DRect(0, 0, 600, 500, false);
		//�����ҵ�̹��
		if(myTank.isLive)drawTank(myTank,g);
		else if(myTank.life>0)drawBomb(myTank,g);
		//�����ҵ�̹�˵��ӵ�
		for(int i=0;i<myTank.ss.size();i++){
			Shot s=myTank.ss.get(i);
			if(s != null && s.isLive){
				g.setColor(Color.blue);
				g.fill3DRect(s.x, s.y, 3, 3, false);
			}
			else if(s != null && s.isLive == false){
				myTank.ss.remove(s);
			}
		}
		
		//�������˵�̹��
		for(int i=0;i<ets.size();i++){
			EnemyTank et=ets.get(i);
			if(et.isLive){  //et!=null&&
				drawTank(et,g);
				//�������˵��ӵ�
				for(int j=0;j<et.ss.size();j++){
					Shot s=et.ss.get(j);
					if(s.isLive){
					g.setColor(Color.red);
					g.fill3DRect(s.x, s.y, 3, 3, false);
					}
					else et.ss.remove(s);
				}
			}else if(et.life>0){
				 drawBomb(et,g); 
			}
			else{
				ets.remove(et);
			}
		}
		this.showInfo(g);
		
		//��Ϸ˵��
		g.setColor(Color.black);
		g.drawString("����˵��",615,290);
		String str[] = { "��ͣ/����   ESC","  ����      ��", "  ����      ��", "  ����      ��", "  ����      ��", "�����ӵ�    �ո�","����    Enter" };
		for(int i=0;i<str.length;i++){
			g.drawString(str[i],605,320+30*i);
		}
		
		// ����ҵ�̹�������������꣬��Ϸ����
		if (myTank.size == 0) {
			for (int i = 0; i < ets.size(); i++) {
				ets.remove(i);
			}
			g.setColor(Color.black);
			g.fillRect(0, 0, 600, 500);
			// ��ʾ��Ϣ
			if (time % 2 == 0)// ͨ����ʾ����ʾ��������Ч��
			{
				g.setColor(Color.red);
				// ������Ϣ������
				Font myFont = new Font("����", Font.BOLD, 50);
				g.setFont(myFont);
				g.drawString("Game over��", 200, 250);
			}
		}
		
		// ��������˵�̹�˶����𣬽��뵽�ڶ���
		if (total == ensize && level == 1) {

			myTank = new MyTank();
			// �������˵�̹��
			for (int i = 0; i < ensize2; i++) {
				EnemyTank et = new EnemyTank(15+i*100,10,6,2);
				et.speed=5;
				ets.add(et);
				Thread t = new Thread(et);
				t.start();
				// ��MyPanel�ĵ���̹�����������õ���̹��
				et.setEts(ets);
			}
			level = 0;
		}
		
		if (total == 3 + ensize2) {
			g.setColor(Color.black);
			g.fillRect(0, 0, 600, 500);
			// ��ʾ��Ϣ
			if (time % 2 == 0)// ͨ����ʾ����ʾ��������Ч��
			{
				g.setColor(Color.CYAN);
				// ������Ϣ������
				Font myFont = new Font("����", Font.BOLD, 50);
				g.setFont(myFont);
				g.drawString("ʤ������", 200, 250);
			}
		}
	}
	
	public void drawTank(Tank t,Graphics g){
		int x=t.x,y=t.y,w=t.w,h=12*w;
		if(t.direct==0)
		{
			g.setColor(t.c1);
			g.fill3DRect(x, y, w, h, false);
			g.fill3DRect(x + 7 * w, y, w, h, false);
			g.setColor(t.c2);
			g.fill3DRect(x + w, y + 2 * w, 6 * w, 8 * w, false);
			g.setColor(t.c3);
			g.fillOval(x + 2 * w, y + 4 * w, 4 * w, 4 * w);
			g.setColor(t.c4);
			Graphics2D g2d = (Graphics2D) g;
			g2d.setStroke(new BasicStroke(5.0f));
			g2d.drawLine(x + 4 * w, y, x + 4 * w, y + 6 * w);
		}
		else if(t.direct==1)
		{
			g.setColor(t.c1);
			g.fill3DRect(x, y, h, w, false);
			g.fill3DRect(x , y+ 7 * w, h, w, false);
			g.setColor(t.c2);
			g.fill3DRect(x + 2*w, y +  w, 8 * w, 6 * w, false);
			g.setColor(t.c3);
			g.fillOval(x + 4 * w, y + 2 * w, 4 * w, 4 * w);
			g.setColor(t.c4);
			Graphics2D g2d = (Graphics2D) g;
			g2d.setStroke(new BasicStroke(5.0f));
			g2d.drawLine(x + h, y+4*w, x + 6 * w, y + 4 * w);
		}
		else if(t.direct==2)
		{
			g.setColor(t.c1);
			g.fill3DRect(x, y, w, h, false);
			g.fill3DRect(x + 7 * w, y, w, h, false);
			g.setColor(t.c2);
			g.fill3DRect(x + w, y + 2 * w, 6 * w, 8 * w, false);
			g.setColor(t.c3);
			g.fillOval(x + 2 * w, y + 4 * w, 4 * w, 4 * w);
			g.setColor(t.c4);
			Graphics2D g2d = (Graphics2D) g;
			g2d.setStroke(new BasicStroke(5.0f));
			g2d.drawLine(x + 4 * w, y+h, x + 4 * w, y + 6 * w);
		}
		else if(t.direct==3)
		{
			g.setColor(t.c1);
			g.fill3DRect(x, y, h, w, false);
			g.fill3DRect(x , y+ 7 * w, h, w, false);
			g.setColor(t.c2);
			g.fill3DRect(x + 2*w, y +  w, 8 * w, 6 * w, false);
			g.setColor(t.c3);
			g.fillOval(x + 4 * w, y + 2 * w, 4 * w, 4 * w);
			g.setColor(t.c4);
			Graphics2D g2d = (Graphics2D) g;
			g2d.setStroke(new BasicStroke(5.0f));
			g2d.drawLine(x , y+4*w, x + 6 * w, y + 4 * w);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
			
		}
	}

	//mytTank�¼�����
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode()==KeyEvent.VK_LEFT&&myTank.isLive){
			if(myTank.x>0){
				myTank.x-=myTank.speed;
				myTank.direct=3;
				myTank.invincible=false;
			}
		}
		else if(e.getKeyCode()==KeyEvent.VK_RIGHT&&myTank.isLive){
			if(myTank.x<600-myTank.h){
				myTank.x+=myTank.speed;
				myTank.direct=1;
				myTank.invincible=false;
			}
		}
		else if(e.getKeyCode()==KeyEvent.VK_UP&&myTank.isLive){
			if(myTank.y>0){
				myTank.y-=myTank.speed;
				myTank.direct=0;
				myTank.invincible=false;
			}
		}
		else if(e.getKeyCode()==KeyEvent.VK_DOWN&&myTank.isLive){
			if(myTank.y<500-myTank.h ){
				myTank.y+=myTank.speed;
				myTank.direct=2;
				myTank.invincible=false;
			}
		}
		else if(e.getKeyCode()==KeyEvent.VK_SPACE&&myTank.isLive){
			if(myTank.ss.size()<5){// �ҵ�̹��ֻ��������5���ӵ�
			myTank.shotEnemyTank();
			myTank.invincible=false;
			}
		}
		else if(e.getKeyCode()==KeyEvent.VK_ENTER&&myTank.isLive==false&&myTank.size>0){
			myTank.isLive=true;
			myTank.life=9;
			myTank.invincible=true;
		}
		this.repaint();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO �Զ����ɵķ������
	}

	@Override
	public void run() {
		while(true){
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
			if(myTank.isLive && myTank.invincible==false){
	              this.hitEnemyTank();
	              if(this.hitMe()) 
	            	  myTank.size--;
	            }
			time++;
			this.repaint();
		}
	}
	

	//�ж��ӵ��Ƿ����̹��
	public boolean isHitTank(Shot s,Tank t){
		
		switch(t.direct){
		case 0:
		case 2:
			if(s.x>t.x&&s.x<t.x+8*t.w&&s.y>t.y&&s.y<t.y+t.h){
				s.isLive=false;
				t.isLive=false;
				return true;
			}
			break;
		case 1:
		case 3:
			if(s.x>t.x&&s.x<t.x+t.h&&s.y>t.y&&s.y<t.y+8*t.w){
				s.isLive=false;
				t.isLive=false;
				return true;
			}
		}
		return false;
	}
	
	//�ҵ�̹�˵��ӵ��Ƿ���е��˵�̹��
	public void hitEnemyTank(){
		Shot s=null;
		for(int i=0;i<myTank.ss.size();i++){
			s=myTank.ss.get(i);
			if(s.isLive){
				for(int j=0;j<ets.size();j++){
					EnemyTank et=ets.get(j);
					if(et.isLive){
						if (this.isHitTank(s, et))
							total++;// ���е��˵�̹�ˣ�total+1��
					}
				}
			}
		}
	}
    
	public void drawBomb(Tank t,Graphics g)  //��ըЧ��
	{
		if(t.life>6) g.drawImage(image1, t.x,t.y,90,90, this);
        else if(t.life>3) g.drawImage(image2, t.x,t.y,60,60, this);
        else if(t.life>0) g.drawImage(image3, t.x,t.y,30,30, this);
        t.life--;  
	}
	
	//���˵��ӵ��Ƿ������
	public boolean hitMe(){
		for(int i=0;i<ets.size();i++){
			EnemyTank et = ets.get(i);
			for (int j = 0; j < et.ss.size(); j++) {
				Shot s=et.ss.get(j);
				if(myTank.isLive){
					if(isHitTank(s,myTank)) 
						return true;
				}
			}
		}
		return false;
	}

	// ������ʾ��Ϣ
	public void showInfo(Graphics g) {
		g.drawString("ʣ������ֵ", 10, 535);
		//�з�tankʣ������ֵ
		EnemyTank et=new EnemyTank(80,510,4,0);
		this.drawTank(et, g);
		int t = 0;
		for (int i = 0; i < ets.size(); i++) {
			EnemyTank et1 = ets.get(i);
			if (et1.isLive)
				t++;
		}
		g.drawString(t + "", 125, 540);
		//myTankʣ������ֵ
		MyTank mt = new MyTank(300, 510, 4, 0);
		this.drawTank(mt, g);
		g.drawString(myTank.size + "", 345, 540);
		//my�÷�
		mt.x = 630;
		mt.y = 100;
		this.drawTank(mt, g);
		g.setColor(Color.red);
		g.drawString("��ĳɼ�Ϊ:", 620, 85);
		g.drawString(total + "", 645, 180);
	}
}   //MyPanel

//��Ϸ��ʼ���
class StartPanel extends JPanel implements Runnable{
	int times=0;//���ڿ�����ʾ
	public void paint(Graphics g)
	{
		super.paint(g);
		g.fillRect(0, 0, 700, 600);
		//��ʾ��Ϣ
		if(times%2==0)//ͨ����ʾ����ʾ��������Ч��
		{
			g.setColor(Color.yellow);
			//������Ϣ������
			Font myFont=new Font("������κ",Font.BOLD,50);
			g.setFont(myFont);
			g.drawString("̹�˴�ս", 200, 230);
		}
	}
	public void run() {
		while(true)
		{
			try {
				Thread.sleep(100);
			} catch (Exception e) { }
			times++;
			//�ػ�
			this.repaint();
		}
	}
}

