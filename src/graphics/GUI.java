package graphics;

import pathfinding.Node;
import pathfinding.Pathfinding;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class GUI extends JFrame implements ActionListener, KeyListener {

	private static Toolkit tk = Toolkit.getDefaultToolkit();
	public static Dimension screensize = new Dimension(tk.getScreenResolution() * 7, tk.getScreenResolution() * 7);
	private static int gridWidth = 10;

	private Timer updateTimer;
	private Node[][] nodes;
	private ArrayList<Polygon> obs;
	private ArrayList<Item> items = new ArrayList<>();
	
	private Player player, enemy;
	private Point off, spawnPoint = new Point(screensize.width / 2, screensize.height / 2);

	private boolean[] keys = new boolean[] {false, false, false, false, false}; //left, right, up, down, ping
	private int ticks = 0, framesPerSecond = 0;
	private double radius = 0;
	private boolean showAll = true, dead = false;
	
	public static void main(String[] args) {
		new GUI();
	}

	public GUI() {

		super("Light In The Dark");
		init();
	}
	
	public void init() {

		setSize(screensize);
		setLocation((tk.getScreenSize().width - getWidth()) / 2, (tk.getScreenSize().height - getHeight()) / 2);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		addKeyListener(this);
		setFocusTraversalKeysEnabled(false);
		
		reset();

		updateTimer = new Timer(10, this);
		updateTimer.start();
		
		setVisible(true);
	}
	
	public void reset() {

		obs = generateObstacles(10);
		nodes = new Node[getWidth() / gridWidth + 1][getHeight() / gridWidth + 1];

		for (int i = 0; i < nodes.length; i++) {
			for (int j = 0; j < nodes[i].length; j++) {
				boolean contains = false;
				for (Polygon poly : obs) {
				    Rectangle bounds = poly.getBounds();
				    bounds.grow(gridWidth, gridWidth);
					if (bounds.contains(i * gridWidth, j * gridWidth)) {
						contains = true;
						break;
					}
				}
				if (!contains) {
					nodes[i][j] = new Node(i, j, gridWidth);
				} else {
                    nodes[i][j] = new Node(i, j, gridWidth, false);
                }
			}
		}
		
		spawnPoint = getPoint(obs);
		player = new Player(spawnPoint.x, spawnPoint.y, 4, getWidth() / 100);
		Point enemySpawn = getPoint(obs);
		enemy = new Player(enemySpawn.x, enemySpawn.y, 2, getWidth() / 100);
		
		off = new Point((int) player.x, (int) player.y);
		
		items = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			
			Point itemPos = getPoint(obs);
			double rand = Math.random();
			if (rand < 0.5) {
				items.add(new Item(itemPos.x, itemPos.y, getWidth() / 50, "Speed", Color.BLUE));
			}
			else {
				items.add(new Item(itemPos.x, itemPos.y, getWidth() / 50, "Vision", Color.YELLOW));
			}
		}
		
		keys = new boolean[]{false, false, false, false, false};
	}
	
	public Point getPoint(ArrayList<Polygon> polys) {
		
		boolean contains = true;
		Point p = new Point((int) (Math.random() * getWidth()), (int) (Math.random() * getHeight()));
		while (contains) {
			for (int j = 0; j < polys.size(); j++) {
				if (polys.get(j).contains(p)) {
					p = new Point((int) (Math.random() * getWidth()), (int) (Math.random() * getHeight()));
				}
				else if (j == polys.size() - 1) {
					contains = false;
				}
			}		
		}
		
		return p;
	}
	
	public void paint(Graphics g) {
		
		double startTime = System.currentTimeMillis();
		
		BufferedImage buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics g2 = buffer.getGraphics();

//        for (int i = 0; i < nodes.length; i++) {
//            for (int j = 0; j < nodes[i].length; j++) {
//                g2.setColor(Color.RED);
//                if (nodes[i][j].isTraversable()) {
//                    g2.setColor(Color.GREEN);
//                }
//                g2.fillOval(nodes[i][j].getX() - 2, nodes[i][j].getY() - 2, 4, 4);
//            }
//        }

		if (showAll || player.vision) {showAll(g2);}
		
		if (!dead) {

			if (keys[4]) { //if the space bar is pressed, progress time
				
				ticks++;

				if (ticks * 3 % 300 >= 295) {ticks = 0; keys[4] = false; radius = 0;}
				radius = ticks * 3 % 300;

				ArrayList<Polygon> tempObs = (ArrayList<Polygon>) obs.clone();
				tempObs.add(enemy);
				double[] sweep = sweep(off, tempObs);

				g2.setColor(new Color(255, 255, 255));
				
				for (int i = 0; i < sweep.length; i++) {
					double a = i * (2 * Math.PI / (double) sweep.length);
					double dist = Math.min(sweep[i], radius);
					int x = (int) (dist * Math.cos(a));
					int y = (int) (dist * Math.sin(a));
					
					for (Item item: items) {
						
						double distToItem = Math.sqrt(Math.pow(off.x + x - item.x, 2) + Math.pow(off.y + y - item.y, 2));
						if (distToItem < 2) {
							item.animate = true;
						}
					}

					if (Math.random() < map(ticks * 3 % 300, 0, 300, 1, 0)) {
						
						g2.fillOval(off.x + x - 2, off.y + y - 2, 4, 4);
					}
				}

				Node closestToEnemy = nodes[(int) Math.min(Math.round(enemy.x / (double) gridWidth), nodes.length - 1)][(int) Math.min(Math.round(enemy.y / (double) gridWidth), nodes[0].length - 1)];
				Node closestToPlayer = nodes[(int) Math.min(Math.round(player.x / (double) gridWidth), nodes.length - 1)][(int) Math.min(Math.round(player.y / (double) gridWidth), nodes[0].length - 1)];

				ArrayList<Node> path = Pathfinding.aStar(nodes, closestToEnemy, closestToPlayer);

                if(!path.isEmpty()) {
                    for (Node node: path) {
                        if (node != null) {
                            g2.setColor(Color.BLUE);
                            g2.fillOval(node.getX() - 2, node.getY() - 2, 4, 4);
                        }
                    }
                }

                double deltaX, deltaY;
                if(!path.isEmpty() && path.size() > 1 && path.get(1) != null) {
                    deltaX = path.get(1).getX() - enemy.x;
                    deltaY = path.get(1).getY() - enemy.y;
                } else {
                    deltaX = player.x - enemy.x;
                    deltaY = player.y - enemy.y;
                }

                if (Math.abs(deltaX) <= 1) {deltaX = 0;}
                if (Math.abs(deltaY) <= 1) {deltaY = 0;}

                enemy.move(Math.signum(deltaX), Math.signum(deltaY));

				boolean colliding = false;
				for (Polygon p: obs) {
					if (p.contains(new Point((int)(enemy.x + enemy.velX), (int) (enemy.y + enemy.velY)))) {
						colliding = true;
						break;
					}
				}
				if (enemy.intersects(player)) {dead = true; ticks = 0;}
				if (!colliding) {enemy.update();}
			}
			
			player.draw(g2);
			boolean colliding = false;
			for (Polygon p: obs) {
				if (p.contains(new Point((int)(player.x + player.velX), (int) (player.y + player.velY)))) {
					colliding = true;
					break;
				}
			}
			if (!colliding && !dead) {
				player.update();
				for (int i = items.size() - 1; i >= 0; i--) {
					Item item = items.get(i);
					if (item.visible && !item.used && !player.upgraded && Math.sqrt(Math.pow(item.x - player.x, 2) + Math.pow(item.y - player.y, 2)) < item.radius) {
						player.upgrade(item.useItem());
						items.remove(i);
					}
				}
			}
			
			for (Item i: items) {
				
				i.draw(g2);
			}
			
		} else {

            showAll(g2);
            player.draw(g2);
            g2.setColor(new Color(150, 0, 0));
            int y = ticks * 8;
            int startY = 0;

            for (int i = 0; i < getWidth(); i++) {
                startY = (int) (y + (getHeight() / 50) * Math.sin((double) (i + 10 * ticks) / (getHeight() / 25)));
                g2.fillRect(i, getHeight() - startY, 1, startY);
            }

            g2.setColor(Color.BLACK);
            g2.setFont(new Font(null, Font.ITALIC, getHeight() / (int) (map(Math.random(), 0, 1, 18, 20))));
            int width = g2.getFontMetrics().stringWidth("You died!");
            g2.drawString("You died!", (getWidth() - width) / 2, getHeight() - y + getHeight() / 2);

            if (startY > getHeight()) {
                dead = false;
                reset();
            }

            ticks++;
        }
		
		g.drawImage(buffer, 0, 0, null);
	
	}
	
	private void showAll(Graphics g) {
		
		Graphics2D g2D = (Graphics2D) g;
		g2D.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 5.5f, new float[]{10.0f}, 10.0f));
		g.setColor(Color.WHITE);
		
		for (Polygon p: obs) {
			
			g.drawPolygon(p);
		}
		enemy.draw(g);
		g2D.setStroke(new BasicStroke());
	}
	
	public static double map(double val, double minVal, double maxVal, double finMinVal, double finMaxVal) {
		
		return finMinVal + ((val - minVal) / (maxVal - minVal)) * (finMaxVal - finMinVal);
	}
	
	public ArrayList<Polygon> generateObstacles(int numObstacles) {
		
		ArrayList<Polygon> obs = new ArrayList<>();

		for (int i = 0; i < numObstacles; i++) {
			
			int sideCount = 3 + (int) (Math.random() * 3);
			double radius = getWidth() / 20 + (Math.random() * getWidth() / 20);
			
			Point off;
			if (obs.size() > 0) {
				off = getPoint(obs);
			} else {
				off = new Point((int) (Math.random() * getWidth()), (int) (Math.random() * getHeight()));
			}
			
			int[] xPoints = new int[sideCount];
			int[] yPoints = new int[sideCount];

			double angleRot = Math.random() * 2 * Math.PI;
			while (angleRot % Math.PI / 2 == 0) {
				angleRot = Math.random() * 2 * Math.PI;
			}
			
			for (int j = 0; j < sideCount; j++) {
				
				double a = j * (2 * Math.PI / sideCount);
				a += angleRot;
				
				xPoints[j] = off.x + (int) (radius * Math.cos(a));
				yPoints[j] = off.y + (int) (radius * Math.sin(a));
			}
			
			obs.add(new Polygon(xPoints, yPoints, sideCount));
		}
		
		return obs;
	}
	
	public double[] sweep(Point off, ArrayList<Polygon> polys) {
		
		ArrayList<Double> dists = new ArrayList<>();
		
		for (double i = 0; i < 2 * Math.PI; i += 0.009) {
			
			double deltaX = Math.cos(i);
			double deltaY = Math.sin(i);
			
			if (deltaX != 0) {
				
				double m = deltaY / deltaX;
				double b = off.y - (deltaY / deltaX) * off.x;
				
				ArrayList<Point> points = new ArrayList<>();
				
				for (Polygon p: polys) {
					
					for (int j = 0; j < p.npoints; j++) {
						int x = p.xpoints[j];
						int x2 = p.xpoints[(j + 1) % p.npoints];
						int y = p.ypoints[j];
						int y2 = p.ypoints[(j + 1) % p.npoints];
						
						double deltaX2 = x2 - x;
						double deltaY2 = y2 - y;
						
						if (deltaX2 != 0) {
							
							double m2 = deltaY2 / deltaX2;
							double b2 = y2 - (deltaY2 / deltaX2) * x2;
							
							double xInt = (b2 - b) / (m - m2);
							double yInt = m * xInt + b;
							
							if (xInt > Math.min(x, x2) && xInt < Math.max(x, x2) && yInt > Math.min(y, y2) && yInt < Math.max(y, y2)
							&& Math.signum(xInt - off.x) == Math.signum(deltaX) && Math.signum(yInt - off.y) == Math.signum(deltaY)) {
								
								points.add(new Point((int) xInt, (int) yInt));
							}
						}
					}
				}
				
				if (points.size() > 0) {
					
					int minDistIndex = 0;
					double minDist = Math.sqrt(Math.pow(points.get(minDistIndex).x - off.x, 2) + Math.pow(points.get(minDistIndex).y - off.y, 2));
					for (int j = 0; j < points.size(); j++) {
						double dist = Math.sqrt(Math.pow(points.get(j).x - off.x, 2) + Math.pow(points.get(j).y - off.y, 2));
						minDist = Math.sqrt(Math.pow(points.get(minDistIndex).x - off.x, 2) + Math.pow(points.get(minDistIndex).y - off.y, 2));
						
						if (dist < minDist) {
							minDistIndex = j;
							minDist = Math.sqrt(Math.pow(points.get(minDistIndex).x - off.x, 2) + Math.pow(points.get(minDistIndex).y - off.y, 2));
						}
					}
					dists.add(minDist);
				}
				else {
					
					dists.add((double) 2 * getWidth());
				}
			}
		}
		
		double[] points = new double[dists.size()];
		for (int i = 0; i < dists.size(); i++) {
			points[i] = dists.get(i);
		}
		return points;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		update();
		repaint();
	}
	
	public void update() {
		
		if (keys[0]) {player.move(-1, Math.signum(player.velY));}
		if (keys[1]) {player.move(1, Math.signum(player.velY));}
		if (!keys[0] && !keys[1]) {player.move(0, Math.signum(player.velY));}
		
		if (keys[2]) {player.move(Math.signum(player.velX), -1);}
		if (keys[3]) {player.move(Math.signum(player.velX), 1);}
		if (!keys[2] && !keys[3]) {player.move(Math.signum(player.velX), 0);}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		
		if (arg0.getKeyCode() == KeyEvent.VK_A) {
			
			keys[0] = true;
		}
		
		if (arg0.getKeyCode() == KeyEvent.VK_D) {
			
			keys[1] = true;
		}
		
		if (arg0.getKeyCode() == KeyEvent.VK_W) {
			
			keys[2] = true;
		}
		
		if (arg0.getKeyCode() == KeyEvent.VK_S) {
			
			keys[3] = true;
		}
		
		if (arg0.getKeyCode() == KeyEvent.VK_SPACE && !keys[4]) {
			
			off = new Point((int) player.x, (int) player.y);
			keys[4] = true;
		}
		
		if (arg0.getKeyCode() == KeyEvent.VK_TAB) {
			
			showAll = !showAll;
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {

		if (arg0.getKeyCode() == KeyEvent.VK_A) {
			
			keys[0] = false;
		}
		
		if (arg0.getKeyCode() == KeyEvent.VK_D) {
			
			keys[1] = false;
		}
		
		if (arg0.getKeyCode() == KeyEvent.VK_W) {
			
			keys[2] = false;
		}
		
		if (arg0.getKeyCode() == KeyEvent.VK_S) {
			
			keys[3] = false;
		}
		
		if (arg0.getKeyCode() == KeyEvent.VK_G) {
			
			ticks = 0;
			dead = !dead;
		}
		
		if (arg0.getKeyCode() == KeyEvent.VK_R) {
			
			reset();
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
