package Build;

import com.almasb.fxgl.entity.Entity;

import java.util.ArrayList;

public class Structure {
    private ArrayList<Double> xCoordsToBuild=new ArrayList<>();
    private ArrayList<Double> yCoordsToBuild=new ArrayList<>();
    private ArrayList<StructureBlock> blocks=new ArrayList<>();
    private int placingIndex=0;
    private int timer;
    private boolean built, canBeUsed=true;
    private double x,y;

    public void addCoord(double x,double y) {
        xCoordsToBuild.add(x);
        yCoordsToBuild.add(y);
    }

    public void addTimer(int time) {
        this.timer+=time;
    }

    public int getTimer() {
        return timer;
    }

    public ArrayList<Entity> getEntities() {
        ArrayList<Entity> entOnScreen=new ArrayList<>();
        for (StructureBlock sb:blocks) {
            entOnScreen.add(sb.getEntityOnScreen());
        }
        return entOnScreen;
    }


    public void setBuilt(boolean built) {
        this.built = built;
    }

    public boolean isBuilt() {
        return built;
    }

    public ArrayList<Double> getxCoordsToBuild() {
        return xCoordsToBuild;
    }

    public ArrayList<Double> getyCoordsToBuild() {
        return yCoordsToBuild;
    }

    public double getyCoordsToBuildAtIndex(int index) {
        return yCoordsToBuild.get(index);
    }
    public double getxCoordsToBuildAtIndex(int index) {
        return xCoordsToBuild.get(index);
    }

    public int getPlacingIndex() {
        return placingIndex;
    }

    public void addPlacingIndex(int amount) {
        this.placingIndex +=amount;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getY() {
        return y;
    }

    public double getX() {
        return x;
    }

    public ArrayList<StructureBlock> getBlocks() {
        return blocks;
    }

    public void setCanBeUsed(boolean canBeUsed) {
        this.canBeUsed = canBeUsed;
    }

    public boolean isCanBeUsed() {
        return canBeUsed;
    }

}