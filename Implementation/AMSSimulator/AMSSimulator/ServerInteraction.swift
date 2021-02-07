//
//  ServerInteraction.swift
//  AMSSimulator
//
//  Created by Vincenzo Riccio on 04/02/2021.
//

import Foundation

class Server {
    static let controller = Server()
    private init(){}
    
    func requestAccess(token: String, store: String, completion: @escaping (String?) -> Void) { //(error)
        do {
            print("*** DB - \(#function) ***")
            let parameters: [String: String] = ["token": token, "storeID" : store]
            let request = initJSONRequest(urlString: ServerRoutes.accreq, body: try JSONSerialization.data(withJSONObject: parameters))
            URLSession.shared.dataTask(with: request) { data, response, error in
                guard error == nil else {return completion("Error in " + #function + ". The error is:\n \(error!.localizedDescription)")}
                guard let responseCode = (response as? HTTPURLResponse)?.statusCode else {return completion("Error in \(#function). Invalid response!")}
                guard responseCode == 200 else {return completion("Bad response code in \(#function): \(responseCode)")}
                guard let data = data, let jsonResponse = try? JSON(data: data) else {return completion("Response error in \(#function)")}
                guard jsonResponse["validated"].boolValue else {return completion("Request \(token) not validated")}
                Shared.instance.cache(token: token, numOfP: jsonResponse["numberOfPeople"].intValue)
                completion(nil)
            }.resume()
        } catch let error {completion("Error in \(#function). The error is:\n \(error.localizedDescription)")}
    }
    
    func confirmAccess(store: String, completion: @escaping (String?) -> Void) { //(error)
        do {
            print("*** DB - \(#function) ***")
            let parameters: [String: String] = ["token": Shared.instance.token!, "storeID" : store, "numberOfPeople" : "\(Shared.instance.numberOfPeople!)"]
            let request = initJSONRequest(urlString: ServerRoutes.accconf, body: try JSONSerialization.data(withJSONObject: parameters))
            URLSession.shared.dataTask(with: request) { data, response, error in
                guard error == nil else {return completion("Error in " + #function + ". The error is:\n \(error!.localizedDescription)")}
                guard let responseCode = (response as? HTTPURLResponse)?.statusCode else {return completion("Error in \(#function). Invalid response!")}
                guard responseCode == 200 else {return completion("Bad response code in \(#function): \(responseCode)")}
                Shared.instance.emptyCache()
                completion(nil)
            }.resume()
        } catch let error {completion("Error in \(#function). The error is:\n \(error.localizedDescription)")}
    }
    
    func requestExit(token: String, store: String, completion: @escaping (String?) -> Void) { //(error)
        do {
            print("*** DB - \(#function) ***")
            let parameters: [String: String] = ["token": token, "storeID" : store]
            let request = initJSONRequest(urlString: ServerRoutes.exitreq, body: try JSONSerialization.data(withJSONObject: parameters))
            URLSession.shared.dataTask(with: request) { data, response, error in
                guard error == nil else {return completion("Error in " + #function + ". The error is:\n \(error!.localizedDescription)")}
                guard let responseCode = (response as? HTTPURLResponse)?.statusCode else {return completion("Error in \(#function). Invalid response!")}
                guard responseCode == 200 else {return completion("Bad response code in \(#function): \(responseCode)")}
                guard let data = data, let jsonResponse = try? JSON(data: data) else {return completion("Response error in \(#function)")}
                guard jsonResponse["validated"].boolValue else {return completion("Request \(token) not validated")}
                Shared.instance.cache(token: token, numOfP: jsonResponse["numberOfPeople"].intValue)
                completion(nil)
            }.resume()
        } catch let error {completion("Error in \(#function). The error is:\n \(error.localizedDescription)")}
    }
    
    func confirmExit(store: String, completion: @escaping (String?) -> Void) { //(error)
        do {
            print("*** DB - \(#function) ***")
            let parameters: [String: String] = ["token": Shared.instance.token!, "storeID" : store, "numberOfPeople" : "\(Shared.instance.numberOfPeople!)"]
            let request = initJSONRequest(urlString: ServerRoutes.exitconf, body: try JSONSerialization.data(withJSONObject: parameters))
            URLSession.shared.dataTask(with: request) { data, response, error in
                guard error == nil else {return completion("Error in " + #function + ". The error is:\n \(error!.localizedDescription)")}
                guard let responseCode = (response as? HTTPURLResponse)?.statusCode else {return completion("Error in \(#function). Invalid response!")}
                guard responseCode == 200 else {return completion("Bad response code in \(#function): \(responseCode)")}
                Shared.instance.emptyCache()
                completion(nil)
            }.resume()
        } catch let error {completion("Error in \(#function). The error is:\n \(error.localizedDescription)")}
    }
    
    private func initJSONRequest(urlString: String, body: Data, httpMethod: String = "POST") -> URLRequest {
        var request = URLRequest(url: URL(string: urlString)!)
        request.httpMethod = httpMethod
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        request.httpBody = body
        return request
    }
}
