package mockclassess;

import it.polimi.se2.ricciosorrentinotriuzzi.business.components.RequestHandler;
import it.polimi.se2.ricciosorrentinotriuzzi.business.components.VisitManager;
import it.polimi.se2.ricciosorrentinotriuzzi.component.DataModel;

public class TestRequestHandler extends RequestHandler {

    public TestRequestHandler(DataModel dataModel, VisitManager visitManager) {
        super();
        this.dataModel = dataModel;
        this.visitManager = visitManager;
    }
}