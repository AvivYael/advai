package pcp.actions;

public class SendHome implements Action {
	@Override
	public String toString() {
		return "SendHome";
	}
	
	@Override
	public int hashCode() {
		return 1;
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof SendHome);
	}
}
