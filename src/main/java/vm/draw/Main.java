package vm.draw;

/**
 * Question:
 * Consider the following design problem and come up with a class hierarchy:
 * Given a drawing mechanism, we always need to invoke some preprocessing before
 * drawing, and postprocessing after drawing, and also each user will draw
 * different shapes like circle, square, whatever he/she likes. Now, make an OO
 * design of classes so that the routine preprocessing/postprocessing can be
 * hidden, and the user can supply whatever the shape he/she wants to draw.
 * Hint: consider a design pattern called Template Pattern.
 *
 */

public class Main {
	public static void main(String[] args) {
		CirclePainter circlePainter = new CirclePainter();
		circlePainter.paint();
	}
}