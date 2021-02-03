import SwiftUI

struct ServerRoutes {
    private static let baseURL = "http://localhost:8080/CLup"
    //CustomerInt
    static let registerApp = baseURL+"/customer/registerApp" //TEST e aggiungi all'avvio!
    static func customerData(_ id: String) -> String { return baseURL+"/customer/\(id)" }
    //StoreInfoInt
    static func store(id: String) -> String { return baseURL+"/store/\(id)/generalInfo" } //TEST!
    static func chainstore(city: String) -> String { return baseURL+"/chainstore?city=\(city)" } //TEST!
    static func stores(chain: String, city: String) -> String { return baseURL+"/chain/\(chain)/stores?city=\(city)" } //TEST!
    //BookingInt
    static let booking = baseURL+"/booking" //TEST!
    //LineUpInt
    static let lineup = baseURL+"/lineup" //TEST!
}
