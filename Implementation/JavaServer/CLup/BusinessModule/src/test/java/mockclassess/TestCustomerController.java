package mockclassess;

import it.polimi.se2.ricciosorrentinotriuzzi.business.components.CustomerController;
import it.polimi.se2.ricciosorrentinotriuzzi.component.DataModel;

 public class TestCustomerController extends CustomerController {

    public TestCustomerController(DataModel dataModel) {
        super();
        this.dataModel = dataModel;
    }
}