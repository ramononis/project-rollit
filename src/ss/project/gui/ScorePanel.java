package ss.project.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ss.project.engine.Game;
import ss.project.engine.Mark;

public class ScorePanel extends JPanel implements Observer {
	private static final long serialVersionUID = 7155579531691129608L;

	class ScorePanelItem extends JComponent {
		private static final long serialVersionUID = -2208001320504860234L;
		private Color color;
		private int score;
		private String name;
		private JLabel colorLabel;
		private JLabel scoreLabel;

		public String getName() {
			return name;
		}

		public void setName(String n) {
			name = n;
			colorLabel.setText(name);
		}

		public ScorePanelItem(Color c, int s, String n) {
			init();
			setScore(s);
			setColor(c);
			setName(n);
		}

		public void init() {
			BoxLayout layout = new BoxLayout(this, BoxLayout.X_AXIS);
			setLayout(layout);
			colorLabel = new JLabel();
			colorLabel.setPreferredSize(new Dimension(20, 10));
			colorLabel.setOpaque(true);
			scoreLabel = new JLabel();
			add(colorLabel);
			add(scoreLabel);
		}

		public Color getColor() {
			return color;
		}

		public void setColor(Color c) {
			color = c;
			colorLabel.setBackground(color);
		}

		public int getScore() {
			return score;
		}

		public void setScore(int s) {
			score = s;
			scoreLabel.setText("   " + score + "   ");
		}
	}

	private Map<Mark, ScorePanelItem> items;

	public ScorePanel(Game game) {
		BoxLayout layout = new BoxLayout(this, BoxLayout.X_AXIS);
		setLayout(layout);
		items = new HashMap<Mark, ScorePanelItem>();
		for (Mark mark : game.getPlayers().keySet()) {
			ScorePanelItem item = new ScorePanelItem(mark.toColor(), 0, game.getPlayers()
							.get(mark).getName());
			add(item);
			items.put(mark, item);
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof Game) {
			Game game = (Game) o;
			for (Mark mark : game.getPlayers().keySet()) {
				items.get(mark).setScore(game.getBoard().getScore(mark));
			}
		}
	}

}
