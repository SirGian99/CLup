import SwiftUI

struct OpaqueOverlay<Content: View>: View {
    @Binding var isPresented: Bool
    let opacity: Double
    let alignment: Alignment
    let toOverlay: Content
    
    init(isPresented: Binding<Bool>, toOverlay: Content, alignment: Alignment = .bottom, opacity: Double = 0.6) {
        self._isPresented = isPresented
        self.toOverlay = toOverlay
        self.alignment = alignment
        self.opacity = opacity
    }
    
    var body: some View {
        GeometryReader { geometry in
            if self.isPresented {
                Color
                    .black
                    .opacity(self.opacity)
                    .onTapGesture{withAnimation{self.isPresented = false}}
                    .overlay(self.toOverlay, alignment: self.alignment)
                    .edgesIgnoringSafeArea(.all)
                    .frame(width: geometry.size.width, height: geometry.size.height, alignment: .center)
                    .animation(.easeInOut)
            } else {
                EmptyView()
                    .animation(.easeInOut)
            }
        }
    }
}

