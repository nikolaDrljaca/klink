import { Component } from "solid-js";
import PageLayout from "~/components/layout/PageLayout";
import ExportSettingsItem from "./components/ExportSettingsItem";
import ImportSettingsItem from "./components/ImportSettingsItem";

const KlinkSettingsRoute: Component = () => {
  return (
    <PageLayout>
      <div class="w-full lg:w-3/6 lg:h-full px-4 pt-6 pb-2">
        <p class="font-inter text-2xl"># Settings</p>

        {/* Settings items */}
        <ImportSettingsItem />
        <ExportSettingsItem />
      </div>
    </PageLayout>
  );
};

export default KlinkSettingsRoute;
