import SwiftUI

struct FirstTab: View {
    @EnvironmentObject var repo: Repository
    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                SizedDivider(height: 10)
                Text("Select a chain or an independent store")
                SizedDivider(height: 5)
                ScrollView(.vertical, showsIndicators: false) {
                    ForEach(repo.chainsArray(), id: \.name) { chain in
                        SizedDivider(height: 10)
                        NavigationLink(destination: StoreList(stores: Array(chain.stores.values))) {
                            ChainView(chain: chain)
                        }
                    }
                    ForEach(repo.storesArray(), id: \.name) { store in
                        SizedDivider(height: 10)
                        StoreView(store: store)
                    }
                }
            }
        }
    }
}

struct SecondTab: View {
    @EnvironmentObject var repo: Repository
    @State var showLURModal = false
    @State var showBRModal = false
    var body: some View {
        VStack(spacing: 0) {
            SizedDivider(height: 10)
            ScrollView(.vertical, showsIndicators: false) {
                if let lur = repo.lur {
                    Button(action: {showLURModal.toggle()}){
                        RequestPreview(req: lur)
                            .sheet(isPresented: $showLURModal){LURDetails(lur: lur)}
                    }
                }
                SizedDivider(height: 5)
                ForEach(repo.brsArray(), id: \.visitToken.uuid) { br in
                    SizedDivider(height: 10)
                    Button(action: {showBRModal.toggle()}){
                        RequestPreview(req: br)
                            .sheet(isPresented: $showBRModal){BRDetails(br: br)}
                    }
                }
            }
        }
    }
}
