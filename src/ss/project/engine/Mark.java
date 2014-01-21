package ss.project.engine;

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
}
