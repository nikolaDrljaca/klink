import type { Component } from 'solid-js';
import KlinkToaster from '~/components/KlinkToaster';
import KlinkRouter from '~/pages/KlinkRouter';

const App: Component = () => {
  return (
    <>
      <KlinkToaster />
      <KlinkRouter />
    </>
  );
};

export default App;
