import { Component } from "solid-js";
import { Toaster } from "solid-toast";

const KlinkToaster: Component = () => {
  return (
    <Toaster
      position="bottom-right"
      toastOptions={{
        style: {
          background: '#2A323C',
          color: 'white'
        }
      }}
    />
  );
}

export default KlinkToaster;
