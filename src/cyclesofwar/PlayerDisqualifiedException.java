package cyclesofwar;

public class PlayerDisqualifiedException extends Exception {

	private static final long serialVersionUID = 6322835873529189090L;
	
	private Player responsilbe;
	
	public PlayerDisqualifiedException(Player responsilbe) {
		this.responsilbe = responsilbe;
	}

	public Player getResponsilbe() {
		return responsilbe;
	}
}
