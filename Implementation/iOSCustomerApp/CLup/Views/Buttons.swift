import SwiftUI

struct CloseButton: View {
    @Environment(\.presentationMode) var presentationMode
    var body: some View {
        Button(action: {
            withAnimation {
                self.presentationMode.wrappedValue.dismiss()
            }
        }){
            Image(systemName: "xmark.circle.fill").font(.largeTitle).tint(.white).opacity(0.9)
        }.customButtonStyle()
    }
}

struct LineUpButton: View {
    let ete: String
    var body: some View {
        Button(action: {print("LINE-UP!!")}){
            VStack {
                Text("Line-up now")
                    .fontWeight(.semibold)
                    .font(.body)
                Text("Estimated waiting: \(ete) min")
                    .font(.subheadline)
            }
            .tint(.blueLabel)
        }.customButtonStyle()
    }
}

struct BookingButton: View {
    var body: some View {
        Button(action: {print("BOOOKKKKK!!")}){
            VStack {
                Text("Book a visit")
                    .fontWeight(.semibold)
                    .font(.body)
            }
            .tint(.blueLabel)
        }.customButtonStyle()
    }
}

struct CancelButton: View {
    var body: some View {
        Button(action: {print("CANCELLLL!!")}){
            VStack {
                Text("Cancel request")
                    .fontWeight(.semibold)
                    .font(.body)
            }
            .tint(.blueLabel)
        }.customButtonStyle()
    }
}

struct DirectionsButton: View {
    let isFilled: Bool = false
    
    var body: some View {
        Button(action: {print("DIR ACTION")}){
            VStack {
                Text("Directions")
                    .fontWeight(.semibold)
                    .font(.body)
            }
            .frame(width: defaultButtonDimensions.width, height: defaultButtonDimensions.height)
            .cornerRadius(10)
            .overlay(RoundedRectangle(cornerRadius: 10).stroke(!isFilled ? getColor(.detailedTaskHeaderBG) : Color(#colorLiteral(red: 0, green: 0, blue: 0, alpha: 0)), lineWidth: 1))
        }.customButtonStyle()
    }
}

struct GenericButton: View {
    let dimensions: (width: CGFloat, height: CGFloat) = defaultButtonDimensions
    let isFilled: Bool
    var isLarge: Bool = false
    let color: Color = getColor(.detailedTaskHeaderBG)//Color(#colorLiteral(red: 0.9910104871, green: 0.6643157601, blue: 0.3115140796, alpha: 1)).opacity(0.9)
    let topText: String
    var bottomText: String? = nil
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            VStack {
                Text(topText)
                    .fontWeight(.semibold)
                    .font(.body)
                    .foregroundColor(!isFilled ? color : Color(#colorLiteral(red: 1, green: 1, blue: 1, alpha: 1)))
                if bottomText != nil {
                    Text(bottomText!)
                        .font(.subheadline)
                        .foregroundColor(!isFilled ? color : Color(#colorLiteral(red: 1, green: 1, blue: 1, alpha: 1)))
                }
            }
            .frame(width: isLarge ? dimensions.width*2 : dimensions.width, height: dimensions.height)
            .background(isFilled ? color : Color(#colorLiteral(red: 0, green: 0, blue: 0, alpha: 0))).cornerRadius(10)
            .overlay(RoundedRectangle(cornerRadius: 10).stroke(!isFilled ? color : Color(#colorLiteral(red: 0, green: 0, blue: 0, alpha: 0)), lineWidth: 1))
        }.customButtonStyle()
    }
}
