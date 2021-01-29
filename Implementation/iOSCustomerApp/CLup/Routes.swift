import Foundation

struct ServerRoutes {
    private static let baseURL = "https://serverlessbackon.now.sh/api"
    static let signUp = {baseURL+"/signin.js"}()
    static let getMyBonds = {baseURL+"/getMyBonds.js"}()
    static let removeTask = {baseURL+"/cancelTask.js"}()
    static let removeRequest = {baseURL+"/deleteRequest.js"}()
    static let discover = {baseURL+"/discover.js"}()
    static let addRequest = {baseURL+"/addRequest.js"}()
    static let addTask = {baseURL+"/addTask.js"}()
    static let reportTask = {baseURL+"/reportTask.js"}()
    static let updateProfile = {baseURL+"/updateProfile.js"}()
    static let sendNotification = {baseURL+"/sendPush.js"}()
}
