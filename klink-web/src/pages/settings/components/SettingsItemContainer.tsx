import { ParentComponent } from "solid-js";

const SettingsItemContainer: ParentComponent = (props) => {
  return (
    <div class="flex flex-col lg:flex-row lg:justify-between lg:items-center py-4 gap-y-3 lg:gap-y-0">
      {props.children}
    </div>
  );
};

export default SettingsItemContainer;
