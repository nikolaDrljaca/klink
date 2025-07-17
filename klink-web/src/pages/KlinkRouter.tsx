import { MatchFilters, Navigate, Route, Router } from "@solidjs/router";
import { Component } from "solid-js";
import RootLayout from "~/components/layout/RootLayout";
import KlinkCollectionRoute from "~/pages/collection/KlinkCollectionRoute";
import KlinkImportRoute from "~/pages/import/KlinkImportRoute";
import KlinkRoute from "~/pages/klink/KlinkRoute";
import ComingSoonRoute from "~/pages/status/ComingSoonRoute";
import NotFoundRoute from "~/pages/status/404Route";

const uuidRouteFilter: MatchFilters = {
  klinkId: (v: string) => v.length === 36,
};

const RedirectToRoot: Component = () => {
  return <Navigate href="/c" />;
};

const KlinkRouter: Component = () => {
  return (
    <Router root={RootLayout} preload>
      <Route path="/" component={RedirectToRoot} />
      <Route
        path="/c"
        component={KlinkCollectionRoute}
      />
      <Route
        path="/c/:klinkId/i"
        component={KlinkImportRoute}
        matchFilters={uuidRouteFilter}
      />
      <Route
        path="/c/:klinkId"
        component={KlinkRoute}
        matchFilters={uuidRouteFilter}
      />
      <Route path="/settings" component={ComingSoonRoute} />
      <Route path="/about" component={ComingSoonRoute} />
      <Route path="*param" component={NotFoundRoute} />
    </Router>
  );
};

export default KlinkRouter;
