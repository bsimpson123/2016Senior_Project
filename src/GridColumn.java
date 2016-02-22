
public class GridColumn {
	protected Block[] blocks;
	protected int columnOffset = 0;
	
	public GridColumn(int size) {
		blocks = new Block[size];
	}
	
	private GridColumn() { }
	
	public GridColumn clone() {
		GridColumn gc = new GridColumn();
		gc.blocks = this.blocks.clone();
		return gc;
	}
}
