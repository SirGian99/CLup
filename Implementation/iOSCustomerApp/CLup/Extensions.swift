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

class ViewWrapper<Content:View>: UIView {
    let body: UIHostingController<Content>
    init(_ rootView: Content) {
        body = UIHostingController(rootView: rootView)
        super.init(frame: CGRect(x: 0, y: 0, width: 1000, height: 1000))
        setupView()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("NSCoder init not implemented!!")
    }
    
    private func setupView() {
        translatesAutoresizingMaskIntoConstraints = false
        body.view.translatesAutoresizingMaskIntoConstraints = false
        body.view.frame = bounds
        body.view.backgroundColor = nil
        addSubview(body.view)
        NSLayoutConstraint.activate([
            body.view.topAnchor.constraint(equalTo: topAnchor),
            body.view.bottomAnchor.constraint(equalTo: bottomAnchor),
            body.view.leftAnchor.constraint(equalTo: leftAnchor),
            body.view.rightAnchor.constraint(equalTo: rightAnchor)
        ])
        sizeToFit()
    }
}

class HostingController<Content:View>: UIHostingController<Content>, UIAdaptivePresentationControllerDelegate {
    let hideStatusBar: Bool
    
    init(
        _ contentView: Content,
        hideStatusBar: Bool = false,
        modalPresentationStyle: UIModalPresentationStyle = .fullScreen,
        preventModalDismiss: Bool = false
    ) {
        self.hideStatusBar = hideStatusBar
        super.init(rootView: contentView)
        self.modalPresentationStyle = modalPresentationStyle
        self.isModalInPresentation = preventModalDismiss
        self.presentationController?.delegate = PresentationDelegate.shared
    }
    
    @objc required dynamic init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    open override var prefersStatusBarHidden: Bool {
        return hideStatusBar
    }
}

class PresentationDelegate: NSObject, UIAdaptivePresentationControllerDelegate {
    static let shared = PresentationDelegate()
    override private init() {
        super.init()
    }

    func presentationControllerDidAttemptToDismiss(_ presentationController: UIPresentationController) {
        let alertToPresent = UIAlertController(title: "You edited some fields", message: "Do you do want to discard changes?", preferredStyle: .alert)
        let action = UIAlertAction(title: "Discard", style: .destructive) { _ in
            presentationController.presentedViewController.dismiss()
        }
        alertToPresent.view.tintColor = .systemOrange
        alertToPresent.addAction(UIAlertAction(title: "Cancel", style: .default, handler: nil))
        alertToPresent.addAction(action)
        presentationController.presentedViewController.present(alertToPresent)
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
