package pcp.states;

import java.util.Objects;

public class State {
	private int patientNo;
	private Disease disease;
	private int hospitalHours;
	private boolean lastSurvives;

	public State(int patientNo, Disease disease, int hospitalHours, boolean lastSurvives) {
		this.patientNo = patientNo;
		this.disease = disease;
		this.hospitalHours = hospitalHours;
		this.lastSurvives = lastSurvives;
	}
	
	public int getPatientNo() {
		return patientNo;
	}

	public Disease getDisease() {
		return disease;
	}

	public int getHospitalHours() {
		return hospitalHours;
	}
	
	public boolean getLastSurvives() {
		return lastSurvives;
	}
	
	@Override
	public String toString() {
		return "(" + patientNo + ", " + disease + ", " + hospitalHours + ", " + lastSurvives + ")";
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(patientNo, disease, hospitalHours, lastSurvives);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof State))
			return false;
		State other = (State)obj;
		return ((this.patientNo == other.patientNo) &&
				(this.disease == other.disease) &&
				(this.hospitalHours == other.hospitalHours) &&
				(this.lastSurvives == other.lastSurvives));
	}
}
