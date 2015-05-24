package pcp;

import java.util.*;
import java.util.Map.Entry;

import pcp.actions.Action;
import pcp.states.State;
import mdp.*;

public class PCPExecuter {
	public static void main(String[] args) {
		int patientsNo = Integer.parseInt(args[0]);
		int maxHospitalHours = Integer.parseInt(args[1]);
		PCP pcp = new PCP(patientsNo, maxHospitalHours);
		double gamma = Double.parseDouble(args[2]);
		double epsilon = Double.parseDouble(args[3]);
		ValueIteration<State, Action> iter = new ValueIteration<State, Action>(gamma);
		Map<State, Double> utils = iter.valueIteration(pcp, epsilon);
		Map<State, Action> policies = findOptimalPolicy(pcp, utils);
		for (Entry<State, Action> entry : policies.entrySet())
			System.out.println(entry.getKey() + "\t" + entry.getValue());
	}
	
	private static <S, A> Map<S, A> findOptimalPolicy(MarkovDecisionProcess<S, A> mdp, Map<S, Double> utils) { 
		HashMap<S, A> map = new HashMap<S, A>();
		for (S s : mdp.states()) {
			Set<A> actions = mdp.actions(s);
			A aBest = null;
			double aBestSum = Double.NEGATIVE_INFINITY;
			for (A a : actions) {
				double aSum = 0;
				for (S sDelta : mdp.states())
					aSum += mdp.transitionProbability(sDelta, s, a) * utils.get(sDelta);
				if (aSum > aBestSum) {
					aBest = a;
					aBestSum = aSum;
				}
			}
			map.put(s, aBest);
		}
		return map;
	}
}
