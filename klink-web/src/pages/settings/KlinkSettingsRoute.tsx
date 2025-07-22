import { Component } from "solid-js";
import ExportSettingsItem from "./components/ExportSettingsItem";
import ImportSettingsItem from "./components/ImportSettingsItem";

const KlinkSettingsRoute: Component = () => {
  return (
    <div class="w-full lg:w-3/6 lg:h-full px-4 pt-6 pb-2">
      <p class="font-inter text-2xl"># Settings</p>

      {/* Settings items */}
      <ImportSettingsItem />
      <ExportSettingsItem />
    </div>
  );
};

export default KlinkSettingsRoute;
