import SwiftUI

struct ServerRoutes {
    private static let baseURL = "https://localhost:8080/api"
    //CustomerInt
    static let registerApp = baseURL+"/customer/registerApp" //TEST!
    static func customerData(_ id: String) -> String { return baseURL+"/customer/\(id)" }
    //StoreInfoInt
    static func chainstore(city: String) -> String { return baseURL+"/chainstore?city=\(city)" }
    static func stores(chain: String, city: String) -> String { return baseURL+"/chain/\(chain)/stores?city=\(city)" }
    //BookingInt
    static let booking = baseURL+"/booking" //TEST!
    //LineUpInt
    static let lineup = baseURL+"/lineup" //TEST!
}
