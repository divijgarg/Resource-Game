package Build;

import com.almasb.fxgl.entity.Entity;

public class AlienShip extends MovingObject{
    private int health=1;
    public AlienShip (double x, double y, Entity entity,double targetx, double targety ){
        super(x,y,0,entity,targetx, targety);
        setEquation(x,y,targetx,targety);
    }
    /**
     * pre: ship exist
     * post: ship is moved towards the target
     */
    public void moveShip(double radius, double centerx, double centery) {
        double changeInX = roundNum(getChangeInX(0.5, getX(), getY()), 4);
        if (changeInX != 0) {
            setY(returnYbasedOnX(getX() + changeInX));
            setX(getX() + changeInX);
            getEntityOnScreen().setX(getX());
            getEntityOnScreen().setY(getY());
        } else {
            if (getY() > getTargetY()) setY(getY() - 0.5);
            else setY(getY() + 0.5);
            getEntityOnScreen().setY(getY());
        }
        if (Math.round(getX()) == Math.round(getTargetX()) && Math.round(getY()) == Math.round(getTargetY())) {
            setFoundTarget(true);
        }


    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}
