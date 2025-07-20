import { ParentComponent } from "solid-js";
import KlinkSidebar from "../KlinkSidebar";

const PageLayout: ParentComponent = (props) => {
  return (
    <div class="flex flex-col lg:flex-row h-screen">
      <div class="w-full lg:w-1/6 lg:h-full border-base-300 lg:border-r-2">
        <KlinkSidebar />
      </div>
      {props.children}
    </div>
  );
};

export default PageLayout;
