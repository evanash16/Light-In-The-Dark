package graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

public class Item{
	
	public int x, y, radius;
	private int ticks = 0;
	private Color col;
	private String upgrade = "";
	public boolean animate, visible = false, used = false;
	
	public Item(int x_, int y_, int radius_, String upgrade_, Color color_){
		
		this.x = x_;
		this.y = y_;
		this.radius = radius_;
		this.upgrade = upgrade_;
		this.col = color_;
	}
	
	public void draw(Graphics g){
		
		g.setColor(col);
		
		if(animate && !visible && !used){
			
			ticks++;
			int aniRad = 3 * ticks;
			if(aniRad >= radius / 2){
				g.drawOval(x - (radius / 2), y - (radius / 2), radius, radius);
				
				for(double i = 0; i < 2 * Math.PI; i += 0.01){
					
					int polarX = (int) (aniRad * Math.cos(i));
					int polarY = (int) (aniRad * Math.sin(i));
					if(Math.random() <= GUI.map(aniRad, 0, 10 * radius, 1, 0)){
						g.fillOval(x + polarX - 2, y + polarY - 2, 4, 4);
					}
				}
				
			}
			else{
				g.drawOval(x - aniRad, y - aniRad, 2 * aniRad, 2 * aniRad);
			}
			
			if(aniRad >= 10 * radius){animate = false; visible = true; ticks = 0;}
		}
		
		if(visible) {g.drawOval(x - (radius / 2), y - (radius / 2), radius, radius);}
	}
	
	public String useItem(){
		
		visible = false;
		used = true;
		
		return upgrade;
	}
}
