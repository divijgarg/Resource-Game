package Build;

public class Portal extends Structure{
    private double startX,startY,sideLength;
    private boolean justTraveled=false;
    private static int placeTimer=0;

    public static int getPlaceTimer() {
        return placeTimer;
    }

    public static void setPlaceTimer(int placeTimer) {
        Portal.placeTimer = placeTimer;
    }
    public Portal(double startX, double startY, double sideLength){
        setCoordinatesOfSquare(startX,startY,sideLength);
        this.startX=startX;
        this.startY=startY;
        this.sideLength=sideLength;
        setX(startX+20);
        setY(startY+20);
    }

    /**
     * pre: portal exists
     * post: finds coordinates of the square
     * @param startX
     * @param startY
     * @param sideLength
     */
    public void setCoordinatesOfSquare(double startX,double startY, double sideLength) {
        double x = startX;
        double y = startY;
        while ((int) x != (int) (startX + sideLength)) {
            addCoord(x, y);
            x += 4;
        }
        while ((int) y != (int) (startY + sideLength)) {
            addCoord(x, y);
            y += 4;
        }
        while ((int) x != (int) (startX)) {
            addCoord(x, y);
            x-=4;
        }
        while ((int) y != (int) (startY)) {
            addCoord(x, y);
            y-=4;
        }
    }

    public double getSideLength() {
        return sideLength;
    }

    public double getStartX() {
        return startX;
    }

    public double getStartY() {
        return startY;
    }

    public boolean isJustTraveled() {
        return justTraveled;
    }

    public void setJustTraveled(boolean justTraveled) {
        this.justTraveled = justTraveled;
    }
}
