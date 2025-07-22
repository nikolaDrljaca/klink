import { MatchFilters, Navigate, Route, Router } from "@solidjs/router";
import { Component, ParentComponent } from "solid-js";
import RootLayout from "~/components/layout/RootLayout";
import KlinkCollectionRoute from "~/pages/collection/KlinkCollectionRoute";
import KlinkImportRoute from "~/pages/import/KlinkImportRoute";
import KlinkRoute from "~/pages/klink/KlinkRoute";
import ComingSoonRoute from "~/pages/status/ComingSoonRoute";
import NotFoundRoute from "~/pages/status/404Route";
import KlinkSettingsRoute from "./settings/KlinkSettingsRoute";
import KlinkSidebar from "~/components/KlinkSidebar";

const uuidRouteFilter: MatchFilters = {
  klinkId: (v: string) => v.length === 36,
};

const RedirectToRoot: Component = () => {
  return <Navigate href="/c" />;
};

const SidebarWrapper: ParentComponent = (props) => {
  return (
    <div class="flex flex-col lg:flex-row h-screen">
      <div class="w-full lg:w-1/6 lg:h-full border-base-300 lg:border-r-2">
        <KlinkSidebar />
      </div>
      {props.children}
    </div>
  );
};

const KlinkRouter: Component = () => {
  return (
    <Router root={RootLayout}>
      <Route path="/" component={RedirectToRoot} />

      <Route
        path="/c"
        component={SidebarWrapper}
      >
        {/* Klink Collection nested routes */}
        <Route path="/" component={KlinkCollectionRoute}>
          <Route path="/" component={() => <></>} />
          <Route
            path="/:klinkId"
            component={KlinkRoute}
            matchFilters={uuidRouteFilter}
          />
          <Route
            path="/:klinkId/i"
            component={KlinkImportRoute}
            matchFilters={uuidRouteFilter}
          />
        </Route>

        <Route path="/settings" component={KlinkSettingsRoute} />
      </Route>

      <Route path="/about" component={ComingSoonRoute} />
      <Route path="*param" component={NotFoundRoute} />
    </Router>
  );
};

export default KlinkRouter;
