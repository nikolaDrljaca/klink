import localforage from "localforage";
import { Component, createSignal, Show } from "solid-js";
import SettingsItemContainer from "./SettingsItemContainer";

const ExportSettingsItem: Component = () => {
  const [loading, setLoading] = createSignal(false);

  const fileDownload = (jsonString: string) => {
    const blob = new Blob([jsonString], { type: "application/json" });
    const url = URL.createObjectURL(blob);
    const filename = `klink-export-${Date.now()}.json`;

    const a = document.createElement("a");
    a.href = url;
    a.download = filename;
    a.click();

    URL.revokeObjectURL(url);
  };

  const handleClick = async () => {
    setLoading(true);
    const out: Record<string, string> = {};
    localforage
      .iterate((value: string, key, num) => {
        out[String(key)] = value;
      })
      .then(() => {
        const content = JSON.stringify(out, null, 2);
        fileDownload(content);
        setLoading(false);
      });
  };

  return (
    <SettingsItemContainer>
      <div class="flex flex-col">
        <p class="text-xl font-inter">Export</p>
        <span class="font-light text-sm text-base-content">
          Export your collections to a file.
        </span>
      </div>

      <button class="btn btn-primary w-24" onClick={handleClick}>
        <Show when={loading()} fallback={<p>Export</p>}>
          <span class="loading loading-dots loading-lg"></span>
        </Show>
      </button>
    </SettingsItemContainer>
  );
};

export default ExportSettingsItem;
