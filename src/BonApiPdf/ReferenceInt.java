package BonApiPdf;

public class ReferenceInt {
	public int Entero=Integer.MAX_VALUE;
	public String LevelCaption="Level";
	private int level=0;
	public void goingDown() {
		Entero--;
		level++;
	}
	public void goingUp() {
		Entero++;
		level--;
	}
	public boolean isEnd() {
		return (Entero <=0);
	}
	public int getLevel() {
		return level;
	}
	public String getNextField() {
		goingDown();
		return LevelCaption + "__" + level;
	}
	public String getCurrentField() {
		return LevelCaption + "__" + level;
	}
}
