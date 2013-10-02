package cyclesofwar;

public class PlayerDisqualifiedException extends Exception {

	private static final long serialVersionUID = 6322835873529189090L;
	public final Exception ex;
	private final Player responsilbe;
	
	public PlayerDisqualifiedException(Player responsilbe, Exception ex) {
		this.responsilbe = responsilbe;
                this.ex = ex;
	}

	public Player getResponsilbe() {
		return responsilbe;
	}
}
