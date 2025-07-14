import { ParentComponent } from "solid-js";

const RootLayout: ParentComponent = (props) => {
  return (
    <div class="lg:container lg:mx-auto h-screen overflow-none">
      {props.children}
    </div>
  );
};

export default RootLayout;
