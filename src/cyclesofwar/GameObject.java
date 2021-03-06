package cyclesofwar;

import cyclesofwar.rules.RuleEngine;

public abstract class GameObject extends Sortable {

	protected Universe universe;
        protected RuleEngine ruleEngine;

	protected Player player;

	protected double x;
	protected double y;

	GameObject(Universe universe, Player player, double x, double y) {
		this.universe = universe;
                this.ruleEngine = universe.getRuleEngine();

		this.player = player;

		this.x = x;
		this.y = y;
	}

	/**
	 * gets the owner
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * horizontal coordinate in the 2-dimensional universe (0 means left,
	 * sqrt(numberOfPlayers) means right)
	 */
	@Deprecated
        public double getX() {
		return x;
	}

	/**
	 * vertical coordinate in the 2-dimensional universe (0 means top,
	 * sqrt(numberOfPlayers) means bottom)
	 */
        @Deprecated
	public double getY() {
		return y;
	}

	protected abstract void update(double elapsedSeconds);
}
