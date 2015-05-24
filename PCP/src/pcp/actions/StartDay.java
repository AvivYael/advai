package pcp.actions;

public class StartDay implements Action {
	@Override
	public String toString() {
		return "StartDay";
	}
	
	@Override
	public int hashCode() {
		return 0;
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof StartDay);
	}
}
