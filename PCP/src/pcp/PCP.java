package pcp;

import java.util.*;

import pcp.actions.*;
import pcp.states.*;
import mdp.MarkovDecisionProcess;

public class PCP implements MarkovDecisionProcess<State, Action> {
	private int patientsNo;
	private HashMap<Disease, Double> diseaseProb;
	private HashMap<Integer, Double> hospitalHoursProb;
	private HashMap<Disease, HashMap<Action, Double>> survivingProb;
	private Set<State> states;
	
	public PCP(int patientsNo, int maxHospitalHours) {
		this.patientsNo = patientsNo;
		
		// Init the disease probabilities
		diseaseProb = new HashMap<Disease, Double>();
		diseaseProb.put(Disease.Flu, 0.8);
		diseaseProb.put(Disease.Cough, 0.1);
		diseaseProb.put(Disease.Ebola, 0.1);
		
		// Init the hospital hours probabilities
		hospitalHoursProb = new HashMap<Integer, Double>();
		for (int hospitalHours = 1; hospitalHours <= maxHospitalHours; hospitalHours++)
			hospitalHoursProb.put(hospitalHours, 0.5);
		
		// Init the surviving probabilities
		survivingProb = new HashMap<Disease, HashMap<Action, Double>>();
		HashMap<Action, Double> fluProb = new HashMap<Action, Double>();
		fluProb.put(new SendHome(), 1.0);
		fluProb.put(new SendToHospital(), 1.0);
		survivingProb.put(Disease.Flu, fluProb);
		HashMap<Action, Double> coughProb = new HashMap<Action, Double>();
		coughProb.put(new SendHome(), 0.5);
		coughProb.put(new SendToHospital(), 1.0);
		survivingProb.put(Disease.Cough, coughProb);
		HashMap<Action, Double> ebolaProb = new HashMap<Action, Double>();
		ebolaProb.put(new SendHome(), 0.0);
		ebolaProb.put(new SendToHospital(), 0.25);
		survivingProb.put(Disease.Ebola, ebolaProb);
		
		// Init the states
		states = new HashSet<State>();
		for (Disease disease : Disease.values()) {
			// The disease is None only in the initial and goal states
			if (disease == Disease.None) {
				// Add the initial state
				states.add(new State(0, Disease.None, 0, false));
				// Add the goal states
				for (int hospitalHours = 0; hospitalHours < maxHospitalHours; hospitalHours++) {
					states.add(new State(patientsNo + 1, disease, hospitalHours, true));
					states.add(new State(patientsNo + 1, disease, hospitalHours, false));
				}
			}
			else {
				// In the first patient state there can't be hospital hours or someone survived
				states.add(new State(1, disease, 0, false));
				// Add the inner patients
				for (int patientNo = 2; patientNo <= patientsNo; patientNo++) {
					for (int hospitalHours = 0; hospitalHours < maxHospitalHours; hospitalHours++) {
						states.add(new State(patientNo, disease, hospitalHours, true));
						states.add(new State(patientNo, disease, hospitalHours, false));
					}
				}
			}
		}
	}
	
	@Override
	public Set<State> states() {
		return states;
	}

	@Override
	public State getInitialState() {
		return new State(0, Disease.None, 0, false);
	}

	@Override
	public Set<Action> actions(State s) {
		HashSet<Action> set = new HashSet<Action>();
		// The disease is None only in the initial and goal states
		if (s.getDisease() == Disease.None) {
			// The patient no. is 0 only in the initial state
			if (s.getPatientNo() == 0)
				set.add(new StartDay());
		}
		else {
			// Each patient can be sent home or to the hospital
			set.add(new SendHome());
			set.add(new SendToHospital());
		}
		return set;
	}

	@Override
	public double transitionProbability(State sDelta, State s, Action a) {
		// The sDelta state can't be the initial state
		if (sDelta.getPatientNo() == 0)
			return 0;
		// The s state can't be a goal state
		if (s.getPatientNo() == patientsNo + 1)
			return 0;
		// The patient no. must increase by 1
		if (sDelta.getPatientNo() - s.getPatientNo() != 1)
			return 0;
		// The hospital hours must decrease by 1 if there was someone in hospital
		if ((s.getHospitalHours() > 0) && (s.getHospitalHours() - sDelta.getHospitalHours() != 1))
			return 0;
		// The hospital hours can't increase when sending home
		if ((a instanceof SendHome) && (sDelta.getHospitalHours() > s.getHospitalHours()))
			return 0;
		
		// If s is the initial state than the probability depends only on the disease of sDelta
		if (s.getPatientNo() == 0)
			return diseaseProb.get(sDelta.getDisease());
		double prob = 1;
		// The probability depends on the disease of sDelta if sDelta isn't a goal state
		if (sDelta.getPatientNo() <= patientsNo)
			prob *= diseaseProb.get(sDelta.getDisease());
		// The probability depends on the hospital hours of sDelta if a is SendToHospital
		if (a instanceof SendToHospital)
			prob *= hospitalHoursProb.get(sDelta.getHospitalHours() + 1);
		// The probability depends on whether the patient survives or not
		if (sDelta.getLastSurvives())
			prob *= survivingProb.get(s.getDisease()).get(a);
		else
			prob *= 1 - survivingProb.get(s.getDisease()).get(a);
		return prob;
	}

	@Override
	public double reward(State s) {
		return (s.getLastSurvives() ? 1 : 0);
	}
}
