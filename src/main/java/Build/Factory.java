package Build;

import java.util.ArrayList;

public class Factory extends Structure {
    private int centerX, centerY, xRadius,yRadius;
    private static int placeTimer=0;

    public static int getPlaceTimer() {
        return placeTimer;
    }

    public static void setPlaceTimer(int placeTimer) {
        Factory.placeTimer = placeTimer;
    }

    private double resourcesAvailable;
    public Factory(int centerX, int centerY, int xRadius, int yRadius){
        this.centerX=centerX;
        this.centerY =centerY;
        this.xRadius=xRadius;
        this.yRadius=yRadius;
        this.resourcesAvailable=0;
        setX(centerX);
        setY(centerY);
        setCoordinatesOfFactory(centerX,centerY,xRadius,yRadius);
    }
    /**
     * pre:factory initialized
     * post:find coordinates of the factory-hyperbola with two lines connecting them
     */
    public void setCoordinatesOfFactory(int centerX, int centerY,int xRadius, int yRadius){
        double x=centerX-20;
        while (x<=(centerX-Math.sqrt(xRadius))+0.25){
            addCoord(x,getYValueForHyperbola(x,xRadius,yRadius,centerX,centerY));
            x+=0.5;
        }
        for (int i = getyCoordsToBuild().size()-1;i>=0; i--) {
            addCoord(getxCoordsToBuild().get(i),centerY+centerY-getyCoordsToBuild().get(i));
        }
        ArrayList<Double> tempX=new ArrayList<>(getxCoordsToBuild());
        ArrayList<Double> tempY=new ArrayList<>(getyCoordsToBuild());
        x=tempX.get(tempX.size()-1);
        while (x!=centerX+20){
            addCoord(x,tempY.get(tempY.size()-1));
            x+=2;
        }
        for (int i = tempY.size()-1;i>=0; i--) {
            addCoord(centerX+centerX- tempX.get(i),tempY.get(i));
        }
        x=getxCoordsToBuild().get(getxCoordsToBuild().size()-1);
        while (x!=centerX-20){
            addCoord(x,getyCoordsToBuild().get(getyCoordsToBuild().size()-1));
            x-=2;
        }
    }
    /**
     * pre: factory is created
     * post: resources near factory are consumed
     */
    public void detectResources(ArrayList<Resource> resources, boolean secondPurpose){
        for (int i = resources.size() - 1; i >= 0; i--) {
            Resource r = resources.get(i);
            if (r.getY() < centerY - 27 && r.getY() > centerY - 100 && getValueOfHyperBolaExpression(r.getX(), r.getY()) < 1&&(!r.isBeingMoved()||secondPurpose)) {
                if (!secondPurpose) {
                    r.setBeingMoved(true);
                    r.setTargetX(centerX);
                    r.setTargetY(centerY - 27);
                    r.setEquation(r.getX(), r.getY(), r.getTargetX(), r.getTargetY());
                }
                else r.setBeingMoved(false);
            }
            if (r.getY()>= centerY - 35&&r.getY() <= centerY -25&&r.isBeingMoved()&&r.getX()>centerX-Math.sqrt(xRadius)&&r.getX()<centerX+Math.sqrt(xRadius)) {
                if (!secondPurpose) {
                    r.setBeRemoved(true);
                    resourcesAvailable += (r.getType() + 1);
                }
            }
        }
    }
    /**
     * pre: the center is initalized
     * post: returns y coord based on the hyperbola formula
     * @param x
     * @param xRadius
     * @param yRadius
     * @param centerX
     * @param centerY
     * @return
     */
    public double getYValueForHyperbola(double x, double xRadius, double yRadius,double centerX, double centerY){
        return (Math.sqrt((yRadius*Math.pow(x-centerX,2)/xRadius)-yRadius)+centerY);
    }

    /**
     * pre: values for hyperbola initialized
     * post: value of expresssion returned
     * @return
     */
    public double getValueOfHyperBolaExpression(double x, double y){
        return (Math.pow((x-centerX),2)/xRadius)-(Math.pow((y-centerY),2)/yRadius);
    }
    public int getCenterX() {
        return centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public double getResourcesAvailable() {
        return resourcesAvailable;
    }

    public void setResourcesAvailable(double resourcesAvailable) {
        this.resourcesAvailable = resourcesAvailable;
    }
}
