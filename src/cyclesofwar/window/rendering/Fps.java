package cyclesofwar.window.rendering;

class Fps {

    private int framesCount = 0;
    private int framesCountAvg = 0;
    private long framesTimer = 0;

    public Fps() {
        framesTimer = System.currentTimeMillis();
    }

    public void update() {
        long now = System.currentTimeMillis();
        framesCount++;
        if (now - framesTimer > 1000) {
            framesTimer = now;
            framesCountAvg = framesCount;
            framesCount = 0;
        }
    }
    
    @Override
    public String toString() {
        return "" + framesCountAvg;
    }
}
