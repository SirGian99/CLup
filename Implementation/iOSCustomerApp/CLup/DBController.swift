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
    
//    func getMyCommitments(completion: @escaping ([String:Task]?, [String:Request]?, [String:User]?, ErrorString?) -> Void) {
//        do {
//            print("*** DB - \(#function) ***")
//            let parameters: [String: String] = ["_id": cdc.loggedUser!.id]
//            let request = initJSONRequest(urlString: ServerRoutes.getMyBonds, body: try JSONSerialization.data(withJSONObject: parameters))
//            URLSession.shared.dataTask(with: request) { data, response, error in
//                guard error == nil else {return completion(nil,nil,nil,"Error in \(#function). The error is:\n\(error!.localizedDescription)")}
//                guard let responseCode = (response as? HTTPURLResponse)?.statusCode else {return completion(nil,nil,nil,"Error in \(#function). Invalid response!")}
//                guard responseCode == 200 else {return completion(nil,nil,nil,"Bad response code in \(#function): \(responseCode)")}
//                guard let data = data, let jsonTasksAndRequests = try? JSON(data: data) else {return completion(nil,nil,nil,"Error with returned data in \(#function)")}
//                var tasksJSONArray = jsonTasksAndRequests["tasks"].arrayValue
//                var requestsJSONArray = jsonTasksAndRequests["requests"].arrayValue
//                var taskDict: [String:Task] = [:]
//                var requestDict: [String:Request] = [:]
//                var userDict: [String:User] = [:]
//                self.parseNeedJSONArray(jsonArray: &tasksJSONArray, needDict: &taskDict, userDict: &userDict)
//                self.parseNeedJSONArray(jsonArray: &requestsJSONArray, needDict: &requestDict, userDict: &userDict)
//                completion(taskDict, requestDict, userDict, nil)
//            }.resume()
//        } catch let error {completion(nil,nil,nil,"Error in \(#function). The error is:\n\(error.localizedDescription)")}
//    }
//
//    func addRequest(request: Request, completion: @escaping (String?, ErrorString?) -> Void) { // (id, error)
//        do {
//            print("*** DB - \(#function) ***")
//            let parameters: [String: Any?] = ["title": request.title, "description": request.descr, "neederID" : cdc.loggedUser!.id, "date": serverDateFormatter(date: request.date), "latitude": request.position.latitude, "longitude": request.position.longitude]
//            let request = initJSONRequest(urlString: ServerRoutes.addRequest, body: try JSONSerialization.data(withJSONObject: parameters))
//            URLSession.shared.dataTask(with: request) { data, response, error in
//                guard error == nil else {return completion(nil, "Error in \(#function). The error is:\n\(error!.localizedDescription)")}
//                guard let responseCode = (response as? HTTPURLResponse)?.statusCode else {return completion(nil,"Error in \(#function). Invalid response!")}
//                guard responseCode == 200 else {return completion(nil,"Response code != 200 in \(#function): \(responseCode)")}
//                guard let data = data, let jsonResponse = try? JSON(data: data) else {return completion(nil, "Error with returned data in \(#function)")}
//                let _id = jsonResponse["_id"].stringValue
//                completion(_id, nil)
//            }.resume()
//        } catch let error {completion(nil, "Error in \(#function). The error is:\n" + error.localizedDescription)}
//    }
//
//    func removeNeed<Content:Need>(toRemove: Content, completion: @escaping (ErrorString?) -> Void) {
//        var isRequest = true
//        if toRemove is Task {isRequest = false}
//        do {
//            let parameters: [String: String] = ["_id": toRemove.id]
//            let request = initJSONRequest(urlString: isRequest ? ServerRoutes.removeRequest : ServerRoutes.removeTask, body: try JSONSerialization.data(withJSONObject: parameters), httpMethod: isRequest ? "DELETE" : "PUT")
//            URLSession.shared.dataTask(with: request) { data, response, error in
//                guard error == nil else {return completion("Error in \(#function) opering with a \(isRequest ? "request" : "task"). The error is:\n\(error!.localizedDescription)")}
//                guard let responseCode = (response as? HTTPURLResponse)?.statusCode else {return completion("Error in \(#function). Invalid response!")}
//                guard responseCode == 200 else {return completion("Bad response code in \(#function): \(responseCode)")}
//                self.sendPushNotification(receiverID: toRemove.user?.id, title: isRequest ? "Don't worry!" : "Oh no! \(self.cdc.loggedUser!.name) can't help you anymore", body: isRequest ? "\(self.cdc.loggedUser!.name) doesn't need your help anymore.\nThanks anyway for your care!" : "Wait for someone else to accept your \(toRemove.title) request.")
//                completion(nil)
//            }.resume()
//        } catch let error {completion("Error in \(#function) opering with a \(isRequest ? "request" : "task"). The error is:\n" + error.localizedDescription)}
//    }
    
    
    private func parseJSON(jsonResponse: JSON) -> (name: String, surname: String?, photoURL: URL?, phoneNumber: String?) {
        let name = jsonResponse["name"].stringValue
        let surname = jsonResponse["surname"].string
        let photoURL = URL(string: jsonResponse["photo"].string)
        let phoneNumber = jsonResponse["phoneNumber"].string
        return (name, surname, photoURL, phoneNumber)
    }
    
    
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
        formatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
        return formatter.string(from: date)
    }
    
    
}
