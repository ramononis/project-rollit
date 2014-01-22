package ss.project.server.leaderboard;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class GameResult {
	private Map<String, Integer> result = new HashMap<String, Integer>();
	private Date date = new Date();

	public GameResult(Map<String, Integer> r, Date d) {
		setResult(r);
		setDate(d);
	}

	public void setResult(Map<String, Integer> r) {
		if (r != null) {
			result = r;
		}
	}

	public ArrayList<Entry<String, Integer>> getResultsByRank() {
		ArrayList<Entry<String, Integer>> ranking = new ArrayList<Map.Entry<String, Integer>>(
				result.entrySet());
		int oi = ranking.size() - 1;
		while (oi > 0) {
			int wi = 0;
			int i = 0;
			while (i < oi) {
				if (ranking.get(i).getValue() > ranking.get(i + 1).getValue()) {
					Entry<String, Integer> temp = ranking.get(i);
					ranking.remove(i);
					ranking.add(i, ranking.get(i));
					ranking.remove(i + 1);
					ranking.add(i + 1, temp);
					wi = i;
				}
				i++;
			}
			oi = wi;
		}
		return ranking;
	}

	public Map<String, Integer> getResult() {
		return result;
	}

	public int getScore(String player) {
		return result.get(player);
	}

	public String getByRank(int rank) {
		return getResultsByRank().get(rank).getKey();
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date d) {
		if (d != null) {
			date = d;
		}
	}
}
