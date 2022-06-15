package Build;

import com.almasb.fxgl.entity.Entity;

public class Bullet extends MovingObject{
    private int type=0;
    public Bullet(double targetX, double targetY, double x, double y, Entity entBullet, int type){
        super(x,y,0,entBullet,targetX,targetY);
        this.type=type;
    }
    /**
     * pre:bullet exists
     * post: moves the bullet
     */
    public void moveBullet(double width, double height) {
        if (getTargetX()!=getX()) {
            double change = roundNum(getChangeInX(3, getX(), getY()),6);
            if (change==0){
                if (getTargetY()>getY())  setY(getY()+3);
                else  setY(getY()-3);
                getEntityOnScreen().setY(getY());
            }
            else {
                setY(returnYbasedOnX(getX() + change));
                setX(getX() + change);
                getEntityOnScreen().setX(getX());
                getEntityOnScreen().setY(getY());
            }
        }
        else{
            if (getTargetY()>getY())  setY(getY()+3);
            else  setY(getY()-3);
            getEntityOnScreen().setY(getY());
        }

    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
