import Foundation
import SwiftUI
import UIKit
import CoreImage.CIFilterBuiltins

struct CustomButtonStyle: ButtonStyle {
    func makeBody(configuration: Self.Configuration) -> some View {
        configuration.label
            .blur(radius: configuration.isPressed ? 2 : 0)
    }
}


extension Button {
    func customButtonStyle() -> some View {
        return self.buttonStyle(CustomButtonStyle())
    }
}

extension UIImage {
    convenience init(qrFrom: String) {
        let context = CIContext()
        let data = qrFrom.data(using: String.Encoding.ascii)
        let filter = CIFilter(name: "CIQRCodeGenerator")!
        filter.setValue(data, forKey: "inputMessage")
        let transform = CGAffineTransform(scaleX: 10, y: 10)
        let qrCode = filter.outputImage!.transformed(by: transform)
        let cgImage = context.createCGImage(qrCode, from: qrCode.extent)
        self.init(cgImage: cgImage!)
    }
}

extension View {
    func opaqueOverlay<Content:View>(isPresented: Binding<Bool>, toOverlay: Content) -> some View {
        return self.overlay(OpaqueOverlay(isPresented: isPresented, toOverlay: toOverlay))
    }
    func tint(_ color: Palette) -> some View {
        return self.foregroundColor(getColor(color))
    }
    func tintIf(_ apply: Bool, _ color: Palette, _ otherwise: Palette) -> some View {
        return apply ? self.tint(color) : self.tint(otherwise)
    }
    func background(_ color: Palette) -> some View {
        return self.background(getColor(color))
    }
    func lightBlueCard() -> some View {
        return self.background(.lightBlueHeaderBG).cornerRadius(10).padding(.horizontal).padding(.vertical, 10)
    }
    func blueCard() -> some View {
        return self.background(.blueHeaderBG).cornerRadius(10).padding(.horizontal).padding(.vertical, 10)
    }
}



enum Palette {
    case blueHeaderBG
    case lightBlueHeaderBG
    case blueLabel
    case gray
    case gray3
    case gray6
    case black
    case white
    case primary
    case secondary
    case systemBG
}

func getColor(_ color: Palette) -> Color {
    switch color {
    case .blueHeaderBG:
        return Color(#colorLiteral(red: 0.8031415343, green: 0.8537344933, blue: 0.8799108267, alpha: 1))
    case .lightBlueHeaderBG:
        return Color(#colorLiteral(red: 0.9490196078, green: 0.9607843137, blue: 0.968627451, alpha: 1))
    case .blueLabel:
        return Color(#colorLiteral(red: 0.3137254902, green: 0.3647058824, blue: 0.4078431373, alpha: 1))
    case .gray:
        return Color(.systemGray)
    case .gray3:
        return Color(.systemGray3)
    case .gray6:
        return Color(.systemGray6)
    case .white:
        return Color.white
    case .systemBG:
        return Color(.systemBackground)
    case .secondary:
        return Color.secondary
    case .primary:
        return Color.primary
    case .black:
        return Color.black
    }
}

/*
 GUIDA AI COLORI
 - aggiungere alla palette i nomi dei colori
 - aggiungere i case corrispondenti nella funzione tint
 - applicare tint agli elementi desiderati
 
 N.B. i Color(.system^^^^^) si adattano automaticamente ai cambi light/dark mode e viceversa
 (e le coppie di colori utilizzati sono studiate da Apple. Le potete vedere a questo link: https://www.avanderlee.com/wp-content/uploads/2019/02/SemanticUI_app_Aaron_Brethorst.png)
 
 Se usate colori non .system^^^^^ e che devono cambiare a seconda della modalità nella vista in cui chiamate la .tint aggiungete
 @Environment(.\colorScheme) var colorScheme
 */

extension Color {
    public init(hex: String) {
        let r, g, b: CGFloat
        if hex.hasPrefix("#") {
            let start = hex.index(hex.startIndex, offsetBy: 1)
            let hexColor = String(hex[start...])
            if hexColor.count == 6 {
                let scanner = Scanner(string: hexColor)
                var hexNumber: UInt64 = 0
                if scanner.scanHexInt64(&hexNumber) {
                    r = CGFloat((hexNumber & 0xff0000) >> 16) / 255
                    g = CGFloat((hexNumber & 0x00ff00) >> 8) / 255
                    b = CGFloat(hexNumber & 0x0000ff) / 255
                    self.init(UIColor(red: r, green: g, blue: b, alpha: 1))
                    return
                }
            }
        }
        self.init(.clear)
    }
}
