package mockclassess;

import it.polimi.se2.ricciosorrentinotriuzzi.business.components.VisitManager;
import it.polimi.se2.ricciosorrentinotriuzzi.component.DataModel;
import it.polimi.se2.ricciosorrentinotriuzzi.entities.Booking;
import it.polimi.se2.ricciosorrentinotriuzzi.entities.Store;

public class TestVisitManager extends VisitManager {

    public TestVisitManager(DataModel dataModel) {
        super();
        this.dataModel = dataModel;
    }

    @Override
    protected synchronized void scheduleBooking(Booking request, int finalCurrentReadyOccupancy, Store store) {
        super.scheduleBooking(request, finalCurrentReadyOccupancy, store);
        this.notifyAll();
    }
}