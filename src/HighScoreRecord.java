import java.time.LocalDateTime;
import java.util.zip.DataFormatException;

public final class HighScoreRecord implements Comparable<HighScoreRecord> {
	private String name, date, sScore, level;
	private int score;
	
	public HighScoreRecord(String name, LocalDateTime date, int score, int playLevel) {
		this.name = name;
		this.date = String.format("%1$tm/%1$td/%1$tY", date); // format as MM/DD/YYYY with leading zeros where necessary
		this.score = score;
		this.sScore = Integer.toString(score);
		this.level = String.format("%d", playLevel);
	}
	
	private HighScoreRecord() {
		name = "Unknown Player";
		date = "01/01/2016";
		score = 0;
		sScore = "0";
		level = "0";
	}
	
	public static HighScoreRecord getNewEmptyRecord() { return new HighScoreRecord(); }
	
	public String getName() { return name; }
	public String getDate() { return date; }
	public int getScore() { return score; }
	public String getScoreAsString() { return sScore; }
	public String getLevel() { return level; }

	@Override
	public int compareTo(HighScoreRecord record) {
		if (score < record.score) { return 1; }
		else if (score > record.score) { return -1; }
		// scores equal, check dates
		int dateComp = date.compareTo(record.date);
		if (dateComp != 0) { return dateComp; }
		// dates equal, last check by name
		return name.compareTo(record.name);
	}
	
	@Override
	public String toString(){
		return String.format("%s,%s,%d,%s", name, date, score, level);
	}
	
	public void readRecord(String recordData) throws DataFormatException {
		String[] arrData;
		int value;
		arrData = recordData.split(",");
		if (arrData.length != 4) {
			throw new DataFormatException("Invalid high score stream data.");
		}
		name = arrData[0];
		date = arrData[1];
		try {
			value = Integer.parseInt(arrData[2]);
		} catch (NumberFormatException nfe) {
			throw new DataFormatException("Unparsable score in high score stream data.");
		}
		score = value;
		sScore = arrData[2];
		level = arrData[3];
	}
}
