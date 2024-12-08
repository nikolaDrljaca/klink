import { ParentComponent } from "solid-js";
import { KlinkCollectionStoreProvider } from "~/lib/klinks/context";


const RootLayout: ParentComponent = (props) => {
  return (
    <KlinkCollectionStoreProvider>
      <div class="container overflow-none h-screen mx-auto">
        {props.children}
      </div>
    </KlinkCollectionStoreProvider>
  );
}

export default RootLayout;
