package Build;

public class ResourceCollector extends Structure {
    private int centerX,centerY,rad;
    private static int placeTimer=0;

    public static int getPlaceTimer() {
        return placeTimer;
    }

    public static void setPlaceTimer(int placeTimer) {
        ResourceCollector.placeTimer = placeTimer;
    }
    public ResourceCollector(int centerX, int centerY, int radius){
        setCoordinatesOfCircle(centerX,centerY,radius);
        this.centerX=centerX;
        this.centerY=centerY;
        setX(centerX);
        setY(centerY);
        this.rad=radius;
    }
    /**
     * pre:circle exists
     * post:find coordinates of the circle
     */
    public void setCoordinatesOfCircle(int centerX, int centerY, int radius){
        double x=centerX-radius;
        while ((int)x<centerX+radius){
            addCoord(x,circleCoordY(x,centerX,centerY,radius));
            x+=2;
        }
        addCoord(x,circleCoordY(x,centerX,centerY,radius));
        int index=getyCoordsToBuild().size()-1;
        while ((int)x>centerX-radius){
            addCoord(x,centerY-getyCoordsToBuild().get(index)+centerY);
            x-=2;
            index--;
        }
        addCoord(x,centerY-getyCoordsToBuild().get(index)+centerY);
    }
    /**
     * pre:circle exists
     * post:uses the equation of a circle to find the y coord
     */
    public double circleCoordY(double pointX,double centerX, double centerY, double rad){
        return Math.sqrt(Math.pow(rad,2)-Math.pow(pointX-centerX,2))+centerY;
    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public int getRad() {
        return rad;
    }
}
