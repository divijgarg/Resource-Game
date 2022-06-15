package Build;


import com.almasb.fxgl.entity.Entity;

import java.util.ArrayList;

public class Planet {
    private double xCoord, yCoord, radius;
    private int defenseType;//0=bullets, 1= enemy
    private int detectedTimer;//tracks how long before it can fire again
    private int bulletTimer=0;
    private int satellites=0;
    private int conqueredTimer;
    private boolean detected, conquered;
    private Entity entity;
    private ArrayList<Bullet> bullets=new ArrayList<>();
    private ArrayList<AlienShip> ships=new ArrayList<>();
    public Planet(double xCoord, double yCoord, double radius, int defenseType, Entity entity ){
        this.xCoord=xCoord+radius/2;
        this.yCoord=yCoord+radius/2;
        this.radius=radius;
        this.defenseType=defenseType;
        this.entity=entity;
    }

    public Entity getEntity() {
        return entity;
    }

    /**
     * pre: player exists
     * post: detects if player is within boundary and fires bullet
     * @param player
     * @return
     */
    public boolean detectPlayer(Entity player, int amount) {
        return (Math.pow(player.getPositionComponent().getX() - xCoord, 2) + Math.pow(player.getPositionComponent().getY() - yCoord, 2) < Math.pow(radius + amount, 2));
    }

    public void addBullet(Bullet bullet) {
        bullets.add(bullet);
    }

    public void addDetectedTimer(int addTimer) {
        this.detectedTimer +=addTimer;
    }

    public void setDetectedTimer(int detectedTimer) {
        this.detectedTimer = detectedTimer;
    }

    public ArrayList<Bullet> getBullets() {
        return bullets;
    }

    public int getDetectedTimer() {
        return detectedTimer;
    }

    public boolean isDetected() {
        return detected;
    }

    public int getDefenseType() {
        return defenseType;
    }

    public void setDetected(boolean detected) {
        this.detected = detected;
    }

    public double getxCoord() {
        return xCoord;
    }

    public double getyCoord() {
        return yCoord;
    }
    /**
     * pre: planets exist
     * post: moves bullets fired from planets
     */
    public void moveBullets(double width, double height){
        for (Bullet b:getBullets()) {
            b.moveBullet(width,height);
        }
    }
    /**
     * pre: ships exist
     * post: moves ship fired from planets
     */
    public void moveShips(){
        for (AlienShip as: ships) {
            as.moveShip(radius,xCoord,yCoord);
        }
    }
    public void setBulletTimer(int bulletTimer) {
        this.bulletTimer = bulletTimer;
    }

    public int getBulletTimer() {
        return bulletTimer;
    }

    public boolean isConquered() {
        return conquered;
    }

    public void setConquered(boolean conquered) {
        this.conquered = conquered;
    }

    public void addSatellites(int satellites) {
        this.satellites += satellites;
    }
    public void addSatelliteTimer(int timer) {
        this.conqueredTimer += timer;
    }

    public void setConqueredTimer(int conqueredTimer) {
        this.conqueredTimer = conqueredTimer;
    }

    public int getConqueredTimer() {
        return conqueredTimer;
    }

    public int getSatellites() {
        return satellites;
    }

    public ArrayList<AlienShip> getShips() {
        return ships;
    }

    public void setShips(ArrayList<AlienShip> ships) {
        this.ships = ships;
    }
}
