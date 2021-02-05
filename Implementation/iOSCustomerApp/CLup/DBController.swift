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
    
    func getMyRequests(completion: @escaping (String?) -> Void) { //(error)
        print("*** DB - \(#function) ***")
        let request = initJSONRequest(urlString: ServerRoutes.customerData(deviceToken), body: Data(), httpMethod: "GET")
        URLSession.shared.dataTask(with: request) { data, response, error in
            guard error == nil else {return completion("Error in \(#function). The error is:\n\(error!.localizedDescription)")}
            guard let responseCode = (response as? HTTPURLResponse)?.statusCode else {return completion("Error in \(#function). Invalid response!")}
            guard responseCode == 200 else {return completion("Bad response code in \(#function): \(responseCode)")}
            guard let data = data, let jsonResponse = try? JSON(data: data) else {return completion("Error with returned data in \(#function)")}
            let lurArray = jsonResponse["lineupRequests"].arrayValue
            let brArray = jsonResponse["bookingRequests"].arrayValue
            for lurJson in lurArray {
                let hfid = lurJson["visitToken"]["hfid"].stringValue
                let uuid = lurJson["visitToken"]["uuid"].stringValue
                let state = lurJson["state"].intValue
                let numberOfPeople = lurJson["numberOfPeople"].intValue
                let storeID = lurJson["storeID"].stringValue
                let ete = lurJson["estimatedTimeOfEntrance"].stringValue
                self.getStoreFromID(storeID: storeID) { (store, error) in
                    guard error == nil else {return completion(error!)}
                    let lur = LineUpRequest(numberOfPeople: numberOfPeople, visitToken: Token(hfid: hfid, uuid: UUID(uuidString: uuid)!), state: VRState(rawValue: state)!, ete: state == 1 ? nil : self.serverDateTimeFormatter(date: ete), store: store!)
                    print("Downloaded ",lur)
                    DispatchQueue.main.async { Repository.singleton.lurs[uuid] = lur }
                }
            }
            for brJson in brArray {
                let hfid = brJson["visitToken"]["hfid"].stringValue
                let uuid = brJson["visitToken"]["uuid"].stringValue
                let state = brJson["state"].intValue
                let numberOfPeople = brJson["numberOfPeople"].intValue
                let storeID = brJson["storeID"].stringValue
                let start = brJson["desiredTimeInterval"]["start"].stringValue
                let duration = self.durationFormatter(d: brJson["desiredTimeInterval"]["duration"].stringValue)
                let desiredTimeInterval = CTimeInterval(startingDateTime: self.serverDateTimeFormatter(date: start), duration: duration)
                let sections = brJson["productSectionsNames"].arrayObject! as! [String]
                self.getStoreFromID(storeID: storeID) { (store, error) in
                    guard error == nil else {return completion(error!)}
                    var sectionArray: [Section] = []
                    for sectName in sections {
                        sectionArray.append(store!.sections.compactMap{sect in
                            sect.name == sectName ? sect : nil
                        }.first!)
                    }
                    let br = BookingRequest(numberOfPeople: numberOfPeople, visitToken: Token(hfid: hfid, uuid: UUID(uuidString: uuid)!), state: VRState(rawValue: state)!, desiredTimeInterval: desiredTimeInterval, sections: sectionArray, store: store!)
                    print("Downloaded ",br)
                    DispatchQueue.main.async { Repository.singleton.brs[uuid] = br }
                }
            }
            completion(nil)
        }.resume()
    }

    func register(token: String, completion: @escaping (String?) -> Void) { // (error)
        do {
            print("*** DB - \(#function) ***")
            let parameters: [String: String] = ["appID":token]
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
                let ete = jsonResponse["estimatedTimeOfEntrance"].stringValue
                let hfid = jsonResponse["visitToken"]["hfid"].stringValue
                let uuid = jsonResponse["visitToken"]["uuid"].stringValue
                let state = jsonResponse["state"].intValue
                let lur = LineUpRequest(numberOfPeople: numberOfPeople, visitToken: Token(hfid: hfid, uuid: UUID(uuidString: uuid)!), state: VRState(rawValue: state)!, ete: state == 1 ? nil : self.serverDateTimeFormatter(date: ete), store: store)
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
                "start":serverDateTimeFormatter(date: desiredTimeInterval.startingDateTime),
                "duration":durationFormatter(d: desiredTimeInterval.duration)
            ]
            let sectionsIDs: [String] = sections.map {section in section.id}
            let parameters: [String: Any] = [
                "storeID":store.id,
                "numberOfPeople":"\(numberOfPeople)",
                "customerID":deviceToken,
                "desiredTimeInterval":timeIntervalDict,
                "sectionsIDs":sectionsIDs,
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
    
    func getChainStore(city: String, completion: @escaping ([String:Chain]?, [String:Store]?, String?) -> Void) { // (chainDict, storeDict, error)
        print("*** DB - \(#function) ***")
        let request = initJSONRequest(urlString: ServerRoutes.chainstore(city: city), body: Data(), httpMethod: "GET")
        URLSession.shared.dataTask(with: request) { data, response, error in
            guard error == nil else {return completion(nil, nil, "Error in \(#function). The error is:\n\(error!.localizedDescription)")}
            guard let responseCode = (response as? HTTPURLResponse)?.statusCode else {return completion(nil, nil, "Error in \(#function). Invalid response!")}
            guard responseCode == 200 else {return completion(nil, nil, "Response code != 200 in \(#function): \(responseCode)")}
            guard let data = data, let jsonResponse = try? JSON(data: data) else {return completion(nil, nil, "Error with returned data in \(#function)")}
            let chainArray = jsonResponse["chains"].arrayValue
            let storeArray = jsonResponse["autonomousStores"].arrayValue
            var chainDict: [String:Chain] = [:]
            var storeDict: [String:Store] = [:]
            for chainJson in chainArray {
                let name = chainJson["name"].stringValue
                let description = chainJson["description"].stringValue
                //let image = chainJson["image"].stringValue
                let chain = Chain(name: name, description: description, image: UIImage(named: "MissingImg")!)
                chainDict[name] = chain
            }
            for storeJson in storeArray {
                let id = storeJson["id"].stringValue
                let name = storeJson["name"].stringValue
                let description = storeJson["description"].string
                let currOcc = storeJson["currentOccupancy"].intValue
                let estimatedQueueDTimeStr = storeJson["estimatedQueueDisposalTime"].stringValue
                let estimatedQueueDTime = self.serverDateTimeFormatter(date: estimatedQueueDTimeStr)
                let estimatedQueueDTimeMinutes = Int(Date().distance(to: estimatedQueueDTime)/60)
                let addressJson = storeJson["address"]
                let address = Address(streetName: addressJson["streetName"].stringValue, streetNumber: addressJson["streetNumber"].stringValue, city: addressJson["city"].stringValue, postalCode: addressJson["postalCode"].stringValue, country: addressJson["country"].stringValue)
                let whArray = storeJson["workingHours"].arrayValue
                var whs = WorkingHours()
                for whJson in whArray {
                    let day = whJson["dayOfTheWeek"].intValue
                    let start = whJson["start"].stringValue
                    let end = whJson["end"].stringValue
                    (whs.wh[day])?.append(DayInterval(day: day, start: Time(time: start), end: Time(time: end)))
                }
                let sectionsArray = storeJson["productSections"].arrayValue
                var sections: [Section] = []
                for sectJson in sectionsArray {
                    let id = sectJson["id"].stringValue
                    let name = sectJson["name"].stringValue
                    sections.append(Section(id: id, name: name))
                }
                let store = Store(id: id, name: name, description: description, image: nil, address: address, currentOccupancy: currOcc, workingHours: whs, estimatedQueueDisposalTime: estimatedQueueDTimeMinutes, sections: sections, chain: nil)
                storeDict[id] = store
            }
            completion(chainDict, storeDict, nil)
        }.resume()
    }
    
    func getStores(chain: Chain, city: String, completion: @escaping (String?) -> Void) { // (error)
        print("*** DB - \(#function) ***")
        let request = initJSONRequest(urlString: ServerRoutes.stores(chain: chain.name, city: city), body: Data(), httpMethod: "GET")
        URLSession.shared.dataTask(with: request) { data, response, error in
            guard error == nil else {return completion("Error in \(#function). The error is:\n\(error!.localizedDescription)")}
            guard let responseCode = (response as? HTTPURLResponse)?.statusCode else {return completion("Error in \(#function). Invalid response!")}
            guard responseCode == 200 else {return completion("Response code != 200 in \(#function): \(responseCode)")}
            guard let data = data, let jsonResponse = try? JSON(data: data) else {return completion("Error with returned data in \(#function)")}
            let storeArray = jsonResponse["stores"].arrayValue
            var storeDict: [String:Store] = [:]
            for storeJson in storeArray {
                let id = storeJson["id"].stringValue
                let name = storeJson["name"].stringValue
                let description = storeJson["description"].string
                let currOcc = storeJson["currentOccupancy"].intValue
                let estimatedQueueDTimeStr = storeJson["estimatedQueueDisposalTime"].stringValue
                let estimatedQueueDTime = self.serverDateTimeFormatter(date: estimatedQueueDTimeStr)
                let estimatedQueueDTimeMinutes = Int(Date().distance(to: estimatedQueueDTime)/60)
                let addressJson = storeJson["address"]
                let address = Address(streetName: addressJson["streetName"].stringValue, streetNumber: addressJson["streetNumber"].stringValue, city: addressJson["city"].stringValue, postalCode: addressJson["postalCode"].stringValue, country: addressJson["country"].stringValue)
                let whArray = storeJson["workingHours"].arrayValue
                var whs = WorkingHours()
                for whJson in whArray {
                    let day = whJson["dayOfTheWeek"].intValue
                    let start = whJson["start"].stringValue
                    let end = whJson["end"].stringValue
                    (whs.wh[day])?.append(DayInterval(day: day, start: Time(time: start), end: Time(time: end)))
                }
                let sectionsArray = storeJson["productSections"].arrayValue
                var sections: [Section] = []
                for sectJson in sectionsArray {
                    let id = sectJson["id"].stringValue
                    let name = sectJson["name"].stringValue
                    sections.append(Section(id: id, name: name))
                }
                let store = Store(id: id, name: name, description: description, image: nil, address: address, currentOccupancy: currOcc, workingHours: whs, estimatedQueueDisposalTime: estimatedQueueDTimeMinutes, sections: sections, chain: chain)
                storeDict[id] = store
            }
            completion(nil)
        }.resume()
    }
    
    func getStoreFromID(storeID: String, completion: @escaping (Store?, String?) -> Void) { // (store, error)
        print("*** DB - \(#function) ***")
        let request = initJSONRequest(urlString: ServerRoutes.store(id: storeID), body: Data(), httpMethod: "GET")
        URLSession.shared.dataTask(with: request) { data, response, error in
            guard error == nil else {return completion(nil, "Error in \(#function). The error is:\n\(error!.localizedDescription)")}
            guard let responseCode = (response as? HTTPURLResponse)?.statusCode else {return completion(nil, "Error in \(#function). Invalid response!")}
            guard responseCode == 200 else {return completion(nil, "Response code != 200 in \(#function): \(responseCode)")}
            guard let data = data, let storeJson = try? JSON(data: data) else {return completion(nil, "Error with returned data in \(#function)")}
            let id = storeJson["id"].stringValue
            let name = storeJson["name"].stringValue
            let description = storeJson["description"].string
            let currOcc = storeJson["currentOccupancy"].intValue
            let estimatedQueueDTime = storeJson["estimatedQueueDisposalTime"].intValue
            let addressJson = storeJson["address"]
            let address = Address(streetName: addressJson["streetName"].stringValue, streetNumber: addressJson["streetNumber"].stringValue, city: addressJson["city"].stringValue, postalCode: addressJson["postalCode"].stringValue, country: addressJson["country"].stringValue)
            let whArray = storeJson["workingHours"].arrayValue
            var whs = WorkingHours()
            for whJson in whArray {
                let day = whJson["dayOfTheWeek"].intValue
                let start = whJson["start"].stringValue
                let end = whJson["end"].stringValue
                (whs.wh[day])?.append(DayInterval(day: day, start: Time(time: start), end: Time(time: end)))
            }
            let sectionsArray = storeJson["productSections"].arrayValue
            var sections: [Section] = []
            for sectJson in sectionsArray {
                let id = sectJson["id"].stringValue
                let name = sectJson["name"].stringValue
                sections.append(Section(id: id, name: name))
            }
            let store = Store(id: id, name: name, description: description, image: nil, address: address, currentOccupancy: currOcc, workingHours: whs, estimatedQueueDisposalTime: estimatedQueueDTime, sections: sections, chain: nil)
            completion(store, nil)
        }.resume()
    }
    
    private func initJSONRequest(urlString: String, body: Data, httpMethod: String = "POST") -> URLRequest {
        var request = URLRequest(url: URL(string: urlString)!)
        request.httpMethod = httpMethod
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        request.httpBody = body
        return request
    }
    
    private func serverDateTimeFormatter(date: String) -> Date {
        let formatter = DateFormatter()
        formatter.locale = .current//Locale(identifier: "en_US_POSIX")
        formatter.dateFormat = "yyyy-MM-dd HH:mm:ss.S"
        formatter.timeZone = .current
        if let parsedDate = formatter.date(from: date) {
            return parsedDate
        }
        fatalError("Wrong format for \(#function)")
    }
    
    private func serverDateTimeFormatter(date: Date) -> String {
        let formatter = DateFormatter()
        formatter.locale = .current//Locale(identifier: "en_US_POSIX")
        formatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
        formatter.timeZone = .current
        return formatter.string(from: date)
    }
    
//    private func serverDateFormatter(date: Date) -> String {
//        let formatter = DateFormatter()
//        formatter.locale = Locale(identifier: "en_US_POSIX")
//        formatter.dateFormat = "yyyy-MM-dd"
//        return formatter.string(from: date)
//    }
    
//    private func serverTimeFormatter(date: Date) -> String {
//        let formatter = DateFormatter()
//        formatter.locale = Locale(identifier: "en_US_POSIX")
//        formatter.dateFormat = "HH:mm:ss"
//        return formatter.string(from: date)
//    }
    
    private func durationFormatter(d: Duration) -> String {
        let hour = Int(d/60)
        let minutes = d - hour*60
        let hourStr = hour<=9 ? "0\(hour)" : "\(hour)"
        let minStr = minutes<=9 ? "0\(minutes)" : "\(minutes)"
        return "\(hourStr):\(minStr):00"
    }
    
    private func durationFormatter(d: String) -> Duration {
        let firstIndex = d.firstIndex(of: ":")!
        let hour = Int(d[d.startIndex..<firstIndex])!
        let secondPart = d[d.index(after: d.firstIndex(of: ":")!)..<d.endIndex]
        let secondIndex = secondPart.firstIndex(of: ":") ?? secondPart.endIndex
        let minute = Int(secondPart[secondPart.startIndex..<secondIndex])!
        return hour*60+minute
    }
    
}
