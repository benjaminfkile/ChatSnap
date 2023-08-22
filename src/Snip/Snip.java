package Snip;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import Capture.Capture;
import Snip.Snip;

public class Snip {


	public static void main(String[] args) {
		new Snip();
	}

	public Snip() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					System.out.println(e);
				}

				JFrame frame = new JFrame();
				frame.setUndecorated(true);
				// This works differently under Java 6
				frame.setBackground(new Color(0, 0, 0, 0));
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setLayout(new BorderLayout());
				frame.add(new SnipItPane());
				frame.setBounds(getVirtualBounds());
				frame.setVisible(true);
			}
		});
	}

	public class SnipItPane extends JPanel {

		private static final long serialVersionUID = 1L;
		private Point mouseAnchor;
		private Point dragPoint;
		private SelectionPane selectionPane;

		public SnipItPane() {
			setOpaque(false);
			setLayout(null);
			selectionPane = new SelectionPane();
			add(selectionPane);
			MouseAdapter adapter = new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					mouseAnchor = e.getPoint();
					dragPoint = null;
					selectionPane.setLocation(mouseAnchor);
					selectionPane.setSize(0, 0);
				}

				@Override
				public void mouseDragged(MouseEvent e) {
					dragPoint = e.getPoint();
					int width = dragPoint.x - mouseAnchor.x;
					int height = dragPoint.y - mouseAnchor.y;

					int x = mouseAnchor.x;
					int y = mouseAnchor.y;

					if (width < 0) {
						x = dragPoint.x;
						width *= -1;
					}
					if (height < 0) {
						y = dragPoint.y;
						height *= -1;
					}
					selectionPane.setBounds(x, y, width, height);
					selectionPane.revalidate();
					repaint();
				}
			};
			addMouseListener(adapter);
			addMouseMotionListener(adapter);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			Graphics2D g2d = (Graphics2D) g.create();

			Rectangle bounds = new Rectangle(0, 0, getWidth(), getHeight());
			Area area = new Area(bounds);
			area.subtract(new Area(selectionPane.getBounds()));
			g2d.setColor(new Color(192, 192, 192, 64));
			g2d.fill(area);

		}
	}

	public class SelectionPane extends JPanel {

		private static final long serialVersionUID = 1L;
		private JButton closeBtn;
		private JButton captureBtn;

		public SelectionPane() {
			JPanel southPanel = new JPanel();
			southPanel.setLayout(new BorderLayout());
			Rectangle bounds = getVirtualBounds();
			closeBtn = new JButton("Close");
			captureBtn = new JButton("Capture");
			setOpaque(false);
			setLayout(new BorderLayout());
			add(southPanel, BorderLayout.SOUTH);
			southPanel.add(captureBtn, BorderLayout.EAST);
			southPanel.add(closeBtn, BorderLayout.WEST);

			closeBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {

					SwingUtilities.getWindowAncestor(SelectionPane.this).dispose();
				}
			});

			captureBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					//remove(southPanel);
					Capture c = new Capture();
					Rectangle r = new Rectangle(getX(), getY(), getWidth(), getHeight() - 25);
					r.x = bounds.x + getX();
					r.y = bounds.y + getY() - 25;
					c.capture(r);
				}
			});
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g.create();
			// I've chosen NOT to fill this selection rectangle, so that
			// it now appears as if you're "cutting" away the selection
			
			//g2d.setColor(new Color(128, 128, 128, 64));
			//g2d.fillRect(0, 0, getWidth(), getHeight());

			float dash1[] = { 10.0f };
			BasicStroke dashed = new BasicStroke(3.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1,
					0.0f);
			g2d.setColor(Color.BLACK);
			g2d.setStroke(dashed);
			g2d.drawRect(0, 0, getWidth() - 3, getHeight() - 3);
			g2d.dispose();
		}
	}

	public static Rectangle getVirtualBounds() {

		Rectangle bounds = new Rectangle(0, 0, 0, 0);

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice lstGDs[] = ge.getScreenDevices();
		for (GraphicsDevice gd : lstGDs) {

			bounds.add(gd.getDefaultConfiguration().getBounds());

		}

		return bounds;

	}
}