package cyclesofwar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class GameObject {

	protected Universe universe;

	protected Player player;

	protected double x;
	protected double y;

	GameObject(Universe universe, Player player, double x, double y) {
		this.universe = universe;

		this.player = player;

		this.x = x;
		this.y = y;
	}

	/*
	 * gets the owner
	 */
	public Player getPlayer() {
		return player;
	}

	/*
	 * horizontal coordinate in the 2-dimensional universe (0 means left,
	 * sqrt(numberOfPlayers) means right)
	 */
	public double getX() {
		return x;
	}

	/*
	 * vertical coordinate in the 2-dimensional universe (0 means top,
	 * sqrt(numberOfPlayers) means bottom)
	 */
	public double getY() {
		return y;
	}

	protected abstract void update(double elapsedSeconds);
	
	/*
	 * sorts a list using a comparator
         * e.g.: Fleet.sortBy(Fleet.ArrivalTimeComparator, fleets);
	 */
	public static <T> void sortBy(Comparator<T> comparator, List<T> list) {
		Collections.sort(list, comparator);
	}
	
	/*
	 * returns a new list with items sorted according to the comparator
         * e.g.: List<Fleet> sortedFleets = Fleet.sortedBy(Fleet.ArrivalTimeComparator, fleets);
	 */
	public static <T> List<T> sortedBy(Comparator<T> comparator, List<T> list) {
		List<T> result = new ArrayList<>();
		result.addAll(list);
		sortBy(comparator, list);
		return result;
	}
}
