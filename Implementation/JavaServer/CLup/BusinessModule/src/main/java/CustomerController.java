import it.polimi.se2.ricciosorrentinotriuzzi.Customer;
import it.polimi.se2.ricciosorrentinotriuzzi.components.DataModel;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class CustomerController {
    @EJB(name = "it.polimi.se2.ricciosorrentinotriuzzi.components/DataModel")
    private DataModel dataModel;

    public CustomerController() {}

    public Customer registerApp(String appid) {
        return dataModel.newAppCustomer(appid);
    }
}
