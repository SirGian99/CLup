import SwiftUI

struct ServerRoutes {
    private static let baseURL = "http://192.168.1.10:8080/CLup"
    //CustomerInt
    static let registerApp = baseURL+"/customer/registerApp"
    static func customerData(_ id: String) -> String { return baseURL+"/customer/\(id)" }
    //StoreInfoInt
    static func store(id: String) -> String { return baseURL+"/store/\(id)/generalInfo" }
    static func chainstore(city: String) -> String { return baseURL+"/chainstore?city=\(city)" }
    static func stores(chain: String, city: String) -> String { return baseURL+"/chain/\(chain)/stores?city=\(city)" }
    //BookingInt
    static let booking = baseURL+"/booking"
    //LineUpInt
    static let lineup = baseURL+"/lineup"
}
