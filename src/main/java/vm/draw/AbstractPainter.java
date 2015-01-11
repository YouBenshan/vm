package vm.draw;

public abstract class AbstractPainter {

	private void post() {
		System.out.println("post");
	}

	private void pre() {
		System.out.println("pre");
	}

	protected abstract void draw();

	public final void paint() {
		this.pre();
		this.draw();
		this.post();
	}
}
