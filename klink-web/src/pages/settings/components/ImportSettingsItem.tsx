import localforage from "localforage";
import { Component, createSignal, Show } from "solid-js";
import toast from "solid-toast";
import { KlinkService as service } from "~/stores/klink-store";
import { Klink } from "~/types/domain";
import SettingsItemContainer from "./SettingsItemContainer";

const ImportSettingsItem: Component = () => {
  const [loading, setLoading] = createSignal(false);
  const handleFile = async (event: Event) => {
    setLoading(true);
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = async () => {
      try {
        const json = JSON.parse(reader.result as string);
        // handle store - done via service since makePersisted is loaded once
        const store: Record<string, Klink> = JSON.parse(json["klink-store"]);
        for (const key of Object.keys(store)) {
          service.createKlinkRaw(store[key]);
        }
        // handle items - done via localForage since its loaded on demand
        for (const key of Object.keys(json)) {
          // skip store item
          if (key.includes("klink-store")) {
            continue;
          }
          const value = json[key];
          await localforage.setItem(key, value);
        }
        toast.success("Successfully imported collections!");
      } catch (err) {
        toast.error("Failed to import collections!");
      }
      setLoading(false);
    };

    reader.readAsText(file);
  };

  return (
    <SettingsItemContainer>
      <div class="flex flex-col">
        <p class="text-xl font-inter">Import</p>
        <span class="font-light text-sm text-base-content">
          Import Klink collections froma JSON file.
        </span>
      </div>

      <div class="flex items-center gap-x-2">
        <Show when={loading()}>
          <span class="loading loading-dots loading-lg"></span>
        </Show>
        <input
          type="file"
          accept=".json"
          disabled={loading()}
          onChange={handleFile}
          class="file-input file-input-bordered file-input-primary w-full max-w-xs"
        />
      </div>
    </SettingsItemContainer>
  );
};

export default ImportSettingsItem;
