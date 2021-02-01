//
//  DatabaseController.swift
//  BackOn
//
//  Created by Riccio Vincenzo, Sorrentino Giancarlo, Triuzzi Emanuele, Zanfardino Gennaro on 18/02/2020.
//  Copyright © 2020 Riccio Vincenzo, Sorrentino Giancarlo, Triuzzi Emanuele, Zanfardino Gennaro. All rights reserved.
//

import Foundation
import SwiftUI

class DB {
    @AppStorage("deviceToken") var deviceToken: String = ""
    static var controller = DB()
    private init(){}
    
    func getMyRequests(completion: @escaping (LineUpRequest?, [String:BookingRequest]?, String?) -> Void) {
        //(lur, brDict, error)
        print("*** DB - \(#function) ***")
        let request = initJSONRequest(urlString: ServerRoutes.customerData(deviceToken), body: Data(), httpMethod: "GET")
        URLSession.shared.dataTask(with: request) { data, response, error in
            guard error == nil else {return completion(nil,nil,"Error in \(#function). The error is:\n\(error!.localizedDescription)")}
            guard let responseCode = (response as? HTTPURLResponse)?.statusCode else {return completion(nil,nil,"Error in \(#function). Invalid response!")}
            guard responseCode == 200 else {return completion(nil,nil,"Bad response code in \(#function): \(responseCode)")}
            guard let data = data, let jsonResponse = try? JSON(data: data) else {return completion(nil,nil,"Error with returned data in \(#function)")}
            
            let brArray = jsonResponse["bookingRequests"].arrayValue
            var lur: LineUpRequest? = nil
            var brDict: [String:BookingRequest] = [:]
            if jsonResponse["lineupRequest"].exists() {
                let lurJson = jsonResponse["lineupRequest"]
                let hfid = lurJson["visitToken"]["hfid"].stringValue
                let uuid = lurJson["visitToken"]["uuid"].stringValue
                let state = lurJson["state"].intValue
                let numberOfPeople = lurJson["numberOfPeople"].intValue
                let storeID = lurJson["storeID"].stringValue
                // TODO  CHIAMA LA GET INFO DELLO STORE!!!!!
                let lur = LineUpRequest(numberOfPeople: numberOfPeople, visitToken: Token(hfid: hfid, uuid: UUID(uuidString: uuid)!), state: VRState(rawValue: state)!, ete: state == 1 ? nil : Date(timeIntervalSinceNow: 600), store: store1)
            }
            completion(lur, brDict, nil)
        }.resume()
    }

    func register(completion: @escaping (String?) -> Void) { // (error)
        do {
            print("*** DB - \(#function) ***")
            let parameters: [String: String] = ["appID":deviceToken]
            let request = initJSONRequest(urlString: ServerRoutes.registerApp, body: try JSONSerialization.data(withJSONObject: parameters), httpMethod: "PUT")
            URLSession.shared.dataTask(with: request) { data, response, error in
                guard error == nil else {return completion("Error in \(#function). The error is:\n\(error!.localizedDescription)")}
                guard let responseCode = (response as? HTTPURLResponse)?.statusCode else {return completion("Error in \(#function). Invalid response!")}
                guard responseCode == 200 else {return completion("Response code != 200 in \(#function): \(responseCode)")}
                completion(nil)
            }.resume()
        } catch let error {completion("Error in \(#function). The error is:\n" + error.localizedDescription)}
    }
    
    func lineup(store: Store, numberOfPeople: Int, completion: @escaping (LineUpRequest?, String?) -> Void) { // (lur, error)
        do {
            print("*** DB - \(#function) ***")
            let parameters: [String: String] = ["storeID":store.id, "numberOfPeople":"\(numberOfPeople)", "customerID":deviceToken]
            let request = initJSONRequest(urlString: ServerRoutes.lineup, body: try JSONSerialization.data(withJSONObject: parameters))
            URLSession.shared.dataTask(with: request) { data, response, error in
                guard error == nil else {return completion(nil, "Error in \(#function). The error is:\n\(error!.localizedDescription)")}
                guard let responseCode = (response as? HTTPURLResponse)?.statusCode else {return completion(nil, "Error in \(#function). Invalid response!")}
                guard responseCode == 200 else {return completion(nil, "Response code != 200 in \(#function): \(responseCode)")}
                guard let data = data, let jsonResponse = try? JSON(data: data) else {return completion(nil, "Error with returned data in \(#function)")}
                guard jsonResponse["validated"].boolValue else {return completion(nil, "Request rejected")}
                let hfid = jsonResponse["visitToken"]["hfid"].stringValue
                let uuid = jsonResponse["visitToken"]["uuid"].stringValue
                let state = jsonResponse["state"].intValue
                let lur = LineUpRequest(numberOfPeople: numberOfPeople, visitToken: Token(hfid: hfid, uuid: UUID(uuidString: uuid)!), state: VRState(rawValue: state)!, ete: state == 1 ? nil : Date(timeIntervalSinceNow: 600), store: store)
                completion(lur, nil)
            }.resume()
        } catch let error {completion(nil, "Error in \(#function). The error is:\n" + error.localizedDescription)}
    }
    
