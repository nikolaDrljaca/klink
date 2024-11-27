import { ParentComponent } from "solid-js";


const RootLayout: ParentComponent = (props) => {
  return (
    <div class="overflow-none h-screen">
      {props.children}
    </div>
  );
}

export default RootLayout;
