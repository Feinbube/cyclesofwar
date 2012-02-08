package cyclesofwar.window.rendering;

class Tag {

	int x;
	int y;
	int w;
	int h;
	Object tag;

	Tag(int x, int y, int w, int h, Object tag) {
		super();
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.tag = tag;
	}

	boolean intersects(int x, int y) {
		return x > this.x && x < this.x + this.w && y > this.y && y < this.y + this.h;
	}
}