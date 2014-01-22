package ss.project.engine;

public class HumanPlayer implements Player {
	private String name = "humanplayer";

	public HumanPlayer(String n) {
		setName(n);
	}

	public void setName(String n) {
		if (n != null) {
			name = n;
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void requestMove(Game g) {

	}

}
