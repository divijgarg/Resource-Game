package Build;

import com.almasb.fxgl.entity.Entity;

import java.util.ArrayList;

public class BlackHole extends MovingObject{
    private boolean isWithinBoundary;
    public BlackHole(double x, double y,double targetx, double targety, double radius, Entity entHole){
        super(x,y,radius,entHole,targetx,targety);
    }

    /**
     * pre: black hole has spawned
     * post: entities moved towards blackHole
     * @param resources
     */
    public void detectEntities(ArrayList<Resource> resources,ArrayList<Asteroid> asteroids, ArrayList<StructureBlock> structures, Entity player, Entity turret, ArrayList<ResourceProbe> probes) {
        for (Resource r : resources) {
            if (r.getX() >= getX() - getRadius() && r.getX() <= getX() + getRadius() && r.getY() >= getY() - getRadius() && r.getY() <= getY() + getRadius()) {
                r.setTargetX(getX() + 5);
                r.setTargetY(getY() + 5);
                r.setBeingMoved(true);
                r.setEquation(r.getX(), r.getY(), getX() + 5, getY() + 5);
                r.setSpeed(5);
            }
        }

        for (Asteroid r : asteroids) {
            if (r.getX() >= getX() - getRadius() && r.getX() <= getX() + getRadius() && r.getY() >= getY() - getRadius() && r.getY() <= getY() + getRadius()) {
                r.setTargetX(getX() + 5);
                r.setTargetY(getY() + 5);
                r.setEquation(r.getX(), r.getY(), getX() + 5, getY() + 5);
            }
        }

        for (StructureBlock r : structures) {
            if (r.getX() >= getX() - getRadius() && r.getX() <= getX() + getRadius() && r.getY() >= getY() - getRadius() && r.getY() <= getY() + getRadius()) {
                r.setEquation(r.getX(), r.getY(), getX(), getY());
                r.setTargetX(getX());
                r.setTargetY(getY());
                if (r.getTargetX() != r.getX()) {
                    r.setY(r.returnYbasedOnX(r.getX() + ((r.getTargetX() - r.getX()) / Math.abs(r.getTargetX() - r.getX()))));
                    r.setX(r.getX() + ((r.getTargetX() - r.getX()) / Math.abs(r.getTargetX() - r.getX())));
                    r.getEntityOnScreen().setY(r.getY());
                    r.getEntityOnScreen().setX(r.getX());
                }
            }
        }
        if (player.getX() >= getX() - getRadius() && player.getX() <= getX() + getRadius() && player.getY() >= getY() - getRadius() && player.getY() <= getY() + getRadius()) {
            if (player.getX() != getX()) {
                player.setX(player.getX() + (getX() - player.getX()) / (Math.abs(getX() - player.getX())) / 2);
                turret.setX(player.getX() + player.getWidth() / 2 - 4);

            }
            if (player.getY() != getY()) {
                player.setY(player.getY() + (getY() - player.getY()) / (Math.abs(getY() - player.getY())) / 2);
                turret.setY(player.getY() + player.getHeight() / 2 - 4);
            }
        }
        for (ResourceProbe probe : probes) {
            if (probe.getX() >= getX() - getRadius() && probe.getX() <= getX() + getRadius() && probe.getY() >= getY() - getRadius() && probe.getY() <= getY() + getRadius()) {
                probe.setTargetX(getX());
                probe.setTargetY(getY());
                probe.setEquation(probe.getX(), probe.getY(), probe.getTargetX(), probe.getTargetY());
                probe.setBeingPulled(true);
            }
        }
    }

    /**
     * pre: blackhole exists
     * post: location updated
     */
    public void moveBlackHole(){
        if (getTargetX()!=getX()) {
            double change = roundNum(getChangeInX(0.5, getX(), getY()), 6);
            if (change == 0) {
                if (getTargetY() > getY()) setY(getY() + 0.5);
                else setY(getY() - 0.5);
                getEntityOnScreen().setY(getY());
            } else {
                setY(returnYbasedOnX(getX() + change));
                setX(getX() + change);
                getEntityOnScreen().setX(getX());
                getEntityOnScreen().setY(getY());
            }
        }
        else{
            if (getTargetY()>getY())setY(getY()+0.5);
            else setY(getY()-0.5);
            getEntityOnScreen().setY(getY());
        }
    }

    public boolean isWithinBoundary() {
        return isWithinBoundary;
    }

    public void setWithinBoundary(boolean withinBoundary) {
        isWithinBoundary = withinBoundary;
    }

    /**
     * pre: the asteroid is moving
     * post: detects if asteroid has left the game
     */
    public void detectBlackHole(int width, int height){
        if (0<=getX()&&getX()<=width&&0<=getY()&&getY()<=height)isWithinBoundary=true;
        else isWithinBoundary=false;
    }
}
