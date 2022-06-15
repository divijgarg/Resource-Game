package Build;

import com.almasb.fxgl.entity.Entity;

public class MovingObject  {
    private double centerX,centerY,radius,m,b,targetX,targetY;
    private Entity entityOnScreen;
    private boolean foundTarget=false;
    public MovingObject(double centerX, double centerY,double radius, Entity entityOnScreen,double targetX, double targetY){
        this.centerX=centerX;
        this.centerY=centerY;
        this.radius=radius;
        this.targetX=targetX;
        this.targetY=targetY;
        this.entityOnScreen=entityOnScreen;
    }
    public double returnYbasedOnX(double x){
        return m*x+b;
    }

    /**
     * pre: object exists
     * post: returns teh x value based on the distance travelled
     * @param distance
     * @param xcoord
     * @param ycoord
     * @return
     */
    public double getChangeInX(double distance, double xcoord, double ycoord){
        double changeInX=distance;
        double expression=Math.pow(changeInX,2)+Math.pow(returnYbasedOnX(xcoord+changeInX)-returnYbasedOnX(xcoord),2);
        for (double i = Math.pow(distance,2); i >=0; i-=0.01) {
            if (expression<=Math.pow(distance,2))break;
            changeInX-=0.1;
            expression=Math.pow(changeInX,2)+Math.pow(returnYbasedOnX(xcoord+changeInX)-returnYbasedOnX(xcoord),2);
        }
        if (getTargetX()>getX()) return changeInX;
        else return changeInX*-1;
    }
    public void setEquation(double x1, double y1, double x2, double y2){
        m=(y2-y1)/(x2-x1);
        b=y1-m*x1;
    }

    public double getTargetX() {
        return targetX;
    }

    public double getM() {
        return m;
    }

    public double getTargetY() {
        return targetY;
    }

    public double getX() {
        return centerX;
    }

    public double getY() {
        return centerY;
    }

    public void setX(double centerX) {
        this.centerX = centerX;
    }

    public void setY(double centerY) {
        this.centerY = centerY;
    }

    public Entity getEntityOnScreen() {
        return entityOnScreen;
    }

    public void setTargetY(double targetY) {
        this.targetY = targetY;
    }

    public void setTargetX(double targetX) {
        this.targetX = targetX;
    }

    public void setFoundTarget(boolean foundTarget) {
        this.foundTarget = foundTarget;
    }

    public boolean isFoundTarget() {
        return foundTarget;
    }
    /**
     * PRE: double entered
     * post: returns rounded value
     */
    public double roundNum(double num, int places){
        num*=Math.pow(10,places);
        num=Math.round(num);
        return num/(Math.pow(10,places));
    }

    public double getRadius() {
        return radius;
    }

}
