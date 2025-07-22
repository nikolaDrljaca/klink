import { ParentComponent } from "solid-js";
import KlinkCollection from "./components/KlinkCollection";

const KlinkCollectionRoute: ParentComponent = (props) => {
  return (
    <>
      <div class="w-full lg:w-2/6 h-full lg:border-base-300 lg:border-r-2">
        <KlinkCollection />
      </div>
      {props.children}
    </>
  );
};

export default KlinkCollectionRoute;
