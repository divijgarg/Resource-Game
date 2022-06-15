package Build;

import java.util.ArrayList;

public class Player {
    private int height,width,food,buildingSupplies, fuel,bulletTimer=0;
    private double moveSpeed;
    private ArrayList<ResourceCollector> collectors=new ArrayList<>();
    private ArrayList<Portal> portals=new ArrayList<>();
    private ArrayList<Factory> factories=new ArrayList<>();
    private ArrayList<Bullet> bullets=new ArrayList<>();
    public Player(double moveSpeed,int height,int width) {
        this.moveSpeed = moveSpeed;
        this.height = height;
        this.width = width;
        this.food = 25;
        this.fuel = 25;
        this.buildingSupplies=0;
    }

    public void setCollectors(ResourceCollector collector) {
        collectors.add(collector);
    }

    public ArrayList<ResourceCollector> getCollectors() {
        return collectors;
    }
    public void setPortals(Portal portal) {
        portals.add(portal);
    }
    public void setFactories(Factory factory) {
        factories.add(factory);
    }

    public ArrayList<Factory> getFactories() {
        return factories;
    }

    public ArrayList<Portal> getPortals() {
        return portals;
    }
    public int getBuildingSupplies() {
        return buildingSupplies;
    }

    public int getBulletTimer() {
        return bulletTimer;
    }

    public void setBulletTimer(int bulletTimer) {
        this.bulletTimer = bulletTimer;
    }

    public int getFood() {
        return food;
    }

    public int getFuel() {
        return fuel;
    }

    public void addBuildingSupplies(int buildingSupplies) {
        this.buildingSupplies += buildingSupplies;
    }

    public void addFood(int food) {
        this.food += food;
    }

    public void addFuel(int fuel) {
        this.fuel += fuel;
    }


    public double getMoveSpeed() {
        return moveSpeed;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setMoveSpeed(int moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    /**
     * pre: structures exist
     * post: returns the index
     * 0=resource
     * 1=portal
     * 2=factory
     * @param x
     * @param y
     * @param type
     * @return
     */
    public int returnIndexOfStructure(int x, int y, int type){
        if (type==0) {
            for (int i = 0; i < collectors.size(); i++) {
                for (int j = 0; j < collectors.get(i).getBlocks().size(); j++) {
                    if ((int)collectors.get(i).getBlocks().get(j).getX() == x && (int)collectors.get(i).getBlocks().get(j).getY() == y) return i;
                }
            }
        }
        if (type==1) {
            for (int i = 0; i < portals.size(); i++) {
                for (int j = 0; j < portals.get(i).getBlocks().size(); j++) {
                    if ((int)portals.get(i).getBlocks().get(j).getX() == x && (int)portals.get(i).getBlocks().get(j).getY() == y)
                        return i;
                }
            }
        }
        if (type==2) {
            for (int i = 0; i < factories.size(); i++) {
                for (int j = 0; j < factories.get(i).getBlocks().size(); j++) {
                    if ((int)factories.get(i).getBlocks().get(j).getX() == x && (int)factories.get(i).getBlocks().get(j).getY() == y)
                        return i;
                }
            }
        }
        return -1;
    }

    public void setBullets(ArrayList<Bullet> bullets) {
        this.bullets = bullets;
    }

    public ArrayList<Bullet> getBullets() {
        return bullets;
    }
}
