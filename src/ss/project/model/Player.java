package ss.project.model;

public class Player {
	private String name = "";

	public Player() {
	}

	public Player(String n) {
		setName(n);
	}

	public String getName() {
		return name;
	}

	public void requestMove(Game g) {

	}

	public void setName(String n) {
		if (n != null) {
			name = n;
		}
	}
}
