package pcp.actions;

public class SendToHospital implements Action {
	@Override
	public String toString() {
		return "SendToHospital";
	}
	
	@Override
	public int hashCode() {
		return 2;
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof SendToHospital);
	}
}
