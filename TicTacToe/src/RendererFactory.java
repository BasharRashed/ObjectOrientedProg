public class RendererFactory {
	public RendererFactory() {}

	public Renderer buildRenderer(String type,int size) {
		switch (type.toLowerCase()) {
			case "console":
				return new ConsoleRenderer(size);
			case "void":
				return new VoidRenderer();
			default:
				return null;
		}
	}
}
