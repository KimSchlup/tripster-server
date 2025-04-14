package ch.uzh.ifi.hase.soprafs24.constant;

public enum DecisionProcess {
    MAJORITY, OWNER_DECISION, DEFAULT;

    public static DecisionProcess getDefault() {
        return DecisionProcess.MAJORITY;
    }
}
