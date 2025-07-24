import { Accessor, createSignal, onCleanup } from "solid-js";

export default function createPasteKeyEvent(): Accessor<Symbol | null> {
  const [value, setValue] = createSignal(null);

  const listener = (event: KeyboardEvent) => {
    if (event.ctrlKey && event.key === "v") {
      setValue(Symbol());
    }
  };

  document.addEventListener("keydown", listener);
  onCleanup(() => {
    document.removeEventListener("keydown", listener);
  });

  return value;
}
