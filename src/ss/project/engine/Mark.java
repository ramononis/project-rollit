package ss.project.engine;

import java.awt.Color;

/**
 * Represents a mark in the Rolit game. There five possible values: Mark.RED,
 * Mark.GREEN, Mark.BLUE, Mark.YELLOW and Mark.EMPTY. Module 2 lab assignment
 * 
 * @author Ramon Onis
 */
public enum Mark {

	EMPTY, RED, GREEN, BLUE, YELLOW;
	/*
	 * @ ensures this == Mark.RED ==> \result == Mark.GREEN; ensures this ==
	 * Mark.GREEN ==> \result == Mark.BLUE; ensures this == Mark.BLUE ==>
	 * \result == Mark.YELLOW; ensures this == Mark.YELLOW ==> \result ==
	 * Mark.RED; ensures this == Mark.EMPTY ==> \result == Mark.EMPTY;
	 */
	/**
	 * Returns the next mark.
	 * 
	 * @return the next mark is this mark is not EMPTY
	 */
	public Mark next() {
		switch (this) {
			case RED:
				return GREEN;
			case GREEN:
				return BLUE;
			case BLUE:
				return YELLOW;
			case YELLOW:
				return RED;
			default:
				return EMPTY;
		}
	}

	public Color toColor() {
		Color color = null;
		switch (this) {
			case BLUE:
				color = new Color(0, 0, 255);
				break;
			case GREEN:
				color = new Color(0, 255, 0);
				break;
			case RED:
				color = new Color(255, 0, 0);
				break;
			case YELLOW:
				color = new Color(255, 255, 0);
				break;
			default:
				color = new Color(127, 127, 127);
				break;
		}
		return color;
	}

	public static Mark fromString(String s) {
		
		Mark result = null;
		if (s.equals("BLUE")) {
			result = BLUE;
		} else if (s.equals("GREEN")) {
			result = GREEN;
		} else if (s.equals("RED")) {
			result = RED;
		} else if (s.equals("YELLOW")) {
			result = YELLOW;
		} else if (s.equals("EMPTY")) {
			result = EMPTY;
		}
		return result;
	}
}
