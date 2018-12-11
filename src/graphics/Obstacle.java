package graphics;

import java.awt.*;

public class Obstacle extends Polygon {

    private int x, y;

    public Obstacle (int[] xpoints, int[] ypoints, int npoints) {
        super(xpoints, ypoints, npoints);
        int avgX = 0, avgY = 0;
        for (int i = 0; i < npoints; i++) {
            avgX += xpoints[i];
            avgY += ypoints[i];
        }
        this.x = avgX / npoints;
        this.y = avgY / npoints;
    }

    public void translate(int deltaX, int deltaY) {
        super.translate(deltaX, deltaY);
        x += deltaX;
        y += deltaY;
    }

    public Obstacle scaled(double factor) {
        int[] newXPoints = new int[npoints];
        int[] newYPoints = new int[npoints];
        for (int i = 0; i < npoints; i++) {
            newXPoints[i] = getX() + (int) (factor * (xpoints[i] - getX()));
            newYPoints[i] = getY() + (int) (factor * (ypoints[i] - getY()));
        }
        return new Obstacle(newXPoints, newYPoints, npoints);
    }

    public Obstacle offset(int offset) {
        int[] newXPoints = new int[npoints];
        int[] newYPoints = new int[npoints];
        for (int i = 0; i < npoints; i++) {
            newXPoints[i] = getX() + (int) (Math.signum((xpoints[i] - getX())) * offset) + (xpoints[i] - getX());
            newYPoints[i] = getY() + (int) (Math.signum((ypoints[i] - getY())) * offset) + (ypoints[i] - getY());
        }
        return new Obstacle(newXPoints, newYPoints, npoints);
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }
}
