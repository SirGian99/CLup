package it.polimi.se2.ricciosorrentinotriuzzi.entities;

public enum VisitRequestStatus {
    PENDING(0), READY(1), FULFILLED(2), COMPLETED (3);

    private final int value;

    VisitRequestStatus(int value) {
        this.value = value;
    }

    public static VisitRequestStatus getVisitStatusFromInt(int value) {
        switch (value) {
            case 0:
                return VisitRequestStatus.PENDING;
            case 1:
                return VisitRequestStatus.READY;
            case 2:
                return VisitRequestStatus.FULFILLED;
            case 3:
                return VisitRequestStatus.COMPLETED;
        }
        return null;
    }

    public int getValue() {
        return value;
    }

}
