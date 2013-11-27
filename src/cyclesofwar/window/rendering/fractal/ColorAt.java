package cyclesofwar.window.rendering.fractal;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

public class ColorAt implements Comparable<ColorAt> {

	static final Color defaultColor = new Color(255, 240, 245);
	private final double value;
	private final Color color;

	public ColorAt(double value, Color color) {
		this.value = value;
		this.color = color;
	}

	public double getValue() {
		return value;
	}

	public Color getColor() {
		return color;
	}

	public static Color getColor(List<ColorAt> colors, double value) {
		Collections.sort(colors);

		for (int i = 0; i < colors.size(); i++) {
			if (colors.get(i).getValue() > value) {

				return interpolate(i == 0 ? new ColorAt(0.0, defaultColor)
						: colors.get(i - 1), colors.get(i), value);
			}
		}
		return defaultColor;
	}

	private static Color interpolate(ColorAt c1, ColorAt c2, double value) {
		return Colors.interpolate(c1.getColor(), c2.getColor(),
				(value - c1.getValue()) / (c2.getValue() - c1.getValue()));
	}

	@Override
	public int compareTo(ColorAt o) {
		return Double.compare(value, o.value);
	}
}
