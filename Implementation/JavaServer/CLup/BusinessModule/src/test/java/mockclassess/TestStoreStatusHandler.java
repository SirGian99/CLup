package mockclassess;

import it.polimi.se2.ricciosorrentinotriuzzi.business.components.StoreStatusHandler;
import it.polimi.se2.ricciosorrentinotriuzzi.component.DataModel;

public class TestStoreStatusHandler extends StoreStatusHandler {

    public TestStoreStatusHandler(DataModel dataModel) {
        super();
        this.dataModel = dataModel;
    }
}
