package Build;

import com.almasb.fxgl.entity.Entity;

import java.util.ArrayList;

public class ResourceProbe extends MovingObject {
    private boolean beingPulled=false;
    private static int placeTimer=0;

    public static int getPlaceTimer() {
        return placeTimer;
    }

    public static void setPlaceTimer(int placeTimer) {
        ResourceProbe.placeTimer = placeTimer;
    }
    public ResourceProbe(double x, double y, Entity entity){
        super(x,y,0,entity,x,y);
    }

    /**
     * pre: probe exists and found previous resource
     * post: finds next resoure to pathfind to
     */
    public void detectResource(ArrayList<Resource> resources) {
        if (!beingPulled) {
            double shortestDistance = 12345678;
            int index = -1;
            for (int i = 0; i < resources.size(); i++) {
                Resource r = resources.get(i);
                if (Math.sqrt(Math.pow(getX() - r.getX(), 2) + Math.pow(getY() - r.getY(), 2)) < shortestDistance && r.getType() != 2) {
                    shortestDistance = Math.sqrt(Math.pow(getX() - r.getX(), 2) + Math.pow(getY() - r.getY(), 2));
                    index = i;
                }
            }
            if (index != -1) {
                setTargetX(resources.get(index).getX());
                setTargetY(resources.get(index).getY());
                setEquation(getX(), getY(), getTargetX(), getTargetY());
                getEntityOnScreen().rotateBy(returnAngleBetweenCoordinates(getX(), getY(), getTargetX(), getTargetY()));
                setFoundTarget(true);
            }
        }
    }
    /**
     * pre: coordinates are double values
     * post: returns angle between them
     */
    public double returnAngleBetweenCoordinates(double x1, double y1, double x2, double y2){
        int rotate =(int)(180*(Math.acos(Math.abs(x2-x1)/(Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2)))))/Math.PI);
        if (y2>=y1&&x2>=x1)return rotate;
        if (y2>=y1&&x2<=x1)return 180-rotate;
        if (y2<=y1&&x2>=x1)return -1*rotate;
        return 180+rotate;//(y2<=y1&&x2<=x1)
    }
    /**
     * pre: probes exist
     * post: probes are moved towards the target
     */
    public void moveProbe(){
        double changeInX = roundNum(getChangeInX(0.5,getX(), getY()),4);
        if (changeInX!=0) {
            setY(returnYbasedOnX(getX() + changeInX));
            setX(getX() + changeInX);
            getEntityOnScreen().setX(getX());
            getEntityOnScreen().setY(getY());
        }
        else {
            if(getY()>getTargetY())  setY(getY()-0.5);
            else setY(getY()+0.5);
            getEntityOnScreen().setY(getY());
        }
        if (Math.round(getX()) == Math.round(getTargetX()) && Math.round(getY()) == Math.round(getTargetY())) {
            setFoundTarget(true);
        }
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

    public void setBeingPulled(boolean beingPulled) {
        this.beingPulled = beingPulled;
    }

    public boolean isBeingPulled() {
        return beingPulled;
    }
}
