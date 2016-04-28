import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class GridColumn implements Cloneable {
	protected Block[] blocks;
	protected int columnOffset = 0;
	
	public GridColumn(int size) {
		blocks = new Block[size];
	}
	
	private GridColumn() { }
	
	public GridColumn clone() {
		GridColumn gc = new GridColumn();
		//gc.blocks = this.blocks.clone();
		gc.blocks = new Block[this.blocks.length];
		for (int i = 0; i < gc.blocks.length; i++) {
			gc.blocks[i] = this.blocks[i].clone();
		}
		return gc;
	}
	
	protected static void writeToFile(GridColumn[] grid) {
		String[] conv = new String[grid.length];
		String[] subset;
		LocalDateTime time = LocalDateTime.now();
		String filename = String.format("%1$tF %1$tH%1$tM%1$tS.csv", time);
		for (int x = 0; x < grid.length; x++) {
			subset = new String[grid[0].blocks.length];
			String.join(",", conv);
			for (int y = 0; y < grid[0].blocks.length; y++) {
				switch(grid[x].blocks[y].type) {
					case BLOCK:
						subset[y] = String.format("1%d", grid[x].blocks[y].colorID);
						break;
					case BOMB:
						subset[y] = String.format("2%d", grid[x].blocks[y].colorID);
						break;
					case WEDGE:
						subset[y] = "30";
						break;
					case STAR:
						subset[y] = "31";
						break;
					case TRASH:
						subset[y] = "32";
						break;
					case HEART:
						subset[y] = "33";
						break;
					case ROCK:
						subset[y] = "34";
						break;
				}
			}
			conv[x] = String.join(",", subset);
			
		}
		try {
			BufferedWriter outFile = new BufferedWriter(new FileWriter(filename));
			
			outFile.write("1a"); // This is the file version
			outFile.newLine();
			outFile.write(String.format("%d,%d", grid.length, grid[0].blocks.length)); // write grid size to the file
			outFile.newLine();
			
			outFile.close();
		} catch (IOException ioe) {
			Global.writeToLog(String.format("Error writing to custom map file.\n%s", ioe.getMessage()), true);
		}
	}
	
	protected static GridColumn[] loadFromFile(String filename) {
		GridColumn[] grid = null;
		try {
			BufferedReader inf = new BufferedReader(new FileReader(filename));
			String version = inf.readLine();
			switch (version) {
				case "0": // original file format
					
					break;
				case "1a":
					
					break;
				default: // unknown
					
			}
			inf.close();
		} catch (IOException ieo) {
			
		}
		return grid;
	}
	
}
