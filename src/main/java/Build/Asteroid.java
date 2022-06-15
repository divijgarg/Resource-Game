package Build;

import com.almasb.fxgl.entity.Entity;

public class Asteroid extends MovingObject{
    private int speed;
    private boolean isWithinBoundary;
    public Asteroid(double centerX, double centerY,double radius, Entity asteroidOnScreen,double targetX, double targetY){
        super(centerX, centerY,radius, asteroidOnScreen,targetX, targetY);
        speed=returnRandInt(1,10);
    }
    /**
     * pre:asteroids exist
     * post: position of asteroids is updated
     */
    public void moveAsteroid() {
        if (getTargetX()!=getX()) {
            double change = roundNum(getChangeInX(speed, getX(), getY()), 6);
            if (change == 0) {
                if (getTargetY() > getY()) setY(getY() + speed);
                else setY(getY() - speed);
                getEntityOnScreen().setY(getY());
            } else {
                setY(returnYbasedOnX(getX() + change));
                setX(getX() + change);
                getEntityOnScreen().setX(getX());
                getEntityOnScreen().setY(getY());
            }
        }
        else{
            if (getTargetY()>getY())setY(getY()+speed);
            else setY(getY()-speed);
            getEntityOnScreen().setY(getY());
        }
    }
    /**
     * pre:num1<num2
     * post:returns randint between them, inclusive
     */
    public int returnRandInt(int num1,int num2){
        return (int)(Math.random()*(num2-num1+1)+num1);
    }
    /**
     * pre: the asteroid is moving
     * post: detects if asteroid has left the game
     */
    public void detectAsteroid(int width, int height){
        if (0<=getX()&&getX()<=width&&0<=getY()&&getY()<=height)isWithinBoundary=true;
        else isWithinBoundary=false;
    }

    public boolean isWithinBoundary() {
        return isWithinBoundary;
    }

    public int getSpeed() {
        return speed;
    }
}
