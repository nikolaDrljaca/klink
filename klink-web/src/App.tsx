import type { Component } from 'solid-js';
import { Navigate, Route, Router } from '@solidjs/router';
import RootLayout from '~/components/layout/RootLayout';
import KlinkRoute from '~/pages/KlinkRoute'
import KlinkCollectionRoute from '~/pages/KlinkCollectionRoute'
import NotFoundRoute from './pages/404Route';
import { Toaster } from 'solid-toast';

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
        <Route path="/c/:klinkId?" component={KlinkCollectionRoute} />
        <Route path="/c/:klinkId/k" component={KlinkRoute} />
        <Route path="*param" component={NotFoundRoute} />
      </Router>
    </>
  );
};

export default App;
