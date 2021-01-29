import Foundation
import SwiftUI
import UIKit

extension URL {
    init?(string: String?) {
        if string != nil {
            self.init(string: string!)
        } else {
            return nil
        }
    }
}

extension Date {
    func getTime(withSeconds: Bool = false) -> String? {
        if withSeconds {
            let components = Calendar.current.dateComponents([.hour, .minute, .second], from: self)
            guard let hour = components.hour, let minute = components.minute, let second = components.second else {return nil}
            var hourStr = "\(hour)"
            var minuteStr = "\(minute)"
            var secondStr = "\(second)"
            if hour < 10 {hourStr = "0"+hourStr}
            if minute < 10 {minuteStr = "0"+minuteStr}
            if second < 10 {secondStr = "0"+secondStr}
            return hourStr+":"+minuteStr+":"+secondStr
        } else {
            let components = Calendar.current.dateComponents([.hour, .minute], from: self)
            guard let hour = components.hour, let minute = components.minute else {return nil}
            var hourStr = "\(hour)"
            var minuteStr = "\(minute)"
            if hour < 10 {hourStr = "0"+hourStr}
            if minute < 10 {minuteStr = "0"+minuteStr}
            return hourStr+":"+minuteStr
        }
    }
}

struct NavigationConfigurator: UIViewControllerRepresentable {
    var configure: (UINavigationController) -> Void = { _ in }

    func makeUIViewController(context: UIViewControllerRepresentableContext<NavigationConfigurator>) -> UIViewController {
        UIViewController()
    }
    func updateUIViewController(_ uiViewController: UIViewController, context: UIViewControllerRepresentableContext<NavigationConfigurator>) {
        if let nc = uiViewController.navigationController {
            self.configure(nc)
        }
    }
}


extension UIViewController {
    func toggleEditMode(observedVar: Binding<Bool>) {
        isEditing ? setEditing(false, animated: true) : setEditing(true, animated: true)
        observedVar.wrappedValue.toggle()
        isModalInPresentation = isEditing
    }
    
    func setEditMode(observedVar: Binding<Bool>, value: Bool) {
        setEditing(value, animated: true)
        observedVar.wrappedValue = value
        isModalInPresentation = value
    }
    
    func present(_ toPresent: UIViewController) {
        DispatchQueue.main.async { self.present(toPresent, animated: true, completion: nil) }
    }
    
    func dismiss() {
        DispatchQueue.main.async { self.dismiss(animated: true, completion: nil) }
    }
    
    static var main: UIViewController {
        return UIApplication.shared.windows.first!.rootViewController!
    }
    
    static var foremost: UIViewController {
        var toReturn = UIViewController.main
        while toReturn.presentedViewController != nil {
            toReturn = toReturn.presentedViewController!
        }
        return toReturn
    }
}

extension Binding where Value == PresentationMode {
    func dismiss() {
        wrappedValue.dismiss()
    }
}
