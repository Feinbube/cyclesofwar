package cyclesofwar;
import java.awt.Color;


public class IdlePlayer extends Player {
	
	public IdlePlayer(Color c){
		super(c, null);
	}

	@Override
	public void think() {
		// do nothing
	}

}
