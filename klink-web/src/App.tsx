import type { Component } from 'solid-js';
import Health from './components/Health';

const App: Component = () => {
  return (
    <div class="flex flex-col align-middle justify-center">
      <p class="text-4xl text-green-700 text-center py-20">Hello tailwind!</p>
      <Health />
    </div>
  );
};

export default App;
