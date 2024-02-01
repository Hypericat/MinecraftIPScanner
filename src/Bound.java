import java.awt.*;

public class Bound {
    int x;
    int y;
    int width;
    int height;
    public Bound (int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        if (width == -1) width = 2000;
        if (height == -1) height = 2000;
        this.width = width;
        this.height = height;
    }
    public Rectangle toRect() {
        return new Rectangle(x, y, width, height);
    }
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    public int getX2() {
        return this.x + this.width;
    }
    public int getY2() {
        return this.y + this.height;
    }
    public double getDiag() {
        return Math.sqrt((this.height * this.height) + (this.width * this.width));
    }
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}