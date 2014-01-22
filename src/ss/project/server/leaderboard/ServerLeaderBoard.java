package ss.project.server.leaderboard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class ServerLeaderBoard {
	private String name = "";

	public ArrayList<GameResult> getResults() {
		return results;
	}

	private ArrayList<GameResult> results = new ArrayList<GameResult>();

	public ServerLeaderBoard(String n) {
		setName(n);
	}

	@SuppressWarnings("unchecked")
	public void loadResults() {
		results.clear();
		ObjectInputStream in;
		try {
			in = new ObjectInputStream(new FileInputStream(getFile()));
			results = (ArrayList<GameResult>) in.readObject();
			in.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveResults() {
		results.clear();
		ObjectOutputStream out;
		try {
			out = new ObjectOutputStream(new FileOutputStream(getFile()));
			out.writeObject(results);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getName() {
		return name;
	}

	public void addResult(GameResult result) {
		results.add(result);
	}

	public void setName(String n) {
		if (n != null) {
			name = n;
		}
	}

	public File getFile() {
		return new File(System.getenv("APPDATA") + File.separator
				+ "leaderboard" + name + ".lbd");
	}
}
