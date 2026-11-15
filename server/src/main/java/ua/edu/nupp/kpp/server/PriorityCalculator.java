package ua.edu.nupp.kpp.server;

public class PriorityCalculator {

    private static final double URGENCY_WEIGHT = 0.40;
    private static final double IMPACT_WEIGHT = 0.35;
    private static final double CRITICALITY_WEIGHT = 0.25;

    public static double calculate(int urgency, int impact, int criticality) {
        double rawIndex = URGENCY_WEIGHT * urgency
                + IMPACT_WEIGHT * impact
                + CRITICALITY_WEIGHT * criticality;
        return Math.round(rawIndex * 100.0) / 100.0;
    }
}