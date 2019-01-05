package tankgame;
import java.awt.*;
import java.util.ArrayList;
//̹����
public class Tank {

	int x,y;  //λ��
	int w,h; //����
	int speed,direct;
	int life = 9;
	boolean isLive=true;
	Color c1,c2,c3,c4;
	
	public Tank(int x, int y, int w, int direct) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h=this.w*12;
		this.direct = direct;
		this.speed=5;
	}
	public Tank(){
		this.x=0;
		this.y=0;
		this.w=5;
		this.h=12*this.w;
		this.direct=0;
		this.speed=5;
	}
}

class MyTank extends Tank{
	public int size=3;
	public boolean invincible=true;//�����ж��ҵ�̹���Ƿ����޵�״̬
	ArrayList<Shot> ss=new ArrayList<Shot>();
	Shot s=null;
	
	public MyTank() {
		super();
		this.x=250;
		this.y=400;
		this.speed=5;
		c1=new Color(255,0,255);  //˫���ӵ�color
		c2=new Color(0,255,255);  //�м䷽��
		c3=new Color(255,0,0);   //�м�Բ��
		c4=new Color(0,0,230);   //�ڸ�
	}

	public MyTank(int x, int y, int w, int direct) {
		super(x, y, w, direct);
		this.speed=5;
		c1=new Color(128,128,128);
		c2=new Color(0,0,180);
		c3=new Color(0,200,0);
		c4=new Color(0,0,230);
	}
	
	public void setSpeed(int speed){
		this.speed = speed;
	}
	public int getSpeed(){
		return this.speed;
	}
	// �����ҵ�̹�˵��ӵ�
	public void shotEnemyTank(){//�����ӵ�
		switch(direct){
		case 0:s=new Shot(x+4*w,y,direct);ss.add(s);break;
		case 1:s=new Shot(x+h,y+4*w,direct);ss.add(s);break;
		case 2:s=new Shot(x+4*w,y+h,direct);ss.add(s);break;
		case 3:s=new Shot(x,y+4*w,direct);ss.add(s);break;
		}
		Thread t=new Thread(s);
		t.start();
	}
}

class EnemyTank extends Tank implements Runnable{
	ArrayList<EnemyTank> ets=new ArrayList<EnemyTank>();    //����Tank����
	ArrayList<Shot> ss = new ArrayList<Shot>(); //����Tank�ӵ�����
	
	public EnemyTank() {
		super();
		this.x=10;
		this.y=10;
		this.speed=2;
		c1=new Color(200,200,128);
		c2=new Color(0,200,0);
		c3=new Color(0,255,127);
		c4=new Color(200,0,0);
	}
	
	public void setEts(ArrayList<EnemyTank> ets)
	{
		this.ets=ets;
	}

	public EnemyTank(int x, int y, int w, int direct) {
		super(x, y, w, direct);
		this.speed=3;
		c1=new Color(200,200,128);
		c2=new Color(0,200,0);
		c3=new Color(0,255,127);
		c4=new Color(200,0,0);
	}
	
	public void setSpeed(int speed){
		this.speed = speed;
	}
	public int getSpeed(){
		return this.speed;
	}

	@Override
	public void run() {
		// ̹������
		Tank t;//tΪ�ƶ����̹��
		int num = 0;
		while(true){
			t=new Tank(x,y,w,direct);
			switch(direct){
			case 0:t.y -= this.speed;break;
			case 1:t.x += this.speed;break;
			case 2:t.y += this.speed;break;
			case 3:t.x -= this.speed;break;
			}
			
			if(isTouchOtherTank(t)){
				changeDirect();//���ص���ı�з�Tank����
			}
			else{//�����ƶ�̹��
				x=t.x;
				y=t.y;
			}
			if(num==30|| isToBorder ()){//��̹������һ�������ƶ�һ��ʱ��򵽴�߽��
				num=0;
				changeDirect();//�ı�̹�˵ķ���
			}
			num++;
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {}

			//����ӵ�
            if (this.isLive && ss.size() < 50 && num %10 == 0) {
                Shot s = null;
                switch (direct) {
                    case 0:
                        s = new Shot(x + 4 * w, y, direct);
                        break;
                    case 1:
                        s = new Shot(x + h, y + 4 * w, direct);
                        break;
                    case 2:
                        s = new Shot(x + 4 * w, y + h, direct);
                        break;
                    case 3:
                        s = new Shot(x, y + 4 * w, direct);
                        break;
                }
                ss.add(s);
                Thread t1 = new Thread(s);
                t1.start();
            }
		}   //while
	}
	public boolean isToBorder (){//�ж�̹���Ƿ�ﵽ�߽�
		switch(direct){
		case 0:if(y<4*w) return true;break;
		case 1:if(x+h>600-4*w) return true;break;
		case 2:if(y+h>500-4*w) return true;break;
		case 3:if(x<4*w) return true;break;
		}
		return false;
	}
	//�ı�̹�˵ķ���
	public void changeDirect(){
			int	d=(int)(Math.random()*4);
			if(d==direct) direct=(direct+1)%4;
			else direct=d;
	}
	
	public boolean isTouchOtherTank(Tank t){
        for(int i=0;i<ets.size();i++){
            EnemyTank et=ets.get(i);
            if(et!=this){
                if(distance(t,et)<h+15){//����С��̹�˵ĳ���+15ʱ�޸�̹�˵ķ���
                    return true;
                }
            }
        }
        return false;
    }
//������̹�˵ľ���
    public int distance(Tank t1,Tank t2){
        Point p1,p2;
        p1=centerPoint(t1);
        p2=centerPoint(t2);
        return (int)(Math.sqrt((p2.x-p1.x)*(p2.x-p1.x)+(p2.y-p1.y)*(p2.y-p1.y)));
    }
//����̹�˵����ĵ�
    public Point centerPoint(Tank t){
        Point p=new Point(0,0);
        if(t.direct==0||t.direct==2){
            p.x=t.x+4*t.w;
            p.y=t.y+6*t.w;
        }
        if(t.direct==1||t.direct==3){
            p.x=t.x+6*t.w;
            p.y=t.y+4*t.w;
        }
        return p;
    }

}   //EnemyTank

//�ӵ���
class Shot implements Runnable{
	int x,y,direct,speed=10;
	boolean isLive=true;

	public Shot(int x, int y, int direct) {
//		super();
		this.x = x;
		this.y = y;
		this.direct = direct;
	}
	public void setSpeed(int speed){
		this.speed = speed;
	}
	public int getSpeed(){
		return this.speed;
	}

	@Override
	public void run() {//�ӵ��ƶ�
		while(true){
			switch(direct){
			case 0:y-=speed;break;
			case 1:x+=speed;break;
			case 2:y+=speed;break;
			case 3:x-=speed;break;
			}
			if(x<0||x>600||y<0||y>500){  //���߽�ʱ��ʧ
				isLive=false;
				break;
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}



