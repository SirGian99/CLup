import Foundation
import SwiftUI
import UIKit

extension Date {
    func getDate() -> String {
        let formatter = DateFormatter()
        formatter.locale = .current//Locale(identifier: "en_US_POSIX")
        formatter.dateFormat = "yyyy-MM-dd"
        return formatter.string(from: self)
    }
    func getTime() -> String {
        let formatter = DateFormatter()
        formatter.locale = .current//Locale(identifier: "en_US_POSIX")
        formatter.dateStyle = .none
        formatter.dateFormat = "HH:mm"
        formatter.timeZone = .current //TimeZone(secondsFromGMT: 0)
        return formatter.string(from: self)
    }
    func getTimeAsDuration() -> Int {
        let components = Calendar.current.dateComponents([.hour, .minute, .second], from: self)
        guard let hour = components.hour, let minute = components.minute else {return -1}
        return hour*60+minute
    }
}

extension Binding where Value == PresentationMode {
    func dismiss() {
        wrappedValue.dismiss()
    }
}
