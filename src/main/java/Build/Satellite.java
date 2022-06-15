package Build;

import com.almasb.fxgl.entity.Entity;

import java.util.ArrayList;

public class Satellite {
    private double x,y, targetx, targety,a,b,c;
    private int moveIndex=0;
    private ArrayList<Double> xcoordsorbit=new ArrayList<>();
    private ArrayList<Double> ycoordsorbit=new ArrayList<>();
    private Entity entity, beam=new Entity();
    private static int placeTimer=0;

    public static int getPlaceTimer() {
        return placeTimer;
    }

    public static void setPlaceTimer(int placeTimer) {
        Satellite.placeTimer = placeTimer;
    }
    public Satellite(double x, double y, Entity entity){
        this.x=x;
        this.y=y;
        this.entity=entity;
        this.c=1;
    }

    /**
     * pre: satellite and planets exist
     * post: finds planet to orbit
     * @param planets
     */
    public void settarget(ArrayList<Planet> planets){
        double shortest=10000;
        int index=0;
        for (int i = 0; i <planets.size() ; i++) {
            if (Math.pow(x-planets.get(i).getxCoord(),2)+Math.pow(y-planets.get(i).getyCoord(),2)<shortest)index=i;
        }
        targetx=planets.get(index).getxCoord();
        targety=planets.get(index).getyCoord();
    }

    /**
     * pre:circle exists
     * post:uses the equation of a circle to find the y coord
     */
    public double ellipseCoordY(double newX){
        return b*Math.sqrt(Math.pow(c,2)-Math.pow(newX-targetx,2)/Math.pow(a,2))+targety;
    }
    /**
     * pre:circle exists
     * post:uses the equation of a circle to find the x coord
     */
    public double ellipseCoordX(double newY){
        return a*Math.sqrt(Math.pow(c,2)-Math.pow(newY-targety,2)/Math.pow(b,2))+targetx;
    }


    /**
     * pre: satellite has been placed
     * post: satellites position updated
     */
    public void move(){
        setY(ycoordsorbit.get(moveIndex));
        setX(xcoordsorbit.get(moveIndex));
        moveIndex++;
        if (moveIndex>=xcoordsorbit.size())moveIndex=0;
    }

    /**
     * every variable of the equation should be initialized
     */
    public void setOrbit(){

        double addx=targetx-a;
        double addy=targety;
        double amount=(b+targety+addy)/2;
        while(addy<=amount){
            addy+=1;
            addx=ellipseCoordX(addy);
            xcoordsorbit.add(2*targetx-addx);
            ycoordsorbit.add(addy);
        }

        amount=(targetx+a+targetx)/2;
        addx=2*targetx-addx;
        while (addx<= amount){
            addx+=1;
            addy=ellipseCoordY(addx);
            xcoordsorbit.add(addx);
            ycoordsorbit.add(addy);
        }

        amount=targety;
        while(addy>=amount){
            addy-=1;
            addx=ellipseCoordX(addy);
            xcoordsorbit.add(addx);
            ycoordsorbit.add(addy);
        }

        amount=(targety+targety-b)/2;
        while(addy>=amount){
            addy-=1;
            addx=ellipseCoordX(addy);
            xcoordsorbit.add(addx);
            ycoordsorbit.add(addy);
        }

        amount=(targetx-a+targetx)/2;
        while (addx>= amount){
            addx-=1;
            addy=ellipseCoordY(addx);
            xcoordsorbit.add(addx);
            ycoordsorbit.add(2*targety-addy);
        }

        addy=2*targety-addy;
        amount=targety;
        while(addy<=amount){
            addy+=1;
            addx=ellipseCoordX(addy);
            xcoordsorbit.add(2*targetx- addx);
            ycoordsorbit.add(addy);
        }

        moveIndex=0;
        for (int i = 0; i < xcoordsorbit.size(); i++) {
            if (Math.pow(xcoordsorbit.get(i)-x,2)+Math.pow(ycoordsorbit.get(i)-y,2)<Math.pow(xcoordsorbit.get(moveIndex)-x,2)+Math.pow(ycoordsorbit.get(moveIndex)-y,2)){
                moveIndex=i;
            }
        }

    }

    public void setA(double a) {
        this.a = a;
    }

    public void setB(double b) {
        this.b = b;
    }

    public void setX(double x) {
        this.x = x;
        entity.setX(x);
    }

    public void setY(double y) {
        this.y = y;
        entity.setY(y);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getTargetx() {
        return targetx;
    }

    public double getA() {
        return a;
    }

    public double getTargety() {
        return targety;
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

    public Entity getEntity() {
        return entity;
    }

    public void setBeam(Entity beam) {
        this.beam = beam;
    }

    public Entity getBeam() {
        return beam;
    }

    public ArrayList<Double> getXcoordsorbit() {
        return xcoordsorbit;
    }

    public ArrayList<Double> getYcoordsorbit() {
        return ycoordsorbit;
    }

    public int getMoveIndex() {
        return moveIndex;
    }
}
