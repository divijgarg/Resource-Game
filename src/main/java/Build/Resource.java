package Build;


import com.almasb.fxgl.entity.Entity;

public class Resource extends MovingObject {
    private int type,amountBenefit,height,width,speed;//type: 0=food, 1=fuel,2=supplies
    private boolean beingMoved=false,beRemoved=false;
    public Resource(int type,int amountBenefit,double x, double y,int height,int width, Entity entity){
        super(x,y,0,entity,0,0);
        this.type=type;
        this.amountBenefit=amountBenefit;
        this.height=height;
        this.width=width;
        this.speed=1;
    }

    public void setBeRemoved(boolean beRemoved) {
        this.beRemoved = beRemoved;
    }

    public void setAmountBenefit(int amountBenefit) {
        this.amountBenefit = amountBenefit;
    }

    public boolean isBeRemoved() {
        return beRemoved;
    }

    public int getType() {
        return type;
    }

    public void setBeingMoved(boolean beingMoved) {
        this.beingMoved = beingMoved;
    }


    public boolean isBeingMoved() {
        return beingMoved;
    }


    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getAmountBenefit() {
        return amountBenefit;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