    func deleteLUR(lur: LineUpRequest, completion: @escaping (String?) -> Void) { // (error)
        print("*** DB - \(#function) ***")
        let request = initJSONRequest(urlString: ServerRoutes.lineup+"/\(lur.visitToken.uuid.uuidString)", body: Data(), httpMethod: "DELETE")
        URLSession.shared.dataTask(with: request) { data, response, error in
            guard error == nil else {return completion("Error in \(#function). The error is:\n\(error!.localizedDescription)")}
            guard let responseCode = (response as? HTTPURLResponse)?.statusCode else {return completion("Error in \(#function). Invalid response!")}
            guard responseCode == 200 else {return completion("Response code != 200 in \(#function): \(responseCode)")}
            completion(nil)
        }.resume()
    }
    
    func booking(store: Store, sections: [Section], numberOfPeople: Int, desiredTimeInterval: CTimeInterval, completion: @escaping (BookingRequest?, String?) -> Void) { // (br, error)
        do {
            print("*** DB - \(#function) ***")
            let timeIntervalDict: [String:String] = [
                "date":serverDateFormatter(date: desiredTimeInterval.startingDateTime),
                "start":serverTimeFormatter(date: desiredTimeInterval.startingDateTime),
                "duration":"\(desiredTimeInterval.duration)"
            ]
            let sectionsIDs: [String] = sections.map {section in section.id}
            let parameters: [String: Any] = [
                "storeID":store.id,
                "numberOfPeople":"\(numberOfPeople)",
                "customerID":deviceToken,
                "desiredTimeInterval":timeIntervalDict,
                "sectionIDs":sectionsIDs,
                "alternativesDesired":"0"
            ]
            let request = initJSONRequest(urlString: ServerRoutes.booking, body: try JSONSerialization.data(withJSONObject: parameters))
            URLSession.shared.dataTask(with: request) { data, response, error in
                guard error == nil else {return completion(nil, "Error in \(#function). The error is:\n\(error!.localizedDescription)")}
                guard let responseCode = (response as? HTTPURLResponse)?.statusCode else {return completion(nil, "Error in \(#function). Invalid response!")}
                guard responseCode == 200 else {return completion(nil, "Response code != 200 in \(#function): \(responseCode)")}
                guard let data = data, let jsonResponse = try? JSON(data: data) else {return completion(nil, "Error with returned data in \(#function)")}
                guard jsonResponse["validated"].boolValue else {return completion(nil, "Request rejected")}
                let hfid = jsonResponse["visitToken"]["hfid"].stringValue
                let uuid = jsonResponse["visitToken"]["uuid"].stringValue
                let state = jsonResponse["state"].intValue
                let br = BookingRequest(numberOfPeople: numberOfPeople, visitToken: Token(hfid: hfid, uuid: UUID(uuidString: uuid)!), state: VRState(rawValue: state)!, desiredTimeInterval: desiredTimeInterval, sections: sections, store: store)
                completion(br, nil)
            }.resume()
        } catch let error {completion(nil, "Error in \(#function). The error is:\n" + error.localizedDescription)}
    }
    
    func deleteBR(br: BookingRequest, completion: @escaping (String?) -> Void) { // (error)
        print("*** DB - \(#function) ***")
        let request = initJSONRequest(urlString: ServerRoutes.booking+"/\(br.visitToken.uuid.uuidString)", body: Data(), httpMethod: "DELETE")
        URLSession.shared.dataTask(with: request) { data, response, error in
            guard error == nil else {return completion("Error in \(#function). The error is:\n\(error!.localizedDescription)")}
            guard let responseCode = (response as? HTTPURLResponse)?.statusCode else {return completion("Error in \(#function). Invalid response!")}
            guard responseCode == 200 else {return completion("Response code != 200 in \(#function): \(responseCode)")}
            completion(nil)
        }.resume()
    }
    
//    private func parseJSON(jsonResponse: JSON) -> (name: String, surname: String?, photoURL: URL?, phoneNumber: String?) {
//        let name = jsonResponse["name"].stringValue
//        let surname = jsonResponse["surname"].string
//        let photoURL = URL(string: jsonResponse["photo"].string)
//        let phoneNumber = jsonResponse["phoneNumber"].string
//        return (name, surname, photoURL, phoneNumber)
//    }
    
    
    private func initJSONRequest(urlString: String, body: Data, httpMethod: String = "POST") -> URLRequest {
        var request = URLRequest(url: URL(string: urlString)!)
        request.httpMethod = httpMethod
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        request.httpBody = body
        //request.setValue("close", forHTTPHeaderField: "Connection")
        return request
    }
    
    private func serverDateFormatter(date: String) -> Date {
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "en_US_POSIX")
        formatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
        if let parsedDate = formatter.date(from: date) {
            return parsedDate
        }
        return Date()
    }
    
    private func serverDateFormatter(date: Date) -> String {
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "en_US_POSIX")
        formatter.dateFormat = "yyyy-MM-dd"
        return formatter.string(from: date)
    }
    
    private func serverTimeFormatter(date: Date) -> String {
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "en_US_POSIX")
        formatter.dateFormat = "HH:mm:ss"
        return formatter.string(from: date)
    }
    
}
