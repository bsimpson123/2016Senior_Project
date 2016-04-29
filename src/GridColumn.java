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
		gc.blocks = this.blocks.clone();
		/*
		gc.blocks = new Block[this.blocks.length];
		for (int i = 0; i < gc.blocks.length; i++) {
			gc.blocks[i] = this.blocks[i].clone();
		} //*/
		return gc;
	}
	
	public static GridColumn[] copyGrid(GridColumn[] grid) {
		GridColumn[] copy = new GridColumn[grid.length];
		int blocks = grid[0].blocks.length;
		for (int x = 0; x < copy.length; x++) {
			copy[x] = new GridColumn(blocks);
			for (int y = 0; y < blocks; y++) {
				copy[x].blocks[y] = grid[x].blocks[y].clone();
			}
		}
		return copy;
	}
	
	
	protected static void writeToFile(GridColumn[] grid) {
		writeToFile(grid, "1a");
	}
	
	protected static void writeToFile(GridColumn[] grid, String version) {

		switch (version) {
			case "1a":
				String[] conv = new String[grid.length];
				String[] subset;
				LocalDateTime time = LocalDateTime.now();
				String filename = String.format("level.%1$tF.%1$tH%1$tM%1$tS.dat", time);
				for (int x = 0; x < grid.length; x++) {
					subset = new String[grid[0].blocks.length];
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
					conv[x] = String.join("", subset);
					
				}
				try {
					BufferedWriter outFile = new BufferedWriter(new FileWriter(filename));
					
					outFile.write("1a"); // This is the file version
					outFile.newLine();
					outFile.write(String.format("%d,%d", grid.length, grid[0].blocks.length)); // write grid size to the file
					outFile.newLine();
					
					for (int x = 0; x < conv.length; x++) {
						outFile.write(conv[x]);
						outFile.newLine();
					}
					
					outFile.close();
				} catch (IOException ioe) {
					Global.writeToLog(String.format("Error writing to custom map file.\n%s", ioe.getMessage()), true);
				}				
				break;
		} // end switch (version)

	}
	
	protected static GridColumn[] loadFromFile(String filename) {
		GridColumn[] grid = null;
		Block block;
		String buffer, sub;
		String[] set;
		int xs = 20, ys = 20;
		int id, data, raw;
		try {
			BufferedReader inf = new BufferedReader(new FileReader(filename));
			String version = inf.readLine().trim();
			switch (version) {
				case "0": // original file format, only supports 20x20 grid
					grid = new GridColumn[20];
					buffer = inf.readLine();
					int xc = 0;
					while (buffer != null) {
						set = buffer.split(",");
						grid[xc] = new GridColumn(20);
						for (int i = 0; i < set.length; i++) {
							grid[xc].blocks[i] = new Block(Block.BlockType.BLOCK, Integer.parseInt(set[i]));
						}
						xc++;
						buffer = inf.readLine();
					}
					
					inf.close();
					break;
				case "1a":
					buffer = inf.readLine().trim();
					set = buffer.split(",");
					xs = Integer.parseInt(set[0]);
					ys = Integer.parseInt(set[1]);
					grid = new GridColumn[xs];
					for (int x = 0; x < xs; x++) {
						grid[x] = new GridColumn(ys);
						buffer = inf.readLine();
						for (int y = 0; y < ys; y++) {
							sub = buffer.substring(2 * y, 2 * y + 2);
							raw = Integer.parseInt(sub);
							id = raw / 10;
							data = raw % 10;
							switch (id) {
								case 1: // Block
									block = new Block(Block.BlockType.BLOCK, data);
									break;
								case 2: // Bomb
									block = new Block(Block.BlockType.BOMB, data);
									break;
								case 3: // Other
									switch (data) {
										case 0:
											block = new Block(Block.BlockType.WEDGE);
											break;
										case 1:
											block = new Block(Block.BlockType.STAR);
											break;
										case 2:
											block = new Block(Block.BlockType.TRASH);
											break;
										case 3:
											block = new Block(Block.BlockType.HEART);
											break;
										case 4:
											block = new Block(Block.BlockType.ROCK);
											break;
										default: // if error return basic blue block
											block = new Block(Block.BlockType.BOMB, 9);
											break;
									} // end switch(data)
									break;
								default:
									block = new Block(Block.BlockType.BOMB, 5);
									break;
							} // end switch(id)
							grid[x].blocks[y] = block;
						}
					}
					break; // end version 1a format
				default: // unknown file format
					
			}
			inf.close();
		} catch (Exception err) {
			Global.writeToLog(String.format("An error occured trying to read from file '%s'",filename), true);
			Global.writeToLog(err.getMessage(), true);
			return null;
		}
		return grid;
	}
	
}
