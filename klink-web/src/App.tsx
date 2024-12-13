import type { Component } from 'solid-js';
import { MatchFilters, Navigate, Route, Router } from '@solidjs/router';
import RootLayout from '~/components/layout/RootLayout';
import KlinkRoute from '~/pages/KlinkRoute'
import KlinkCollectionRoute from '~/pages/KlinkCollectionRoute'
import NotFoundRoute from './pages/404Route';
import { Toaster } from 'solid-toast';
import KlinkImportRoute from '~/pages/KlinkImportRoute';

const App: Component = () => {
  return (
    <>
      <Toaster
        position="bottom-right"
        toastOptions={{
          style: {
            background: '#2A323C',
            color: 'white'
          }
        }}
      />
      <Router root={RootLayout}>
        <Route path="/" component={() => <Navigate href="/c" />} />
        <Route path="/c" component={KlinkCollectionRoute} />
        <Route path="/c/:klinkId/i" component={KlinkImportRoute} matchFilters={uuidRouteFilter} />
        <Route path="/c/:klinkId" component={KlinkRoute} />
        <Route path="*param" component={NotFoundRoute} />
      </Router>
    </>
  );
};

const uuidRouteFilter: MatchFilters = {
  klinkId: (v: string) => v.length === 36
}

export default App;
