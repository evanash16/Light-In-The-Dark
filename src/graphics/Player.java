package graphics;

import java.awt.*;

public class Player extends Polygon {

	public double velMag;
	private double tempMag;
	public double velX, velY, x, y, radius, angle;
	public boolean vision = false, upgraded = false;
	private int cooldown = 0;
	
	public Player(int x, int y, double velMag, int radius) {
		
		for (double i = 0; i < 2 * Math.PI; i += 2 * Math.PI / 3) {
			int x_ = x + (int) (radius * Math.cos(i - Math.PI / 2));
			int y_ = y + (int) (radius * Math.sin(i - Math.PI / 2));
			this.addPoint(x_, y_);
		}
		
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.velMag = velMag;
		tempMag = this.velMag;
	}
	
	public void upgrade(String upgrade) {
		
		upgraded = true;
		
		switch (upgrade) {
			
		case "Speed":
			cooldown = 500;
			tempMag = velMag * 2;
			break;
		case "Vision":
			cooldown = 250;
			vision = true;
			break;
		default:
			upgraded = false;
			System.out.println("Not a valid upgrade.");
		}
	}
	
	public void update() {
		
		x += velX;
		y += velY;
		
		for (int i = 0; i < npoints; i++) {
			
			xpoints[i] += velX;
			ypoints[i] += velY;
		}
		
		angle = Math.atan2(velX, -velY);

		for (int i = 0; i < npoints; i++) {
			angle = Math.atan2(velY, velX) + (Math.PI / 2) + i * (2 * Math.PI / npoints);
			xpoints[i] = (int) (x + (radius * Math.cos(angle - Math.PI / 2)));
			ypoints[i] = (int) (y + (radius * Math.sin(angle - Math.PI / 2)));
		}
	}
	
	public void draw(Graphics g) {
		
		g.setColor(Color.WHITE);
		g.drawPolygon(this);
		
		cooldown = Math.max(0, cooldown - 1);
		if (cooldown == 0 && upgraded) {
			upgraded = false; 
			vision = false; 
			tempMag = velMag;
		}

		String coolTime = Integer.toString(cooldown);
		if (upgraded) {
			g.setFont(new Font(null, Font.PLAIN, GUI.screensize.height / 75));
			g.drawString(coolTime, (int) x, (int) y - getBounds().height);
		}
	}
	
	public void move(double x_, double y_) {
		
		velX = x_ * tempMag;
		velY = y_ * tempMag;
		
		if (x + velX > GUI.screensize.width || x + velX < 0) {x -= Math.signum(x + velX - GUI.screensize.width) * GUI.screensize.width;}
		if (y + velY > GUI.screensize.height || y + velY < 0) {y -= Math.signum(y + velY - GUI.screensize.height) * GUI.screensize.height;}
	}
	
	public boolean intersects(Polygon p) {
		for (int i = 0; i < p.npoints; i++) {
			for (int j = 0; j < this.npoints; j++) {
				if (Math.sqrt(Math.pow(this.xpoints[j] - p.xpoints[i], 2) + Math.pow(this.ypoints[j] - p.ypoints[i], 2)) < radius) {
					return true;
				}
			}
		}
		return false;
	}
}
